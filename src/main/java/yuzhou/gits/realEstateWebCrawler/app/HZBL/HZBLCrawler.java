package yuzhou.gits.realEstateWebCrawler.app.HZBL;

import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.app.HZBL.ProjectListExtractCallback;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;

@CrawlerAnnotation(crawlerCityName = "HZBL", 
	crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.HZBL.HZBLConfig")
public class HZBLCrawler extends DefaultRealEstateCrawlerStub {
	private String projectType = "PRE";
	public HZBLCrawler(){
		this.callbacksInPage = new ProjectListExtractCallback();
	}
	
	@Override
	public void init(String crawlerConfigPath,String cfgClzName,
			Map<String,Object> env) {
		super.init(crawlerConfigPath, cfgClzName, env);
		HZBLConfig _config = (HZBLConfig)this.baseCfg;
		this.projectType = _config.projectType;
	}
	
	@Override
	protected PagingCallback createPagingCallback() throws Exception {
		PagingCallback projPaingCallback = new AbstractPagingCallback(){
			@Override
			protected WebCrawling createNextPageWebCrawling() {
				String pageURL = "";
				if("NOW".equals(projectType)){
					pageURL = HZBLConfig.nowSaleBaseURL
						+"?page="+this.nextAvalPageNo
						+"&&projectname=&&compname=&&address=";
				}else{
					pageURL = HZBLConfig.preSaleBaseURL
							+"?page="+this.nextAvalPageNo
							+"&&projectname=&&compname=&&address=";
				}
				WebCrawling pageCrawling = new WebCrawling(HttpMethod.GET, null, 
						pageURL, 100000, 100000, 100000);
				 return pageCrawling;
			}
			
			@Override
			protected int computeTotalPages(Object...args){
				Document baseDoc = Jsoup.parse((String)args[0]);
				return extractor.getTotalPageNums(baseDoc,HZBLConfig.pageInfoP, 
						HZBLConfig.projPageInfoSelector);
			}
		};
		return projPaingCallback;
	}
}