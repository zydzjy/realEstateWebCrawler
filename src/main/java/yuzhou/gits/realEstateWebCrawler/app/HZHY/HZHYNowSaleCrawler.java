package yuzhou.gits.realEstateWebCrawler.app.HZHY;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;

@CrawlerAnnotation(crawlerCityName = "HZHY", 
	crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.HZHY.HZHYConfig")
public class HZHYNowSaleCrawler extends DefaultRealEstateCrawlerStub {
	private static String __VIEWSTATE="";
	private static String __EVENTVALIDATION="";
	private final String __EVENTTARGET="GridView1";
	private final String __EVENTARGUMENT="Page$[page]";
	
	
	public static String get__VIEWSTATE() {
		return __VIEWSTATE;
	}

	public static void set__VIEWSTATE(String __VIEWSTATE) {
		HZHYNowSaleCrawler.__VIEWSTATE = __VIEWSTATE;
	}

	public static String get__EVENTVALIDATION() {
		return __EVENTVALIDATION;
	}

	public static void set__EVENTVALIDATION(String __EVENTVALIDATION) {
		HZHYNowSaleCrawler.__EVENTVALIDATION = __EVENTVALIDATION;
	}

	public HZHYNowSaleCrawler() {
		this.callbacksInPage = new ProjectListExtractCallback();
		this.callbackInFirstPage = new ProjectListExtractCallback();
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
				String pageURL = HZHYConfig.nowSaleBaseURL;						
				WebCrawling pageCrawling = new WebCrawling(HttpMethod.POST, null, 
						pageURL, 100000, 100000, 100000);
				pageCrawling.setPostFormData(populateFormData());
				 return pageCrawling;
			}
			
			private Object populateFormData() {
				String[] postFormData = { "__VIEWSTATE",__VIEWSTATE,
						"__EVENTVALIDATION", __EVENTVALIDATION,
						"__EVENTTARGET", __EVENTTARGET, "__EVENTARGUMENT", __EVENTARGUMENT.replace("[page]",String.valueOf(this.nextAvalPageNo)) };
				return postFormData;
			}
			final Pattern p = Pattern.compile("(\\d+)");
			@Override
			protected int computeTotalPages(Object...args){
				
				Document baseDoc = Jsoup.parse((String)args[0]);
				Matcher m = p.matcher(baseDoc.selectFirst("#Labrecord").ownText());
				if(m.find()){
					int totalRecords = Integer.parseInt(m.group(0));
					int pageSize = 15;
					int pages = totalRecords / pageSize + (totalRecords % pageSize > 0 ? 1 : 0);
					return pages;
				}else{
					return 0;
				}
			}
		};
		
		return projPaingCallback;
	}
}