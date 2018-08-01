package yuzhou.gits.realEstateWebCrawler.app.KMAN;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class RoomExtractCallback extends DefaultRealEstateCrawlingCallback {

	public RoomExtractCallback(String collectionName,DefaultRealEstateCrawlingCallback nextCallback) {
		super(collectionName,nextCallback);
	}

	@Override
	public void extracting(String respStr) throws Exception {
		int currCount = (Integer)this.crawlTask.getPathContext().getAttr("currCount");
		Map<String,String> projMap =(Map<String,String>) this.crawlTask.getPathContext().getAttr("projMap");
		Map<String,String> buildMap =(Map<String,String>) this.crawlTask.getPathContext().getAttr("buildMap");
		
		Document roomDoc = Jsoup.parse(respStr);
		Elements roomTrE = roomDoc.select(KMANConfig.roomListSelector);
		Iterator<Element> roomTrIt = roomTrE.iterator();
		while (roomTrIt.hasNext()) {
			Element roomE = roomTrIt.next();
			Map<String,String> roomMap=new HashMap<String,String>();
			this.extractor.extractDataByCssSelectors(KMANConfig.roomDetailSelectorMap,roomE, roomMap);
			mapExtendMap(projMap,roomMap);
			mapExtendMap(buildMap,roomMap);
			roomMap.put("cityName", "昆明");
			Object[] callbackArgs = { collectionNames.get(0), crawlerId, currPageNo, currCount,
					roomMap };
			mongoDBCallback.doCallback(callbackArgs);
		}
	
	}

	
	public void mapExtendMap(Map<String, String> fatherMap, Map<String, String> sonMap) {
		Iterator<Map.Entry<String, String>> it = fatherMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			String propName = entry.getKey();
			String propVal = entry.getValue();
			sonMap.put(propName, propVal);
		}
	}
}