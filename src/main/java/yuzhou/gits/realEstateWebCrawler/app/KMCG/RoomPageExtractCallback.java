package yuzhou.gits.realEstateWebCrawler.app.KMCG;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class RoomPageExtractCallback extends AbstractPagingCallback {
	public class RoomExtractCallback extends DefaultRealEstateCrawlingCallback {
		private String roomCollectionName = "kmcg_room";
		
		@Override
		public void init(Object... args) throws Exception {
			super.init(args);
			this.roomCollectionName = this.roomCollectionName + this.datasetSuffix;
			this.mongoDBCallback.addPropsCollection(this.roomCollectionName,100);
		}
		
		@Override
		public void clean(Object... args) throws Exception {
			this.mongoDBCallback.flush(roomCollectionName);
		}
		
		@Override
		protected void extracting(String respStr) throws Exception {
			Document baseDoc = Jsoup.parse(respStr);
			Elements roomListE = baseDoc.select(KMCGConfig.roomListDataSelector);
			Iterator<Element> roomListEIt = roomListE.iterator();
			String projName = (String) this.crawlTask.getPathContext().getAttr("projName");
			String buildingName = (String) this.crawlTask.getPathContext().getAttr("buildingName");
			
			while(roomListEIt.hasNext()){
				Element roomE = roomListEIt.next();
				Map<String,String> roomPropsMap = new HashMap<String,String>();
				roomPropsMap.put("projName",projName);
				roomPropsMap.put("buildingName",buildingName);
				this.extractor.extractDataByCssSelectors(KMCGConfig.roomDataSelectorMap, 
						roomE, roomPropsMap);
				Object[] callbackArgs = { roomCollectionName,"", 
						"",1, roomPropsMap};
				this.mongoDBCallback.doCallback(callbackArgs);
			}
		}
	}
	
	public RoomPageExtractCallback(){
		this.callbacksInPage = new WebResourceCrawlingCallback[1];
		this.callbacksInPage[0] = new RoomExtractCallback();
	}
	
	@Override
	public void init(Object... args) throws Exception {
		this.callbacksInPage[0].init(args);
	}
	
	@Override
	public void clean(Object... args) throws Exception {
		this.callbacksInPage[0].clean(args);
	}
	
	@Override
	protected WebCrawling createNextPageWebCrawling() {
		String pageURL = (String)this.crawlTask.getCrawling().getUrl()
				.replace("page=1", "page="+String.valueOf(this.nextAvalPageNo));
		//System.out.println(pageURL);
		WebCrawling pageCrawling = new WebCrawling(HttpMethod.GET, null, 
				pageURL, 100000, 100000, 100000);
		return pageCrawling;
	}
	public final static String pageInfoSelector = "body > div > form";
	public final static Pattern pageInfoP = Pattern.compile("页次:1/(\\d+)");
	@Override
	protected int computeTotalPages(Object... args) {
		/*try {
			FileWriter writer = new FileWriter("e:\\test.txt");
			writer.write((String) args[0]);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		Document baseDoc = null;
		baseDoc = Jsoup.parse((String) args[0]);
		return extractor.getTotalPageNums(baseDoc, pageInfoP,pageInfoSelector);
	}
}
