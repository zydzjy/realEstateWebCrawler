package yuzhou.gits.realEstateWebCrawler.app.TZ;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;

@CrawlerAnnotation(crawlerCityName = "TZ", 
	crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.TZ.TZConfig")
public  class TZCrawler extends DefaultRealEstateCrawlerStub {
	public TZCrawler() {
		this.callbacksInPage = new ProjectListExtractCallback();
	}

	@Override
	protected PagingCallback createPagingCallback() throws Exception {
		PagingCallback projPaingCallback = new AbstractPagingCallback() {
			@Override
			protected WebCrawling createNextPageWebCrawling() {
				String pageURL = baseCfg.basePageUrl;
				WebCrawling pageCrawling = new WebCrawling(HttpMethod.POST, null, pageURL, 100000, 100000, 100000);
				String respStr = (String) this.crawlTask.getCrawling().getResponse();
				pageCrawling.setPostFormData(populateFormData(respStr, this.nextAvalPageNo));
				return pageCrawling;
			}

			private Object populateFormData(Object... args) {
				int toPageNo = (Integer) args[1];
				String[] postFormData = { "keytype", "1", "page", String.valueOf(toPageNo) };

				return postFormData;
			}

			@Override
			protected int computeTotalPages(Object... args) {
				Document baseDoc = Jsoup.parse((String) args[0]);
				return extractor.getTotalPageNums(baseDoc, TZConfig.pageInfoP, TZConfig.projPageInfoSelector);
			}
		};

		return projPaingCallback;
	}
}