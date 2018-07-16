package yuzhou.gits.realEstateWebCrawler.app.KS;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.PathContext;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;


public class BuildingListExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected RoomExtractCallback roomCallback = new RoomExtractCallback();
	protected String buildingCollectionName = "ks_building20180704";
	
	public BuildingListExtractCallback(){
	}
	
	@Override
	public void extracting(String respStr) throws Exception {
		int currPageNo = (Integer)this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);
		int currCount = (Integer)this.crawlTask.getPathContext().getAttr(CURR_COUNT_ATTR);
		Document buildingListPageDoc = Jsoup.parse(respStr);
	   
		Elements buildingListE = buildingListPageDoc.select(
				KSConfig.buildingListSelector);
		Iterator<Element> buildingListEIt = buildingListE.iterator();
		PathContext pathContext = this.crawlTask.getPathContext();
		while(buildingListEIt.hasNext()){
			try{
				Element e = buildingListEIt.next();
				Map<String,String> buildingPropsMap = new HashMap<String,String>();
				this.extractor.extractDataByCssSelectors(KSConfig.buildingListDataSelector, 
						e, buildingPropsMap);
				
				//TODO:do callback
				String projName = (String) pathContext.getAttr("projName");
				buildingPropsMap.put("projName", projName);
				Object[] callbackArgs = { this.buildingCollectionName, "", currPageNo,currCount,
						buildingPropsMap };
				this.mongoDBCallback.doCallback(callbackArgs);
				
				String buildingDetailPageURL = KSConfig.siteDomain+
						KSConfig.projCtxPath+"/"+
						e.selectFirst(KSConfig.buildingDetailPageURLSelector)
						.attr("href");
				WebCrawling buildingDetailPageCrawling = 
						new WebCrawling(HttpMethod.GET, null, buildingDetailPageURL, 
								100000, 100000, 100000);
				WebCrawlingTask buildingDetailPageCrawlingTask = 
						this.deriveNewTask("", false, buildingDetailPageCrawling, roomCallback);
				buildingDetailPageCrawlingTask.getPathContext().
					setAttr("gaBuildingNo", buildingPropsMap.get("gaBuildingNo"));
				executor.execute(buildingDetailPageCrawlingTask);
				
			}catch(Exception e){e.printStackTrace();}
		}
	}
	
	@Override
	public void init(Object...args) throws Exception {
		 this.roomCallback.init(args);
		this.mongoDBCallback.addPropsCollection(this.buildingCollectionName  , 1);
	}
	
	@Override
	public void clean(Object...args) throws Exception {
		this.mongoDBCallback.flush(this.buildingCollectionName);
		this.roomCallback.clean(args);
	}
}