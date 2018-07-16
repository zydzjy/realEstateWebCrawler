package yuzhou.gits.realEstateWebCrawler.app.KS;

import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub; 

@CrawlerAnnotation(crawlerCityName = "KS", 
	crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.KS.KSConfig")
public class KSCrawler extends DefaultRealEstateCrawlerStub {
	public KSCrawler(){
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
	protected PagingCallback createPagingCallback() throws Exception {
		PagingCallback projPaingCallback = new AbstractPagingCallback(){
			@Override
			protected WebCrawling createNextPageWebCrawling() {
				//TODO: 昆山分页有问题，尝试等待50秒后重试
				try{
					System.out.println("昆山分页有问题，尝试等待50秒后重试");
					Thread.sleep(50*1000);
				}catch(Exception e){}
				String pageURL = baseCfg.basePageUrl;
				WebCrawling pageCrawling = new WebCrawling(HttpMethod.POST, null, 
						pageURL, 100000, 100000, 100000);
				String respStr = (String) this.crawlTask.getCrawling().getResponse();
				pageCrawling.setPostFormData(populateFormData(respStr,this.nextAvalPageNo));
				return pageCrawling;
			}
			
			protected String[] populateFormData(Object...args){
				Document baseDoc = Jsoup.parse((String)args[0]);
				int toPageNo = (Integer)args[1];
				String[] postFormData = {
						"__EVENTTARGET","ddlistpagecount",
						"__EVENTARGUMENT",baseDoc.selectFirst("#__EVENTARGUMENT").attr("value"),
						"__LASTFOCUS",baseDoc.selectFirst("#__LASTFOCUS").attr("value"),
						"__VIEWSTATE",baseDoc.selectFirst("#__VIEWSTATE").attr("value"),
						"__VIEWSTATEENCRYPTED",baseDoc.selectFirst("#__VIEWSTATEENCRYPTED").attr("value"),
						"__EVENTVALIDATION",baseDoc.selectFirst("#__EVENTVALIDATION").attr("value"),
						"__VIEWSTATEENCRYPTED",baseDoc.selectFirst("#__VIEWSTATEENCRYPTED").attr("value"),
						"ddlistpagecount",String.valueOf(toPageNo),
						"ddlistwhere:","-1",
						"ddlistkind","全部"
				};
				return postFormData;
			}
			
			@Override
			protected int computeTotalPages(Object...args){
				Document baseDoc = Jsoup.parse((String)args[0]);
				return extractor.getTotalPageNums(baseDoc,KSConfig.pageInfoP, 
						KSConfig.projPageInfoSelector);
			}
		};
		
		return projPaingCallback;
	}	 
}