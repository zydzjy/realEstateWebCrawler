package yuzhou.gits.realEstateWebCrawler.app;

import java.util.Arrays;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoDBUtil {

	public static MongoClient getClient(){
		return MongoClients.create(MongoClientSettings.builder()
				.applyToClusterSettings(builder -> builder.hosts(Arrays.asList(
				new ServerAddress("localhost", 27027))))
				.build());
	}
}
