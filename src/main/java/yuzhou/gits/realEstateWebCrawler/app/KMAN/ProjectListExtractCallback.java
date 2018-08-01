package yuzhou.gits.realEstateWebCrawler.app.KMAN;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
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
		Elements projListE = baseDoc.select(KMANConfig.projListDataSelector);
		Iterator<Element> projListIt = projListE.iterator();
		while (projListIt.hasNext()) {
			try {
				Element e = projListIt.next();
				Map<String, String> projMap = new HashMap<String, String>();
				this.extractor.extractDataByCssSelectors(KMANConfig.baseSelectorMap, e, projMap);

				System.out.println(projMap.get("projName") + "(" + crawlerId + "," + currPageNo + "," + (currCount) + ")");

				String lastUrl = e.select("tbody >tr:nth-child(1)>td:nth-child(2) > a").attr("href");
				if (StringUtil.isBlank(lastUrl)) {
					continue;
				}
				
				String buildingsUrl = (KMANConfig.contentURL+lastUrl).replace("preMessInfo", "buildList");
				WebCrawling buildingsCrawling = new WebCrawling(HttpMethod.GET, null, buildingsUrl, 600000, 600000,
						600000);
				WebCrawlingTask buildingsCrawlTask = this.deriveNewTask("", true, buildingsCrawling,
						nextCallBack);
				buildingsCrawlTask.getPathContext().setAttr("currCount", currCount);
				buildingsCrawlTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, currPageNo);
				buildingsCrawlTask.getPathContext().setAttr("projMap", projMap);
				this.executor.execute(buildingsCrawlTask);
				currCount++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
