/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2008-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.web.rest.v1;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.opennms.core.criteria.Alias.JoinType;
import org.opennms.core.criteria.CriteriaBuilder;
import org.opennms.core.criteria.restrictions.Restrictions;
import org.opennms.core.logging.Logging;
import org.opennms.core.spring.BeanUtils;
import org.opennms.netmgt.dao.api.NotificationDao;
import org.opennms.netmgt.model.OnmsNotification;
import org.opennms.netmgt.model.OnmsNotificationCollection;
import org.opennms.netmgt.notifd.api.NotificationConfigProvider;
import org.opennms.netmgt.notifd.api.NotificationTester;
import org.opennms.netmgt.provision.service.MonitorHolder;
import org.opennms.web.api.Authentication;
import org.opennms.web.rest.support.MultivaluedMapImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>NotificationRestService class.</p>
 *
 * @author ranger
 * @version $Id: $
 * @since 1.8.1
 */
@Component("notificationRestService")
@Path("notifications")
@Tag(name = "Notifications", description = "Notifications API")
public class NotificationRestService extends OnmsRestService {
    @Autowired
    private NotificationDao m_notifDao;

    /**
     * <p>getNotification</p>
     *
     * @param notifId a {@link java.lang.String} object.
     * @return a {@link org.opennms.netmgt.model.OnmsNotification} object.
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{notifId}")
    @Transactional
    public OnmsNotification getNotification(@PathParam("notifId") Integer notifId) {
        if (notifId == null) {
            throw getException(Status.BAD_REQUEST, "Notification ID is required");
        }
        final OnmsNotification notif = m_notifDao.get(notifId);
        if (notif == null) {
            throw getException(Status.NOT_FOUND, "Notification {} was not found.", Integer.toString(notifId));
        }
        return notif;
    }
    
    /**
     * <p>getCount</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("count")
    @Transactional
    public String getCount() {
        return Integer.toString(m_notifDao.countAll());
    }

    @GET
    @Path("summary")
    @Produces({MediaType.APPLICATION_JSON})
    public NotificationSummary getInfo(@Context final SecurityContext securityContext) {
        final String user = securityContext.getUserPrincipal().getName();
        final NotificationSummary info = new NotificationSummary();
        info.setUser(user);

        // All notifications (ack + unack)
        info.setTotalCount(m_notifDao.countAll());

        // All unack notifications
        info.setTotalUnacknowledgedCount(m_notifDao.countMatching(new CriteriaBuilder(OnmsNotification.class).isNull("answeredBy").toCriteria()));

        // All unacknowledged notifications for current user
        info.setUserUnacknowledgedCount(m_notifDao.countMatching(new CriteriaBuilder(OnmsNotification.class).isNull("answeredBy")
                .alias("usersNotified", "usersNotified").eq("usersNotified.userId", user)
                .toCriteria()));

        // Determine number of notices not acknowledged and not "assigned to" current user
        info.setTeamUnacknowledgedCount(m_notifDao.countMatching(new CriteriaBuilder(OnmsNotification.class)
                .isNull("answeredBy")
                .alias("usersNotified", "usersNotified", JoinType.LEFT_JOIN)
                .or(Restrictions.ne("usersNotified.userId", user), Restrictions.isNull("usersNotified.userId"))
                .toCriteria()));

        // Load newest unacknowledged notifications for user, but only N
        if (info.getUserUnacknowledgedCount() != 0) {
            final List<OnmsNotification> newestNotifications = m_notifDao.findMatching(new CriteriaBuilder(OnmsNotification.class).isNull("answeredBy")
                    .alias("usersNotified", "usersNotified").eq("usersNotified.userId", user)
                    .orderBy("pageTime", false)
                    .limit(10)
                    .toCriteria());
            info.setUserUnacknowledgedNotifications(new OnmsNotificationCollection(newestNotifications));
        }
        return info;
    }

    /**
     * <p>getNotifications</p>
     *
     * @return a {@link org.opennms.netmgt.model.OnmsNotificationCollection} object.
     */
    @GET
    // We have to have a blank path here so that the UriBuilder calls work
    @Path("")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Transactional
    public OnmsNotificationCollection getNotifications(@Context final UriInfo uriInfo) {
        final CriteriaBuilder builder = getCriteriaBuilder(uriInfo.getQueryParameters());
        builder.orderBy("notifyId").desc();

        OnmsNotificationCollection coll = new OnmsNotificationCollection(m_notifDao.findMatching(builder.toCriteria()));

        coll.setTotalCount(m_notifDao.countMatching(builder.count().toCriteria()));

        return coll;
    }
    
    /**
     * <p>updateNotification</p>
     *
     * @param notifId a {@link java.lang.String} object.
     * @param ack a {@link java.lang.Boolean} object.
     */
    @PUT
    @Path("{notifId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response updateNotification(@Context final SecurityContext securityContext, @PathParam("notifId") final Integer notifId, @FormParam("ack") final Boolean ack) {
        writeLock();
        
        try {
            if(ack==null) {
                throw getException(Status.BAD_REQUEST, "Must supply the 'ack' parameter, set to either 'true' or 'false'");
            }
            OnmsNotification notif= getNotification(notifId);
            processNotifAck(securityContext, notif,ack);
            return Response.noContent().build();
        } finally {
            writeUnlock();
        }
    }
    
    /**
     * <p>updateNotifications</p>
     *
     * @param params a {@link org.opennms.web.rest.support.MultivaluedMapImpl} object.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response updateNotifications(@Context final SecurityContext securityContext, final MultivaluedMapImpl params) {
        writeLock();
        
        try {
            Boolean ack=false;
            if(params.containsKey("ack")) {
                ack="true".equals(params.getFirst("ack"));
                params.remove("ack");
            }

            final CriteriaBuilder builder = getCriteriaBuilder(params);
            
            for (final OnmsNotification notif : m_notifDao.findMatching(builder.toCriteria())) {
                processNotifAck(securityContext, notif, ack);
            }
            return Response.noContent().build();
        } finally {
            writeUnlock();
        }
    }

    @POST
    @Path("destination-paths/{destinationPathName}/trigger")
    @Transactional
    public Response triggerDestinationPath(@Context final SecurityContext securityContext, @PathParam("destinationPathName") final String destinationPathName) {
        if (!securityContext.isUserInRole(Authentication.ROLE_ADMIN)) {
            throw getException(Status.FORBIDDEN, "User {} does not have access to trigger notifications.", securityContext.getUserPrincipal().getName());
        }

        NotificationConfigProvider notificationConfigProvider = BeanUtils.getBean("notifdContext",
                "notificationConfigProvider", NotificationConfigProvider.class);
        NotificationTester notificationTester = BeanUtils.getBean("notifdContext",
                "notificationTester", NotificationTester.class);

        List<String> targetNames = notificationConfigProvider.getTargetNames(destinationPathName, false);
        if (targetNames.isEmpty()) {
            return Response.noContent().build();
        }

        for (String targetName : targetNames) {
            for (String command : notificationConfigProvider.getCommands(destinationPathName, targetName, false)) {
                try(Logging.MDCCloseable ignored = Logging.withPrefixCloseable("notifd")) {
                    notificationTester.triggerNotificationsForTarget(targetName, command);
                }
            }
        }

        return Response.accepted().build();
    }

    private void processNotifAck(final SecurityContext securityContext, final OnmsNotification notif, final Boolean ack) {
        if(ack) {
            notif.setRespondTime(new Date());
            notif.setAnsweredBy(securityContext.getUserPrincipal().getName());
        } else {
            notif.setRespondTime(null);
            notif.setAnsweredBy(null);
        }
        m_notifDao.save(notif);
    }

    private static CriteriaBuilder getCriteriaBuilder(final MultivaluedMap<String, String> params) {
        final CriteriaBuilder builder = new CriteriaBuilder(OnmsNotification.class);
        builder.alias("node", "node", JoinType.LEFT_JOIN);
        builder.alias("node.snmpInterfaces", "snmpInterface", JoinType.LEFT_JOIN);
        builder.alias("node.ipInterfaces", "ipInterface", JoinType.LEFT_JOIN);
        builder.alias("event", "event", JoinType.LEFT_JOIN);
        builder.alias("usersNotified", "usersNotified", JoinType.LEFT_JOIN);

        applyQueryFilters(params, builder);
        return builder;
    }

}
