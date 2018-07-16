package yuzhou.gits.realEstateWebCrawler.app.ZH;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class ProjectListExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected BuildingExtractCallback buildingPageCallback = new BuildingExtractCallback();
	public ProjectListExtractCallback() {
	}

	int currPageNo = 0;

	@Override
	public void extracting(String respStr) throws Exception {
		//this.stub.login();
		
		int currPageNo = (Integer) this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);
		Document baseDoc = Jsoup.parse((String) respStr);
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;
		JsonElement projsJson = new JsonParser().parse(respStr);
		if (projsJson.isJsonArray()) {
			JsonArray projListData = (JsonArray) projsJson;

			Iterator<JsonElement> projIt = projListData.iterator();
			while (projIt.hasNext()) {
				JsonElement aProjData = projIt.next();
				String SXID = aProjData.getAsJsonObject().get("SXID").getAsString();
				String BusinessCode = aProjData.getAsJsonObject().get("BusinessCode").getAsString();
				String InstanceId = aProjData.getAsJsonObject().get("InstanceId").getAsString();
				Map<String, String> projPropsMap = new HashMap<String, String>();
				this.extractor.extractDataByJson(ZHConfig.projJsonMap, aProjData, projPropsMap);

				System.out.println(
						projPropsMap.get("projName") + "(" + crawlerId + "," + currPageNo + "," + (currCount) + ")");
				// TODO: do callback
				// this.projIntoDBCallback.doCallback(projPropsMap);

				// extracting presaleLicense
				String projDetailPageURL = ZHConfig.siteDomain + "/Foundation/easyInfo/ShowForm.aspx?" + "BusinessCode="
						+ BusinessCode + "&TaskId=&BusinessInstanceId=" + SXID + "&InstanceId=" + InstanceId
						+ "&SceneId=wwgs&CooperateType=0&Dep=&" + "AccessGroup=SystemRead&InitialGroup=Default&"
						+ "DocID=09b1a7f4-9e4f-4621-8ce7-c69888521b7e&"
						+ "FormID=e0a69cd3-2ad5-4f27-8867-31fbcf08b791&FormDocMutil=0&"
						+ "IsDatumGroup=true&ParameterName=SXID&ParameterValue=" + SXID
						+ "&VT=&RefParent=&GoToDatum=&objectID=undefined&=undefined";
				WebCrawlingTask projDetailTask = this.deriveNewTask("", true, projDetailPageURL,
						new DefaultRealEstateCrawlingCallback() {

							@Override
							protected void extracting(String respStr) throws Exception {
								Document projDetailDoc = Jsoup.parse(respStr);
								// DumpElementToFile.dump("e:\\p.html", projDetailDoc);

								Map<String, String> presaleLicensePropsMap = new HashMap<String, String>();
								this.extractor.extractDataByCssSelectors(ZHConfig.presaleLicenseSelectorMap, projDetailDoc,
										presaleLicensePropsMap);
								// TODO: do callback
								// System.out.println(presaleLicensePropsMap.get("bankNum"));
								presaleLicensePropsMap.put("projName", projPropsMap.get("projName"));
								presaleLicensePropsMap.put("presaleLicenseNo", projPropsMap.get("presaleLicenseNo"));
								presaleLicensePropsMap.put("developer", projPropsMap.get("developer"));
								//this.presalelicenseDBCallback.doCallback(presaleLicensePropsMap);
							}
						});
				projDetailTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, currPageNo);
				this.executor.execute(projDetailTask);
				
				// extracting buildings
				String buildingListPageURL = "";
				if ("YSXKSP".equals(BusinessCode)) {
					buildingListPageURL = ZHConfig.siteDomain
							+ "/Foundation/easyWork/WorkDatumTree.aspx?BusinessCode=" + BusinessCode
							+ "&TaskId=&BusinessInstanceId=" + SXID + "&InstanceId=" + InstanceId
							+ "&SceneId=wwgs&CooperateType=&Dep=&VT=&RefParent=&GoToDatum=&objectID=undefined&=undefined";
				} else {
					buildingListPageURL = ZHConfig.siteDomain
							+ "/Foundation/easySimpleManage/SMWorkDatumTree.aspx?BusinessCode=" + BusinessCode
							+ "&SceneId=Default&VT=&RefParent=&GotoDatum=&" + "BusinessInstanceId=" + SXID
							+ "&SiteId=94bf2092-7895-4910-a439-33d56f390864";
				}

				WebCrawlingTask buildingTask = this.deriveNewTask("", true, buildingListPageURL,
						this.buildingPageCallback);
				buildingTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, currPageNo);
				this.executor.execute(buildingTask);
				
				currCount++;
			}
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		this.buildingPageCallback.init(args);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.buildingPageCallback.clean(args);
	}
}