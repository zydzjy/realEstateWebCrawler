package yuzhou.gits.realEstateWebCrawler.app.ShangHai.ShangHai2;

import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;

@CrawlerAnnotation(crawlerCityName = "ShangHai2", 
	crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.ShangHai.ShangHai2.ShangHai2Config")
public class ShangHaiCrawler2 extends DefaultRealEstateCrawlerStub {
	public ShangHaiCrawler2() {
		this.callbacksInPage = new ProjectExtractCallback();
	}
	protected void createFirstPageCallback(){
		this.callbackInFirstPage = new ProjectExtractCallback();
	}
	@Override
	protected PagingCallback createPagingCallback() throws Exception {
		PagingCallback projPaingCallback = new AbstractPagingCallback() {
			@Override
			protected WebCrawling createNextPageWebCrawling() {
				String pageURL = baseCfg.basePageUrl.replace("[PAGENO]", 
						String.valueOf(this.nextAvalPageNo));
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
				Document baseDoc = Jsoup.parse((String) args[0]);
				try {
					//baseDoc = Jsoup.parse((InputStream) args[0],"gb2312","");
					return extractor.getTotalPageNums(baseDoc, ShangHai2Config.pageInfoP,
							ShangHai2Config.pageInfoSelector);
				} catch (Exception e) {
					e.printStackTrace();
					return 0;
				}
			}
		};

		return projPaingCallback;
	}
}