package yuzhou.gits.realEstateWebCrawler.app.HZBL;

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
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class RoomExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected String roomCollectionName = "hzbl_room20180710";

	public RoomExtractCallback() {
	}

	public static Pattern p = Pattern.compile("(\\d+),(\\d+)");

	@Override
	public void extracting(String respStr) throws Exception {
		Document roomListPageDoc = Jsoup.parse(respStr);
		int currPageNo = (Integer)this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);
		int currCount = (Integer)this.crawlTask.getPathContext().getAttr(DefaultRealEstateCrawlingCallback.CURR_COUNT_ATTR);
		Map<String,String> project = (Map<String, String>) this.crawlTask.getPathContext().getAttr("project");
		Map<String,String> building = (Map<String, String>) this.crawlTask.getPathContext().getAttr("building");
		
		Elements roomListE = roomListPageDoc.select(HZBLConfig.roomListSelector);
		if (roomListE != null && roomListE.size() > 0) {
			Iterator<Element> roomListEIt = roomListE.iterator();
			// String floorName = "";
			while (roomListEIt.hasNext()) {
				try {
					Element e = roomListEIt.next();
					Element roomE = e.selectFirst(HZBLConfig.roomColorSelector);
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
						
						String roomDetailURLInfo = e.attr("onClick");
						Matcher m = p.matcher(roomDetailURLInfo);
						m.find();
						String id = m.group(1);
						String lcStr = m.group(2);
						roomDetailURLInfo = HZBLConfig.roomPageDetailURL.replace("[id]", id).replace("[lcStr]",
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
										Document roomDetailDoc = Jsoup.parse(respStr);
										this.extractor.extractDataByCssSelectors(
												HZBLConfig.roomDetailSelectorMap, 
												roomDetailDoc, roomPropsMap);
										// TODO:fire callback
										Object[] callbackArgs = { roomCollectionName,"", 
												currPageNo,currCount, roomPropsMap };
										this.mongoDBCallback.doCallback(callbackArgs);
									}
						});
						this.executor.execute(roomDetailCrawlTask);
					}
				}catch (Exception e) {
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