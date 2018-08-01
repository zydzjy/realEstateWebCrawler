package yuzhou.gits.realEstateWebCrawler.app.NN;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class BuildingExtractCallback extends DefaultRealEstateCrawlingCallback {
	static final Pattern projDetailUrlP = Pattern.compile("\'(.*)\'");
	
	@Override
	protected void extracting(String respStr) throws Exception {
		Document projDetailPageDoc = Jsoup.parse(respStr);
		Map<String,String> project = (Map<String,String>)this.crawlTask.getPathContext()
				.getAttr("project");
		Elements buildingListE = projDetailPageDoc
				.select(NNConfig.buildingListSelector);
		Iterator<Element> buildingListEIt = 
				buildingListE.iterator();
		while(buildingListEIt.hasNext()){
			Element e = buildingListEIt.next();
			Map<String,String> buildPropValsMap =
					new HashMap<String,String>();
			this.extractor.extractDataByCssSelectors
				(NNConfig.buildingListDataSelectorMap, 
						e, buildPropValsMap);
			String roomsDetailPageUrlStr = e.attr("onclick");
			Matcher m = projDetailUrlP.matcher(roomsDetailPageUrlStr);
			m.find();
			String roomsDetailPageUrl = m.group(1).trim();
			WebCrawlingTask roomsDetailTask = this.deriveNewTask("", false, roomsDetailPageUrl, new DefaultRealEstateCrawlingCallback(){
				@Override
				protected void extracting(String respStr) throws Exception {
					Document baseDoc = Jsoup.parse(respStr);
					Elements roomListE = baseDoc.select(NNConfig.roomListSelector);
					Iterator<Element> roomListEIt = roomListE.iterator();
					while(roomListEIt.hasNext()){
						try{
							Element e = roomListEIt.next();
							Elements roomInfosE = e.select(NNConfig.roomInfoStrSelect);
							Iterator<Element> roomInfosEIt = roomInfosE.iterator();
							while(roomInfosEIt.hasNext()){
								Element roomInfoE = roomInfosEIt.next();
								String roomInfoStr = roomInfoE.attr("title");
								String bkColor = roomInfoE.attr("style");
								Map<String,String> roomPropsMap = new HashMap<String,String>();
								roomPropsMap.put("saleState",bkColor);
								String floorNo = e.selectFirst("th").ownText();
								roomPropsMap.put("floorNo",floorNo);
								this.extractor.extractDataByRegExps(NNConfig.roomDetailRegExprs, 
										roomInfoStr, roomPropsMap);
								//TODO: do callback
								Object[] callbackArgs = { roomCollectionName, "", currPageNo, 
										"", project,buildPropValsMap,roomPropsMap };
								this.mongoDBCallback.doCallback(callbackArgs);
							}
						}catch(Exception e){e.printStackTrace();}
					}}
			});
			this.executor.execute(roomsDetailTask);
		}
	}
	
	protected String roomCollectionName = "nn_room";
	@Override
	public void init(Object... args) throws Exception {
		super.init(args);
		this.roomCollectionName = this.roomCollectionName + this.datasetSuffix;
		this.mongoDBCallback.addPropsCollection(this.roomCollectionName, 100);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.roomCollectionName);
	}
}
