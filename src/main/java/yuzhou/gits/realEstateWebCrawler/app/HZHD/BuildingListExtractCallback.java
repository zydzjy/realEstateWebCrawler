package yuzhou.gits.realEstateWebCrawler.app.HZHD;

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
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;
public class BuildingListExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected String buildingCollectionName = "hzhd_building20180710";
	protected RoomExtractCallback roomCallback = new RoomExtractCallback();
	public BuildingListExtractCallback(){
	}
	
	@Override
	public void extracting(String respStr) throws Exception {
		 
		Document projDetailDoc = Jsoup.parse((String)respStr);
		int currCount = (Integer) this.crawlTask.getPathContext().getAttr(CURR_COUNT_ATTR);
		Map<String,String> projPropsMap = (Map<String, String>) this.crawlTask.getPathContext().getAttr("project");
		
		Elements buidingListE = projDetailDoc.select(HZHDConfig.buildingListSelector);
		Iterator<Element> buildingListEIt = buidingListE.iterator();
		Map<String,String> buildingPropsMap = new HashMap<String,String>();
		buildingPropsMap.put("presaleLicense", projPropsMap.get("licenseNo"));
		buildingPropsMap.put("projName", projPropsMap.get("projName"));
		buildingPropsMap.put("projLocation", projPropsMap.get("projLocation"));
		buildingPropsMap.put("developer", projPropsMap.get("developer"));
		   
		this.extractor.extractDataByCssSelectors(HZHDConfig.projDetailSelectorMap, 
				projDetailDoc, buildingPropsMap);
		if (buidingListE.size() > 0) {
			while (buildingListEIt.hasNext()) {
				Element buildingE = buildingListEIt.next();
				Map<String,String> _buildingPropsMap = new HashMap<String,String>();
				this.extractor.extractDataByCssSelectors(HZHDConfig.buildingListDataItemsSelectorMap, 
						buildingE, _buildingPropsMap);
				//TODO:fire callback
				Object[] callbackArgs = { this.buildingCollectionName,
						crawlerId, currPageNo,currCount,buildingPropsMap, _buildingPropsMap };
				this.mongoDBCallback.doCallback(callbackArgs); 
				
				//extracting rooms
				String roomListPageURL = 
						HZHDConfig.siteDomain+"web/"+buildingE.selectFirst(HZHDConfig.roomListPageURLSelector).attr("href");
				WebCrawling buildingsCrawling = new WebCrawling(HttpMethod.GET, null,
						roomListPageURL, 600000, 600000, 600000);
				WebCrawlingTask roomCrawlTask = this.deriveNewTask("", false, 
						buildingsCrawling,this.roomCallback);
				roomCrawlTask.getPathContext().setAttr("building", _buildingPropsMap);
				this.executor.execute(roomCrawlTask);
			}
		}else{
			//TODO:fire callback
			//System.out.println(_b);
			Object[] callbackArgs = { this.buildingCollectionName,
					crawlerId, currPageNo,currCount,buildingPropsMap };
			this.mongoDBCallback.doCallback(callbackArgs); 
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		this.mongoDBCallback.addPropsCollection(this.buildingCollectionName,10);
		this.roomCallback.init(args);
	}
	
	@Override
	public void clean(Object...args) throws Exception {
		 this.mongoDBCallback.flush(this.buildingCollectionName);
		 this.roomCallback.clean(args);
	}
}