package yuzhou.gits.realEstateWebCrawler.app.NB;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;

@CrawlerAnnotation(crawlerCityName = "NB", 
crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.NB.NBConfig")
public class NBCrawler extends DefaultRealEstateCrawlerStub {
	 
	public NBCrawler(){
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
				/*return extractor.getTotalPageNums(baseDoc,NBConfig.pageInfoP, 
						NBConfig.projPageInfoSelector);*/
				String str = baseDoc.selectFirst(NBConfig.projPageInfoSelector).attr("href");
				return extractor.getTotalPageNums(NBConfig.pageInfoP,str);
			}
		};

		return projPaingCallback;
	}
}