package com.byteparity.assetsstatistics.action;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;

import com.byteparity.assetsstatistics.common.util.LiferayAssetsStatisticsUtil;
import com.byteparity.assetsstatistics.constants.LiferayAssetsStatisticsPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

@Component(property = { 
		"javax.portlet.name=" + LiferayAssetsStatisticsPortletKeys.PORTLET_ID,
		"mvc.command.name=/get_assets" }, service = MVCResourceCommand.class)

public class GetAssetsListMVCResourceCommand implements MVCResourceCommand {

	private static Log _log = LogFactoryUtil.getLog(GetAssetsListMVCResourceCommand.class.getName());

	@Override
	public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws PortletException {

		//Get Portlet Configuration
		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		PortletPreferences portletPreferences = resourceRequest.getPreferences();
		HttpSession httpSession = PortalUtil.getHttpServletRequest(resourceRequest).getSession();
		String assets = portletPreferences.getValue("selectedAssets", StringPool.TRUE);
		String selectedDateFormat = portletPreferences.getValue("selectedDateFormat", StringPool.NULL);
		httpSession.setAttribute("selectedDateFormat", selectedDateFormat);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
		if (Validator.isNotNull(assets)) {
			String[] assetList = assets.split(",");
			for (String assetName : assetList) {
				try {
					jsonObject.put(assetName,LiferayAssetsStatisticsUtil.getAssetsCount(0, themeDisplay.getCompanyId(), assetName));
				} catch (PortalException e) {
					_log.error(e.getMessage());
				}
			}
		}
		try {
			resourceResponse.getWriter().println(jsonObject);
		} catch (IOException e) {
			_log.error(e.getMessage());
		}
		return false;
	}
}
