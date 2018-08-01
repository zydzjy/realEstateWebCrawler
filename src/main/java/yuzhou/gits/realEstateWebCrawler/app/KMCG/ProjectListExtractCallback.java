package yuzhou.gits.realEstateWebCrawler.app.KMCG;

import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.crawler.http.URLEncoder;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class ProjectListExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected RoomPageExtractCallback roomPageExtractCallback = new RoomPageExtractCallback();
	
	public ProjectListExtractCallback() {
	}
	
	@Override
	public void extracting(String respStr) throws Exception {
		Document baseDoc = Jsoup.parse((String) respStr);
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;
		Elements projListE = baseDoc.select(KMCGConfig.projListSelector);
		Iterator<Element> projListIt = projListE.iterator();
		while (projListIt.hasNext()) {
			try {
				Element projDetailE = projListIt.next();
				String roomListURL = KMCGConfig.siteDomain+projDetailE.attr("href");
				String projName = projDetailE.selectFirst("a").ownText();
				System.out.println(projName + "(" + crawlerId + "," 
						+ currPageNo + "," +  ")");
				roomListURL = URLEncoder.encode(roomListURL,"GBK");
				WebCrawlingTask roomListTask = this.deriveNewTask("", true, roomListURL, 
						new DefaultRealEstateCrawlingCallback(){
							@Override
							protected void extracting(String respStr) throws Exception {
								Document baseDoc = Jsoup.parse(respStr);
								String _roomListURL = (String)this.crawlTask.
										getPathContext().getAttr("roomListURL");
								String bldSelOptionsSelector = "#bid > option";
								String[][] buildings = 
										this.extractor.extractSelectEVals(
												baseDoc.select(bldSelOptionsSelector));
								for(int i=0;i<buildings.length;i++){
									String buildingNO = buildings[i][1];
									if(buildingNO != null && "0".equals(buildingNO) == false &&
											"".equals(buildingNO) == false){
										//http://pre.chghouse.org/newhouse/houseprice.asp?page=2&aid=4&preid=3038&prename=%C0%A5%C3%F7%D6%D0%BD%BB%B3%C7%A3%A8A2%B5%D8%BF%E9%A3%A9%A2%F4&bid=57132&cid=-1
										String roomPageURL = _roomListURL + 
												("&bid="+buildingNO+"&page=1&cid=-1");
										//System.out.println(roomPageURL);
										this.crawlTask.getPathContext().setAttr(Constants.PAGE_START_NO,1);
										this.crawlTask.getPathContext().setAttr(Constants.PAGE_END_NO,-1);
										String buildingName = buildings[i][0];
										this.crawlTask.getPathContext().setAttr("buildingName",buildingName);
										WebResourceCrawlingCallback[] callbacks = new WebResourceCrawlingCallback[
										            ProjectListExtractCallback.this.roomPageExtractCallback.getCallbacksInPage().length+1];
										System.arraycopy(ProjectListExtractCallback.this.roomPageExtractCallback.getCallbacksInPage(), 
												0,callbacks, 0, callbacks.length-1);
										callbacks[callbacks.length-1] = ProjectListExtractCallback.this.roomPageExtractCallback;
										this.executor.execute(this.deriveNewTask("", false, roomPageURL, 
												callbacks));
									}
								}
							}
				});
				roomListTask.getPathContext().setAttr("projName", projName);
				roomListTask.getPathContext().setAttr("roomListURL", roomListURL);
				roomListTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, this.currPageNo);
				this.executor.execute(roomListTask);
			}catch(Exception e){e.printStackTrace();}
		}
		currCount++;
	}

	@Override
	public void init(Object... args) throws Exception {
		this.roomPageExtractCallback.init(args);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.roomPageExtractCallback.clean(args);
	}
}