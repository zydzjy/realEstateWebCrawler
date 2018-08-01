package yuzhou.gits.realEstateWebCrawler.app.JN;

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
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class ProjectListExtractCallback extends DefaultRealEstateCrawlingCallback {

	public ProjectListExtractCallback(String collectionName,DefaultRealEstateCrawlingCallback nextCallback) {
		super(collectionName,nextCallback);
	}

	@Override
	protected void extracting(String respStr) throws Exception {
		Document baseDoc = Jsoup.parse(respStr);
		Elements projListE = baseDoc.select(JNConfig.projectListSelector);
		Iterator<Element> projListIt = projListE.iterator();
		int currCount = 1;
		while (projListIt.hasNext()) {
			final int countCurr=currCount;
			try {
				Element e = projListIt.next();
				Map<String, String> docP = new HashMap<String, String>();
				this.extractor.extractDataByCssSelectors(JNConfig.projectSelectorMap, e, docP);
				docP.put("city", "济南");
				String prjnoId = e.select("tr td:nth-child(2)>a").attr("href");
				
				if (prjnoId.contains("prjno=")) {
					prjnoId = prjnoId.split("prjno=")[1];
				} else {
					continue;
				}
				String projDetailUrl = JNConfig.projectDetailURL.replace("[buildPage]", "").replace("[prjnoId]", prjnoId);
				WebCrawling projDetailCrawling = new WebCrawling(HttpMethod.GET, null, projDetailUrl, 600000,
						600000, 600000);
				WebCrawlingTask projectDetailCrawlTask = this.deriveNewTask("", true, projDetailCrawling,new WebResourceCrawlingCallback(){
					public void doCallback(WebCrawlingTask task) throws Exception {
						Document detailDoc = Jsoup.parse((String) (task.getCrawling().getResponse()));
						Element buildFirstE = detailDoc.selectFirst(JNConfig.buildFirstSelectorMap);
						Map<String, String> buildFirstMap = new HashMap<String, String>();
						extractor.extractDataByCssSelectors(JNConfig.firstBuildSelectorMap, buildFirstE,
								buildFirstMap);

						System.out.println(buildFirstMap.get("projName") + "(" + crawlerId + "," + currPageNo + "," + (countCurr) + ")");
						
						Map<String, Object> projMap = new HashMap<String, Object>();
						mapExtendMap(docP,projMap);
						mapExtendMap(buildFirstMap,projMap);
						Object[] callbackArgs = { collectionNames.get(0), crawlerId, currPageNo, countCurr,
								projMap };
						mongoDBCallback.doCallback(callbackArgs);
					}

					@Override
					public void init(Object... args) throws Exception {						
					}

					@Override
					public void clean(Object... args) throws Exception {
						// TODO Auto-generated method stub
						
					}
				});
				this.executor.execute(projectDetailCrawlTask);		
				
				WebCrawling buildingsCrawling = new WebCrawling(HttpMethod.GET, null, projDetailUrl, 600000,
						600000, 600000);
				WebCrawlingTask buildingsCrawlTask = this.deriveNewTask("", true, 
						buildingsCrawling, nextCallBack);
				buildingsCrawlTask.getPathContext().setAttr("prjnoId", prjnoId);
				buildingsCrawlTask.getPathContext().setAttr("currCount", currCount);
				buildingsCrawlTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, 
						currPageNo);
				this.executor.execute(buildingsCrawlTask);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			currCount++;
		}
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
