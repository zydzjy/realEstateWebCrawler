package yuzhou.gits.realEstateWebCrawler.app.KM;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class RoomExtractCallback extends DefaultRealEstateCrawlingCallback {

	public RoomExtractCallback(String collectionName,DefaultRealEstateCrawlingCallback nextCallback) {
		super(collectionName,nextCallback);
	}

	@Override
	public void extracting(String respStr) throws Exception {
		int currPageNo = (Integer)this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);
		int currCount = (Integer)this.crawlTask.getPathContext().getAttr("currCount");
		String hxArea = (String)this.crawlTask.getPathContext().getAttr("hxArea");
		String projName=(String) this.crawlTask.getPathContext().getAttr("projName");
		String buildName=(String) this.crawlTask.getPathContext().getAttr("buildName");
		
		Document roomDoc = Jsoup.parse(respStr);
		Elements roomTrE = roomDoc.select(KMConfig.roomDetailSelector);
		Iterator<Element> roomTrIt = roomTrE.iterator();
		while (roomTrIt.hasNext()) {
			Element e = roomTrIt.next();
			Map<String,String> tmpRoom=new HashMap<String,String>();
			this.extractor.extractDataByCssSelectors(KMConfig.roomDetailSelectorMap, e, tmpRoom);
			tmpRoom.put("hxArea",hxArea);
			tmpRoom.put("inArea",tmpRoom.get("inArea").toString().split("�")[0]);
			tmpRoom.put("poolArea",tmpRoom.get("poolArea").toString().split("�")[0]);
			tmpRoom.put("projName",projName);
			tmpRoom.put("buildName",buildName);
			Object[] callbackArgs = { collectionNames.get(0), crawlerId, currPageNo, currCount,
					tmpRoom };
			mongoDBCallback.doCallback(callbackArgs);
		}
	
	}

}