package yuzhou.gits.realEstateWebCrawler.realEstateCrawl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import yuzhou.gits.crawler.crawl.DefaultCrawlingCallback;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.SingletonMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.app.Bootstrap;
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
		this.extracting((String) task.getCrawling().getResponse());
	}
	protected List<String> collectionNames=new ArrayList<String>();
	protected DefaultRealEstateCrawlingCallback nextCallBack;
	protected int bathSize=0;
	private final int DEFAULT_PROJ_SIZE=1;
	private final int DEFAULT_BUILD_SIZE=10;
	private final int DEFAULT_ROOM_SIZE=500;
	public DefaultRealEstateCrawlingCallback(){
		this.mongoDBCallback = SingletonMongoDBCallback.singleton;
	}
	
	public DefaultRealEstateCrawlingCallback(String collectionName,DefaultRealEstateCrawlingCallback nextCallBack,Object... args){
		this.collectionNames.add(collectionName);
		this.nextCallBack=nextCallBack;
		this.mongoDBCallback = SingletonMongoDBCallback.singleton;
		addPropsCollections();	
	}
	
	public DefaultRealEstateCrawlingCallback(String collectionName,int bathSize,DefaultRealEstateCrawlingCallback nextCallBack,Object... args){
		this.collectionNames.add(collectionName);
		this.bathSize=bathSize;
		this.nextCallBack=nextCallBack;
		this.mongoDBCallback = SingletonMongoDBCallback.singleton;
		addPropsCollections();	
	}
	
	public DefaultRealEstateCrawlingCallback(List<String> collectionNames,DefaultRealEstateCrawlingCallback nextCallBack,Object... args){
		this.collectionNames=collectionNames;
		this.nextCallBack=nextCallBack;
		this.mongoDBCallback = SingletonMongoDBCallback.singleton;
		addPropsCollections();	
	}
	
	public DefaultRealEstateCrawlingCallback(List<String> collectionNames,int bathSize,DefaultRealEstateCrawlingCallback nextCallBack,Object... args){
		this.bathSize=bathSize;
		this.collectionNames=collectionNames;
		this.nextCallBack=nextCallBack;
		this.mongoDBCallback = SingletonMongoDBCallback.singleton;
		addPropsCollections();	
	}
	
	boolean initialized = false;
	@Override
	public void init(Object... args) throws Exception {
		//if(initialized) return;
		Map<String,Object> env = (Map<String,Object>)args[0];
		this.datasetSuffix = (String) env.get(Bootstrap.ENV_DATASET_SUFFIX);
		//this.datasetSuffix = (String) args[0];
		//this.initialized = true;
	}
	private void addPropsCollections() {
		if(this.bathSize!=0){
			if(collectionNames!=null&&collectionNames.size()>0){
				for(String collectionName:collectionNames){
					if(collectionName!=null && "".equals(collectionName.trim()) == false)
					this.mongoDBCallback.addPropsCollection(collectionName, bathSize);
				}
			}
		}else {
			if(collectionNames!=null&&collectionNames.size()>0){
				for(String collectionName:collectionNames){
					if(collectionName!=null && "".equals(collectionName.trim()) == false){
						if(collectionName.contains("build")){
							bathSize=DEFAULT_BUILD_SIZE;
						}else if(collectionName.contains("room")){
							bathSize=DEFAULT_ROOM_SIZE;
						}else{
							bathSize=DEFAULT_PROJ_SIZE;
						}
						this.mongoDBCallback.addPropsCollection(collectionName, bathSize);
					}
				}
			}
		}
		
	}
	@Override
	public void clean(Object... args) throws Exception {
	}
	
	protected abstract void extracting(String respStr) throws Exception;
	/*protected void extracting(InputStream respStream) throws Exception {}*/
}