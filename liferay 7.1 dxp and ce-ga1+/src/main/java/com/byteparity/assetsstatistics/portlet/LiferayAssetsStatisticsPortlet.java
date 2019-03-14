package com.byteparity.assetsstatistics.portlet;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

import com.byteparity.assetsstatistics.constants.LiferayAssetsStatisticsPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

/**
 * @author baps
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category="+LiferayAssetsStatisticsPortletKeys.PORTLET_CATEGORY,
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name="+LiferayAssetsStatisticsPortletKeys.PORTLET_DISPLAY_NAME,
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + LiferayAssetsStatisticsPortletKeys.PORTLET_ID,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class LiferayAssetsStatisticsPortlet extends MVCPortlet {
	
}