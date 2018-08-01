package yuzhou.gits.realEstateWebCrawler.realEstateCrawl;

import java.util.Map;

import yuzhou.gits.crawler.crawl.DefaultCrawlerStub;
import yuzhou.gits.crawler.crawl.DefaultCrawlingTask;
import yuzhou.gits.crawler.crawl.PagingCallback;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.SingletonMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.app.Bootstrap;
import yuzhou.gits.realEstateWebCrawler.app.IntoMongoDBCallback;

public abstract class DefaultRealEstateCrawlerStub extends DefaultCrawlerStub {
	protected WebResourceCrawlingCallback callbacksInPage = null;
	public static final String MONGODB_CALLBACK = "MONGODB_CALLBACK";
	protected IntoMongoDBCallback mongodbCallback = SingletonMongoDBCallback.singleton;
	
	protected String datasetSuffix = "";
	protected abstract PagingCallback createPagingCallback() throws Exception;
	public String getDatasetSuffix() {
		return datasetSuffix;
	}
	public void setDatasetSuffix(String datasetSuffix) {
		this.datasetSuffix = datasetSuffix;
	}
	@Override
	public void init(String crawlerConfigPath,String cfgClzName,
			Map<String,Object> env) {

		super.init(crawlerConfigPath, cfgClzName, env);
		try {
			/*this.datasetSuffix = (String) env.get(Bootstrap.ENV_DATASET_SUFFIX);
			if(this.callbacksInPage != null)
				this.callbacksInPage.init(this.datasetSuffix);*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public WebCrawlingTask createBaseCrawlTask(Object arg) throws Exception {
		WebCrawlingTask baseTask = super.createBaseCrawlTask(arg);
		baseTask.setStub(this);
		return baseTask;
	}
	
	protected WebResourceCrawlingCallback callbackInFirstPage = null;
	protected void createFirstPageCallback(){}
	@Override
	protected WebResourceCrawlingCallback[] createBaseCrawlingCallbacks(Object arg) throws Exception {
		WebCrawlingTask baseTask = (DefaultCrawlingTask) arg;
		try {
			int startPageNo = (Integer) stubEnv.get(Bootstrap.ENV_START_PAGE_NO);
			int endPageNo = (Integer) stubEnv.get(Bootstrap.ENV_END_PAGE_NO);

			PagingCallback pagingCallback = this.createPagingCallback();
			if (callbacksInPage != null) {
				pagingCallback.setCallbacksInPage(callbacksInPage);
			}

			// baseTask.getTaskContext().put(Constants.DATA_SET_SUFFIX,
			// this.dataSetSuffix);
			
			WebResourceCrawlingCallback[] callbacks = null;
			if(startPageNo == 1){
				this.createFirstPageCallback();
				callbacks = new WebResourceCrawlingCallback[2];
				callbacks[0] = this.callbackInFirstPage;
				callbacks[1] = (WebResourceCrawlingCallback) pagingCallback;

				baseTask.getPathContext().setAttr(Constants.PAGE_START_NO, startPageNo +1 );
			}else{
				callbacks = new WebResourceCrawlingCallback[1];
				callbacks[0] = (WebResourceCrawlingCallback) pagingCallback;

				baseTask.getPathContext().setAttr(Constants.PAGE_START_NO, startPageNo);
			}
			
			baseTask.getPathContext().setAttr(MONGODB_CALLBACK, this.mongodbCallback);
			baseTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, startPageNo);
			baseTask.getPathContext().setAttr(Constants.PAGE_END_NO, endPageNo);
			
			/*baseTask.getTaskContext().put(Constants.PAGE_START_NO, startPageNo +1 );
			baseTask.getTaskContext().put(Constants.PAGE_CURRENT_NO, startPageNo);
			baseTask.getTaskContext().put(Constants.PAGE_END_NO, endPageNo);*/

			return callbacks;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}