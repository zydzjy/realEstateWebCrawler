package yuzhou.gits.realEstateWebCrawler.tools;

import java.util.Arrays;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.DBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoDBDataSource implements DataSource {

	MongoClient mongoClient = null;
	MongoDatabase db = null;
	MongoCollection<Document> collection = null;
	private MongoCursor<Document> cursor = null; 
	private Document currDoc = null;
	@Override
	public Object getSrc() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getObjVal(String...objNames) throws Exception {
		String[] vals = new String[objNames.length];
		for(int i=0;i<objNames.length;i++){
			Object _val = this.currDoc.get(objNames[i]);
			if(_val == null){
				vals[i] = null;
			}
			else{
				vals[i] = _val.toString();
			}
		}
		return vals;
	}

	@Override
	public boolean next() throws DataSrcDrainedReachedException {
		if(this.cursor.hasNext()){
			this.currDoc = this.cursor.next();
			return true;
		}else{
			throw new DataSrcDrainedReachedException();
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		String host = (String)args[0];
		int port = (Integer)args[1];
		//String user = "";
		//String password = "";
		this.mongoClient = MongoClients.create(MongoClientSettings.builder()
				.applyToClusterSettings(builder -> 
				builder.hosts(Arrays.asList(new ServerAddress(host, port))))
				.build());
	}
	
	public void findCollection(String dbName,String collectionName){
		this.db = mongoClient.getDatabase(dbName);
		this.collection = this.db.getCollection(collectionName);
		FindIterable<Document> iterables = collection.find();
		this.cursor = iterables.iterator();  
	}
	
	@Override
	public void destroy() throws Exception {
		this.mongoClient.close();
	}

	public void findCollection(String dbName, String collectionName, String filterStr) {
		this.db = mongoClient.getDatabase(dbName);
		this.collection = this.db.getCollection(collectionName);
		Document filter = Document.parse(filterStr);
		FindIterable<Document> iterables = collection.find(filter);
		this.cursor = iterables.iterator();  
		
	}
}
