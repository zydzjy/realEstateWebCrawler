package yuzhou.gits.realEstateWebCrawler.app.NB;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;

public class RoomPageExtractCallback extends AbstractPagingCallback {
	public class RoomExtractCallback extends DefaultRealEstateCrawlingCallback {
		private String roomCollectionName = "nb_room";
		
		@Override
		public void init(Object...args) throws Exception {
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
			Elements roomListE = baseDoc.select(NBConfig.roomListDataSelector);
			Iterator<Element> roomListEIt = roomListE.iterator();
			Map<String,String> project = (Map<String,String>) this.crawlTask.getPathContext().getAttr("project");
			Map<String,String> projDetail = (Map<String,String>) this.crawlTask.getPathContext().getAttr("projDetail");
			Map<String,String> building = (Map<String,String>) this.crawlTask.getPathContext().getAttr("building");
			
			while(roomListEIt.hasNext()){
				Element roomE = roomListEIt.next();
				Map<String,String> roomPropsMap = new HashMap<String,String>();
				this.extractor.extractDataByCssSelectors(NBConfig.roomDetailSelectorMap, 
						roomE, roomPropsMap);
				Object[] callbackArgs = { roomCollectionName,"", 
						"",1, project,projDetail,building,roomPropsMap};
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
				+"&page="+String.valueOf(this.nextAvalPageNo);
		//System.out.println(pageURL);
		WebCrawling pageCrawling = new WebCrawling(HttpMethod.GET, null, 
				pageURL, 100000, 100000, 100000);
		return pageCrawling;
	}
	public final static String pageInfoSelector = "body > div:nth-child(4) > div > main > div > div.panel-col-bottom.panel-panel > div > div > div > div.item-list > ul > li.pager-last.last > a";
	public final static String pageInfoP = "page=(\\d+)";
	@Override
	protected int computeTotalPages(Object... args) {
		Document baseDoc = null;
		baseDoc = Jsoup.parse((String) args[0]);
		Element e = baseDoc.selectFirst(pageInfoSelector);
		if(e == null)
			return 0;
		String pageInfoStr = e.attr("href");
		return extractor.getTotalPageNums(pageInfoP, pageInfoStr);
	}
}
