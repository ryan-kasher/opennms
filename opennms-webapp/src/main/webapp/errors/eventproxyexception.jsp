<%--
/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2002-2014 The OpenNMS Group, Inc.
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

--%>

<%@page language="java"
	contentType="text/html"
	session="true"
	isErrorPage="true"
	import="org.opennms.netmgt.events.api.EventProxyException, org.opennms.web.utils.ExceptionUtils"
%>


<%
    EventProxyException e = ExceptionUtils.getRootCause(exception, EventProxyException.class);
%>


<%@ page import="org.opennms.web.utils.Bootstrap" %>
<% Bootstrap.with(pageContext)
          .headTitle("Connection Error")
          .headTitle("Error")
          .breadcrumb("Error")
          .build(request);
%>
<jsp:directive.include file="/includes/bootstrap.jsp" />

<h1>Event Delivery Error</h1>

<p>
  Could not send an event to the OpenNMS event daemon due to this
  error: <%= org.opennms.web.api.Util.htmlify(e.getMessage()) %>
</p>

<p>
  Is the OpenNMS daemon running?
</p>

<jsp:include page="/includes/bootstrap-footer.jsp" flush="false" />
