package yuzhou.gits.realEstateWebCrawler.app.HZHD;

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
import yuzhou.gits.realEstateWebCrawler.SingletonMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class RoomExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected String roomCollectionName = "hzhd_room20180710";

	public RoomExtractCallback() {
		this.mongoDBCallback = SingletonMongoDBCallback.singleton;
	}

	public static Pattern p = Pattern.compile("(\\d+),(\\d+)");

	@Override
	public void extracting(String respStr) throws Exception {
		try {
			Document roomListPageDoc =  Jsoup.parse(respStr);
			Map<String,String> building = 
					(Map<String, String>) this.crawlTask.getPathContext().getAttr("building");
			Map<String,String> project = 
					(Map<String, String>) this.crawlTask.getPathContext().getAttr("project");
			
			Elements roomListE = roomListPageDoc.select(HZHDConfig.roomListSelector);
			if (roomListE != null && roomListE.size() > 0) {
				Iterator<Element> roomListEIt = roomListE.iterator();
				// String floorName = "";
				while (roomListEIt.hasNext()) {
					try {
						Element e = roomListEIt.next();
						Element roomE = e.selectFirst(HZHDConfig.roomColorSelector);
						if (roomE != null) {
							Map<String,String> roomPropsMap = new HashMap<String,String>();
							String roomColor = roomE.attr("src");
							roomPropsMap.put("roomColor", roomColor);
							roomPropsMap.put("projName", project.get("projName"));
							roomPropsMap.put("buildingName", building.get("buildingName"));
							roomPropsMap.put("buildingNo", building.get("buildingNo"));
							roomPropsMap.put("developer", project.get("developer"));
							roomPropsMap.put("preSaleLicense", project.get("licenseNo"));
							roomPropsMap.put("isNew", "");
							roomPropsMap.put("roomUsage", "");
							
							String roomDetailURLInfo = e.attr("onClick");
							Matcher m = p.matcher(roomDetailURLInfo);
							m.find();
							String id = m.group(1);
							String lcStr = m.group(2);
							roomDetailURLInfo = HZHDConfig.roomPageDetailURL.replace("[id]", id).replace("[lcStr]",
									lcStr);
							WebCrawling roomDetailCrawling = new WebCrawling(HttpMethod.GET, null,
									roomDetailURLInfo, 600000, 600000, 600000);
							WebCrawlingTask roomDetailCrawlTask = this.deriveNewTask("", false, 
									roomDetailCrawling,new DefaultRealEstateCrawlingCallback(){
										@Override
										public void init(Object... args) throws Exception {}
										@Override
										public void clean(Object... args) throws Exception {}
										@Override
										protected void extracting(String respStr) throws Exception {
											int currCount = (Integer) this.crawlTask.getPathContext().getAttr(CURR_COUNT_ATTR);
											
											Document roomDetailDoc = Jsoup.parse(respStr);
											Map<String,String> roomDetailPropsMap = new
													HashMap<String,String>();
											this.extractor.extractDataByCssSelectors(
													HZHDConfig.roomDetailSelectorMap, 
													roomDetailDoc, 
													roomDetailPropsMap);
											// TODO: fire callback
											Object[] callbackArgs = { roomCollectionName,
													crawlerId, currPageNo,currCount, roomPropsMap,
													roomDetailPropsMap };
											this.mongoDBCallback.doCallback(callbackArgs);
										}
							});
							this.executor.execute(roomDetailCrawlTask);
						}
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
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