<%--
/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2014 The OpenNMS Group, Inc.
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

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ page import="org.opennms.web.utils.Bootstrap" %>
<% Bootstrap.with(pageContext)
          .headTitle("Application")
          .breadcrumb("Admin", "admin/index.jsp")
          .breadcrumb("Applications", "admin/applications.htm")
          .breadcrumb("Show")
          .build(request);
%>
<jsp:directive.include file="/includes/bootstrap.jsp" />

<div class="row">
  <div class="col-md-8">
    <div class="card">
      <div class="card-header">
        <span>Application: ${fn:escapeXml(model.application.name)}</span>
      </div>
      <div class="card-body">
        <p>
        Application '${fn:escapeXml(model.application.name)}' has ${fn:length(model.memberServices)} services.
        </p>

        <p>
        <a href="admin/applications.htm?edit=services&applicationid=${model.application.id}">Edit application</a>
        </p>

        <p>
          <h5>Defined service(s):</h5>

          <table class="table table-sm">
            <tr>
              <th>Node</th>
              <th>Interface</th>
              <th>Service</th>
            </tr>
            <c:forEach items="${model.memberServices}" var="service">
              <tr>
                <td><a href="element/node.jsp?node=${service.ipInterface.node.id}">${fn:escapeXml(service.ipInterface.node.label)}</a></td>
                <td><a href="element/interface.jsp?ipinterfaceid=${service.ipInterface.id}">${service.ipInterface.ipAddress.hostAddress}</a></td>
                <td><a href="element/service.jsp?ifserviceid=${service.id}">${fn:escapeXml(service.serviceName)}</a></td>
              </tr>
            </c:forEach>
          </table>
        </p>

        <p>
          <h5>Associated location(s):</h5>

          <table class="table table-sm">
            <tr>
              <th>Location</th>
            </tr>
            <c:forEach items="${model.memberLocations}" var="location">
              <tr>
                <td>${location.locationName}</td>
              </tr>
            </c:forEach>
          </table>
        </p>

      </div> <!-- card-body -->
    </div> <!-- panel -->
  </div> <!-- column -->
</div> <!-- row -->

<jsp:include page="/includes/bootstrap-footer.jsp" flush="false"/>
