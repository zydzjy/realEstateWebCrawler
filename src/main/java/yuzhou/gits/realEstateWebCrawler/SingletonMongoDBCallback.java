package yuzhou.gits.realEstateWebCrawler;

import yuzhou.gits.realEstateWebCrawler.app.IntoMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.app.MongoDBUtil;

public class SingletonMongoDBCallback extends IntoMongoDBCallback{
	public static IntoMongoDBCallback singleton;
	static {
		singleton = new SingletonMongoDBCallback();
		try {
			singleton.init(MongoDBUtil.getClient(),"test");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}