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
          .headTitle("Category")
          .breadcrumb("Admin", "admin/index.jsp")
          .breadcrumb("Application", "admin/applications.htm")
          .breadcrumb("Show")
          .build(request);
%>
<jsp:directive.include file="/includes/bootstrap.jsp" />

<form action="admin/applications.htm" method="get">
  <input type="hidden" name="ifserviceid" value="${model.service.id}"/>
  <input type="hidden" name="edit" value=""/>

<div class="row">
  <div class="col-md-12">
    <div class="card">
      <div class="card-header">
        <span>Edit applications on ${fn:escapeXml(model.service.serviceName)}</span>
      </div>
      <div class="card-body">
        <p>
        Service <a href="<c:url value='element/service.jsp?ifserviceid=${model.service.id}'/>">${fn:escapeXml(model.service.serviceName)}</a>
        on interface <a href="<c:url value='element/interface.jsp?ipinterfaceid=${model.service.ipInterface.id}'/>">${model.service.ipAddressAsString}</a>
        of node <a href="<c:url value='element/node.jsp?node=${model.service.ipInterface.node.id}'/>">${fn:escapeXml(model.service.ipInterface.node.label)}</a>
        (node ID: ${model.service.ipInterface.node.id}) has ${fn:length(model.service.applications)} applications.
        </p>
        <div class="row">
          <div class="col-md-5">
            <label>Available applications</label>
            <select name="toAdd" size="20" class="form-control" multiple>
              <c:forEach items="${model.applications}" var="application">
                <option value="${application.id}">${fn:escapeXml(application.name)}</option>
              </c:forEach>
            </select>
          </div> <!-- column -->
          <div class="col-md-2 my-auto text-center">
            <input type="submit" name="action" class="btn btn-secondary" value="&#139;&#139; Remove"/>
            <input type="submit" name="action" class="btn btn-secondary" value="Add &#155;&#155;"/>
          </div> <!-- column -->
          <div class="col-md-5">
            <label>Applications on service</label>
            <select name="toDelete" size="20" class="form-control" multiple>
              <c:forEach items="${model.sortedApplications}" var="application">
                <option value="${application.id}">${fn:escapeXml(application.name)}</option>
              </c:forEach>
            </select>
          </div> <!-- column -->
        </div> <!-- row -->
      </div> <!-- card-body -->
    </div> <!-- panel -->
  </div> <!-- column -->
</div> <!-- row -->

</form>

<jsp:include page="/includes/bootstrap-footer.jsp" flush="false"/>
