/*package yuzhou.gits.realEstateWebCrawler.app.HEB;

import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.DefaultCrawlerStub;
import yuzhou.gits.crawler.crawl.DefaultCrawlingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.crawl.WebCrawlingExecutor;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebCrawlingTaskFactory;
import yuzhou.gits.crawler.dataExtractor.Constants;


public class BuildingListExtractCallback extends DefaultCrawlingCallbackimplements WebResourceCrawlingCallback {
	BuildingExtractCallback buildingExtractCallback;
	public BuildingListExtractCallback(){
		this.buildingExtractCallback = new BuildingExtractCallback();
	}
	
	@Override
	public void _doCallback(WebCrawlingTask task) throws Exception {
		
		String resp = (String)(task.getCrawling().getResponse());
		Document detailPageDoc = Jsoup.parse((String)resp);
		Elements buildingSelE = detailPageDoc.select("#buildnum option");
		Iterator<Element> buildingIt = buildingSelE.iterator();
		if (buildingIt.hasNext()) {
			// first building
			//buildingIt.next();
			//taskContext.put("buildingName", buildingName);
			// next buildings
			while (buildingIt.hasNext()) {
				try {
					Element e = buildingIt.next();
					String buildingId = e.attr("value");
					String buildingName = e.ownText();
					Map<String, String> postData = new HashMap<String, String>();
					postData.put("buildnum", buildingId);
					String prirecord_id = detailPageDoc.selectFirst("#form > input[type=\"hidden\"]").attr("value");
					postData.put("prirecord_id", prirecord_id);
					String prirecord_id = detailPageDoc.selectFirst("#form > input[type=\"hidden\"]").attr("value");
					String[] postData =  {"buildnum", buildingId,"prirecord_id", prirecord_id};
					WebCrawling buildingsDetailCrawl = new WebCrawling(HttpMethod.POST,
							null,HEBConfig.buildingBaseURL,1000000,1000000,1000000);
					buildingsDetailCrawl.setPostFormData(postData);
					WebCrawlingTask buildingsDetailCrawlTask = this.deriveNewTask("", false, 
									buildingsDetailCrawl, buildingExtractCallback);		
					buildingsDetailCrawlTask.getPathContext().setAttr("buildingName", buildingName);

					WebCrawling buildingsDetailCrawl = new WebCrawling(HttpMethod.POST,
							null,HEBConfig.buildingBaseURL,1000000,1000000,1000000);
					buildingsDetailCrawl.setPostFormData(postData);
					DefaultCrawlingTask buildingsDetailCrawlTask = new DefaultCrawlingTask("heb building detail task", 
							buildingsDetailCrawl);
					Map<String,Object> buildingsDetailTaskContext = new HashMap<String,Object>();
					buildingsDetailTaskContext.put("projPropsMap", taskContext.get("projPropsMap"));
					buildingsDetailTaskContext.put("currCount", taskContext.get("currCount"));
					buildingsDetailTaskContext.put(Constants.PAGE_CURRENT_NO, taskContext.get(Constants.PAGE_CURRENT_NO));
					buildingsDetailTaskContext.put("buildingName", buildingName);
					buildingsDetailCrawlTask.setTaskContext(buildingsDetailTaskContext);
					buildingsDetailCrawlTask.addCallbacks(this.buildingExtractCallback);
					this.executor.execute(buildingsDetailCrawlTask);
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} 
	}
	
	@Override
	public void init(Object... args) throws Exception {
		this.buildingExtractCallback.init(args);
	}
	
	@Override
	public void clean(Object...args) throws Exception {
		this.buildingExtractCallback.clean();
	}
}*/