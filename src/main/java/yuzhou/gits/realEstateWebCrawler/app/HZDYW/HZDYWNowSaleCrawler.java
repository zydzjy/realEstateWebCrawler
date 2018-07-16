package yuzhou.gits.realEstateWebCrawler.app.HZDYW;

import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.app.HZHD.ProjectListExtractCallback;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;

@CrawlerAnnotation(crawlerCityName = "HZDYW_NOW", 
	crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.HZDYW.HZDYWConfig")
public class HZDYWNowSaleCrawler extends DefaultRealEstateCrawlerStub {
	
	public HZDYWNowSaleCrawler() {
		this.callbacksInPage = new ProjectListExtractCallback();
	}
	
	@Override
	public void login() {
	}

	@Override
	protected PagingCallback createPagingCallback() 
			throws Exception {
		PagingCallback projPaingCallback = new AbstractPagingCallback(){
			@Override
			protected WebCrawling createNextPageWebCrawling() {
				String pageURL = HZDYWConfig.nowSaleBaseURL
						+"?page="+this.nextAvalPageNo
						+"&&projectname=&&compname=&&address=";
				WebCrawling pageCrawling = new WebCrawling(HttpMethod.GET, null, 
						pageURL, 100000, 100000, 100000);
				 return pageCrawling;
			}
			
			@Override
			protected int computeTotalPages(Object...args){
				Document baseDoc = Jsoup.parse((String)args[0]);
				return extractor.getTotalPageNums(baseDoc,HZDYWConfig.pageInfoP, 
						HZDYWConfig.projPageInfoSelector);
			}
		};
		
		return projPaingCallback;
	}
}