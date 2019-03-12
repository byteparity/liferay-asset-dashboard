package com.byteparity.assetsstatistics.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;

import com.byteparity.assetsstatistics.common.util.LiferayAssetsStatisticsUtil;
import com.byteparity.assetsstatistics.constants.LiferayAssetsStatisticsPortletKeys;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
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

@Component(property = { 
		"javax.portlet.name=" + LiferayAssetsStatisticsPortletKeys.PORTLET_ID,
		"mvc.command.name=/get_webcontent" }, service = MVCResourceCommand.class)

public class GetWebContentMVCResourceCommand implements MVCResourceCommand {

	private static Log _log = LogFactoryUtil.getLog(GetWebContentMVCResourceCommand.class.getName());

	@Override
	public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws PortletException {

		// Get Date Format
		HttpSession httpSession = PortalUtil.getHttpServletRequest(resourceRequest).getSession();
		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		String dateFormat = (String) httpSession.getAttribute("selectedDateFormat");

		long groupId = 0;
		long companyId = themeDisplay.getCompanyId();
		try {
			resourceResponse.getWriter().println(getWebContent(groupId, companyId, dateFormat));
		} catch (IOException e) {
			_log.error(e.getMessage());
		}
		return false;
	}

	/**
	 * Return Web Content
	 * 
	 * @param groupId
	 * @param companyId
	 * @return
	 */
	private static JSONObject getWebContent(long groupId, long companyId, String dateFormat) {

		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		List<List<Object>> recordsList = new ArrayList<List<Object>>();
		DynamicQuery journalArticleQuery = JournalArticleLocalServiceUtil.dynamicQuery();
		if (groupId > 0) {
			journalArticleQuery.add(PropertyFactoryUtil.forName("groupId").eq(groupId));
		}
		if (companyId > 0) {
			journalArticleQuery.add(PropertyFactoryUtil.forName("companyId").eq(companyId));
		}
		journalArticleQuery.addOrder(OrderFactoryUtil.asc("id"));
		List<JournalArticle> articles = JournalArticleLocalServiceUtil.dynamicQuery(journalArticleQuery);

		Map<Long, Integer> latestArticleMap = new HashMap<Long, Integer>();
		long classNameId = PortalUtil.getClassNameId(JournalArticle.class.getName());

		//GET LEATEST JOURNAL ARTICLE
		for (JournalArticle article : articles) {

			DynamicQuery dynamicQuery = JournalArticleLocalServiceUtil.dynamicQuery();
			dynamicQuery.add(PropertyFactoryUtil.forName("resourcePrimKey").eq(article.getResourcePrimKey()));
			dynamicQuery.addOrder(OrderFactoryUtil.desc("modifiedDate"));
			List<JournalArticle> list = JournalArticleLocalServiceUtil.dynamicQuery(dynamicQuery);
			if (list.size() > 0) {
				latestArticleMap.put(list.get(0).getId(), list.get(0).getStatus());
			}
		}

		//GET STATUS WISE COUNT
		JSONObject statusJsonObject = LiferayAssetsStatisticsUtil.getCountByStatus(latestArticleMap,false);

		//TABLE BODY DATA
		for (Map.Entry<Long, Integer> entry : latestArticleMap.entrySet()) {
			try {
				List<Object> row = new ArrayList<Object>();
				JournalArticle journalArticle = JournalArticleLocalServiceUtil.getJournalArticle(entry.getKey());
				row.add(journalArticle.getId());
				row.add(journalArticle.getTitle(Locale.getDefault()));
				row.add(journalArticle.getVersion());
				String stausLabel = WorkflowConstants.getStatusCssClass(journalArticle.getStatus());
				String statusColor = LiferayAssetsStatisticsPortletKeys.getLabelStatusColor(stausLabel);
				row.add("<span class='badge status' style='background:" + statusColor + "'>" + stausLabel + "</span>");
				row.add(journalArticle.getDDMStructure().getName(Locale.getDefault()));

				//GET DDM TEMPLATE
				DynamicQuery ddmTemplateQuery = DDMTemplateLocalServiceUtil.dynamicQuery();
				ddmTemplateQuery.add(PropertyFactoryUtil.forName("templateKey").eq(journalArticle.getDDMTemplateKey()));
				List<DDMTemplate> ddmTemplates = DDMTemplateLocalServiceUtil.dynamicQuery(ddmTemplateQuery);

				//GET ASSETS ENTRY
				DynamicQuery dynamicQuery = AssetEntryLocalServiceUtil.dynamicQuery();
				dynamicQuery.add(PropertyFactoryUtil.forName("classPK").eq(journalArticle.getResourcePrimKey()));
				List<AssetEntry> entries = AssetEntryLocalServiceUtil.dynamicQuery(dynamicQuery);

				row.add(ddmTemplates.get(0).getName(Locale.getDefault()));
				row.add("<span class='rating' data-score='" + LiferayAssetsStatisticsUtil
						.getRatingByEntryId(journalArticle.getResourcePrimKey(), classNameId) + "'></span>");
				row.add("<span class='glyphicon glyphicon-eye-open'> " + entries.get(0).getViewCount() + "</span>");
				row.add(journalArticle.getUserName());
				row.add(format.format(journalArticle.getCreateDate()));
				row.add(journalArticle.getExpirationDate() != null ? format.format(journalArticle.getExpirationDate())
						: "");
				recordsList.add(row);
			} catch (PortalException e) {
			}
		}
		return LiferayAssetsStatisticsUtil.getJsonData(latestArticleMap.size(), statusJsonObject, recordsList,tableFields());
	}

	/**
	 * Return Table fields
	 * 
	 * @return
	 */
	private static JSONArray tableFields() {
		JSONArray field = JSONFactoryUtil.createJSONArray();
		field.put("Article Id");
		field.put("Title");
		field.put("Version");
		field.put("Status");
		field.put("Structure Name");
		field.put("Template Name");
		field.put("Rating");
		field.put("Total View");
		field.put("Creator");
		field.put("Created Date");
		field.put("Expiration Date");
		return field;
	}
}
