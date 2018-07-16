package yuzhou.gits.realEstateWebCrawler.app.HZDYW;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.DefaultCrawlingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.SingletonMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.app.IntoMongoDBCallback;

public class RoomExtractCallback extends DefaultCrawlingCallback {
	protected IntoMongoDBCallback mongoDBCallback;
	protected String roomCollectionName = "hzdyw_room20180709";

	public RoomExtractCallback() {
		 
	}

	public static Pattern p = Pattern.compile("(\\d+),(\\d+)");

	@Override
	public void _doCallback(WebCrawlingTask task) throws Exception {
		Document roomListPageDoc = Jsoup.parse((String) (task.getCrawling().getResponse()));
		int currPageNo = (Integer)this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);
		int currCount = (Integer)this.crawlTask.getPathContext().getAttr("currCount");
		
		Elements roomListE = roomListPageDoc.select(HZDYWConfig.roomListSelector);
		if (roomListE != null && roomListE.size() > 0) {
			Iterator<Element> roomListEIt = roomListE.iterator();
			// String floorName = "";
			Map<String,String> building = (Map<String,String>)task.getPathContext().getAttr("building");
			Map<String,String> projDetail = (Map<String,String>)task.getPathContext().getAttr("projDetail");
			while (roomListEIt.hasNext()) {
				try {
					Element e = roomListEIt.next();
					Element roomE = e.selectFirst(HZDYWConfig.roomColorSelector);
					if (roomE != null) {
						String roomColor = roomE.attr("src");
						
						String roomDetailURLInfo = roomE.parent().attr("onClick");
						Matcher m = p.matcher(roomDetailURLInfo);
						m.find();
						String id = m.group(1);
						String lcStr = m.group(2);
						roomDetailURLInfo = HZDYWConfig.roomPageDetailURL.replace("[id]", id).replace("[lcStr]", lcStr);
						WebCrawling roomDetailCrawling = new WebCrawling(HttpMethod.GET, null, roomDetailURLInfo,
								600000, 600000, 600000);
						WebCrawlingTask roomDetailCrawlTask = 
								this.deriveNewTask("", false, roomDetailCrawling, new WebResourceCrawlingCallback() {
									@Override
									public void doCallback(WebCrawlingTask task) throws Exception {
										Document roomDetailDoc = Jsoup.parse((String) (task.getCrawling().getResponse()));
										Map<String, String> roomDetailPropsMap = new HashMap<String, String>();
										extractor.extractDataByCssSelectors(HZDYWConfig.roomDetailSelectorMap, roomDetailDoc,
												roomDetailPropsMap);
										roomDetailPropsMap.put("roomColor", roomColor);
										roomDetailPropsMap.put("projName", projDetail.get("projName"));
										roomDetailPropsMap.put("buildingName", building.get("buildingName"));
										roomDetailPropsMap.put("buildingNo", building.get("buildingNo"));
										roomDetailPropsMap.put("preSaleLicense", projDetail.get("preSaleLicense"));
										roomDetailPropsMap.put("developer", projDetail.get("developer"));
										roomDetailPropsMap.put("isNew", "");
										
										// TODO,save room detail
										Object[] callbackArgs = { roomCollectionName,
												"", currPageNo,currCount, roomDetailPropsMap };
										mongoDBCallback.doCallback(callbackArgs);
									}
									@Override
									public void init(Object... args) throws Exception {}
									@Override
									public void clean(Object... args) throws Exception {}
								});
						/*Map<String, Object> roomDetailTaskContext = task.getTaskContext();
						roomDetailCrawlTask.setTaskContext(roomDetailTaskContext);
						roomDetailCrawlTask.addCallbacks(new WebResourceCrawlingCallback() {
							@Override
							public void doCallback(WebCrawlingTask task) throws Exception {
								Document roomDetailDoc = Jsoup.parse((String) (task.getCrawling().getResponse()));
								Map<String, String> roomDetailPropsMap = new HashMap<String, String>();
								extractor.extractDataByCssSelectors(HZDYWConfig.roomDetailSelectorMap, roomDetailDoc,
										roomDetailPropsMap);
								// TODO,save room detail
							}

							@Override
							public void init(Object... args) throws Exception {
							}

							@Override
							public void clean(Object... args) throws Exception {
							}
						});*/
						this.executor.execute(roomDetailCrawlTask);
						/*
						 * Document roomDetailDoc = (Document)
						 * http.get(roomDetailURLInfo
						 * ,HttpStrategy.RespType.HTML,emptyHeaders);
						 * this.dataExtractor.extractDataByCssSelectors(
						 * HZDYWConfig.roomDetailSelectorMap, roomDetailDoc,
						 * room);
						 */
						// TODO: fire callback

					}
				} catch (Exception _1) {
					_1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		this.mongoDBCallback.addPropsCollection(this.roomCollectionName,100);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.roomCollectionName);
	}

}