package com.byteparity.assetsstatistics.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;

import com.byteparity.assetsstatistics.common.util.LiferayAssetsStatisticsUtil;
import com.byteparity.assetsstatistics.constants.LiferayAssetsStatisticsPortletKeys;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.kernel.service.BlogsEntryLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
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

@Component(property = { 
		"javax.portlet.name=" + LiferayAssetsStatisticsPortletKeys.PORTLET_ID,
		"mvc.command.name=/get_blogs" }, service = MVCResourceCommand.class)

public class GetBlogsMVCResourceCommand implements MVCResourceCommand {

	private static Log _log = LogFactoryUtil.getLog(GetBlogsMVCResourceCommand.class.getName());

	@Override
	public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws PortletException {

		//Get Date Format
		HttpSession httpSession = PortalUtil.getHttpServletRequest(resourceRequest).getSession();
		String dateFormat = (String) httpSession.getAttribute("selectedDateFormat");
		
		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = 0;
		long companyId = themeDisplay.getCompanyId();
		try {
			resourceResponse.getWriter().println(getBlogs(groupId, companyId,dateFormat));
		} catch (IOException e) {
			_log.error(e.getMessage());
		}
		return false;
	}

	/**
	 * Return Blogs
	 * 
	 * @param groupId
	 * @param comapnyId
	 * @return
	 */
	public static JSONObject getBlogs(long groupId, long comapnyId,String dateFormat) {

		List<List<Object>> recordsList = new ArrayList<List<Object>>();
		DynamicQuery blogQuery = BlogsEntryLocalServiceUtil.dynamicQuery();
		if(groupId > 0){
			blogQuery.add(PropertyFactoryUtil.forName("groupId").eq(groupId));
		}
		if(comapnyId > 0){
			blogQuery.add(PropertyFactoryUtil.forName("companyId").eq(comapnyId));
		}
		List<BlogsEntry> blogsEntries = BlogsEntryLocalServiceUtil.dynamicQuery(blogQuery);
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		
		for (BlogsEntry blogsEntry : blogsEntries) {
			List<Object> row = new ArrayList<Object>();
			try {
				row.add(blogsEntry.getEntryId());
				row.add(blogsEntry.getTitle());
				String stausLabel = WorkflowConstants.getStatusCssClass(blogsEntry.getStatus());
				String statusColor = LiferayAssetsStatisticsPortletKeys.getLabelStatusColor(stausLabel);
				row.add("<span class='badge status' style='background:"+statusColor+"'>"+stausLabel+"</span>");

				// GET ASSETS ENTRY
				DynamicQuery dynamicQuery = AssetEntryLocalServiceUtil.dynamicQuery();
				dynamicQuery.add(PropertyFactoryUtil.forName("classPK").eq(blogsEntry.getEntryId()));
				List<AssetEntry> entries = AssetEntryLocalServiceUtil.dynamicQuery(dynamicQuery);

				// GET LIKE DISLIKE
				List<Integer> rating = LiferayAssetsStatisticsUtil.getLikeDislikeByEntryId(blogsEntry.getEntryId());
				int like = rating != null ? rating.get(0) : 0;
				long disLike = rating != null ? rating.get(1) : 0;
				row.add("<span class='glyphicon glyphicon-thumbs-up'></span>&nbsp;&nbsp;"+like);
				row.add("<span class='glyphicon glyphicon-thumbs-down'></span>&nbsp;&nbsp;"+disLike);
				row.add("<span class='glyphicon glyphicon-eye-open'></span>&nbsp;&nbsp;"+entries.get(0).getViewCount());
				row.add(blogsEntry.getUserName());
				row.add(format.format(blogsEntry.getCreateDate()));
				row.add(format.format(blogsEntry.getModifiedDate()));

				recordsList.add(row);

			} catch (Exception e) {
				_log.error(e.getMessage());
			}
		}
		return LiferayAssetsStatisticsUtil.getJsonData(blogsEntries.size(), getCountBlogByStatus(groupId, comapnyId),
				recordsList, tableFields());
	}
	/**
	 * Return status count
	 * 
	 * @param groupId
	 * @param companyId
	 * @return
	 */
	private static JSONObject getCountBlogByStatus(long groupId, long companyId) {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
		
		for (int status : LiferayAssetsStatisticsPortletKeys.STATUS) {
			DynamicQuery blogDynamicQuery = BlogsEntryLocalServiceUtil.dynamicQuery();
			if(groupId > 0){
				blogDynamicQuery.add(PropertyFactoryUtil.forName("groupId").eq(groupId));
			}
			if(companyId > 0){
				blogDynamicQuery.add(PropertyFactoryUtil.forName("companyId").eq(companyId));
			}
			blogDynamicQuery.add(PropertyFactoryUtil.forName("status").eq(status));
			List<BlogsEntry> blogsEntries = BlogsEntryLocalServiceUtil.dynamicQuery(blogDynamicQuery);
			if (blogsEntries.size() > 0) {
				String label = WorkflowConstants.getStatusCssClass(status);
				String color = LiferayAssetsStatisticsPortletKeys.getLabelStatusColor(label);
				jsonObject.put(WorkflowConstants.getStatusCssClass(status), "<span class='badge' style='background:"+color+"'>"+label+" ("+blogsEntries.size()+")</span>");
			}
		}
		return jsonObject;
	}

	/**
	 * Return Table Fields
	 * 
	 * @return
	 */
	private static JSONArray tableFields() {
		JSONArray field = JSONFactoryUtil.createJSONArray();
		field.put("Entry Id");
		field.put("Title");
		field.put("Status");
		field.put("Like");
		field.put("Dislike");
		field.put("Total View");
		field.put("Creator");
		field.put("Created Date");
		field.put("Modified Date");
		return field;
	}
}
