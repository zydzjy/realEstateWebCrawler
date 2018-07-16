package yuzhou.gits.realEstateWebCrawler.app.HZDYW;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.DefaultCrawlerStub;
import yuzhou.gits.crawler.crawl.DefaultCrawlingCallback;
import yuzhou.gits.crawler.crawl.DefaultCrawlingTask;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawlingExecutor;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.crawler.dataExtractor.DataExtractor;
import yuzhou.gits.crawler.dataExtractor.DataExtractorJsoupImpl;
import yuzhou.gits.crawler.http.URLEncoder;
import yuzhou.gits.realEstateWebCrawler.SingletonMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.app.IntoMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;

public class ProjectListExtractCallback extends DefaultCrawlingCallback {
	protected BuildingListExtractCallback buildingListExtractCallback;
	protected IntoMongoDBCallback mongoDBCallback;
	protected String projCollectionName = "hzdyw_project20180709";
	public ProjectListExtractCallback() {
		this.buildingListExtractCallback = new BuildingListExtractCallback();
		this.mongoDBCallback = SingletonMongoDBCallback.singleton;
	}
	
	@Override
	public void _doCallback(WebCrawlingTask task) throws Exception {
		String resp = (String)(task.getCrawling().getResponse());
		Document baseDoc = Jsoup.parse((String) resp);
		int currPageNo = (Integer)this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);
		
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1; 
				try{
					if (baseDoc != null) {
						Elements projListE = baseDoc.select(HZDYWConfig.projListDataSelector);
						Iterator<Element> projListIt = projListE.iterator();
						while (projListIt.hasNext()) {
							try{
								Element projListItemE = projListIt.next();
								/*if(currPageNo == 22 && currCount < 16){
									currCount++;
									continue;
								}*/
								Map<String,String> projPropsMap = new HashMap<String,String>();
								this.extractor.extractDataByCssSelectors(HZDYWConfig.nowProjListDataItemsSelectorMap, 
										projListItemE, projPropsMap);
								
								//save building first,then project for get location
								System.out.println(
										projPropsMap.get("projName") + "(" + crawlerId + "," + currPageNo + "," + (currCount) + ")");
								// extract building
								String buildingURL = HZDYWConfig.siteDomain+"web/"+
										projListItemE.selectFirst(HZDYWConfig.buildingURLSelector).attr("href");
								buildingURL = URLEncoder.encode(buildingURL, "gbk");
								WebCrawling buildingsCrawling = new WebCrawling(HttpMethod.GET, null,
										buildingURL, 600000, 600000, 600000);
								WebCrawlingTask buildingsCrawlTask = this.deriveNewTask("", true, 
										buildingsCrawling, buildingListExtractCallback);
								buildingsCrawlTask.getPathContext().setAttr("project", projPropsMap);
								buildingsCrawlTask.getPathContext().setAttr("currCount", currCount);
								buildingsCrawlTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, 
										currPageNo);
								
								executor.execute(buildingsCrawlTask);
								// TODO:first extract project detail for project location
								Object[] callbackArgs = { this.projCollectionName,
										crawlerId, currPageNo,currCount, projPropsMap };
								projPropsMap.put("projLocation", (String)buildingsCrawlTask.getPathContext()
										.getAttr("projLocation"));
								this.mongoDBCallback.doCallback(callbackArgs);
								
								currCount++;
							}catch(Exception _1){_1.printStackTrace();}
						}
					}
				}catch(Exception _2){_2.printStackTrace();}
	}

	@Override
	public void init(Object... args) throws Exception {
		 this.mongoDBCallback.addPropsCollection(this.projCollectionName,10);
		 this.buildingListExtractCallback.init(args);
	}

	@Override
	public void clean(Object...args) throws Exception {
		this.mongoDBCallback.flush(this.projCollectionName);
		this.buildingListExtractCallback.clean(args);
	}
}