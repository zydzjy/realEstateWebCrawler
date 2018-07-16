package yuzhou.gits.realEstateWebCrawler.app;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class AppConfig {
	public final static String mongodbHost = "mongodb.host";
	public final static String mongodbPort = "mongodb.port";
	public final static String mongodbDbName = "mongodb.dbname";
	public void loadCfg(String cfgPath) throws Exception {
		properties.load(new FileInputStream(new File(cfgPath)));
	}
	
	protected Properties properties;
}
