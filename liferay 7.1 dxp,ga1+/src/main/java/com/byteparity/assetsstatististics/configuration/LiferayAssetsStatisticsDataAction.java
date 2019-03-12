package com.byteparity.assetsstatististics.configuration;

import java.io.IOException;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;

import com.byteparity.assetsstatistics.constants.LiferayAssetsStatisticsPortletKeys;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;

@Component(
		configurationPid = "com.byteparity.assetsstatististics.configuration.LiferayAssetsStatisticsDataAction", 
		configurationPolicy = ConfigurationPolicy.OPTIONAL, 
		immediate = true, 
		property = { "javax.portlet.name=" + LiferayAssetsStatisticsPortletKeys.PORTLET_ID }, 
		service = ConfigurationAction.class)

public class LiferayAssetsStatisticsDataAction extends DefaultConfigurationAction {
	
	@Override
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws IOException, PortletException {
		super.serveResource(resourceRequest, resourceResponse);
	}

	@Override
	public void processAction(PortletConfig portletConfig, ActionRequest actionRequest, ActionResponse actionResponse)
			throws Exception {
		setPreference(actionRequest, "selectedAssets", ParamUtil.getString(actionRequest, "values"));
		setPreference(actionRequest, "selectedDateFormat", ParamUtil.getString(actionRequest, "dateFormat"));
		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	@Override
	public void include(PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		httpServletRequest.setAttribute(LiferayAssetsStatisticsDataConfiguration.class.getName(), _assetsStatisticsDataConfiguration);
		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {
		_assetsStatisticsDataConfiguration = ConfigurableUtil.createConfigurable(LiferayAssetsStatisticsDataConfiguration.class, properties);
	}

	private volatile LiferayAssetsStatisticsDataConfiguration _assetsStatisticsDataConfiguration;
}
