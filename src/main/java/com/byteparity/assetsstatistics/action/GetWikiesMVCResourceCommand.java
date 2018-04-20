package com.byteparity.assetsstatistics.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;

import com.byteparity.assetsstatistics.common.util.LiferayAssetsStatisticsUtil;
import com.byteparity.assetsstatistics.constants.LiferayAssetsStatisticsPortletKeys;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalServiceUtil;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;

@Component(property = { 
		"javax.portlet.name=" + LiferayAssetsStatisticsPortletKeys.PORTLET_ID,
		"mvc.command.name=/get_wikies" }, service = MVCResourceCommand.class)

public class GetWikiesMVCResourceCommand implements MVCResourceCommand {

	private static Log _log = LogFactoryUtil.getLog(GetWikiesMVCResourceCommand.class.getName());

	@Override
	public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws PortletException {
		
		//Get Date Format
		HttpSession httpSession = PortalUtil.getHttpServletRequest(resourceRequest).getSession();
		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		String dateFormat = (String) httpSession.getAttribute("selectedDateFormat");
				
		long groupId = 0;
		long companyId = themeDisplay.getCompanyId();
		try {
			resourceResponse.getWriter().println(getWikies(groupId, companyId,dateFormat));
		} catch (IOException e) {
			_log.error(e.getMessage());
		}
		return false;
	}

	/**
	 * Return Wikies
	 * 
	 * @param groupId
	 * @param companyId
	 * @return
	 */
	public static JSONObject getWikies(long groupId, long companyId,String dateFormat) {
		List<List<Object>> recordsList = new ArrayList<List<Object>>();
		DynamicQuery wikiDynamicQuery = WikiNodeLocalServiceUtil.dynamicQuery();
		if(groupId > 0){
			wikiDynamicQuery.add(PropertyFactoryUtil.forName("groupId").eq(groupId));
		}
		if(companyId > 0){
			wikiDynamicQuery.add(PropertyFactoryUtil.forName("companyId").eq(companyId));
		}
		List<WikiNode> wikiNodes = WikiNodeLocalServiceUtil.dynamicQuery(wikiDynamicQuery);
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		long classNameId = PortalUtil.getClassNameId(WikiPage.class.getName());

		for (WikiNode wikiNode : wikiNodes) {
			Map<Long, Long> latestPageMap = new HashMap<Long, Long>();
			try {

				DynamicQuery wikiPageDynamicQuery = WikiPageLocalServiceUtil.dynamicQuery();
				wikiPageDynamicQuery.add(PropertyFactoryUtil.forName("nodeId").eq(wikiNode.getNodeId()));
				wikiPageDynamicQuery.addOrder(OrderFactoryUtil.asc("pageId"));
				List<WikiPage> wikiPages = WikiPageLocalServiceUtil.dynamicQuery(wikiPageDynamicQuery);

				for (WikiPage wikiPage : wikiPages) {
					latestPageMap.put(wikiPage.getResourcePrimKey(), wikiPage.getPageId());
				}
				for (Map.Entry<Long, Long> entry : latestPageMap.entrySet()) {
					List<Object> row = new ArrayList<Object>();
					WikiPage wikiPage = WikiPageLocalServiceUtil.getWikiPage(entry.getValue());
					row.add(wikiNode.getNodeId());
					row.add(wikiNode.getName());
					row.add(wikiPage.getTitle());
					String pageStatusLabel = WorkflowConstants.getStatusCssClass((wikiPage.getStatus()));
					String pageStatusColor = LiferayAssetsStatisticsPortletKeys.getLabelStatusColor(pageStatusLabel);
					row.add("<span class='badge status' style='background:"+pageStatusColor+"'>"+pageStatusLabel+"</span>");
					row.add(wikiPage.getVersion());
					row.add("<span class='rating' data-score='"+LiferayAssetsStatisticsUtil.getRatingByEntryId(wikiPage.getResourcePrimKey(), classNameId)+"'></span>");
					row.add(wikiNode.getUserName());
					row.add(format.format(wikiNode.getCreateDate()));
					row.add(format.format(wikiNode.getModifiedDate()));
					recordsList.add(row);
				}
			} catch (Exception e) {
				_log.error(e.getMessage());
			}
		}
		return LiferayAssetsStatisticsUtil.getJsonData(wikiNodes.size(), getCountWikiPageByStatus(groupId, companyId),
				recordsList, tableFields());
	}

	/**
	 * Return status count
	 * 
	 * @param groupId
	 * @param companyId
	 * @return
	 */
	private static JSONObject getCountWikiPageByStatus(long groupId, long companyId) {
		
		DynamicQuery wikiPageDynamicQuery = WikiPageLocalServiceUtil.dynamicQuery();
		if(groupId > 0){
			wikiPageDynamicQuery.add(PropertyFactoryUtil.forName("groupId").eq(groupId));
		}
		if(companyId > 0){
			wikiPageDynamicQuery.add(PropertyFactoryUtil.forName("companyId").eq(companyId));
		}
		List<WikiPage> wikiPages = WikiPageLocalServiceUtil.dynamicQuery(wikiPageDynamicQuery);
		
		Map<Long, Integer> latestPageMap = new HashMap<Long, Integer>();
		for(WikiPage page :wikiPages ){
			latestPageMap.put(page.getResourcePrimKey(), page.getStatus());
		}
		return  LiferayAssetsStatisticsUtil.getCountByStatus(latestPageMap,true);
		
	}

	/**
	 * Return Table fields
	 * 
	 * @return
	 */
	private static JSONArray tableFields() {
		JSONArray field = JSONFactoryUtil.createJSONArray();
		field.put("Node Id");
		field.put("Node Name");
		field.put("Page Name");
		field.put("Page Status");
		field.put("Page Version");
		field.put("Page Rating");
		field.put("Creator");
		field.put("Created Date");
		field.put("Modified Date");
		return field;
	}
}
