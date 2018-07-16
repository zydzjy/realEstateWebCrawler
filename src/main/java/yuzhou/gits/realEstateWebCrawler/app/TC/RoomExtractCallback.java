package yuzhou.gits.realEstateWebCrawler.app.TC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class RoomExtractCallback extends DefaultRealEstateCrawlingCallback {
	private String roomCollectionName = "tc_room20180711";

	@Override
	public void init(Object... args) throws Exception {
		this.mongoDBCallback.addPropsCollection(this.roomCollectionName, 100);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.roomCollectionName);
	}

	@Override
	protected void extracting(String respStr) throws Exception {
		Document buildDetailDoc = Jsoup.parse(respStr);
		Map<String, String> projListData = (Map<String, String>) this.crawlTask.getPathContext()
				.getAttr("projectListData");
		Map<String, String> buildPropsMap = (Map<String, String>) this.crawlTask.getPathContext().getAttr("building");
		int currCount = (Integer) this.crawlTask.getPathContext().getAttr(CURR_COUNT_ATTR);

		Elements roomsE = buildDetailDoc.select(TCConfig.roomsSelector);
		List<String> roomColors = new ArrayList<String>();
		Iterator<Element> roomsEIt = roomsE.iterator();
		List<String> roomDetailPageUrls = new ArrayList<String>();
		while (roomsEIt.hasNext()) {
			try {
				Element e = roomsEIt.next();
				String roomColor = e.attr("class");
				Element roomDetailPageUrlE = e.selectFirst("a");
				if (roomDetailPageUrlE == null)
					continue;
				roomColors.add(roomColor);
				String roomDetailPageUrlStr = roomDetailPageUrlE.attr("onclick");
				Matcher m = TCConfig.projDetailPageURLPattern.matcher(roomDetailPageUrlStr);
				if (m.find()) {
					String roomDetailPageURL = TCConfig.siteDomain + "/" + m.group(1);
					if (m.find()) {
						roomDetailPageURL += "id=" + m.group(1);
						roomDetailPageUrls.add(roomDetailPageURL);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// rooms:
		Iterator<String> roomDetailPageUrlsIt = roomDetailPageUrls.iterator();
		int i = 0;
		while (roomDetailPageUrlsIt.hasNext()) {
			try {
				Map<String, String> roomPropsMap = new HashMap<String, String>();
				roomPropsMap.put("roomColor", roomColors.get(i++));
				String roomDetailPageURL = roomDetailPageUrlsIt.next();
				WebCrawling roomDetailCrawling = new WebCrawling(HttpMethod.GET, null, roomDetailPageURL, 600000,
						600000, 600000);
				WebCrawlingTask roomDetailCrawlTask = this.deriveNewTask("", false, roomDetailCrawling,
						new DefaultRealEstateCrawlingCallback() {
							@Override
							public void init(Object... args) throws Exception {
							}

							@Override
							public void clean(Object... args) throws Exception {
							}

							@Override
							protected void extracting(String respStr) throws Exception {
								Document roomDetailDoc = Jsoup.parse(respStr);
								this.extractor.extractDataByCssSelectors(TCConfig.roomSelectorMap, roomDetailDoc,
										roomPropsMap);
								String floorNo = roomDetailDoc
										.selectFirst("#tb_first > tbody > tr:nth-child(2) > td:nth-child(2)").text();
								roomPropsMap.put("floorNo", floorNo);
								roomPropsMap.put("buildName", buildPropsMap.get("buildName"));
								roomPropsMap.put("projName", projListData.get("projName"));
								// TODO:do callback
								// System.out.println(roomPropsMap.get("roomNo"));
								Object[] callbackArgs = { roomCollectionName, "", currPageNo, currCount, roomPropsMap };
								this.mongoDBCallback.doCallback(callbackArgs);
							}
						});
				this.executor.execute(roomDetailCrawlTask);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
