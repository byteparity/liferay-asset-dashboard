<%@page import="com.byteparity.assetsstatistics.constants.LiferayAssetsStatisticsPortletKeys"%>
<%@ include file="init.jsp" %>
<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />
<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<%

	List<KeyValuePair> leftList = new ArrayList<KeyValuePair>();
	List<KeyValuePair> rightList = new ArrayList<KeyValuePair>();
	String assetList[] = LiferayAssetsStatisticsPortletKeys.ASSETS;
	for(String assetName : assetList){
		rightList.add(new KeyValuePair(assetName, assetName));
	}
	
	LiferayAssetsStatisticsDataConfiguration  assetsStatisticsDataConfiguration =(LiferayAssetsStatisticsDataConfiguration)renderRequest.getAttribute(LiferayAssetsStatisticsDataConfiguration.class.getName());
    String []assetsStatisticsData = null;
    
    if (Validator.isNotNull(assetsStatisticsDataConfiguration)) {
    	String assets = portletPreferences.getValue("selectedAssets", assetsStatisticsDataConfiguration.getAssetsStatistics());
    	if(Validator.isNotNull(assets)){
    		assetsStatisticsData = assets.split(",");
    	}
    }
    
    if(Validator.isNotNull(assetsStatisticsData)){
    	for(String str : assetsStatisticsData){
    		leftList.add(new KeyValuePair(str, str));
    		rightList.remove(new KeyValuePair(str, str));
    	}
    }
    
    String selectedDateFormat = portletPreferences.getValue("selectedDateFormat", assetsStatisticsDataConfiguration.getDateFormat());
    String dateFormatArray[] = LiferayAssetsStatisticsPortletKeys.DATE_FORMAT;
    
    pageContext.setAttribute("leftList", leftList);
	pageContext.setAttribute("rightList", rightList);
	pageContext.setAttribute("dateFormatArray",dateFormatArray);
	pageContext.setAttribute("selectedDateFormat", selectedDateFormat);
%>

<aui:form action="<%= configurationActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
    <aui:input name="redirect" type="hidden"  value="<%= configurationRenderURL %>" />
    <aui:input name="values" type="hidden" />
    <br/>
	<div class="container-fluid-1280">
		<div class="row">
			<div class="col-md-12">
				<div class="alert alert-info"><liferay-ui:message key="config-msg"/></div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="card-horizontal main-content-card">
					<div class="row"></div>
					<div class="row">
						<div class="col-md-12">
							<liferay-ui:input-move-boxes  rightList="${leftList}" rightTitle="right-title" leftBoxName="availableAssets" leftList="${rightList}" rightBoxName="selectedAssets" leftTitle="left-title" leftReorder="false"  rightReorder="false" cssClass="move_boxes" />
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<aui:select name="dateFormat" label="select-date-fmt">
					<c:forEach var="dateFormat" items="${dateFormatArray}">
						<c:choose>
							<c:when test="${selectedDateFormat eq dateFormat}">
								<aui:option value="${dateFormat}" selected="true">${dateFormat}</aui:option>
							</c:when>
							<c:otherwise>
								<aui:option value="${dateFormat}">${dateFormat}</aui:option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</aui:select>
			</div>
		</div>
	</div>
	<aui:button-row>
        <aui:button type="submit"></aui:button>
    </aui:button-row>
</aui:form> 

<aui:script use="liferay-util-list-fields">
	A.one('#<portlet:namespace/>fm').on('submit', function(event) {
	    var selectedValues = Liferay.Util.listSelect('#<portlet:namespace/>selectedAssets');
	    A.one('#<portlet:namespace/>values').val(selectedValues);
	    submitForm('#<portlet:namespace/>fm');
	});
</aui:script>