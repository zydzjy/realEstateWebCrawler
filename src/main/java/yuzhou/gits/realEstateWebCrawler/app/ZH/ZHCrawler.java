package yuzhou.gits.realEstateWebCrawler.app.ZH;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import yuzhou.gits.crawler.crawl.AbstractPagingCallback;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

@CrawlerAnnotation(crawlerCityName = "ZH", 
crawlerCfgClzName = "yuzhou.gits.realEstateWebCrawler.app.ZH.ZHConfig")
public class ZHCrawler extends DefaultRealEstateCrawlerStub {
	public void login(){
		String loginURL = ZHConfig.siteDomain + "/LeadingCP/LCP_Login/Execute/Login.ashx";
		String userName = "gszh";
		String userPwd = "";// "8f735360-c975-4683-8bfc-e0929086694e";
		String[] loginPostData = { "tag", "GHlogin", "userName", userName, "userPwd", userPwd, "SiteId",
				"94bf2092-7895-4910-a439-33d56f390864" };

		try {
			WebCrawlingTask loginTask = this.getTaskFactory().createPostWebCrawlingTask("", 
					loginURL, null, loginPostData, 
					this.getBaseCfg().getHttpConnectTimeout(), 
					this.getBaseCfg().getHttpReadTimeout(),
					this.getBaseCfg().getHttpWriteTimeout());
			this.crawlingExecutor.execute(loginTask);
			System.out.println("login site!!!!!!");
		} catch (Exception e) {
		}
	}
	public ZHCrawler(){
		this.callbacksInPage = new ProjectListExtractCallback();
	}
	
	@Override
	protected PagingCallback createPagingCallback() throws Exception {
		PagingCallback projPaingCallback = new AbstractPagingCallback() {
			@Override
			protected WebCrawling createNextPageWebCrawling() {
				/*String pageURL = baseCfg.basePageUrl;
				WebCrawling pageCrawling = new WebCrawling(HttpMethod.POST, null, 
						pageURL, 100000, 100000, 100000);
				String[] postData = {};
				pageCrawling.setPostFormData(postData);
				return pageCrawling;*/
				return null;
			}
			//final static int pageSize = 20;
			@Override
			protected int computeTotalPages(Object... args) {
				JsonElement projsJson = new JsonParser().parse((String) args[0]);
				if (projsJson.isJsonArray()) {
					JsonArray projListData = (JsonArray) projsJson;
					int totalRecords = projListData.size();
					//int totalPages = totalRecords / pageSize + (totalRecords % pageSize != 0 ? 1 : 0);
					return totalRecords > 0 ? 1 : 0;
				}else{
					return 0;
				}
			}
		};

		return projPaingCallback;
	}
}