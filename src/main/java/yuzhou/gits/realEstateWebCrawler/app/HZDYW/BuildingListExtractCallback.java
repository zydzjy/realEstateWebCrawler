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
import yuzhou.gits.realEstateWebCrawler.SingletonMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.app.IntoMongoDBCallback;
public class BuildingListExtractCallback extends DefaultCrawlingCallback {
	protected IntoMongoDBCallback mongoDBCallback;
	protected String buildingCollectionName = "hzdyw_building20180709";
	RoomExtractCallback roomExtractCallback = new RoomExtractCallback();

	public BuildingListExtractCallback(){
		this.mongoDBCallback = SingletonMongoDBCallback.singleton;
	}
	
	@Override
	public void _doCallback(WebCrawlingTask task) throws Exception {
		Object respStr = task.getCrawling().getResponse();
		Document projDetailDoc = Jsoup.parse((String)respStr);
		int currPageNo = (Integer)this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);
		int currCount = (Integer)this.crawlTask.getPathContext().getAttr("currCount");
		
		Elements buidingListE = projDetailDoc.select(HZDYWConfig.buildingListSelector);
		Iterator<Element> buildingListEIt = buidingListE.iterator();
		Map<String,String> projDetailPropsMap = new HashMap<String,String>();
		Map<String,String> project = (Map<String, String>) task.getPathContext().getAttr("project");
		this.extractor.extractDataByCssSelectors(HZDYWConfig.projDetailSelectorMap, 
				projDetailDoc, projDetailPropsMap);
		projDetailPropsMap.put("projName", project.get("projName"));
		String projLocation = projDetailDoc.selectFirst("#Searchform > div > table > tbody > tr:nth-child(2) > td").text();
		task.getPathContext().setAttr("projLocation", projLocation);
		projDetailPropsMap.put("preSaleLicense", project.get("licenseNo"));
		projDetailPropsMap.put("projLocation",projLocation);
		projDetailPropsMap.put("developer",project.get("developer"));
		if (buidingListE.size() > 0) {
			while (buildingListEIt.hasNext()) {
				Element buildingE = buildingListEIt.next();
				
				Map<String,String> buildingPropsMap = new HashMap<String,String>();
				this.extractor.extractDataByCssSelectors(HZDYWConfig.buildingListDataItemsSelectorMap, 
						buildingE, buildingPropsMap);
				Object[] callbackArgs = { this.buildingCollectionName,
						"", currPageNo,currCount, projDetailPropsMap,buildingPropsMap };
				this.mongoDBCallback.doCallback(callbackArgs);
				String roomListPageURL = HZDYWConfig.siteDomain+"web/"+
						buildingE.selectFirst(HZDYWConfig.roomListPageURLSelector).attr("href");
				WebCrawling roomCrawling = new WebCrawling(HttpMethod.GET, null,
						roomListPageURL, 600000, 600000, 600000);
				WebCrawlingTask roomCrawlTask = this.deriveNewTask("", false, 
						roomCrawling, roomExtractCallback);
				roomCrawlTask.getPathContext().setAttr("projDetail", projDetailPropsMap);
				roomCrawlTask.getPathContext().setAttr("building", buildingPropsMap);
				executor.execute(roomCrawlTask); 
			}
		}else{
			Object[] callbackArgs = { this.buildingCollectionName,
					"", currPageNo,currCount, projDetailPropsMap };
			this.mongoDBCallback.doCallback(callbackArgs);
			
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		this.mongoDBCallback.addPropsCollection(this.buildingCollectionName,60);
		this.roomExtractCallback.init(args);
	}
	
	@Override
	public void clean(Object...args) throws Exception {
		 this.mongoDBCallback.flush(this.buildingCollectionName);
		 this.roomExtractCallback.clean(args);
	}
}