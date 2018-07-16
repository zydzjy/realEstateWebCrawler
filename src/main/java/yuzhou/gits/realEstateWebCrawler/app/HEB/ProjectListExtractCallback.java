/*package yuzhou.gits.realEstateWebCrawler.app.HEB;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.DefaultCrawlerStub;
import yuzhou.gits.crawler.crawl.DefaultCrawlingTask;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.crawl.WebCrawlingExecutor;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebCrawlingTaskFactory;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.crawler.dataExtractor.DataExtractor;
import yuzhou.gits.crawler.dataExtractor.DataExtractorJsoupImpl;

public class ProjectListExtractCallback implements WebResourceCrawlingCallback {
	BuildingListExtractCallback buildingListExtractCallback;
	DataExtractor extractor = new DataExtractorJsoupImpl();
	public ProjectListExtractCallback() {
		this.buildingListExtractCallback = new BuildingListExtractCallback();
	}
	
	int currPageNo = 0;
	@Override
	public void doCallback(WebCrawlingTask task) throws Exception {
		String resp = (String)(task.getCrawling().getResponse());
		Map<String, Object> taskContext = task.getTaskContext();
		DefaultCrawlerStub stub = 
				(DefaultCrawlerStub) taskContext.get(Constants.CRAWLER_STUB);
		WebCrawlingExecutor executor = stub.getCrawlingExecutor();
		WebCrawlingTaskFactory factory = stub.getTaskFactory();
		
		Document baseDoc = Jsoup.parse((String) resp);
		this.currPageNo = (Integer) taskContext.get(Constants.PAGE_CURRENT_NO);
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;
		Elements projListE = baseDoc.select(HEBConfig.projectListSelector);
		Iterator<Element> projListIt = projListE.iterator();

		while (projListIt.hasNext()) {
			try {
				Element projTr = projListIt.next();
				Map<String, String> projPropsMap = new HashMap<String, String>();
				extractor.extractDataByCssSelectors(HEBConfig.projListDataSelector, projTr, projPropsMap);
				_extractContext.put("projPropsMap", projPropsMap);
				_extractContext.put("currCount", currCount);
				if(currCount <= 8) {currCount++;continue; }
				else{
					System.out.println(
							projPropsMap.get("projName") + "(" + crawlerId + "," + currPageNo + "," + (currCount) + ")");
				}
				
				if (projTr.selectFirst("td:eq(5)>a") != null) {
					String _subLinkUrl = HEBConfig.siteDomain + projTr.selectFirst("td:eq(5)>a").attr("href");
					String qryStr = _subLinkUrl.substring(_subLinkUrl.indexOf("?")).replace("id=", "prirecord_id=");
					// extract buildings
					WebCrawling buildingsCrawling = new WebCrawling(HttpMethod.GET, null,
							HEBConfig.projectDetailURL + qryStr, 600000, 600000, 600000);
					WebCrawlingTask buildingsCrawlTask = 
							factory.createWebCrawlingTask("", true, 
									buildingsCrawling, buildingListExtractCallback);
					buildingsCrawlTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, this.currPageNo);
					buildingsCrawlTask.getPathContext().setAttr("projPropsMap", projPropsMap);
					buildingsCrawlTask.getPathContext().setAttr("currCount", currCount);
					DefaultCrawlingTask buildingsCrawlTask = new DefaultCrawlingTask("heb building task", buildingsCrawling);
					Map<String, Object> builidingsTaskContext = task.getTaskContext();
					builidingsTaskContext.put("projPropsMap", projPropsMap);
					builidingsTaskContext.put("currCount", currCount);
					builidingsTaskContext.put(Constants.PAGE_CURRENT_NO, this.currPageNo);
					buildingsCrawlTask.setTaskContext(builidingsTaskContext);
					buildingsCrawlTask.addCallbacks(this.buildingListExtractCallback);
					executor.execute(buildingsCrawlTask);
					
					currCount++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		this.buildingListExtractCallback.init(args);
	}

	@Override
	public void clean(Object...args) throws Exception {
		this.buildingListExtractCallback.clean();
	}
}*/