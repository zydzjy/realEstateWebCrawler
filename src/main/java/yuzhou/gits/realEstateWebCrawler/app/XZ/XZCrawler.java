package yuzhou.gits.realEstateWebCrawler.app.XZ;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;

@CrawlerAnnotation(crawlerCityName = "XZ", 
crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.XZ.XZConfig")
public class XZCrawler extends DefaultRealEstateCrawlerStub {

	public XZCrawler(){
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
				String __EVENTARGUMENT = baseDoc.selectFirst("#__EVENTARGUMENT").attr("value");
				String __VIEWSTATE = baseDoc.selectFirst("#__VIEWSTATE").attr("value");
				String __EVENTVALIDATION = baseDoc.selectFirst("#__EVENTVALIDATION").attr("value");
				
				String[] postFormData = {
						"__EVENTTARGET", "ctl00$ContentPlaceHolder1$TableNavigator1$lbtn_Go",
						"__EVENTARGUMENT", __EVENTARGUMENT,
						"__VIEWSTATE", __VIEWSTATE,
						"__EVENTVALIDATION", __EVENTVALIDATION,
						"ctl00$ContentPlaceHolder1$TableNavigator1$txt_CurrPage", 
								String.valueOf(toPageNo),
						"ctl00$ContentPlaceHolder1$TableNavigator1$hfld_CurrPage",
								String.valueOf(toPageNo-1),
						"ctl00$ContentPlaceHolder1$TableNavigator1$hfld_Pages",
								String.valueOf(this.totalPages)};
				return postFormData;
			}
			
			@Override
			protected int computeTotalPages(Object... args) {
				Document baseDoc = Jsoup.parse((String) args[0]);
				return extractor.getTotalPageNums(baseDoc, XZConfig.pageInfoP,XZConfig.projPageInfoSelector);
			}
		};

		return projPaingCallback;
	}
}