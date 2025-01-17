<%--
/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2010-2014 The OpenNMS Group, Inc.
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%@ page import="org.opennms.web.utils.Bootstrap" %>
<% Bootstrap.with(pageContext)
          .headTitle("System Reports")
          .breadcrumb("Support", "support/index.htm")
          .breadcrumb("System Reports")
          .build(request);
%>
<jsp:directive.include file="/includes/bootstrap.jsp" />

<script type="text/javascript">
<!-- Begin
function checkAll(field)
{
for (i = 0; i < field.length; i++)
	field[i].checked = true ;
}

function uncheckAll(field)
{
for (i = 0; i < field.length; i++)
	field[i].checked = false ;
}

function toggle(box, field)
{
	if (box.checked == true) {
		checkAll(field);
	} else {
		uncheckAll(field);
	}
}
//  End -->
</script>

<form role="form" name="report" class="form" action="admin/support/systemReport.htm" method="post" class="normal">

<div class="card">
  <div class="card-header">
    <span>Plugins &nbsp;&nbsp;&nbsp; <input type="checkbox" name="all" onclick="toggle(document.report.all, document.report.plugins)" checked /> All</span>
  </div>
  <div class="card-body">
    <p>Choose which plugins to enable:</p>
    <c:forEach items="${report.plugins}" var="plugin">
     <input type="checkbox" name="plugins" value="${plugin.name}" checked /> <c:out value="${plugin.name}" />: <c:out value="${plugin.description}" /> <br />
    </c:forEach>
  </div>
</div> <!-- panel -->

<div class="card">
  <div class="card-header">
    <span>Report Type</span>
  </div>
  <div class="card-body">
      <div class="form-group row">
          <label class="col-2" id="formatter" for="formatter">Choose which report to use</label>
          <select name="formatter" class="col-10 form-control custom-select">
              <c:forEach items="${report.formatters}" var="formatter">
                  <c:choose>
                      <c:when test="${formatter.name == 'text'}">
                          <c:set var="formatterSelected" value="selected" />
                      </c:when>
                      <c:otherwise>
                          <c:set var="formatterSelected" value="" />
                      </c:otherwise>
                  </c:choose>
                  <option value="<c:out value="${formatter.name}" />" <c:out value="${formatterSelected}" />><c:out value="${formatter.name}: ${formatter.description}" /></option>
              </c:forEach>
          </select>
      </div>
      <div class="form-group row">
          <label for="filename" class="col-2">File name <small>(optional)</small></label>
          <input type="text" id="filename" name="output" class="form-control col-10" />
      </div>
      <div class="form-group">
          <input type="hidden" name="operation" value="run" />
          <input type="submit" class="btn btn-secondary" value="Generate System Report" />
      </div>
  </div> <!-- card-body -->
</div> <!-- panel -->

</form>

<jsp:include page="/includes/bootstrap-footer.jsp" flush="false"/>
