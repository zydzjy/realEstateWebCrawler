package yuzhou.gits.realEstateWebCrawler.app.TC;

import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;

@CrawlerAnnotation(crawlerCityName = "TC", 
	crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.TC.TCConfig")
public class TCCrawler extends DefaultRealEstateCrawlerStub {
	Pattern p = Pattern.compile("\"(.*)\"");

	public TCCrawler() {
		this.callbacksInPage = new ProjectListExtractCallback();
	}
	
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
	protected PagingCallback createPagingCallback() throws Exception {
		PagingCallback projPaingCallback = new AbstractPagingCallback() {
			@Override
			protected WebCrawling createNextPageWebCrawling() {
				String pageURL = baseCfg.basePageUrl;
				WebCrawling pageCrawling = new WebCrawling(HttpMethod.POST, null, 
						pageURL, 100000, 100000, 100000);
				String respStr = (String) this.crawlTask.getCrawling().getResponse();
				pageCrawling.setPostFormData(populateFormData(respStr,this.nextAvalPageNo));
				return pageCrawling;
			}

			private Object populateFormData(Object...args) {
				Document baseDoc = Jsoup.parse((String)args[0]);
				int toPageNo = (Integer)args[1];
				String[] postFormData = { "__VIEWSTATE", baseDoc.selectFirst("#__VIEWSTATE").attr("value"),
						"__VIEWSTATEGENERATOR", baseDoc.selectFirst("#__VIEWSTATEGENERATOR").attr("value"),
						"__EVENTTARGET", "AspNetPager1", "__EVENTARGUMENT", String.valueOf(toPageNo) };
				return postFormData;
			}

			@Override
			protected int computeTotalPages(Object... args) {
				Document baseDoc = Jsoup.parse((String) args[0]);
				return extractor.getTotalPageNums(baseDoc, TCConfig.pageInfoP,TCConfig.projPageInfoSelector);
			}
		};

		return projPaingCallback;
	}
}