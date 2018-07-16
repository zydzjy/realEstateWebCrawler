package yuzhou.gits.realEstateWebCrawler.app.HZDYW;

import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;

@CrawlerAnnotation(crawlerCityName = "HZDYW_PRE", 
	crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.HZDYW.HZDYWConfig")
public class HZDYWPreSaleCrawler extends DefaultRealEstateCrawlerStub {
	
	public HZDYWPreSaleCrawler() {
		this.callbacksInPage = new PreSaleListExtractCallback();
	}
	
	@Override
	public void init(String crawlerConfigPath,String cfgClzName,
			Map<String,Object> env) {

		super.init(crawlerConfigPath, cfgClzName, env);
		try {
			this.callbacksInPage.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				String pageURL = HZDYWConfig.preSaleLicenseListBaseURL
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