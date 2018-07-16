package yuzhou.gits.realEstateWebCrawler.app.CS;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class BuildingExtractCallback extends DefaultRealEstateCrawlingCallback {
	static Pattern buildingPageInfoP = Pattern.compile("/(\\d+)页");
	String roomCollectionName = "cs_room";
	@Override
	public void init(Object... args) throws Exception {
		super.init(args);
		this.roomCollectionName = this.roomCollectionName + this.datasetSuffix;
		this.mongoDBCallback.addPropsCollection(this.roomCollectionName,60);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.roomCollectionName);
	}

	static final String buildingPageInfoSelector = "#AspNetPager1 > table > tbody > tr > td:nth-child(1)";

	@Override
	protected void extracting(String respStr) throws Exception {
		Document buildDetailPageDoc = Jsoup.parse(respStr);
		Map<String,String> project = (Map<String, String>) this.crawlTask.getPathContext().getAttr("project");
		
		Elements buildListE = buildDetailPageDoc.select(CSConfig.buildListDataSelector);
		Iterator<Element> roomListEIt = buildListE.iterator();
		while (roomListEIt.hasNext()) {
			try {
				Element buildE = roomListEIt.next();
				Map<String, String> buildPropsMap = new HashMap<String, String>();
				this.extractor.extractDataByCssSelectors(CSConfig.buildSelectorMap, buildE, buildPropsMap);
				String buildDetailPageURL = CSConfig.siteDomain + "NewHouse/"
						+ buildE.selectFirst(CSConfig.buildDetailPageURLSelector).attr("href");
				WebCrawlingTask buildingDetailTask = this.deriveNewTask("", false, buildDetailPageURL,
						new DefaultRealEstateCrawlingCallback(){
					@Override
					protected void extracting(String respStr) throws Exception {
						Map<String,String> mainBuildPropsMap = (Map<String, String>) 
								this.crawlTask.getPathContext().getAttr("mainBuildPropsMap");
						Document buildPageDetailDoc = Jsoup.parse(respStr);
						Elements roomEs = buildPageDetailDoc.select(CSConfig.roomsSelecotr);
						Iterator<Element> roomEsIt = roomEs.iterator();
						while(roomEsIt.hasNext()){
							try{
								Element roomE = roomEsIt.next();
								String roomColor = roomE.attr("class");
								String roomInfoStr = roomE.selectFirst(
										CSConfig.roomItemsSelector).attr("title");
								Map<String,String> roomPropsMap = new HashMap<String,String>();
								this.extractor.extractDataByRegExps(
										CSConfig.roomDetailRegExprs, 
										roomInfoStr, 
										roomPropsMap);
								roomPropsMap.put("roomColor", roomColor);
								//TODO:do callback
								//System.out.println(roomPropsMap.get("roomNo"));
								Object[] callbackArgs = { roomCollectionName,"", 
										currPageNo,1, mainBuildPropsMap,buildPropsMap, roomPropsMap};
								this.mongoDBCallback.doCallback(callbackArgs);
							}catch(Exception e){e.printStackTrace();}
						}
					}
				});
				buildPropsMap.put("projName", project.get("projName"));
				this.executor.execute(buildingDetailTask);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}