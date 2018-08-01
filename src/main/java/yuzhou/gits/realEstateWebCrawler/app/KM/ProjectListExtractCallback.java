package yuzhou.gits.realEstateWebCrawler.app.KM;

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
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class ProjectListExtractCallback extends DefaultRealEstateCrawlingCallback {

	public ProjectListExtractCallback(String collectionName,DefaultRealEstateCrawlingCallback nextCallback) {
		super(collectionName,nextCallback);
	}

	@Override
	protected void extracting(String respStr) throws Exception {
		Document baseDoc = Jsoup.parse(respStr);
		int currPageNo = (Integer) this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);
		String crawlerId = "";
		int currCount = 1;
		Elements projListE = baseDoc.select(KMConfig.projListDataSelector);
		Iterator<Element> projListIt = projListE.iterator();
		while (projListIt.hasNext()) {
			try {
				Element e = projListIt.next();
				Map<String, String> proj = new HashMap<String, String>();
				this.extractor.extractDataByCssSelectors(KMConfig.baseSelectorMap, e, proj);

				System.out.println(proj.get("projName") + "(" + crawlerId + "," + currPageNo + "," + (currCount) + ")");

				String pid = e.select("tr td:nth-child(1)>a").attr("href");
				if (pid.contains("id=")) {
					pid = pid.split("id=")[1].split("&")[0];
				} else {
					continue;
				}
				Object[] callbackArgs = { collectionNames.get(0), crawlerId, currPageNo, currCount, proj };
				mongoDBCallback.doCallback(callbackArgs);
				String buildingUrl = KMConfig.projectDetailBaseURL.replace("[pid]", pid);
				WebCrawling buildingsCrawling = new WebCrawling(HttpMethod.GET, null, buildingUrl, 600000, 600000,
						600000);
				WebCrawlingTask buildingsCrawlTask = this.deriveNewTask("", true, buildingsCrawling,
						nextCallBack);
				buildingsCrawlTask.getPathContext().setAttr("currCount", currCount);
				buildingsCrawlTask.getPathContext().setAttr("pid", pid);
				buildingsCrawlTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, currPageNo);
				buildingsCrawlTask.getPathContext().setAttr("proj", proj);
				this.executor.execute(buildingsCrawlTask);
				currCount++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
