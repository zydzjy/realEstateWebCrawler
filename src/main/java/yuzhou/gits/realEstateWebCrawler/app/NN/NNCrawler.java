package yuzhou.gits.realEstateWebCrawler.app.NN;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;

@CrawlerAnnotation(crawlerCityName = "NN", 
crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.NN.NNConfig")
public class NNCrawler extends DefaultRealEstateCrawlerStub {
	 
	public NNCrawler(){
		this.callbacksInPage = new ProjectListExtractCallback();
	}

	@Override
	protected PagingCallback createPagingCallback() throws Exception {
		PagingCallback projPaingCallback = new AbstractPagingCallback() {
			@Override
			protected WebCrawling createNextPageWebCrawling() {
				String pageURL = baseCfg.basePageUrl.replace("[PAGENO]", 
						String.valueOf(this.nextAvalPageNo));
				WebCrawling pageCrawling = new WebCrawling(HttpMethod.GET, null, 
						pageURL, 100000, 100000, 100000);
				return pageCrawling;
			}
			 
			@Override
			protected int computeTotalPages(Object... args) {
				Document baseDoc = Jsoup.parse((String)args[0]);
				return extractor.getTotalPageNums(baseDoc, NNConfig.pageInfoP, 
						NNConfig.pageInfoSelector);
			}
		};

		return projPaingCallback;
	}
}