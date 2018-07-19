package yuzhou.gits.realEstateWebCrawler.realEstateCrawl;

import java.util.ArrayList;
import java.util.List;

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
	protected List<String> collectionNames=new ArrayList<String>();
	protected DefaultRealEstateCrawlingCallback nextCallBack;
	protected int bathSize=0;
	private final int DEFAULT_PROJ_SIZE=1;
	private final int DEFAULT_BUILD_SIZE=10;
	private final int DEFAULT_ROOM_SIZE=500;
	
	protected void _doCallback(WebCrawlingTask task) throws Exception {
		this.currPageNo = (Integer)this.crawlTask.getPathContext()
				.getAttr(Constants.PAGE_CURRENT_NO);
		String respStr = (String)(task.getCrawling().getResponse());
		this.extracting(respStr);
	}
	
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
	
	private void addPropsCollections() {
		if(this.bathSize!=0){
			if(collectionNames!=null&&collectionNames.size()>0){
				for(String collectionName:collectionNames){
					this.mongoDBCallback.addPropsCollection(collectionName, bathSize);
				}
			}
		}else {
			if(collectionNames!=null&&collectionNames.size()>0){
				for(String collectionName:collectionNames){
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

	@Override
	public void init(Object... args) throws Exception {
		this.datasetSuffix = (String)args[0];
	}
	@Override
	public void clean(Object... args) throws Exception {
		if(collectionNames!=null&&collectionNames.size()>0){
			for(String collectionName:collectionNames){
				this.mongoDBCallback.flush(collectionName);
			}
		}
		if(nextCallBack!=null){
			nextCallBack.clean(args);
		}
	}
	
	protected abstract void extracting(String respStr) throws Exception ;
}