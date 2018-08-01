package yuzhou.gits.realEstateWebCrawler.app.WZ;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;



public class BuildingExtractCallback extends DefaultRealEstateCrawlingCallback {
	String roomCollectionName = "wz_room";
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
	
	protected void extracting(String respStr) throws Exception {
		Document projDetailPageDoc = Jsoup.parse(respStr);
		int currCount = (int) this.crawlTask.getPathContext().getAttr("currCount");
		Map<String,String> project = (Map<String, String>) 
				this.crawlTask.getPathContext().getAttr("project");
		Map<String,String> projDetailPropsMap = new HashMap<String,String>();
		this.extractor.extractDataByCssSelectors(WZConfig.projDetailSelectorMap, 
				projDetailPageDoc, projDetailPropsMap);
		
		Elements bldListE = projDetailPageDoc.select(WZConfig.tdBldListSelector);
		Iterator<Element> bldListEIt = bldListE.iterator();
		while(bldListEIt.hasNext()){
			Element bldEle = bldListEIt.next();
			String bldName=bldEle.ownText();
			String bldId=bldEle.attr("id").replace("Bd", "Bt");
			Element bldE = projDetailPageDoc.selectFirst("[id='"+bldId+"']");
			Elements floorsE = bldE.select("tr");
			Iterator<Element> floorsEIt = floorsE.iterator();
			while(floorsEIt.hasNext()){
				Element floorE = floorsEIt.next();
				Element floorNameE = floorE.selectFirst("tr>td.floor>a");
				String floorName = "";
				if(floorNameE != null){
					floorName = floorNameE.ownText();
				}
				Elements roomsE = floorE.select("td:nth-child(n+2):nth-last-child(n+1)>a");
				Iterator<Element> roomsEIt = roomsE.iterator();
				while(roomsEIt.hasNext()){
					Element roomE = roomsEIt.next();
					String saleState = roomE.attr("class");
					String roomId = roomE.attr("id").substring(1);
					Map<String,String> roomPropsMap = new HashMap<String,String>();
					roomPropsMap.put("floorNo", floorName);
					roomPropsMap.put("bldName", bldName);
					roomPropsMap.put("saleState", saleState);
					if(roomId == null || "".equalsIgnoreCase(roomId)){
						//fill dummy data
						this.extractor.fillPropsNullVal(WZConfig.roomDetailSelectorMap, roomPropsMap);
					}else{
						String roomDetailPageURL = (WZConfig.siteDomain+WZConfig.roomDetailPageURL)
								.replace("[houseID]", roomId);
						this.executor.execute(this.deriveNewTask("", false, roomDetailPageURL, new DefaultRealEstateCrawlingCallback(){
							@Override
							protected void extracting(String respStr) throws Exception {
								Element roomDetailE = Jsoup.parse(respStr);
								this.extractor.extractDataByCssSelectors(WZConfig.roomDetailSelectorMap, 
										roomDetailE, roomPropsMap);
								//TODO: do callback
								Object[] callbackArgs = { roomCollectionName,"", currPageNo,currCount, 
										project,projDetailPropsMap,roomPropsMap};
								this.mongoDBCallback.doCallback(callbackArgs);
							}
						}));
					}
				}
			}
		}
	}
	/*@Override
	protected void extracting(String respStr) throws Exception {
		Document projDetailPageDoc = Jsoup.parse(respStr);
		Map<String,String> project = (Map<String, String>) 
				this.crawlTask.getPathContext().getAttr("project");
		Map<String,String> projDetailPropsMap = new HashMap<String,String>();
		this.extractor.extractDataByCssSelectors(WZConfig.projDetailSelectorMap, 
				projDetailPageDoc, projDetailPropsMap);
		
		Elements bldListE = projDetailPageDoc.select(WZConfig.tdBldListSelector);
		Iterator<Element> bldListEIt = bldListE.iterator();
		String[] bldNames = new String[bldListE.size()];
		int bldId=0;
		while(bldListEIt.hasNext()){
			Element bldE = bldListEIt.next();
			bldNames[bldId++] = bldE.ownText();
		}
		Elements tdRoomsBldE = projDetailPageDoc.select(WZConfig.tdRoomsBldSelector);
		Iterator<Element> tdRoomsBldEIt = tdRoomsBldE.iterator();
		bldId = 0;
		while(tdRoomsBldEIt.hasNext()){
			Element bldE = tdRoomsBldEIt.next();
			String bldName = bldNames[bldId++];
			Elements floorsE = bldE.select("tr");
			Iterator<Element> floorsEIt = floorsE.iterator();
			while(floorsEIt.hasNext()){
				Element floorE = floorsEIt.next();
				Element floorNameE = floorE.selectFirst("tr>td.floor>a");
				String floorName = "";
				if(floorNameE != null){
					floorName = floorNameE.ownText();
				}
				Elements roomsE = floorE.select("td:nth-child(n+2):nth-last-child(n+1)>a");
				Iterator<Element> roomsEIt = roomsE.iterator();
				while(roomsEIt.hasNext()){
					Element roomE = roomsEIt.next();
					String saleState = roomE.attr("class");
					String roomId = roomE.attr("id").substring(1);
					Map<String,String> roomPropsMap = new HashMap<String,String>();
					roomPropsMap.put("floorNo", floorName);
					roomPropsMap.put("bldName", bldName);
					roomPropsMap.put("saleState", saleState);
					if(roomId == null || "".equalsIgnoreCase(roomId)){
						//fill dummy data
						this.extractor.fillPropsNullVal(WZConfig.roomDetailSelectorMap, roomPropsMap);
					}else{
						String roomDetailPageURL = (WZConfig.siteDomain+WZConfig.roomDetailPageURL)
								.replace("[houseID]", roomId);
						this.executor.execute(this.deriveNewTask("", false, roomDetailPageURL, new DefaultRealEstateCrawlingCallback(){
							@Override
							protected void extracting(String respStr) throws Exception {
								Element roomDetailE = Jsoup.parse(respStr);
								this.extractor.extractDataByCssSelectors(WZConfig.roomDetailSelectorMap, 
										roomDetailE, roomPropsMap);
								//TODO: do callback
								Object[] callbackArgs = { roomCollectionName,"", currPageNo,"", 
										project,projDetailPropsMap,roomPropsMap};
								this.mongoDBCallback.doCallback(callbackArgs);
							}
						}));
					}
				}
			}
		}
	}*/
}
