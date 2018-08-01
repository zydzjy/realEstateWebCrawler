package yuzhou.gits.realEstateWebCrawler.app.KM;

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
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class BuildingListExtractCallback extends DefaultRealEstateCrawlingCallback {

	public BuildingListExtractCallback(String collectionName,DefaultRealEstateCrawlingCallback nextCallback) {
		super(collectionName,nextCallback);
	}
	
	public final static Pattern areaP = Pattern.compile("户型面积:([\\d]{0,}[.]{0,1}[\\d]{0,})");
	
	@Override
	protected void extracting(String respStr) throws Exception {
		Document detailDoc = Jsoup.parse(respStr);
		Elements bulidListE = detailDoc.select(KMConfig.bulidingTale).get(1).select(KMConfig.buildingListSelector);
		int currPageNo = (Integer) this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);
		int currCount = (Integer) this.crawlTask.getPathContext().getAttr("currCount");
		String pid = (String) this.crawlTask.getPathContext().getAttr("pid");
		Map<String, String> proj = (Map<String, String>) this.crawlTask.getPathContext().getAttr("proj");
		Iterator<Element> bulidListIt = bulidListE.iterator();
		while (bulidListIt.hasNext()) {
			try {
				Element build = bulidListIt.next();
				String buildName = build.selectFirst("tr td:nth-child(1) > a").ownText();
				String bid = build.select("tr td:nth-child(1) > a").attr("href");
				if (bid.contains("BId=")) {
					bid = bid.split("BId=")[1];
				} else {
					continue;
				}
				String buildListUrl = KMConfig.buildingListBaseURL.replace("[bid]", bid).replace("[pid]", pid);
				Document buildingListPageDoc = Jsoup.parse(getDocumentStr(buildListUrl));
				Elements buildingTrE = buildingListPageDoc.select(KMConfig.buildingSelector);
				Elements roomTypeTrE = buildingListPageDoc.select(KMConfig.roomTypeSelector);
				Iterator<Element> buildingTrIt = buildingTrE.iterator();
				Iterator<Element> roomTypeTrIt = roomTypeTrE.iterator();
				while (buildingTrIt.hasNext()) {
					Element e = buildingTrIt.next();
					Map<String, String> buildMap = new HashMap<String, String>();
					this.extractor.extractDataByCssSelectors(KMConfig.bulidDetailSelectorMap, e, buildMap);
					buildMap.put("projName", proj.get("projName"));
					buildMap.put("buildName", buildName);
					Object[] callbackArgs = { collectionNames.get(0), crawlerId, currPageNo, currCount, buildMap };
					mongoDBCallback.doCallback(callbackArgs);
				}
				while (roomTypeTrIt.hasNext()) {
					Element e = roomTypeTrIt.next();
					String roomId = e.select("tr> td>a").attr("href");
					//String hxArea = e.select("tr> td:nth-child(2)").text().split(":")[0].split("户型面积:")[1];
					String hxArea = e.select("tr> td:nth-child(2)").text();
					Matcher m = areaP.matcher(hxArea);
					if(m.find()){
						hxArea = m.group(1);
					}else{
						hxArea = "0";
					}
					if (roomId.contains("Id=")) {
						roomId = roomId.split("Id=")[1].split("&")[0];
					} else {
						continue;
					}
					String roomListUrl = KMConfig.roomURL.replace("[pid]", pid).replace("[id]", roomId);
					WebCrawling roomListCrawling = new WebCrawling(HttpMethod.GET, null, roomListUrl, 600000, 600000,
							600000);
					WebCrawlingTask roomListCrawlTask = this.deriveNewTask("", true, roomListCrawling,
							nextCallBack);
					roomListCrawlTask.getPathContext().setAttr("currCount", currCount);
					roomListCrawlTask.getPathContext().setAttr("hxArea", hxArea);
					roomListCrawlTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, currPageNo);
					roomListCrawlTask.getPathContext().setAttr("projName", proj.get("projName"));
					roomListCrawlTask.getPathContext().setAttr("buildName", buildName);
					this.executor.execute(roomListCrawlTask);
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
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
}
