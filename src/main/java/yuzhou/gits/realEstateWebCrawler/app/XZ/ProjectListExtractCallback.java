package yuzhou.gits.realEstateWebCrawler.app.XZ;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.app.TC.TCConfig;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class ProjectListExtractCallback extends DefaultRealEstateCrawlingCallback {
	// protected BuildingPageExtractCallback buildingPageCallback = new
	// BuildingPageExtractCallback();
	public String projectCollectionName = "xz_project";
	public String carryoutCollectionName = "xz_carryoutlicense";
	public String planningCollectionName = "xz_planninglicense";
	public String presaleCollectionName = "xz_presalelicense";
	public String landCollectionName = "xz_landlicense";
	private String roomCollectionName = "xz_room";
	
	public ProjectListExtractCallback() {
	}

	@Override
	public void extracting(String respStr) throws Exception {
		Document baseDoc = Jsoup.parse((String) respStr);
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;

		Elements projListE = baseDoc.select(XZConfig.projListSelector);
		Iterator<Element> projListIt = projListE.iterator();
		while (projListIt.hasNext()) {
			try {
				Element projListDataItemE = projListIt.next();
				String projDetailUrl = XZConfig.siteDomain + "/" + projListDataItemE.attr("href");
				WebCrawling projectDetailCrawling = new WebCrawling(HttpMethod.GET, null, projDetailUrl, 600000, 600000,
						600000);
				WebCrawlingTask projectDetailCrawlTask = this.deriveNewTask("", true, projectDetailCrawling,
						new DefaultRealEstateCrawlingCallback() {
							@Override
							protected void extracting(String respStr) throws Exception {
								int currCount = (Integer) this.crawlTask.getPathContext().getAttr(CURR_COUNT_ATTR);

								Document baseDoc = Jsoup.parse(respStr);
								Map<String, String> projPropsMap = new HashMap<String, String>();
								extractor.extractDataByCssSelectors(XZConfig.projectDetailSelectorMap, baseDoc,
										projPropsMap);
								System.out.println(projPropsMap.get("projName") + "(" + crawlerId + "," + currPageNo
										+ "," + (currCount) + ")");
								// TODO: fire callback
								Object[] callbackArgs = { projectCollectionName,"", currPageNo,currCount, projPropsMap };
								this.mongoDBCallback.doCallback(callbackArgs);
								
								Element baseE = baseDoc.selectFirst("#aspnetForm > div.newscon > div.lp-list-con > div > div.tab-con.js_trsTab");
								this.extractPreLicenses(projPropsMap, baseE);
								this.extractLandLicenses(projPropsMap, baseE);
								this.extractCarryoutLicenses(projPropsMap, baseE);
								this.extractPlanningLicenses(projPropsMap, baseE);
								//extracting buildings
								this.extractBuildings(projPropsMap, baseE);
							}
							private void extractBuildings(Map<String, String> proj, Element targetE) {
								Elements buidingNoList = targetE.select(XZConfig.buildingListSelector);
								Iterator<Element> listEIt = buidingNoList.iterator();
								//first building
								Element firstBuilding = listEIt.next();
								String buildNo = firstBuilding.text();
								this.extractRooms(proj, buildNo, targetE);
								//others
								while(listEIt.hasNext()){
									try{
										Element build = listEIt.next();
										buildNo = build.text();
										String url = XZConfig.siteDomain + "/"
												+build.attr("href");
										WebCrawling roomDetailCrawling = new WebCrawling(HttpMethod.POST, null,
												url, 600000, 600000, 600000);
										WebCrawlingTask roomDetailCrawlTask = this.deriveNewTask("", false, 
												roomDetailCrawling ,new DefaultRealEstateCrawlingCallback() {
													@Override
													protected void extracting(String respStr) throws Exception {
														Document doc = Jsoup.parse(respStr);
														String buildNo = (String) this.crawlTask.getPathContext().getAttr("buildNo");
														 extractor.extractDataByCssSelectors(XZConfig.projectDetailSelectorMap, doc, proj);
														Element baseE = doc
																.selectFirst("#aspnetForm > div.newscon > div.lp-list-con > div > div.tab-con.js_trsTab");
														extractRooms(proj, buildNo, baseE);
													}
										});
										roomDetailCrawlTask.getPathContext().setAttr("buildNo", buildNo);
										this.executor.execute(roomDetailCrawlTask);
									}catch(Exception e){e.printStackTrace();}
								}
							}
							private void extractPlanningLicenses(Map<String, String> proj, Element targetE) {
								Elements listE = targetE.select(XZConfig.planningLicenseListSelector);
								Iterator<Element> listEIt = listE.iterator();
								while (listEIt.hasNext()) { 
									try {
										Map<String, String> palnningPropsMap = new HashMap<String, String>();
										Element e = listEIt.next();
										extractor.extractDataByCssSelectors(XZConfig.planningLicenseDataSelectorMap, e, palnningPropsMap);
										palnningPropsMap.put("projName",proj.get("projName"));
										// TODO: fire callback
										int currCount = (Integer) this.crawlTask.getPathContext().getAttr(CURR_COUNT_ATTR);
										Object[] callbackArgs = { planningCollectionName,"", 
												currPageNo,currCount, palnningPropsMap };
										this.mongoDBCallback.doCallback(callbackArgs);
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								}
							}
							private void extractCarryoutLicenses(Map<String, String> proj, Element targetE) {
								Elements listE = targetE.select(XZConfig.carryoutLicenseListSelector);
								Iterator<Element> listEIt = listE.iterator();
								while (listEIt.hasNext()) {
									try {
										Map<String, String> carryoutPropsMap = new HashMap<String, String>();
										Element e = listEIt.next();
										extractor.extractDataByCssSelectors(
												XZConfig.carryoutLicenseDataSelectorMap, e, carryoutPropsMap);
										carryoutPropsMap.put("projName",proj.get("projName"));
										// TODO: fire callback
										int currCount = (Integer) this.crawlTask.getPathContext().getAttr(CURR_COUNT_ATTR);
										Object[] callbackArgs = { carryoutCollectionName,"", 
												currPageNo,currCount, carryoutPropsMap };
										this.mongoDBCallback.doCallback(callbackArgs);
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								}
							}
							private void extractLandLicenses(Map<String, String> proj, Element targetE) {
								Elements listE = targetE.select(XZConfig.landLicenseListSelector);
								Iterator<Element> listEIt = listE.iterator();
								while (listEIt.hasNext()) {
									Map<String, String> landPropsMap = new HashMap<String, String>();
									Element e = listEIt.next();
									try {
										extractor.extractDataByCssSelectors(XZConfig.landLicenseDataSelectorMap, e, landPropsMap);
										landPropsMap.put("projName",proj.get("projName"));
										// TODO: fire callback
										int currCount = (Integer) this.crawlTask.getPathContext().getAttr(CURR_COUNT_ATTR);
										Object[] callbackArgs = { landCollectionName,"", 
												currPageNo,currCount, landPropsMap };
										this.mongoDBCallback.doCallback(callbackArgs);
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								}
							}
							private void extractPreLicenses(Map<String, String> proj, Element targetE) {
								Elements listE = targetE.select(XZConfig.preSaleLicenseListSelector);
								Iterator<Element> listEIt = listE.iterator();
								while (listEIt.hasNext()) {
									Map<String, String> presalePropsMap = new HashMap<String, String>();
									Element e = listEIt.next();
									try {
										extractor.extractDataByCssSelectors(XZConfig.preSaleLicenseDataSelectorMap, e, presalePropsMap);
										presalePropsMap.put("projName",proj.get("projName"));
										// TODO:do callback
										int currCount = (Integer) this.crawlTask.getPathContext().getAttr(CURR_COUNT_ATTR);
										Object[] callbackArgs = { presaleCollectionName,"", 
												currPageNo,currCount, presalePropsMap };
										this.mongoDBCallback.doCallback(callbackArgs);
									} catch (Exception e1) {e1.printStackTrace();}
								}
							}
							private void extractRooms(Map<String, String> proj,String buildNo, Element targetE) {
								Elements listE = targetE.select(XZConfig.roomListSelector);
								Iterator<Element> listEIt = listE.iterator();
								String projName = proj.get("projName");
								while (listEIt.hasNext()) {
									try {
										Element e = listEIt.next();
										Map<String,String> roomPropsMap = new HashMap<String,String>();
										roomPropsMap.put("buildNo", buildNo);
										roomPropsMap.put("projName", projName);
										extractor.extractDataByCssSelectors(XZConfig.roomDataSelectorMap, e, roomPropsMap);
										String url = e.selectFirst(XZConfig.roomDetailPageURL).attr("href");
										WebCrawling roomDetailCrawling = new WebCrawling(HttpMethod.POST, null,
												XZConfig.siteDomain+"/"+url, 600000, 600000, 600000);
										WebCrawlingTask roomDetailCrawlTask = this.deriveNewTask("", false, 
												roomDetailCrawling ,new DefaultRealEstateCrawlingCallback() {
													@Override
													protected void extracting(String respStr) throws Exception {
														int currCount = (Integer) this.crawlTask.getPathContext().getAttr(CURR_COUNT_ATTR);
														Document roomDetailDoc = Jsoup.parse(respStr);
														Map<String,String> roomDetailPropsMap = new HashMap<String,String>();
														extractor.extractDataByCssSelectors(XZConfig.roomDetailSelectorMap, roomDetailDoc, 
																roomDetailPropsMap);
														// TODO:do callback
														Object[] callbackArgs = { roomCollectionName,"", 
																currPageNo,currCount, roomPropsMap,roomDetailPropsMap };
														this.mongoDBCallback.doCallback(callbackArgs);
													}
										});
										this.executor.execute(roomDetailCrawlTask);
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								}
								long e = System.currentTimeMillis();
								//System.out.println((e-s)/1000.f);
							}
						});
				projectDetailCrawlTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, currPageNo);
				projectDetailCrawlTask.getPathContext().setAttr(CURR_COUNT_ATTR, currCount);

				this.executor.execute(projectDetailCrawlTask);
				currCount++;
			} catch (Exception _1) {
				_1.printStackTrace();
			}
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		this.datasetSuffix = (String)args[0];
		projectCollectionName = this.projectCollectionName + this.datasetSuffix;
		carryoutCollectionName = this.carryoutCollectionName + this.datasetSuffix;
		planningCollectionName = this.planningCollectionName + this.datasetSuffix;
		landCollectionName = this.landCollectionName + this.datasetSuffix;
		presaleCollectionName = this.presaleCollectionName + this.datasetSuffix;
		roomCollectionName = this.roomCollectionName + this.datasetSuffix;
		this.mongoDBCallback.addPropsCollection(this.projectCollectionName, 10);
		this.mongoDBCallback.addPropsCollection(this.carryoutCollectionName, 10);
		this.mongoDBCallback.addPropsCollection(this.landCollectionName, 10);
		this.mongoDBCallback.addPropsCollection(this.planningCollectionName,10);
		this.mongoDBCallback.addPropsCollection(this.presaleCollectionName, 10);
		this.mongoDBCallback.addPropsCollection(this.roomCollectionName,100);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.projectCollectionName);
		this.mongoDBCallback.flush(this.carryoutCollectionName);
		this.mongoDBCallback.flush(this.landCollectionName );
		this.mongoDBCallback.flush(this.planningCollectionName);
		this.mongoDBCallback.flush(this.presaleCollectionName );
		this.mongoDBCallback.flush(this.roomCollectionName);
		// this.buildingPageCallback.clean(args);
	}
}