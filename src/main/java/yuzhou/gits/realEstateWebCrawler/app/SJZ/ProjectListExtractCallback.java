package yuzhou.gits.realEstateWebCrawler.app.SJZ;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.SingletonMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.app.IntoMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class ProjectListExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected IntoMongoDBCallback mongoDBCallback;
	protected String projCollectionName = "sjz_project";
	private String houseType="非住宅"; //住宅，非住宅
	public ProjectListExtractCallback() {
		this.mongoDBCallback = SingletonMongoDBCallback.singleton;
	}

	@Override
	public void init(Object... args) throws Exception {
		super.init(args);
		this.projCollectionName=this.projCollectionName+this.datasetSuffix;
		this.mongoDBCallback.addPropsCollection(this.projCollectionName, 100);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.projCollectionName);
	}

	private String getValueByKey(JsonObject jsonObject, String key) {
		String value = "";
		try {
			value = jsonObject.get(key).getAsString();
		} catch (Exception e) {

		}
		return value;
	}

	private void extractProject(String crawlerId, int currPageNo, int currCount, Map<String, String> firstMap,
			Map<String, String> secondMap, String yszh, Map<String, String> buildMap, String dy, String floorNum,
			String roomNum, String xszt) {
		try {
			Map<String, Object> proj = new HashMap<String, Object>();
			proj.put("projName", firstMap.get("projName"));
			proj.put("developer", firstMap.get("developer"));
			proj.put("projAddr", firstMap.get("projAddr"));
			proj.put("contactPhone", firstMap.get("contactPhone"));
			proj.put("rjl", secondMap.get("rjl"));
			proj.put("tdyxqDate", secondMap.get("tdyxqDate"));
			proj.put("sgksDate", secondMap.get("sgksDate"));
			proj.put("sgjsDate", secondMap.get("sgjsDate"));
			proj.put("sjdw", secondMap.get("sjdw"));
			proj.put("sgdw", secondMap.get("sgdw"));
			proj.put("jldw", secondMap.get("jldw"));
			proj.put("zjzmj", secondMap.get("zjzmj"));
			proj.put("yszh", yszh);// 预售证号
			proj.put("bulidName", buildMap.get("bulidName"));
			proj.put("buildNum", buildMap.get("buildNum"));
			proj.put("floorCount", buildMap.get("floorCount"));
			proj.put("floorHigh", buildMap.get("floorHigh"));
			proj.put("kqDate", buildMap.get("kqDate"));
			proj.put("rzDate", buildMap.get("rzDate"));
			proj.put("tcw", buildMap.get("tcw"));
			proj.put("wyglf", buildMap.get("wyglf"));
			proj.put("dy", dy);
			proj.put("floorNum", floorNum);
			proj.put("roomNum", roomNum);
			proj.put("xszt", xszt);
			proj.put("type", houseType);
			Object[] callbackArgs = { projCollectionName, crawlerId, currPageNo, currCount, proj };
			mongoDBCallback.doCallback(callbackArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getDocumentStr(String url) throws Exception {
		final Map<String, String> map = new HashMap<String, String>();
		WebCrawling urlCrawling = new WebCrawling(HttpMethod.GET, null, url, 600000, 600000, 600000);
		WebCrawlingTask projectDetailCrawlTask = this.deriveNewTask("", true, urlCrawling,
				new WebResourceCrawlingCallback() {
					@Override
					public void doCallback(WebCrawlingTask task) throws Exception {
						String docStr = (String) (task.getCrawling().getResponse());
						map.put("docStr", docStr);
					}

					@Override
					public void init(Object... args) throws Exception {
					}

					@Override
					public void clean(Object... args) throws Exception {
					}
				});
		this.executor.execute(projectDetailCrawlTask);
		return map.get("docStr");
	}

	@Override
	protected void extracting(String respStr) throws Exception {
		Document baseDoc = Jsoup.parse((String) respStr);
		int currPageNo = (Integer) this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);

		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;
		try {
			if (baseDoc != null) {
				Elements projListE = baseDoc.select(SJZConfig.projectListSelector);
				Iterator<Element> projListIt = projListE.iterator();

				while (projListIt.hasNext()) {
					try {
						Element projListItemE = projListIt.next();
						Map<String, String> firstMap = new HashMap<String, String>();// 项目部分信息
						Map<String, String> secondMap = new HashMap<String, String>();// 项目剩余信息
						//得到项目部分信息
						this.extractor.extractDataByCssSelectors(SJZConfig.projectListFirstSelectorsMap, projListItemE,
								firstMap);
						
						System.out.println(firstMap.get("projName") + "(" + crawlerId + "," + currPageNo + ","
								+ (currCount) + ")");
						
						String pid = projListItemE.select("td:nth-child(1)>a").attr("href").trim();
						if (pid.contains("id=")) {
							pid = pid.split("id=")[1];
						} else {
							continue;
						}
						
						//得到项目详细信息
						String projectDetailURL = SJZConfig.projectURL.replace("[id]", pid);
						Document projDetailDoc = Jsoup.parse(getDocumentStr(projectDetailURL));	
						Element se = projDetailDoc.selectFirst(SJZConfig.projectSelector);
						extractor.extractDataByCssSelectors(SJZConfig.projectListSencodSelectorsMap, se, secondMap);
						
						//得到楼栋集合信息
						String buildListUrl = SJZConfig.leftMenuURL.replace("[id]", pid);
						String menuDoc = getDocumentStr(buildListUrl);
						JsonElement jelement = new JsonParser().parse(menuDoc.trim());
						JsonArray arrayObj = jelement.getAsJsonArray();
						try {
							for (int i = 0; i < arrayObj.size(); i++) {
								JsonElement job = arrayObj.get(i);
								JsonObject jsonObject = job.getAsJsonObject();
								String yszh = getValueByKey(jsonObject, "text");
								if (jsonObject.get("children") == null) {
									System.out.println(
											"*******************楼栋没有子节点,跳出**************************************");
									continue;
								}
								JsonArray chilidJson = jsonObject.get("children").getAsJsonArray();
								for (int j = 0; j < chilidJson.size(); j++) {
									JsonElement chilidObj = chilidJson.get(j);
									JsonObject clilidJsonObject = chilidObj.getAsJsonObject();
									
									//楼栋详细信息
									String buildDetailUrl = clilidJsonObject.get("attributes").getAsJsonObject()
											.get("url").getAsString();
									buildDetailUrl = SJZConfig.contentURL + buildDetailUrl;
									Document buildDetailDoc = Jsoup.parse(getDocumentStr(buildDetailUrl));
									Element buildDetail = buildDetailDoc.selectFirst(SJZConfig.bulidDetailSelector);
									Map<String, String> buildMap = new HashMap<String, String>();// 建筑信息
									extractor.extractDataByCssSelectors(SJZConfig.buildDetailSelectorsMap,
												buildDetail, buildMap);
									if (clilidJsonObject.get("children") == null) {
										System.out.println(
												"*******************clilidJsonObject没有子节点,跳出**************************************");
										continue;
									}
									JsonArray grandSonS = clilidJsonObject.get("children").getAsJsonArray();
									for (int K = 0; K < grandSonS.size(); K++) {
										JsonElement grandSon = grandSonS.get(K);
										String dy = grandSon.getAsJsonObject().get("text").getAsString();// 单元
										String roomUrl = grandSon.getAsJsonObject().get("attributes").getAsJsonObject()
												.get("url").getAsString();
										String roomDetailUrl = SJZConfig.contentURL + roomUrl;
										Document roomDetailDoc = Jsoup.parse(getDocumentStr(roomDetailUrl));
										Elements floorListE = roomDetailDoc.select(SJZConfig.floorListSelector);
										Iterator<Element> floorListIt = floorListE.iterator();
										while (floorListIt.hasNext()) {
											Element el = floorListIt.next();
											String floorNum = el.selectFirst("td:nth-child(1)").ownText();
											Elements roomListE = el.select(SJZConfig.roomListSelector);
											Iterator<Element> roomListIt = roomListE.iterator();
											while (roomListIt.hasNext()) {
												Element roome = roomListIt.next();
												String roomNum = roome.selectFirst("td").ownText();
												String xszt = roome.select("td>img").attr("src");
												if (xszt.contains("Red")) {
													xszt = "限制";
												} else if (xszt.contains("yel")) {
													xszt = "已售";
												} else if (xszt.contains("Green")) {
													xszt = "可售";
												}
												extractProject(crawlerId, currPageNo, currCount, firstMap, secondMap,
														yszh, buildMap, dy, floorNum, roomNum, xszt);
											}
										}

									}

								}

							}
						} catch (Exception ee) {
							ee.printStackTrace();
						}
						currCount++;
					} catch (Exception _1) {
						_1.printStackTrace();
					}
				}
			}
		} catch (Exception _2) {
			_2.printStackTrace();
		}		
	}
}
