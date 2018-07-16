package yuzhou.gits.realEstateWebCrawler.app.TZ;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class ProjectListExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected BuildingPageCallback buildingPageCallback = new BuildingPageCallback();

	public ProjectListExtractCallback() {
	}

	@Override
	public void extracting(String respStr) throws Exception {
		Document baseDoc = Jsoup.parse((String) respStr);
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;

		Elements projListE = baseDoc.select(TZConfig.projListSelector);
		Iterator<Element> projListEIt = projListE.iterator();
		while (projListEIt.hasNext()) {
			Element projDataE = projListEIt.next();
			Map<String, String> projPropsMap = new HashMap<String, String>();
			this.extractor.extractDataByCssSelectors(TZConfig.projListDataSelectorMap, projDataE, projPropsMap);
			String avgPrice = this.parseAvgPrice(projDataE);
			projPropsMap.put("avgPrice", avgPrice);
			System.out
					.println(projPropsMap.get("projName") + "(" + crawlerId + "," + currPageNo + "," + currCount + ")");
			// extract project detail
			String projDetailDocURL = TZConfig.siteDomain
					+ projDataE.selectFirst(TZConfig.onPricePageUrlSelector).attr("href");
			WebCrawlingTask projectDetailTask = this.deriveNewTask("", true, projDetailDocURL,
					new DefaultRealEstateCrawlingCallback() {
						@Override
						protected void extracting(String respStr) throws Exception {
							Document projDetailDoc = Jsoup.parse(respStr);
							projDetailDoc.setBaseUri(projDetailDocURL);
							extractProjDetail(projPropsMap, projDetailDoc);
						}
						private void extractProjDetail(Map<String, String> projPropsMap, Document projDetailDoc) {
							Elements presaleTypesE = projDetailDoc.select(TZConfig.presalesTypeSelector);
							Iterator<Element> presaleTypesEIt = presaleTypesE.iterator();
							String presaleDetailURL = projDetailDoc.baseUri();
							Matcher m = p.matcher(presaleDetailURL);
							m.find();
							String sid = m.group(1);
							m.find();
							String propertyid = m.group(1);
							while (presaleTypesEIt.hasNext()) {
								try {
									Element presaleTypeE = presaleTypesEIt.next();
									String idStr = presaleTypeE.attr("id");
									String id = idStr.substring(idStr.indexOf("_") + 1);
									presaleDetailURL = TZConfig.siteDomain + TZConfig.presalesURL.replace("[sid]", sid);
									presaleDetailURL = presaleDetailURL.replace("[presellid]", id);
									presaleDetailURL = presaleDetailURL.replace("[propertyid]", propertyid);
									WebCrawlingTask presaleTask = this.deriveNewTask("", false, presaleDetailURL,
											new DefaultRealEstateCrawlingCallback() {
												protected JsonParser jsonParser = new JsonParser();

												@Override
												protected void extracting(String respStr) throws Exception {
													Map<String, String> presalePropsMap = new HashMap<String, String>();
													try {
														String presaleDetailStr = respStr;
														JsonObject presaleJson = this.jsonParser.parse(presaleDetailStr).getAsJsonObject();
														this.extractor.extractDataByJson(TZConfig.presaleJsonMap, presaleJson,
																presalePropsMap);
													} catch (Exception e) {
														// FIX: presale info access internal error
														this.extractor.fillPropsNullVal(TZConfig.presaleJsonMap, presalePropsMap);
													}
													String buildingListPageURL = projDetailDoc.baseUri() + "?isopen=&presellid=" + id
															+ "&buildingid=" + "&area=&allprice=&housestate=&housetype=&page=[PAGENO]";
													ProjectListExtractCallback.this.buildingPageCallback.pageBaseURL = 
															buildingListPageURL;
													WebCrawlingTask buildingListTask = this.deriveNewTask("", false, 
															buildingListPageURL.replace("[PAGENO]", "1"),
															ProjectListExtractCallback.this.buildingPageCallback.getCallbacksInPage()[0],
															ProjectListExtractCallback.this.buildingPageCallback);
													buildingListTask.getPathContext().setAttr("presale", presalePropsMap);
													buildingListTask.getPathContext().setAttr(Constants.PAGE_START_NO, 2);
													buildingListTask.getPathContext().setAttr(Constants.PAGE_END_NO, -1);
													this.executor.execute(buildingListTask);
												}
											});
									// presaleTask.getPathContext().setAttr("presalePropsMap", val);
									this.executor.execute(presaleTask);
								} catch (Exception _1) {
									_1.printStackTrace();
								}
							}
						}
					});
			projectDetailTask.getPathContext().setAttr("project", projPropsMap);
			projectDetailTask.getCrawling().setHttpMethod(HttpMethod.GET);
			projectDetailTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, this.currPageNo);
			this.executor.execute(projectDetailTask);
			currCount++;
		}
	}
	static final Pattern p = Pattern.compile("_(\\d+)");
	@Override
	public void init(Object... args) throws Exception {
		super.init(args);
		this.buildingPageCallback.init(args);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.buildingPageCallback.clean(args);
	}

	private String parseAvgPrice(Element projDataE) {
		StringBuffer avgStrBuff = new StringBuffer("");
		Elements avgEs = projDataE.select(TZConfig.avgPriceSelector);
		Iterator<Element> avgEsIt = avgEs.iterator();
		while (avgEsIt.hasNext()) {
			String avgDigital = avgEsIt.next().attr("class");
			String digital = TZConfig.digitsMap.get(avgDigital);
			avgStrBuff.append(digital);
		}
		return avgStrBuff.toString();
	}
}