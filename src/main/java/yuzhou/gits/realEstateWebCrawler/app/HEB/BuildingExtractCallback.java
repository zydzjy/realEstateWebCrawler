/*package yuzhou.gits.realEstateWebCrawler.app.HEB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.app.IntoMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.app.MongoDBUtil;
public class BuildingExtractCallback implements WebResourceCrawlingCallback {
	final static Pattern p = Pattern.compile("\"(.*)\"");
	protected String roomCollectionName = "heb_room20180702";
	protected IntoMongoDBCallback roomCallback;
	protected String dataSetSuffix = "";

	//public final static DateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

	public BuildingExtractCallback(){
		//this.dataSetSuffix = df_yyyyMMdd.format(new Date());
	}
	
	@Override
	public void doCallback(WebCrawlingTask task) throws Exception {
		Map<String,Object> taskContext = task.getTaskContext();
		Object respStr = task.getCrawling().getResponse();
		this.extracting(respStr,taskContext);
	}

	@Override
	public void init(Object... args) throws Exception {
		this.roomCallback = new IntoMongoDBCallback();//(IntoMongoDBCallback)args[0];
		this.roomCallback.init(MongoDBUtil.getClient(),"test");
		this.roomCallback.addPropsCollection(this.roomCollectionName+this.dataSetSuffix,
				100);
	}
	
	@Override
	public void clean(Object...args) throws Exception {
		this.roomCallback.flush();
	}

	public void extracting(Object... args) throws Exception {
		String respStr = (String)args[0];
		Map<String,Object> taskContext = (Map<String,Object>)args[1];
		Document baseE = Jsoup.parse(respStr);
		String buildingName = (String) taskContext.get("buildingName");
		int currPageNo = (int)taskContext.get(Constants.PAGE_CURRENT_NO);
		int currCount = (int)taskContext.get("currCount");
		String crawlerId = "";//(String)_extractContext.get("crawlerId");
		Map<String,String> projPropsMap = (Map<String,String>)taskContext.get("projPropsMap");
		
		List<String> units = new ArrayList<String>();
		if (baseE != null) {
			Elements floorsE = baseE.select(HEBConfig.floorsSelector);
			Iterator<Element> floorsIt = floorsE.iterator();
			if (floorsIt.hasNext()) {// units row
				Element unitsNameR = floorsIt.next();
				Elements unitsNameE = unitsNameR.select(HEBConfig.unitsNameSelector);
				for (int i = 0; i < unitsNameE.size(); i++) {
					units.add(unitsNameE.get(i).text());
				}
			}
			while (floorsIt.hasNext()) {
				try {
					Element floorTr = floorsIt.next();
					Element floorNoE = floorTr.selectFirst(HEBConfig.floorNoESelector);
					Elements roomsPerUnitE = floorTr.select(HEBConfig.roomsPerUnitSelectStr);
					Iterator<Element> roomsPerUnitEIt = roomsPerUnitE.iterator();
					int unit = 0;
					while (roomsPerUnitEIt.hasNext()) {
						try {
							Element roomTE = roomsPerUnitEIt.next();
							Elements roomsE = roomTE.select(HEBConfig.roomsSelectStr);
							Iterator<Element> roomsEIt = roomsE.iterator();
							while (roomsEIt.hasNext()) {
								try {
									Element roomE = roomsEIt.next();
									Map<String,String> roomPropsMap = new HashMap<String,String>();
									
									roomPropsMap.put("buildingName",buildingName);
									roomPropsMap.put("unitName",units.get(unit));
									roomPropsMap.put("floor",floorNoE.text());
									Matcher m = p.matcher(roomE.attr("onmouseover"));
									m.find();
									String roomInfoStr = m.group(1).trim();
									String[] roomInfos = roomInfoStr.split("#");
									roomPropsMap.put("roomNo",roomInfos[0]);
									roomPropsMap.put("constructionArea",roomInfos[3]);
									roomPropsMap.put("innerArea",roomInfos[4]);
									roomPropsMap.put("shareArea",roomInfos[5]);
									roomPropsMap.put("totalPrice",roomInfos[1]);
									// TODO:fire callback
									Object[] callbackArgs = { roomCollectionName + this.dataSetSuffix, 
											crawlerId, currPageNo,currCount, 
											projPropsMap, roomPropsMap };
									roomCallback.doCallback(callbackArgs);
								} catch (Exception _1) {
									_1.printStackTrace();
								}
							}
							unit++;
						} catch (Exception _2) {
							_2.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} 
	}
 
}*/