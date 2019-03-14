package com.byteparity.assetsstatistics.constants;

import com.liferay.portal.kernel.workflow.WorkflowConstants;

/**
 * @author baps
 */
public class LiferayAssetsStatisticsPortletKeys {

	public static final String PORTLET_ID = "com_byteparity_portlet_LiferayAssetsStatisticsPortlet";
	public static final String PORTLET_CATEGORY = "ByteParity";
	public static final String PORTLET_DISPLAY_NAME = "Liferay 7.1 Asset Dashboard";
	public static final int STATUS [] = { WorkflowConstants.ACTION_PUBLISH,WorkflowConstants.ACTION_SAVE_DRAFT,WorkflowConstants.STATUS_ANY,WorkflowConstants.STATUS_APPROVED,WorkflowConstants.STATUS_DENIED,WorkflowConstants.STATUS_DRAFT,WorkflowConstants.STATUS_EXPIRED, WorkflowConstants.STATUS_IN_TRASH,WorkflowConstants.STATUS_INACTIVE, WorkflowConstants.STATUS_INCOMPLETE,WorkflowConstants.STATUS_PENDING,WorkflowConstants.STATUS_SCHEDULED} ;
	public static final String DATE_FORMAT [] = {"dd-MM-yyyy","MM-dd-yyyy","yyyy-MM-dd","dd MMMM yyyy","dd MMM yyyy"} ;
	public static final String ASSETS [] = {"Blog","Document & Media","Forms","Users","Wiki","Web Content"} ;
	public static String getLabelStatusColor(String label) {
		if (label.equals("any")) {
			return "#BA55D3";
		}else if (label.equals("publish")) {
			return "#2E8B57";
		}
		else if (label.equals("approved")) {
			return "#5cb85c";
		}
		else if (label.equals("denied")) {
			return "#f25770";
		}
		else if (label.equals("draft")) {
			return "#740ea9";
		}
		else if (label.equals("expired")) {
			return "#c9302c";
		}
		else if (label.equals("inactive")) {
			return "#f0ad4e";
		}
		else if (label.equals("in-trash")) {
			return "#c9302c";
		}
		else if (label.equals("incomplete")) {
			return "#31b0d5";
		}
		else if (label.equals("pending")) {
			return "#A52A2A";
		}
		else if (label.equals("scheduled")) {
			return "#8B008B";
		}else if (label.equals("in-active")) {
			return "#FF6347";
		}
		else {
			return "#BA55D3";
		}
	}

}
