package com.byteparity.assetsstatistics.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.byteparity.assetsstatistics.constants.LiferayAssetsStatisticsPortletKeys;
import com.liferay.blogs.kernel.service.BlogsEntryLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLFileVersionLocalServiceUtil;
import com.liferay.dynamic.data.lists.service.DDLRecordSetLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.ratings.kernel.model.RatingsStats;
import com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;

public class LiferayAssetsStatisticsUtil {

	/**
	 * Return rating count based on entry id
	 * 
	 * @param entryId
	 * @return
	 */
	public static List<Integer> getLikeDislikeByEntryId(long entryId) {
		DynamicQuery ratingsStatsQuery = RatingsStatsLocalServiceUtil.dynamicQuery();
		ratingsStatsQuery.add(PropertyFactoryUtil.forName("classPK").eq(entryId));
		List<RatingsStats> ratingsStatsList = RatingsStatsLocalServiceUtil.dynamicQuery(ratingsStatsQuery);
		int totalEntries = 0;
		int totalScore = 0;
		int disLike = 0;
		List<Integer> ratingList = null;
		if (ratingsStatsList.size() > 0) {
			for (RatingsStats rating : ratingsStatsList) {
				ratingList = new ArrayList<Integer>();
				totalEntries = rating.getTotalEntries();
				totalScore = (int) rating.getTotalScore();
				disLike = totalEntries - totalScore;
				ratingList.add(totalScore);
				ratingList.add(disLike);
				break;
			}
		}
		return ratingList;
	}

	/**
	 * Return JSON Data Table JSON Object
	 * 
	 * @param total
	 * @param status
	 * @param content
	 * @param tableFields
	 * @return
	 */
	public static JSONObject getJsonData(int total, Object status, List<List<Object>> content, JSONArray tableFields) {
		JSONObject header = JSONFactoryUtil.createJSONObject();
		header.put("total", total);
		header.put("status", status);
		JSONObject responceJsonObject = JSONFactoryUtil.createJSONObject();
		responceJsonObject.put("header", header);
		responceJsonObject.put("fields", tableFields);
		responceJsonObject.put("content", content);
		return responceJsonObject;
	}

	/**
	 * Return rating count based on entry id
	 * 
	 * @param entryId
	 * @return
	 */
	public static int getRatingByEntryId(long entryId, long classNameId) {
		DynamicQuery ratingsStatsQuery = RatingsStatsLocalServiceUtil.dynamicQuery();
		ratingsStatsQuery.add(PropertyFactoryUtil.forName("classPK").eq(entryId));
		ratingsStatsQuery.add(PropertyFactoryUtil.forName("classNameId").eq(classNameId));
		List<RatingsStats> ratingsStatsList = RatingsStatsLocalServiceUtil.dynamicQuery(ratingsStatsQuery);
		int averageIndex = 0;
		if (ratingsStatsList.size() > 0) {
			RatingsStats ratingsStats = ratingsStatsList.get(0);
			averageIndex = (int) Math.floor(ratingsStats.getAverageScore() * 5);
			if (averageIndex % 2 != 0) {
				averageIndex = (int) Math.round(ratingsStats.getAverageScore() * 5);
			}
		}
		return averageIndex;
	}

	/**
	 * Return status count
	 * 
	 * @param assetMap
	 * @return
	 */
	public static JSONObject getCountByStatus(Map<Long, Integer> assetMap,boolean isWikiPage) {
		JSONObject statusJsonObject = JSONFactoryUtil.createJSONObject();
		int ACTION_PUBLISH = 0;
		int STATUS_ANY = 0;
		int STATUS_APPROVED = 0;
		int STATUS_DRAFT = 0;
		int STATUS_EXPIRED = 0;
		int STATUS_IN_TRASH = 0;
		int STATUS_INACTIVE = 0;
		int STATUS_INCOMPLETE = 0;
		int STATUS_PENDING = 0;
		int STATUS_SCHEDULED = 0;
		int STATUS_DENIED = 0;

		for (Map.Entry<Long, Integer> entry : assetMap.entrySet()) {
			int status = entry.getValue();
			if (status == WorkflowConstants.ACTION_PUBLISH && !isWikiPage) {
				ACTION_PUBLISH++;
			} else if (status == WorkflowConstants.STATUS_ANY) {
				STATUS_ANY++;
			} else if (status == WorkflowConstants.STATUS_APPROVED) {
				STATUS_APPROVED++;
			} else if (status == WorkflowConstants.STATUS_DENIED) {
				STATUS_DENIED++;
			} else if (status == WorkflowConstants.STATUS_DRAFT) {
				STATUS_DRAFT++;
			} else if (status == WorkflowConstants.STATUS_EXPIRED) {
				STATUS_EXPIRED++;
			} else if (status == WorkflowConstants.STATUS_IN_TRASH) {
				STATUS_IN_TRASH++;
			} else if (status == WorkflowConstants.STATUS_INACTIVE) {
				STATUS_INACTIVE++;
			} else if (status == WorkflowConstants.STATUS_INCOMPLETE) {
				STATUS_INCOMPLETE++;
			} else if (status == WorkflowConstants.STATUS_PENDING) {
				STATUS_PENDING++;
			} else if (status == WorkflowConstants.STATUS_SCHEDULED) {
				STATUS_SCHEDULED++;
			}
		}
		if (ACTION_PUBLISH > 0) {
			statusJsonObject.put("publish","<span class='badge' style='background:"+LiferayAssetsStatisticsPortletKeys.getLabelStatusColor("publish")+"'>publish ("+ACTION_PUBLISH+")</span>");
		}
		if (STATUS_ANY > 0) {
			statusJsonObject.put("any","<span class='badge' style='background:"+LiferayAssetsStatisticsPortletKeys.getLabelStatusColor("any")+"'>any ("+STATUS_ANY+")</span>");
		}
		if (STATUS_APPROVED > 0) {
			statusJsonObject.put("approved","<span class='badge' style='background:"+LiferayAssetsStatisticsPortletKeys.getLabelStatusColor("approved")+"'>approved ("+STATUS_APPROVED+")</span>");
		}
		if (STATUS_DRAFT > 0) {
			statusJsonObject.put("draft","<span class='badge' style='background:"+LiferayAssetsStatisticsPortletKeys.getLabelStatusColor("draft")+"'>draft ("+STATUS_DRAFT+")</span>");
		}
		if (STATUS_EXPIRED > 0) {
			statusJsonObject.put("expired","<span class='badge' style='background:"+LiferayAssetsStatisticsPortletKeys.getLabelStatusColor("expired")+"'>expired ("+STATUS_EXPIRED+")</span>");
		}
		if (STATUS_IN_TRASH > 0) {
			statusJsonObject.put("in-trash","<span class='badge' style='background:"+LiferayAssetsStatisticsPortletKeys.getLabelStatusColor("in-trash")+"'>in-trash ("+STATUS_IN_TRASH+")</span>");
		}
		if (STATUS_INCOMPLETE > 0) {
			statusJsonObject.put("incomplete","<span class='badge' style='background:"+LiferayAssetsStatisticsPortletKeys.getLabelStatusColor("incomplete")+"'>incomplete ("+STATUS_INCOMPLETE+")</span>");
		}
		if (STATUS_PENDING > 0) {
			statusJsonObject.put("pending","<span class='badge' style='background:"+LiferayAssetsStatisticsPortletKeys.getLabelStatusColor("pending")+"'>pending ("+STATUS_PENDING+")</span>");
		}
		if (STATUS_SCHEDULED > 0) {
			statusJsonObject.put("scheduled","<span class='badge' style='background:"+LiferayAssetsStatisticsPortletKeys.getLabelStatusColor("scheduled")+"'>scheduled ("+STATUS_SCHEDULED+")</span>");
		}
		if (STATUS_INACTIVE > 0) {
			statusJsonObject.put("in-active","<span class='badge' style='background:"+LiferayAssetsStatisticsPortletKeys.getLabelStatusColor("in-active")+"'>in-active ("+STATUS_INACTIVE+")</span>");
		}
		if (STATUS_DENIED > 0) {
			statusJsonObject.put("denied","<span class='badge' style='background:"+LiferayAssetsStatisticsPortletKeys.getLabelStatusColor("denied")+"'>denied ("+STATUS_DENIED+")</span>");
		}
		return statusJsonObject;
	}
	/**
	 * Return Asset Total Count
	 * @param groupId
	 * @param companyId
	 * @param assetName
	 * @return
	 * @throws PortalException
	 */
	public static int getAssetsCount(long groupId,long companyId,String assetName) throws PortalException{
		
		if(assetName.equals("Blog")){
			DynamicQuery blogQuery = BlogsEntryLocalServiceUtil.dynamicQuery();
			if(groupId > 0){
				blogQuery.add(PropertyFactoryUtil.forName("groupId").eq(groupId));
			}
			if(companyId > 0){
				blogQuery.add(PropertyFactoryUtil.forName("companyId").eq(companyId));
			}
			return BlogsEntryLocalServiceUtil.dynamicQuery(blogQuery).size();
		}else if(assetName.equals("Document & Media")){
			
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
				DLFileVersion fileVersion = DLFileVersionLocalServiceUtil
						.getLatestFileVersion(dlFileVersion.getUserId(), dlFileVersion.getFileEntryId());
				map.put(fileVersion.getFileVersionId(), fileVersion.getStatus());
			}
			return map.size();
		}else if(assetName.equals("Forms")){
			DynamicQuery recordSetQuery = DDLRecordSetLocalServiceUtil.dynamicQuery();
			if(groupId > 0){
				recordSetQuery.add(PropertyFactoryUtil.forName("groupId").eq(groupId));
			}
			if(companyId > 0){
				recordSetQuery.add(PropertyFactoryUtil.forName("companyId").eq(companyId));
			}
			return DDLRecordSetLocalServiceUtil.dynamicQuery(recordSetQuery).size();
		}else if(assetName.equals("Users")){
			DynamicQuery userQuery = UserLocalServiceUtil.dynamicQuery();
			userQuery.add(PropertyFactoryUtil.forName("companyId").eq(companyId));
			return UserLocalServiceUtil.dynamicQuery(userQuery).size();
		}else if(assetName.equals("Web Content")){
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

			// GET LEATEST JOURNAL ARTICLE
			for (JournalArticle article : articles) {
				DynamicQuery dynamicQuery = JournalArticleLocalServiceUtil.dynamicQuery();
				dynamicQuery.add(PropertyFactoryUtil.forName("resourcePrimKey").eq(article.getResourcePrimKey()));
				dynamicQuery.addOrder(OrderFactoryUtil.desc("modifiedDate"));
				List<JournalArticle> list = JournalArticleLocalServiceUtil.dynamicQuery(dynamicQuery);
				if (list.size() > 0) {
					latestArticleMap.put(list.get(0).getId(), list.get(0).getStatus());
				}
			}
			return latestArticleMap.size();
		}else if(assetName.equals("Wiki")){
			
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
			return latestPageMap.size();
		}
		return 0;
	}
}
