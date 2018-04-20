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
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

@Component(property = { 
		"javax.portlet.name=" + LiferayAssetsStatisticsPortletKeys.PORTLET_ID,
		"mvc.command.name=/get_users" }, service = MVCResourceCommand.class)

public class GetUsersMVCResourceCommand implements MVCResourceCommand {

	private static Log _log = LogFactoryUtil.getLog(GetUsersMVCResourceCommand.class.getName());

	@Override
	public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws PortletException {
		
		//Get Date Format
		HttpSession httpSession = PortalUtil.getHttpServletRequest(resourceRequest).getSession();
		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		String dateFormat = (String) httpSession.getAttribute("selectedDateFormat");
				
		long companyId = themeDisplay.getCompanyId();
		try {
			resourceResponse.getWriter().println(getUsers(companyId,themeDisplay,dateFormat));
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
	private static JSONObject getUsers(long companyId,ThemeDisplay themeDisplay,String dateFormat) {

		List<List<Object>> recordsList = new ArrayList<List<Object>>();
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		DynamicQuery userQuery = UserLocalServiceUtil.dynamicQuery();
		userQuery.add(PropertyFactoryUtil.forName("companyId").eq(companyId));
		List<User> users = UserLocalServiceUtil.dynamicQuery(userQuery);
		
		// TABLE BODY DATA
		for (User user : users) {
			try {
				List<Object> row = new ArrayList<Object>();
				row.add(user.getUserId());
				row.add("<img src='"+user.getPortraitURL(themeDisplay)+"' class='img-circle' height='35%' width='35%'>");
				row.add(user.getFullName());
				row.add(user.getMale() == true ? "Male" : "Female");
				row.add(user.getScreenName());
				row.add(user.getEmailAddress());
				String stausLabel = WorkflowConstants.getStatusCssClass(user.getStatus());
				String statusColor = LiferayAssetsStatisticsPortletKeys.getLabelStatusColor(stausLabel);
				row.add("<span class='badge status' style='background:"+statusColor+"'>"+stausLabel+"</span>");
				row.add(format.format(user.getCreateDate()));
				row.add(format.format(user.getModifiedDate()));
				recordsList.add(row);

			} catch (PortalException e) {
				_log.error(e.getMessage());
			}
		}
		return LiferayAssetsStatisticsUtil.getJsonData(recordsList.size(), getCountUserByStatus(companyId), recordsList, tableFields());
	}
	/**
	 * Return status count
	 * 
	 * @param groupId
	 * @param companyId
	 * @return
	 */
	private static JSONObject getCountUserByStatus(long companyId) {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
		for (int status : LiferayAssetsStatisticsPortletKeys.STATUS) {
			DynamicQuery userDynamicQuery = UserLocalServiceUtil.dynamicQuery();
			if(companyId > 0){
				userDynamicQuery.add(PropertyFactoryUtil.forName("companyId").eq(companyId));
			}
			userDynamicQuery.add(PropertyFactoryUtil.forName("status").eq(status));
			List<User> userEntries = UserLocalServiceUtil.dynamicQuery(userDynamicQuery);
			if (userEntries.size() > 0) {
				String label = WorkflowConstants.getStatusCssClass(status);
				String color = LiferayAssetsStatisticsPortletKeys.getLabelStatusColor(label);
				jsonObject.put(WorkflowConstants.getStatusCssClass(status), "<span class='badge' style='background:"+color+"'>"+label+" ("+userEntries.size()+")</span>");
			}
		}
		return jsonObject;
	}

	/**
	 * Return Table fields
	 * 
	 * @return
	 */
	private static JSONArray tableFields() {
		JSONArray field = JSONFactoryUtil.createJSONArray();
		field.put("User Id");
		field.put("Profile Image");
		field.put("Name");
		field.put("Gender");
		field.put("Screen Name");
		field.put("Email Address");
		field.put("Status");
		field.put("Created Date");
		field.put("Modified Date");
		return field;
	}
}
