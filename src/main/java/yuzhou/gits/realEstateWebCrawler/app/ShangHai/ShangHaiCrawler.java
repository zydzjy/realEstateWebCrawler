package yuzhou.gits.realEstateWebCrawler.app.ShangHai;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;

@CrawlerAnnotation(crawlerCityName = "ShangHai", crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.ShangHai.ShangHaiConfig")
public class ShangHaiCrawler extends DefaultRealEstateCrawlerStub {
	 
	public ShangHaiCrawler() {
		this.callbackInFirstPage = new ProjectListExtractCallback();
		this.callbacksInPage = new ProjectListExtractCallback();
	}

	@Override
	protected PagingCallback createPagingCallback() throws Exception {
		PagingCallback projPaingCallback = new AbstractPagingCallback() {
			@Override
			protected WebCrawling createNextPageWebCrawling() {
				String pageURL = baseCfg.basePageUrl;
				WebCrawling pageCrawling = new WebCrawling(HttpMethod.POST, null, 
						pageURL, 100000, 100000, 100000);
				pageCrawling.setPostFormData(populateFormData());
				return pageCrawling;
			}
			
			private Object populateFormData(Object...args) {
				String[] postFormData = (String[]) 
						this.crawlTask.getCrawling().getPostFormData();
				postFormData[postFormData.length-1] = String.valueOf(this.nextAvalPageNo);
				return postFormData;
			}
			
			@Override
			protected int computeTotalPages(Object... args) {
				JsonElement respJson = new JsonParser().parse((String)args[0]);
				Document baseDoc = Jsoup.parse(respJson.getAsJsonObject().get("htmlView").getAsString());
				return extractor.getTotalPageNums(baseDoc, ShangHaiConfig.pageInfoP,
						ShangHaiConfig.pageInfoSelector);
			}
		};

		return projPaingCallback;
	}
}