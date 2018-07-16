/*package yuzhou.gits.realEstateWebCrawler.app.HEB;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import yuzhou.gits.crawler.crawl.DefaultCrawlerStub;
import yuzhou.gits.crawler.crawl.DefaultCrawlingTask;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.app.Bootstrap;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;

@CrawlerAnnotation(crawlerCityName = "HEB", crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.HEB.HEBConfig")
public class HEBCrawler extends DefaultRealEstateCrawlerStub {
	public final static DateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	protected String dataSetSuffix;
	
	public HEBCrawler() {
		this.callbacksInPage = new ProjectListExtractCallback();
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
				String pageURL = baseCfg.basePageUrl.replace("[OFFSET]",
						String.valueOf(10 * (this.nextAvalPageNo - 1)))
							.replace("[PAGENO]", String.valueOf(this.nextAvalPageNo));
				WebCrawling pageCrawling = new WebCrawling(HttpMethod.GET, null, 
						pageURL, 100000, 100000, 100000);
				 return pageCrawling;
			}
			
			@Override
			protected int computeTotalPages(Object...args){
				Document baseDoc = Jsoup.parse((String)args[0]);
				return extractor.getTotalPageNums(baseDoc,HEBConfig.pageInfoP, HEBConfig.projPageInfoSelector);
			}
		};
		
		return projPaingCallback;
	}
}*/