package yuzhou.gits.realEstateWebCrawler.app.JN;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

	public BuildingListExtractCallback(String collectionName, DefaultRealEstateCrawlingCallback nextCallback) {
		super(collectionName, nextCallback);
	}

	@Override
	protected void extracting(String respStr) throws Exception {
		int currCount = (Integer) this.crawlTask.getPathContext().getAttr("currCount");
		String prjnoId = (String) this.crawlTask.getPathContext().getAttr("prjnoId");
		Document detailDoc = Jsoup.parse(respStr);
		Element buildFirstE = detailDoc.selectFirst(JNConfig.buildFirstSelectorMap);
		Map<String, String> buildFirstMap = new HashMap<String, String>();
		this.extractor.extractDataByCssSelectors(JNConfig.firstBuildSelectorMap, buildFirstE, buildFirstMap);
		Elements buildListE = detailDoc.select(JNConfig.buildSelectorList);
		//第一頁
		extracting(buildFirstMap,buildListE,currCount);
		int buildPage=2;
		while(true){
			String nextBuildUrl = JNConfig.projectDetailURL.replace("[buildPage]", "_" + buildPage).replace("[prjnoId]", prjnoId);
			respStr= getDocumentStr(nextBuildUrl);
			Document nextDetailDoc = Jsoup.parse(respStr);
			Elements nextBuildListE = nextDetailDoc.select(JNConfig.buildSelectorList);
			if (nextBuildListE.size() == 0) {
				break;
			}else{
				extracting(buildFirstMap,nextBuildListE,currCount);
			}
			buildPage++;
		}
	}

	public void extracting(Map<String, String> buildFirstMap,Elements buildListE,int currCount) throws Exception{
		Iterator<Element> buildListIt = buildListE.iterator();
		while (buildListIt.hasNext()) {
			try {
				Element buildE = buildListIt.next();
				Map<String, String> secondBuildMap = new HashMap<String, String>();
				this.extractor.extractDataByCssSelectors(JNConfig.secondBuildSelectorMap, buildE, secondBuildMap);
				String projName = buildFirstMap.get("projName");
				String buildName = buildE.selectFirst("td:nth-child(2)").attr("title");
				System.out.println("访问页数" + (currPageNo) + "小区" + projName + "楼栋" + buildName);
				secondBuildMap.put("buildName", buildName);
				Map<String, Object> buildMap = new HashMap<String, Object>();
				mapExtendMap(secondBuildMap, buildMap);
				mapExtendMap(buildFirstMap, buildMap);
				buildMap.put("city", "济南");
				Object[] callbackArgs = { collectionNames.get(0), crawlerId, currPageNo, currCount, buildMap };
				mongoDBCallback.doCallback(callbackArgs);
				String bnoId = buildE.select("tr td:nth-child(2)>a").attr("href");
				if (bnoId.contains("bno=")) {
					bnoId = bnoId.split("bno=")[1];
				}
				
				String buildDetailUrl = JNConfig.buildDetailURL.replace("[bnoId]", bnoId);
				WebCrawling buildDetailCrawling = new WebCrawling(HttpMethod.GET, null, buildDetailUrl, 600000,
						600000, 600000);
				WebCrawlingTask buildDetailCrawlTask = this.deriveNewTask("", true, 
						buildDetailCrawling, nextCallBack);
				buildDetailCrawlTask.getPathContext().setAttr("projName", projName);
				buildDetailCrawlTask.getPathContext().setAttr("buildName", buildName);
				buildDetailCrawlTask.getPathContext().setAttr("bnoId", bnoId);
				buildDetailCrawlTask.getPathContext().setAttr("currCount", currCount);
				buildDetailCrawlTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, 
						currPageNo);
				this.executor.execute(buildDetailCrawlTask);
				
			} catch (Exception ex) {
				ex.printStackTrace();
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

	public void mapExtendMap(Map<String, String> fatherMap, Map<String, Object> sonMap) {
		Iterator<Map.Entry<String, String>> it = fatherMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			String propName = entry.getKey();
			String propVal = entry.getValue();
			sonMap.put(propName, propVal);
		}
	}
}
