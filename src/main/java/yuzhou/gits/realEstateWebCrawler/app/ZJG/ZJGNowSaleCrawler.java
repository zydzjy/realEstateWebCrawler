package yuzhou.gits.realEstateWebCrawler.app.ZJG;


import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;

@CrawlerAnnotation(crawlerCityName = "ZJG", 
	crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.ZJG.ZJGConfig")
public class ZJGNowSaleCrawler extends DefaultRealEstateCrawlerStub {
	
	public ZJGNowSaleCrawler() {
		this.callbacksInPage = new BuildingListExtractCallback();
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
				String pageURL = ZJGConfig.projListURL.replace("[pageIndex]", String.valueOf(this.nextAvalPageNo));					
				WebCrawling pageCrawling = new WebCrawling(HttpMethod.POST, null, 
						pageURL, 100000, 100000, 100000);

				 return pageCrawling;
			}
			
			@Override
			protected int computeTotalPages(Object...args){
				try {
					Document doc = Jsoup.parse(_get(ZJGConfig.pageUrl));
					return extractor.getTotalPageNums(doc,ZJGConfig.pageInfoP, 
							ZJGConfig.projPageInfoSelector);
				} catch (Exception e) {
					return 999;
				}		
			}
		};
		
		return projPaingCallback;
	}
	
	protected String _get(String url) throws Exception {
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		// 屏蔽日志信息
	 
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		// 支持JavaScript
		webClient.getOptions().setJavaScriptEnabled(true);// 启用JS解释器，默认为true
		webClient.getOptions().setCssEnabled(false);// 禁用css支持
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);// js运行错误时，是否抛出异常
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setTimeout(5000);// 设置连接超时时间 ，这里是5S。如果为0，则无限期等待
		HtmlPage rootPage = webClient.getPage(url);
		// 设置一个运行JavaScript的时间
		webClient.waitForBackgroundJavaScript(5000);
		String html = rootPage.asXml();
		return html;
	}
}