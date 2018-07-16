package yuzhou.gits.realEstateWebCrawler.app.KS;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class RoomExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected String roomCollectionName = "ks_room20180704";

	public RoomExtractCallback() {}

	@Override
	public void extracting(String respStr) throws Exception {

		Matcher m = KSConfig.buildingListInfoStrP.matcher(respStr);
		if (m.find()) {
			String eleStr = m.group(1);
			Document baseDoc = Jsoup.parse(eleStr);
			// Dump.dump("e:\\test.html", eleStr);
			Elements unitsE = baseDoc.select(KSConfig.unitListSelector);
			Iterator<Element> unitsEIt = unitsE.iterator();
			while (unitsEIt.hasNext()) {
				try {
					Element e = unitsEIt.next();
					Element roomListT = e.parent().parent().parent().nextElementSibling();
					Elements roomTds = roomListT.select(KSConfig.roomTdSelector);
					Iterator<Element> roomTdsIt = roomTds.iterator();
					while (roomTdsIt.hasNext()) {
						try {
							Element roomTd = roomTdsIt.next();
							String roomColor = roomTd.attr("background");
							//String unitName = e.ownText();
							Element roomDiv = roomTd.selectFirst("div");
							if(roomDiv == null){continue;}
							String roomDetailUrl = KSConfig.siteDomain + KSConfig.projCtxPath + "/"
									+ roomDiv.attr("value");
							WebCrawling roomDetailPageCrawling = new WebCrawling(HttpMethod.GET, null, roomDetailUrl,
									600000, 600000, 600000);
							/*DefaultCrawlingTask roomDetailPageCrawlingTask = new DefaultCrawlingTask("",
									roomDetailPageCrawling);*/
							WebCrawlingTask roomDetailPageCrawlingTask = 
									this.deriveNewTask("", false, roomDetailPageCrawling);
							roomDetailPageCrawlingTask.addCallbacks(new DefaultRealEstateCrawlingCallback() {
								@Override
								public void extracting(String respStr) throws Exception {
									Document doc = Jsoup.parse(respStr);
									Map<String, String> roomPropsMap = new HashMap<String, String>();
									extractor.extractDataByCssSelectors(KSConfig.roomDataSelectorsMap, doc,
											roomPropsMap);
									roomPropsMap.put("roomColor", roomColor);
									// roomPropsMap.put("unitName", unitName);
									roomPropsMap.put("projName", (String) this.crawlTask.getPathContext()
											.getAttr("projName"));
									roomPropsMap.put("gaBuildingNo",(String) (String) this.crawlTask.getPathContext()
											.getAttr("gaBuildingNo"));
									// System.out.println(roomPropsMap.get("roomNo"));
									// TODO: save room
									int currPageNo = (Integer)this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);
									int currCount = (Integer)this.crawlTask.getPathContext().getAttr(CURR_COUNT_ATTR);
									
									Object[] callbackArgs = {
											roomCollectionName  , "", currPageNo, currCount, roomPropsMap };
									mongoDBCallback.doCallback(callbackArgs);
								}
								@Override
								public void init(Object... args) throws Exception {}
								@Override
								public void clean(Object... args) throws Exception {}
							});
							executor.execute(roomDetailPageCrawlingTask);
						} catch (Exception _1) {
							_1.printStackTrace();
						}
					}
					// System.out.println(unitName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			return;
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		this.mongoDBCallback.addPropsCollection(this.roomCollectionName  , 100);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.roomCollectionName);
	}

}