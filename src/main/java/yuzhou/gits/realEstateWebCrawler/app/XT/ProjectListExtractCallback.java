package yuzhou.gits.realEstateWebCrawler.app.XT;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class ProjectListExtractCallback extends DefaultRealEstateCrawlingCallback {
	// protected BuildingExtractCallback buildingExtractCallback = new
	// BuildingExtractCallback();

	public ProjectListExtractCallback() {
	}

	public static final Pattern projDetailUrlP = Pattern.compile("'(.*)'");

	@Override
	public void extracting(String respStr) throws Exception {
		Document baseDoc = Jsoup.parse((String) respStr);
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;
		Elements projListDataE = baseDoc.select(XTConfig.projListDataSelector);
		Iterator<Element> projListDataEIt = projListDataE.iterator();
		while (projListDataEIt.hasNext()) {
			try {
				Element projDataE = projListDataEIt.next();
				projDataE.setBaseUri(XTConfig.siteDomain + "/content");
				Element projDetailPageUrlE = projDataE.selectFirst(XTConfig.projDetailPageUrlSelector);
				String projDetailURL = projDetailPageUrlE.absUrl("href");
				WebCrawlingTask projDetailTaks = this.deriveNewTask("", true, projDetailURL,
						new DefaultRealEstateCrawlingCallback() {
							protected JsonParser jsonParser = new JsonParser();

							@Override
							protected void extracting(String respStr) throws Exception {
								Element projDetailDoc = Jsoup.parse(respStr);
								Map<String, String> projPropsMap = new HashMap<String, String>();
								String floorId = projDetailDoc.selectFirst("input[name=\"floor_id\"]").attr("value");
								projPropsMap = new HashMap<String, String>();
								this.extractor.extractDataByCssSelectors(XTConfig.projDetailSelectorMap, projDetailDoc,
										projPropsMap);

								System.out.println(projPropsMap.get("projName") + "(" + crawlerId + "," + currPageNo + "," + ")");

								Elements buildingListE = projDetailDoc.select(XTConfig.buildingListSelector);
								if (buildingListE != null && buildingListE.size() > 0) {
									// extracting building NOs
									Map<String, String[]> paramsMap = new HashMap<String, String[]>();
									this.extractor.extractSelectEVals(projDetailDoc.select("#db_select > option"),
											"buildingNOs", paramsMap);
									String[] buildingNOs = paramsMap.get("buildingNOs");
									if (buildingNOs.length != buildingListE.size()) {
										System.out.println("wrong building???");
									}
									Iterator<Element> buildingEIt = buildingListE.iterator();
									int i = 0;
									// Elements roomListE =
									// projDetailDoc.select(XTConfig.roomListSelector);
									while (buildingEIt.hasNext()) {
										try {
											Element buildingE = buildingEIt.next();
											Map<String, String> buildingPropsMap = new HashMap<String, String>();
											this.extractor.extractDataByCssSelectors(XTConfig.buildingDataSelectorMap,
													buildingE, buildingPropsMap);
											String buildingNo = buildingNOs[i++];
											if (i <= buildingNOs.length) {
												// next building
												String[] postData = { "ywzh", buildingNo, "floorid", floorId };
												WebCrawling crawling = new WebCrawling(HttpMethod.POST,null,
														XTConfig.siteDomain + XTConfig.roomListURL,
														1000,1000,1000);
												crawling.setPostFormData(postData);
												WebCrawlingTask roomsTask = this.deriveNewTask("", false, crawling,
														new DefaultRealEstateCrawlingCallback() {
															private void extractRooms(Map<String, String> project, Map<String, String> building,
																	Elements roomListE) {
																if(roomListE == null){
																	//Map<String, String> roomDetailPropsMap = new HashMap<String, String>();
																	//this.extractor.fillPropsNullVal(XTConfig.roomSelectorMap, roomDetailPropsMap);
																	// TODO: do callback
																	/*Map<String, String>[] propsMapArry = (Map<String, String>[]) new Map[3];
																	propsMapArry[0] = projPropsMap;
																	propsMapArry[1] = buildingPropsMap;
																	propsMapArry[2] = roomDetailPropsMap;
																	this.roomIntoDBCallback.doCallback(propsMapArry);*/
																	return;
																}
																Iterator<Element> roomListEIt = roomListE.iterator();
																while (roomListEIt.hasNext()) {
																	try {
																		Element roomE = roomListEIt.next();
																		Map<String, String> roomDetailPropsMap = new HashMap<String, String>();
																		this.extractor.extractDataByCssSelectors(XTConfig.roomSelectorMap, roomE, roomDetailPropsMap);

																		// TODO: do callback
																		/*Map<String, String>[] propsMapArry = (Map<String, String>[]) new Map[3];
																		propsMapArry[0] = projPropsMap;
																		propsMapArry[1] = buildingPropsMap;
																		propsMapArry[2] = roomDetailPropsMap;
																		this.roomIntoDBCallback.doCallback(propsMapArry);*/
																		Object[] callbackArgs = { roomCollectionName,"", currPageNo,"", 
																				project,building,roomDetailPropsMap};
																		this.mongoDBCallback.doCallback(callbackArgs);
																	} catch (Exception _1) {
																		System.out.println("wrong");
																	}
																}
															}
															
															@Override
															protected void extracting(String respStr) throws Exception {
																String roomListStr = null;
																try {
																	JsonElement roomListJson = jsonParser
																			.parse(respStr);
																	roomListStr = roomListJson.getAsJsonObject()
																			.get("content").getAsString();
																	Element roomListDoc = Jsoup.parse(roomListStr);
																	Elements roomListE = roomListDoc
																			.select("div.scroll > table > tbody > tr");
																	// extracting rooms
																	Map<String,String> project = (Map<String, String>) this.crawlTask
																			.getPathContext().getAttr("project");
																	this.extractRooms(project, buildingPropsMap, roomListE);
																} catch (Exception e) {
																	roomListStr = "<html></html>";
																}
															}
														});
												roomsTask.getPathContext().setAttr("project", projPropsMap);
												this.executor.execute(roomsTask);
											} else {
												System.out.println("x");
												break;
											}
										} catch (Exception _1) {
											_1.printStackTrace();
											System.out.println("wrong:" + projPropsMap.get("projName"));
										}
									}
								}/* else {
									Map<String, String> buildingPropsMap = new HashMap<String, String>();
									this.extractor.fillPropsNullVal(XTConfig.buildingDataSelectorMap, buildingPropsMap);
									this.extractRooms(projPropsMap,buildingPropsMap, null);
								}*/
							}
						});
				projDetailTaks.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, this.currPageNo);
				this.executor.execute(projDetailTaks);

			} catch (Exception _1) {
				_1.printStackTrace();
			}
			currCount++;
		}
	}
	private String roomCollectionName = "xt_room";
	@Override
	public void init(Object... args) throws Exception {
		super.init(args);
		this.roomCollectionName = this.roomCollectionName + this.datasetSuffix;
		this.mongoDBCallback.addPropsCollection(this.roomCollectionName,100);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.roomCollectionName);
	}
}