<%@ include file="init.jsp" %>
<portlet:resourceURL var="getAssetsListURL" id="/get_assets" />

<div class="container">
	<div class="row">
		<div id="dashboard"></div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<div id="headerDiv">
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<hr/>
			<div class="alert alert-info">
				<liferay-ui:message key="msg-info"/>
			</div>
			<div id="tableDiv"></div>
		</div>
	</div>
</div>

<%@ include file="view_js.jsp"%>