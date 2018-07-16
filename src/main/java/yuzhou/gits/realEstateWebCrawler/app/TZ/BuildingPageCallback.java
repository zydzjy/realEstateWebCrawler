package yuzhou.gits.realEstateWebCrawler.app.TZ;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonParser;

import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class BuildingPageCallback extends AbstractPagingCallback {
	protected JsonParser jsonParser = new JsonParser();
	static final Pattern roomListPageInfoP = Pattern.compile("/(\\d+)");
	
	public BuildingPageCallback(){
		this.callbacksInPage = new WebResourceCrawlingCallback[1];
		this.callbacksInPage[0] = new DefaultRealEstateCrawlingCallback(){
			private String roomCollectionName = "tz_room";
			public void init(Object...args) throws Exception {
				this.datasetSuffix = (String)args[0];
				this.roomCollectionName = this.roomCollectionName + this.datasetSuffix;
				this.mongoDBCallback.addPropsCollection(roomCollectionName,100);
			}
			public void clean(Object...args) throws Exception {
				this.mongoDBCallback.flush(this.roomCollectionName);
			}
			private void extractRoomInfos(Map<String, String> roomDataSelectorMap, Element roomE,
					Map<String, String> roomPropsMap) {
				Iterator<Entry<String, String>> it = roomDataSelectorMap.entrySet().iterator();
				while(it.hasNext()){
					Entry<String,String> entry = it.next();
					String propName = entry.getKey();
					String cssSelector = entry.getValue();
					//if(cssSelector.contains("span")){
						Element numParentE = roomE.selectFirst(cssSelector);
						/*if(numParentE == null){
							System.out.println("nulll");
						}else*/{
							Elements numsE = numParentE.select("span");
							if(numsE.size()>0){
								Iterator<Element> numsEIt = numsE.iterator();
								StringBuffer numStrBuff = new StringBuffer();
								while(numsEIt.hasNext()){
									Element numE = numsEIt.next();
									String numStr = numE.attr("class");
									numStrBuff.append(TZConfig.digitsMap.get(numStr));
								}
								numStrBuff.append(numParentE.ownText());
								roomPropsMap.put(propName, numStrBuff.toString());
							}else{
								roomPropsMap.put(propName, numParentE.text());
							}
						}
				}
			}
			
			@Override
			protected void extracting(String respStr) throws Exception {
				
				Map<String,String> projPropsMap = (Map<String, String>) this.crawlTask.getPathContext().getAttr("project");
				Map<String,String> presalePropsMap = (Map<String, String>) this.crawlTask.getPathContext().getAttr("presale");
				
				Document roomListPageDoc = Jsoup.parse(respStr);
				Elements roomListE = roomListPageDoc.select(TZConfig.roomListSelector);
				Iterator<Element> roomListEIt = roomListE.iterator();
				while(roomListEIt.hasNext()){
					Element roomE = roomListEIt.next();
					Map<String,String> roomPropsMap = new HashMap<String,String>();
					this.extractRoomInfos(TZConfig.roomDataSelectorMap, roomE, 
							roomPropsMap);
					//TODO: do callback
					Object[] callbackArgs = { roomCollectionName,"", 
							currPageNo,1, projPropsMap,presalePropsMap, roomPropsMap};
					this.mongoDBCallback.doCallback(callbackArgs);
				}
			}
		};
	}
	
	public String pageBaseURL = "";
	
	@Override
	protected WebCrawling createNextPageWebCrawling() {
		String pageURL = pageBaseURL.replace("[PAGENO]", 
				String.valueOf(this.nextAvalPageNo));
		WebCrawling pageCrawling = new WebCrawling(HttpMethod.GET, null, pageURL, 100000, 100000, 100000);
		return pageCrawling;
	}
	
	@Override
	protected int computeTotalPages(Object... args) {
		Document baseDoc = Jsoup.parse((String) args[0]);
		return extractor.getTotalPageNums(baseDoc, roomListPageInfoP,
				TZConfig.roomPageInfoSelector);
	}
}
