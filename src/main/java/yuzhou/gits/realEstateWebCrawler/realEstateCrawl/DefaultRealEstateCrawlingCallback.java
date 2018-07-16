package yuzhou.gits.realEstateWebCrawler.realEstateCrawl;

import yuzhou.gits.crawler.crawl.DefaultCrawlingCallback;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.SingletonMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.app.IntoMongoDBCallback;

public abstract class DefaultRealEstateCrawlingCallback 
			extends DefaultCrawlingCallback {
	protected IntoMongoDBCallback mongoDBCallback;
	protected String datasetSuffix = "";
	protected int currPageNo;
	protected String crawlerId = "";
	public final static String CURR_COUNT_ATTR = "CURR_COUNT_ATTR";
	protected void _doCallback(WebCrawlingTask task) throws Exception {
		this.currPageNo = (Integer)this.crawlTask.getPathContext()
				.getAttr(Constants.PAGE_CURRENT_NO);
		String respStr = (String)(task.getCrawling().getResponse());
		this.extracting(respStr);
	}
	
	public DefaultRealEstateCrawlingCallback(){
		this.mongoDBCallback = SingletonMongoDBCallback.singleton;
	}
	
	@Override
	public void init(Object... args) throws Exception {
		this.datasetSuffix = (String)args[0];
	}
	@Override
	public void clean(Object... args) throws Exception {
	}
	
	protected abstract void extracting(String respStr) throws Exception ;
}