package yuzhou.gits.realEstateWebCrawler.app.SJZ;

import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;

@CrawlerAnnotation(crawlerCityName = "SJZ", 
	crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.SJZ.SJZConfig")
public class SJZNowSaleCrawler extends DefaultRealEstateCrawlerStub {
	
	public SJZNowSaleCrawler() {
		this.callbacksInPage = new ProjectListExtractCallback();
	}
	
	@Override
	public void init(String crawlerConfigPath,String cfgClzName,
			Map<String,Object> env) {

		super.init(crawlerConfigPath, cfgClzName, env);
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
				String pageURL = SJZConfig.projectListBaseURL.replace("[page]", String.valueOf(String.valueOf(this.nextAvalPageNo)))
						.replace("[type]", "2");//1住宅，2非住宅					
				WebCrawling pageCrawling = new WebCrawling(HttpMethod.POST, null, 
						pageURL, 100000, 100000, 100000);
				 return pageCrawling;
			}
			
			@Override
			protected int computeTotalPages(Object...args){
				Document baseDoc = Jsoup.parse((String)args[0]);
				return extractor.getTotalPageNums(baseDoc,SJZConfig.pageInfoP, 
						SJZConfig.projPageInfoSelector);
			}
		};
		
		return projPaingCallback;
	}
}