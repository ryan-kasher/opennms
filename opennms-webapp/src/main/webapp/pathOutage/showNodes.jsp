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

<%@page language="java" contentType="text/html" session="true"
	import="java.sql.Connection,
			java.util.List,
			java.util.Set,
			org.opennms.core.db.DataSourceFactory,
			org.opennms.core.utils.DBUtils,
			org.opennms.netmgt.dao.api.PathOutageManager,
			org.opennms.netmgt.dao.hibernate.PathOutageManagerDaoImpl"
%>

<%@ page import="org.opennms.web.utils.Bootstrap" %>
<% Bootstrap.with(pageContext)
          .headTitle("Path Outage Nodes")
          .breadcrumb("Path Outages", "pathOutage/index.jsp")
          .breadcrumb("Nodes")
          .build(request);
%>
<jsp:directive.include file="/includes/bootstrap.jsp" />

<% 
      String critIp = request.getParameter("critIp");
      String critSvc = request.getParameter("critSvc");
      PathOutageManager pathOutageManager = PathOutageManagerDaoImpl.getInstance();
      String[] pthData = pathOutageManager.getCriticalPathData(critIp, critSvc);
      Set<Integer> nodeList = pathOutageManager.getNodesInPath(critIp, critSvc);
%>
  
<div class="card fix-subpixel">
    <div class="card-header">
        <span>Path Outage Node List</span>
    </div>
    <table class="table table-sm severity">
          <tr>
          <th>Critical Path</th>
          <th>Status</th>
          </tr>

          <tr>
          <td><%= critIp %></td>
          <td class="bright severity-<%= pthData[3].toLowerCase() %>"><%= critSvc %></td>
          </tr>

          <tr>
          <th>Node</th>
          <th>Status</th>
          </tr>

          <% for (Integer nodeid : nodeList) {
              String labelColor[] = PathOutageManagerDaoImpl.getInstance().getLabelAndStatus(nodeid.toString(), null); %>
              <tr>
              <td><a href="element/node.jsp?node=<%= nodeid %>"><%= labelColor[0] %></a></td>
              <td class="bright severity-<%= labelColor[1].toLowerCase() %>"><%= labelColor[2] %></td>
              </tr>
          <% } %>
    </table>
</div>

<jsp:include page="/includes/bootstrap-footer.jsp" flush="false" />
