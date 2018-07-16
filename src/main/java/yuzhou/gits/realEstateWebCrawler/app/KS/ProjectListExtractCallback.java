package yuzhou.gits.realEstateWebCrawler.app.KS;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.DefaultCrawlerStub;
import yuzhou.gits.crawler.crawl.DefaultCrawlingTask;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.crawl.WebCrawlingExecutor;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.crawler.dataExtractor.DataExtractor;
import yuzhou.gits.crawler.dataExtractor.DataExtractorJsoupImpl;
import yuzhou.gits.realEstateWebCrawler.app.IntoMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.app.MongoDBUtil;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class ProjectListExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected String projectCollectionName = "ks_project20180704";
	protected String projectDetailCollectionName = "ks_projectDetail20180704";
	BuildingListExtractCallback buildingListExtractCallback = new BuildingListExtractCallback();

	public ProjectListExtractCallback() {
	}

	@Override
	public void extracting(String respStr) throws Exception {
		Document baseDoc = Jsoup.parse(respStr);
		int currPageNo = (Integer)this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;

		Elements projListDataE = baseDoc.select(KSConfig.projListSelector);
		Iterator<Element> projListDataEIt = projListDataE.iterator();
		while (projListDataEIt.hasNext()) {
			try {
				Element projListDataItemE = projListDataEIt.next();
				
				String projDetailPageURL = KSConfig.siteDomain + KSConfig.projCtxPath + "/"
						+ projListDataItemE.selectFirst(KSConfig.projDetailPageURLSelector).attr("href");
				Map<String, String> projListDataPropsMap = new HashMap<String, String>();
				extractor.extractDataByCssSelectors(KSConfig.projListDataSelectorMap, projListDataItemE,
						projListDataPropsMap);
				// TODO: save projList
				System.out.println(
						projListDataPropsMap.get("projName") + "(" + crawlerId + "," 
								+ currPageNo + "," + (currCount) + ")");
				
				Object[] callbackArgs = { this.projectCollectionName, 
						crawlerId, currPageNo,currCount,projListDataPropsMap };
				this.mongoDBCallback.doCallback(callbackArgs);
				
				Map<String, String> projDetailPropsMap = new HashMap<String, String>();
				WebCrawling projDetailPageCrawling = new WebCrawling(HttpMethod.GET, null, projDetailPageURL, 600000,
						600000, 600000);
				WebCrawlingTask projDetailPageCrawlingTask = this.deriveNewTask("", true, projDetailPageCrawling);
				projDetailPageCrawlingTask.getPathContext().setAttr("projName", projListDataPropsMap.get("projName"));
				projDetailPageCrawlingTask.getPathContext().setAttr(DefaultRealEstateCrawlingCallback.CURR_COUNT_ATTR, currCount);
				projDetailPageCrawlingTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, currPageNo);
				projDetailPageCrawlingTask.addCallbacks(new DefaultRealEstateCrawlingCallback() {
					@Override
					public void extracting(String respStr) throws Exception {
						Document projDetaiPagDoc = Jsoup.parse(respStr);
						extractor.extractDataByCssSelectors(KSConfig.projDataSelector, projDetaiPagDoc,
								projDetailPropsMap);
						String projDetailPage2URL = KSConfig.siteDomain + KSConfig.projCtxPath + "/"
								+ projDetaiPagDoc.selectFirst(KSConfig.projDetailPage2URLSelector).attr("href");
						WebCrawling projDetailPage2Crawling = new WebCrawling(HttpMethod.GET, null, projDetailPage2URL,
								600000, 600000, 600000);
						/*DefaultCrawlingTask projDetailPage2CrawlingTask = new DefaultCrawlingTask("",
								projDetailPage2Crawling);*/
						WebCrawlingTask projDetailPage2CrawlingTask = 
								this.deriveNewTask("", false, projDetailPage2Crawling);
						
						projDetailPage2CrawlingTask.addCallbacks(new DefaultRealEstateCrawlingCallback() {
							@Override
							public void extracting(String respStr) throws Exception {
								Document projDetailPag2Doc = Jsoup.parse(respStr);
								extractor.extractDataByCssSelectors(KSConfig.projData2Selector, projDetailPag2Doc,
										projDetailPropsMap);
								//TODO:save project detail
								int currCount = (Integer)this.crawlTask.getPathContext().getAttr(CURR_COUNT_ATTR);
								Object[] callbackArgs = { projectDetailCollectionName, 
										crawlerId, currPageNo,currCount,projDetailPropsMap };
								mongoDBCallback.doCallback(callbackArgs);
								
								// extract buildings
								String buildingListPageURL = KSConfig.siteDomain + KSConfig.projCtxPath + "/"
										+ projDetailPag2Doc.selectFirst(KSConfig.buildingListPageURLSelector).attr("href");
								WebCrawling buildingListPageCrawling = new WebCrawling(HttpMethod.GET, null,
										buildingListPageURL, 600000, 600000, 600000);
								WebCrawlingTask buildingListPageCrawlingTask = 
										this.deriveNewTask("", false, buildingListPageCrawling, buildingListExtractCallback); 
								executor.execute(buildingListPageCrawlingTask);
							}
							@Override
							public void init(Object... args) throws Exception {}
							@Override
							public void clean(Object... args) throws Exception {}
						});
						executor.execute(projDetailPage2CrawlingTask);
					}
					@Override
					public void init(Object... args) throws Exception {}
					@Override
					public void clean(Object... args) throws Exception {}
				});
				executor.execute(projDetailPageCrawlingTask);
				currCount++;
				
			} catch (Exception _1) {_1.printStackTrace();}
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		this.buildingListExtractCallback.init(args);
		this.mongoDBCallback.addPropsCollection(this.projectCollectionName  , 1);
		this.mongoDBCallback.addPropsCollection(this.projectDetailCollectionName , 1);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.buildingListExtractCallback.clean(args);
		
		this.mongoDBCallback.flush(this.projectCollectionName);
		this.mongoDBCallback.flush(this.projectDetailCollectionName);
	}
}