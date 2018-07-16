package yuzhou.gits.realEstateWebCrawler.app.HZBL;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class BuildingListExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected String buildingCollectionName = "hzbl_building20180710";
	RoomExtractCallback roomExtractCallback = new RoomExtractCallback();

	public BuildingListExtractCallback() {}

	@Override
	public void extracting(String respStr) throws Exception {
		Document projDetailDoc = Jsoup.parse((String)respStr);
		int currPageNo = (Integer)this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);
		int currCount = (Integer)this.crawlTask.getPathContext().getAttr(CURR_COUNT_ATTR);
		Map<String,String> project = (Map<String,String>)this.crawlTask.getPathContext()
				.getAttr("project");
		
		Elements buidingListE = projDetailDoc.select(HZBLConfig.buildingListSelector);
		Iterator<Element> buildingListEIt = buidingListE.iterator();
		Map<String,String> projDetailPropsMap = new HashMap<String,String>();
		projDetailPropsMap.put("presaleLicense", project.get("licenseNo"));
		projDetailPropsMap.put("projName", project.get("projName"));
		projDetailPropsMap.put("projLocation", project.get("projLocation"));
		projDetailPropsMap.put("developer", project.get("developer"));
		
		extractor.extractDataByCssSelectors(HZBLConfig.projectDetailSelectorMap, 
				projDetailDoc, projDetailPropsMap);
		if (buidingListE.size() > 0) {
			while (buildingListEIt.hasNext()) {
				Element buildingE = buildingListEIt.next();
				Map<String,String> buildingPropsMap = new HashMap<String,String>();
				extractor.extractDataByCssSelectors(HZBLConfig.buildingListDataItemsSelectorMap, buildingE, 
						buildingPropsMap);
				Object[] callbackArgs = { this.buildingCollectionName,
						"", currPageNo,currCount, projDetailPropsMap,buildingPropsMap };
				this.mongoDBCallback.doCallback(callbackArgs);
				String roomListPageURL = HZBLConfig.siteDomain+"web/"+
				buildingE.selectFirst(HZBLConfig.roomListPageURLSelector).attr("href");
				WebCrawling roomCrawling = new WebCrawling(HttpMethod.GET, null,
						roomListPageURL, 600000, 600000, 600000);
				WebCrawlingTask roomCrawlTask = this.deriveNewTask("", false, 
						roomCrawling,this.roomExtractCallback);
				//roomCrawlTask.getPathContext().setAttr("projectDetail", projDetailPropsMap);
				roomCrawlTask.getPathContext().setAttr("building", buildingPropsMap);
				this.executor.execute(roomCrawlTask);
				
			}
		} else {
			//TODO:fire callback
			Object[] callbackArgs = { this.buildingCollectionName,
					"", currPageNo,currCount, projDetailPropsMap };
			this.mongoDBCallback.doCallback(callbackArgs);
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		this.mongoDBCallback.addPropsCollection(this.buildingCollectionName, 10);
		this.roomExtractCallback.init(args);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.buildingCollectionName);
		this.roomExtractCallback.clean(args);
	}
}