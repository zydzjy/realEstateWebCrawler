package yuzhou.gits.realEstateWebCrawler.app.KMAN;

import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.app.Bootstrap;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

@CrawlerAnnotation(crawlerCityName = "KMAN", 
	crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.KMAN.KMANConfig")
public class KMANNowSaleCrawler extends DefaultRealEstateCrawlerStub {
	
	public KMANNowSaleCrawler() {
	
	}
	
	@Override
	public void init(String crawlerConfigPath,String cfgClzName,
			Map<String,Object> env) {
		this.datasetSuffix = (String) env.get(Bootstrap.ENV_DATASET_SUFFIX);
		DefaultRealEstateCrawlingCallback roomCallBack=new RoomExtractCallback("kman_room"+this.datasetSuffix,null);
		DefaultRealEstateCrawlingCallback buildCallBack=new BuildingListExtractCallback("",roomCallBack);
		DefaultRealEstateCrawlingCallback projCallBack=new ProjectListExtractCallback("",buildCallBack);
		this.callbacksInPage =projCallBack;
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
				String pageURL = KMANConfig.baseURL.replace("[page]",
						String.valueOf(this.nextAvalPageNo));						
				WebCrawling pageCrawling = new WebCrawling(HttpMethod.POST, null, 
						pageURL, 100000, 100000, 100000);
				 return pageCrawling;
			}
			
			@Override
			protected int computeTotalPages(Object...args){
				Document baseDoc = Jsoup.parse((String)args[0]);
				return extractor.getTotalPageNums(baseDoc,KMANConfig.pageInfoP, 
						KMANConfig.projPageInfoSelector);
			}
		};
		
		return projPaingCallback;
	}
}