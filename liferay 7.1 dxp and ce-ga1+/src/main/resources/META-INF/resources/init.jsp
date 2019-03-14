<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="java.util.Date"%>
<%@ page import="com.byteparity.assetsstatististics.configuration.LiferayAssetsStatisticsDataConfiguration"%>
<%@ page import="com.liferay.portal.kernel.util.Validator"%>
<%@ page import="com.liferay.portal.kernel.util.StringPool"%>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.liferay.portal.kernel.util.KeyValuePair"%>
<%@ page import="java.util.List"%>
<liferay-theme:defineObjects />

<portlet:defineObjects />

<c:set var="now" value="<%=new Date()%>"/>
<fmt:formatDate var="temp" pattern="s" type = "time"  value = "${now}" />

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/custom.css?${temp}">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/jquery.dataTables.min.css?${temp}">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/jquery.raty.min.css?${temp}">

<script src="<%=request.getContextPath()%>/js/jquery.raty.min.js?${temp}"></script>
<script src="<%=request.getContextPath()%>/js/jquery.dataTables.min.js?${temp}"></script>
<script src="<%=request.getContextPath()%>/js/custom.js?${temp}"></script>
