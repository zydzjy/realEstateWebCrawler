package realEstateWebCrawler;

import java.util.Arrays;

import org.bson.Document;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoDB {

	public static void main(String[] args){
		MongoClient mongoClient = MongoClients.create(
		        MongoClientSettings.builder()
		                .applyToClusterSettings(builder ->
		                        builder.hosts(Arrays.asList(new ServerAddress("localhost",27027))))
		                .build());
		 MongoDatabase testDB = mongoClient.getDatabase("test");
		 MongoCollection<Document> collectionSrc = testDB.getCollection("heb_room20180625");
		 MongoCollection<Document> collectionDest = testDB.getCollection("heb_room20180624");
		 MongoCursor<Document> it = collectionSrc.find().iterator();
		 while(it.hasNext()){
			 collectionDest.insertOne(it.next());
		 }
		 System.out.println("done!");
	}
}
