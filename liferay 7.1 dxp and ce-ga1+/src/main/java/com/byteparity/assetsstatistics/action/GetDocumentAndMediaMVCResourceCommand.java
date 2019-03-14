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
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLFileVersionLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
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
		"mvc.command.name=/get_document_media" }, service = MVCResourceCommand.class)

public class GetDocumentAndMediaMVCResourceCommand implements MVCResourceCommand {

	private static Log _log = LogFactoryUtil.getLog(GetDocumentAndMediaMVCResourceCommand.class.getName());

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
			resourceResponse.getWriter().println(getDocumentAndMedia(groupId, companyId,dateFormat));
		} catch (IOException e) {
			_log.error(e.getMessage());
		}
		return false;
	}

	/**
	 * Return Document & Media
	 * 
	 * @param groupId
	 * @param companyId
	 * @return
	 */
	private static JSONObject getDocumentAndMedia(long groupId, long companyId,String dateFormat) {

		List<List<Object>> recordsList = new ArrayList<List<Object>>();
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		long classNameId = PortalUtil.getClassNameId(DLFileEntry.class.getName());
		DynamicQuery dlFileEntryQuery = DLFileVersionLocalServiceUtil.dynamicQuery();
		if(groupId > 0){
			dlFileEntryQuery.add(PropertyFactoryUtil.forName("groupId").eq(groupId));
		}
		if(companyId > 0){
			dlFileEntryQuery.add(PropertyFactoryUtil.forName("companyId").eq(companyId));
		}
		List<DLFileVersion> dlFileVersions = DLFileVersionLocalServiceUtil.dynamicQuery(dlFileEntryQuery);
		Map<Long, Integer> map = new HashMap<Long, Integer>();

		// GET LEATEST FILE VERSION
		for (DLFileVersion dlFileVersion : dlFileVersions) {
			try {
				DLFileVersion fileVersion = DLFileVersionLocalServiceUtil
						.getLatestFileVersion(dlFileVersion.getUserId(), dlFileVersion.getFileEntryId());
				map.put(fileVersion.getFileVersionId(), fileVersion.getStatus());
			} catch (PortalException e) {
				_log.error(e.getMessage());
			}
		}

		// GET STATUS WISE COUNT
		JSONObject statusJsonObject = LiferayAssetsStatisticsUtil.getCountByStatus(map,false);

		// TABLE BODY DATA
		for (Map.Entry<Long, Integer> entry : map.entrySet()) {
			try {
				List<Object> row = new ArrayList<Object>();
				DLFileVersion dlFileVersion = DLFileVersionLocalServiceUtil.getDLFileVersion(entry.getKey());
				row.add(dlFileVersion.getFileEntryId());
				row.add(dlFileVersion.getTitle());
				row.add(dlFileVersion.getFileName());
				row.add(dlFileVersion.getExtension());
				row.add(dlFileVersion.getVersion());
				String stausLabel = WorkflowConstants.getStatusCssClass(dlFileVersion.getStatus());
				String statusColor = LiferayAssetsStatisticsPortletKeys.getLabelStatusColor(stausLabel);
				row.add("<span class='badge status' style='background:"+statusColor+"'>"+stausLabel+"</span>");

				// GET ASSETS ENTRY
				DynamicQuery dynamicQuery = AssetEntryLocalServiceUtil.dynamicQuery();
				dynamicQuery.add(PropertyFactoryUtil.forName("classPK").eq(dlFileVersion.getFileEntryId()));
				List<AssetEntry> entries = AssetEntryLocalServiceUtil.dynamicQuery(dynamicQuery);
				row.add("<span class='rating' data-score='"+LiferayAssetsStatisticsUtil.getRatingByEntryId(dlFileVersion.getFileEntryId(), classNameId)+"'></span>");
				row.add("<span class='glyphicon glyphicon-eye-open'> " + entries.get(0).getViewCount() + "</span>");
				row.add(dlFileVersion.getUserName());
				row.add(format.format(dlFileVersion.getCreateDate()));
				row.add(format.format(dlFileVersion.getCreateDate()));
				recordsList.add(row);

			} catch (PortalException e) {
				_log.error(e.getMessage());
			}
		}
		return LiferayAssetsStatisticsUtil.getJsonData(map.size(), statusJsonObject, recordsList, tableFields());
	}

	/**
	 * Return Table fields
	 * 
	 * @return
	 */
	private static JSONArray tableFields() {
		JSONArray field = JSONFactoryUtil.createJSONArray();
		field.put("File Entry Id");
		field.put("Title");
		field.put("File Name");
		field.put("File Type");
		field.put("Version");
		field.put("Status");
		field.put("Rating");
		field.put("Total View");
		field.put("Creator");
		field.put("Created Date");
		field.put("Modified Date");
		return field;
	}
}
