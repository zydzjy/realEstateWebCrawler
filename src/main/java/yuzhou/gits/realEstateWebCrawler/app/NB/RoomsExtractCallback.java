package yuzhou.gits.realEstateWebCrawler.app.NB;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class RoomsExtractCallback extends DefaultRealEstateCrawlingCallback {
	private String roomCollectionName = "nb_room";
	private String currSaleState = "";
	@Override
	protected void extracting(String respStr) throws Exception {
		Document roomsPageDoc = Jsoup.parse(respStr);
		String buildingName = (String)this.crawlTask.getPathContext().getAttr("buildingName");
		Map<String,String> projPropsMap = (Map<String,String>)this.crawlTask.getPathContext().getAttr("project");
		Map<String,String> projDetailPropsMap = (Map<String,String>)this.crawlTask.getPathContext().getAttr("projectDetail");
		Elements roomList = roomsPageDoc.select("table[id^=room]");
		Iterator<Element> roomListIt = roomList.iterator();
		while(roomListIt.hasNext()){
			Element roomE = roomListIt.next();
			Element roomStyle = roomE.selectFirst("tbody>tr table");
			String saleState = "";
			if(roomStyle != null) {
				saleState = roomE.selectFirst("tbody>tr table").attr("style");
			}
			this.currSaleState = saleState;
			String roomDetailURL = NBConfig.siteDomain+
					"/openRoomData.aspx?roomId="+roomE.attr("id").replace("room", "");
			this.executor.execute(this.deriveNewTask("", false, roomDetailURL, new DefaultRealEstateCrawlingCallback(){

				@Override
				protected void extracting(String respStr) throws Exception {
					Map<String,String> roomPropsMap = new HashMap<String,String>();
					this.extractor.extractDataByCssSelectors(NBConfig.roomDetailSelectorMap, 
							Jsoup.parse(respStr), roomPropsMap);
					roomPropsMap.put("saleState", RoomsExtractCallback.this.currSaleState);
					roomPropsMap.put("buildingName", buildingName);
					//TODO:do callback
					Object[] callbackArgs = { roomCollectionName,"", currPageNo,1, projPropsMap,
							projDetailPropsMap,roomPropsMap};
					this.mongoDBCallback.doCallback(callbackArgs);
				}
			}));
		}
	}
	@Override
	public void init(Object... args) throws Exception {
		this.datasetSuffix = (String)args[0];
		roomCollectionName = this.roomCollectionName + this.datasetSuffix;
		this.mongoDBCallback.addPropsCollection(this.roomCollectionName, 10);
	}
	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.roomCollectionName);
	}
}
