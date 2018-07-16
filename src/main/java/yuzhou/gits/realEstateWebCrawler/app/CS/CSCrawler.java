package yuzhou.gits.realEstateWebCrawler.app.CS;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;

@CrawlerAnnotation(crawlerCityName = "CS", 
crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.CS.CSConfig")
public class CSCrawler extends DefaultRealEstateCrawlerStub {
	  
	public CSCrawler() {
		this.callbacksInPage = new ProjectListExtractCallback();
	}

	@Override
	protected PagingCallback createPagingCallback() throws Exception {
		PagingCallback projPaingCallback = new AbstractPagingCallback() {
			@Override
			protected WebCrawling createNextPageWebCrawling() {
				String pageURL = baseCfg.basePageUrl;
				WebCrawling pageCrawling = new WebCrawling(HttpMethod.POST, null, 
						pageURL, 100000, 100000, 100000);
				String respStr = (String) this.crawlTask.getCrawling().getResponse();
				pageCrawling.setPostFormData(populateFormData(respStr,this.nextAvalPageNo));
				return pageCrawling;
			}

			private Object populateFormData(Object...args) {
				Document baseDoc = Jsoup.parse((String)args[0]);
				int toPageNo = (Integer)args[1];
				String[] postFormData = {
						"__EVENTTARGET","PageNavigator_NewHouse1$LnkBtnGoto",
						"__EVENTARGUMENT",
							baseDoc.selectFirst("#__EVENTARGUMENT").attr("value"),
						"__VIEWSTATE", 
							baseDoc.selectFirst("#__VIEWSTATE").attr("value"),
						"__EVENTVALIDATION", 
							baseDoc.selectFirst("#__EVENTVALIDATION").attr("value"),
						"head_top_search_one_zhaofang","",
					    "PageNavigator_NewHouse1$txtNewPageIndex", 
							String.valueOf(toPageNo)};
				return postFormData;
			}

			@Override
			protected int computeTotalPages(Object... args) {
				Document baseDoc = Jsoup.parse((String) args[0]);
				return extractor.getTotalPageNums(baseDoc, CSConfig.pageInfoP,CSConfig.projPageInfoSelector);
			}
		};

		return projPaingCallback;
	}
}