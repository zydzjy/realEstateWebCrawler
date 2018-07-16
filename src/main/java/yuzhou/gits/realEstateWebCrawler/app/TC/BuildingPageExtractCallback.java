package yuzhou.gits.realEstateWebCrawler.app.TC;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class BuildingPageExtractCallback extends DefaultRealEstateCrawlingCallback {
	static Pattern buildingPageInfoP = Pattern.compile("/(\\d+)页");
	protected String buildingCollectionName = "tc_building20180711";
	protected RoomExtractCallback roomExtractCallback = new RoomExtractCallback();
	@Override
	public void init(Object... args) throws Exception {
		this.roomExtractCallback.init(args);
		this.mongoDBCallback.addPropsCollection(this.buildingCollectionName,10);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.buildingCollectionName);
		this.roomExtractCallback.clean(args);
	}
	
	static final String buildingPageInfoSelector = "#AspNetPager1 > table > tbody > tr > td:nth-child(1)";
	
	@Override
	protected void extracting(String respStr) throws Exception {
		Document projDetailDoc = Jsoup.parse(respStr);
		Map<String,String> projListData = (Map<String, String>) 
				this.crawlTask.getPathContext().getAttr("projectListData");
		String projId = (String) this.crawlTask.getPathContext().getAttr("projId");
		String buildingPageInfo = projDetailDoc.selectFirst(buildingPageInfoSelector).text();
		Matcher mBuilding = buildingPageInfoP.matcher(buildingPageInfo);
		if (mBuilding.find()) {
			Integer totalBuildingPage = Integer.parseInt(mBuilding.group(1));
			// System.out.println("totalBuildingPage:"+totalBuildingPage);
			int pageNo = 1;
			do {
				try {
					String[] postData = { "__VIEWSTATE", projDetailDoc.selectFirst("#__VIEWSTATE").attr("value"),
							"__VIEWSTATEGENERATOR", projDetailDoc.selectFirst("#__VIEWSTATEGENERATOR").attr("value"),
							"__EVENTTARGET", "AspNetPager1", "__EVENTARGUMENT", String.valueOf(pageNo) };
					
					WebCrawling buildingCrawling = new WebCrawling(HttpMethod.POST, null,
							TCConfig.buildingPageURL + projId, 600000, 600000, 600000);
					buildingCrawling.setPostFormData(postData);
					WebCrawlingTask buildingCrawlTask = this.deriveNewTask("", false, 
							buildingCrawling ,new DefaultRealEstateCrawlingCallback() {
								@Override
								public void init(Object... args) throws Exception {}
								@Override
								public void clean(Object... args) throws Exception {}
								@Override
								protected void extracting(String respStr) throws Exception {
									Document projDetailDoc = Jsoup.parse(respStr);
									Elements buildDataListE = projDetailDoc.select(TCConfig.buildListSelector);
									int currCount = (Integer) this.crawlTask.getPathContext()
											.getAttr(DefaultRealEstateCrawlingCallback.CURR_COUNT_ATTR);
									Iterator<Element> buildDataListEIt = buildDataListE.iterator();
									if (buildDataListE.size() > 0) {
										while (buildDataListEIt.hasNext()) {
											Element e = buildDataListEIt.next();
											Map<String, String> buildPropsMap = new HashMap<String, String>();
											try {
												this.extractor.extractDataByCssSelectors(TCConfig.buildSelectorMap, e, buildPropsMap);
												// TODO:do callback
												Object[] callbackArgs = { buildingCollectionName,"", 
														currPageNo,currCount, projListData,buildPropsMap };
												this.crawlTask.getPathContext().setAttr("building", buildPropsMap);
												this.mongoDBCallback.doCallback(callbackArgs);
												String buildPageURLInfo = e.selectFirst(TCConfig.buildDetailURLSelector).attr("onclick");
												Matcher m = TCConfig.projDetailPageURLPattern.matcher(buildPageURLInfo);
												if (m.find()) {
													String projDetailPageURL = TCConfig.siteDomain + "/" + m.group(1);
													if (m.find()) {
														projDetailPageURL += "id=" + m.group(1);
														WebCrawling buildingDetailCrawling = new WebCrawling(HttpMethod.GET, null,
																projDetailPageURL, 600000, 600000, 600000);
														WebCrawlingTask buildingDetailCrawlTask = this.deriveNewTask("", false, 
																buildingDetailCrawling,BuildingPageExtractCallback.this.roomExtractCallback);
														this.executor.execute(buildingDetailCrawlTask);
													}
												}
											}catch (Exception e1) {e1.printStackTrace();}
										}
									}
								} 
							});
					this.executor.execute(buildingCrawlTask);
					pageNo++;
				} catch (Exception _1) {
					_1.printStackTrace();
				}
			} while (pageNo <= totalBuildingPage);
		}
	}
}