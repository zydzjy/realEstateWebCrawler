package yuzhou.gits.realEstateWebCrawler.app.KM;

import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

@CrawlerAnnotation(crawlerCityName = "KM", 
	crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.KM.KMConfig")
public class KMNowSaleCrawler extends DefaultRealEstateCrawlerStub {
	
	public KMNowSaleCrawler() {
		DefaultRealEstateCrawlingCallback roomCallBack=new RoomExtractCallback("km_room20180719",null);
		DefaultRealEstateCrawlingCallback buildCallBack=new BuildingListExtractCallback("km_building20180719",roomCallBack);
		DefaultRealEstateCrawlingCallback projCallBack=new ProjectListExtractCallback("km_project20180719",buildCallBack);
		this.callbacksInPage =projCallBack;
	}
	
	@Override
	public void init(String crawlerConfigPath,String cfgClzName,
			Map<String,Object> env) {

		super.init(crawlerConfigPath, cfgClzName, env);
		/*try {
			this.callbacksInPage.init();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
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
				String pageURL = KMConfig.baseURL.replace("[page]",
						String.valueOf(this.nextAvalPageNo));						
				WebCrawling pageCrawling = new WebCrawling(HttpMethod.POST, null, 
						pageURL, 100000, 100000, 100000);
				 return pageCrawling;
			}
			
			@Override
			protected int computeTotalPages(Object...args){
				Document baseDoc = Jsoup.parse((String)args[0]);
				return extractor.getTotalPageNums(baseDoc,KMConfig.pageInfoP, 
						KMConfig.projPageInfoSelector);
			}
		};
		
		return projPaingCallback;
	}
}