package yuzhou.gits.realEstateWebCrawler.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import yuzhou.gits.crawler.dataExtractor.BeanExtractedCallback;

public class IntoMongoDBCallback implements BeanExtractedCallback {
	public static final SimpleDateFormat sdf_yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
	Map<String, Integer> collectionBatchSizeMap = new HashMap<String, Integer>();
	Map<String, List<Map<String, Object>[]>> propsCollectionMap = new HashMap<String, List<Map<String, Object>[]>>();
	MongoClient mongoClient = null;
	public IntoMongoDBCallback() {}

	MongoDatabase db = null;
	protected List<Map<String, Object>[]> currCollectionIntoDB = null;
	protected String currCollectionName = "";
	MongoCollection<Document> collection = null;

	@Override
	public void doCallback(Object object) throws Exception {
		String currTime = sdf_yyyyMMddHHmmss.format(new Date());
		Object[] args = (Object[]) object;
		String collectionName = (String) args[0];
		
		this.currCollectionIntoDB = this.propsCollectionMap.get(collectionName);
		//TODO: populate meta info
		Map<String,Object> metaInfo = new HashMap<String,Object>();
		metaInfo.put("_taskId", (String)args[1]);
		metaInfo.put("_basePageNo", args[2]);
		metaInfo.put("_baseCount",  args[3]);
		metaInfo.put("_insertTime", currTime);
		Map<String,Object>[] maps = new Map[args.length-3];
		maps[0] = metaInfo;
		for(int i=4;i<args.length;i++){
			maps[i-3] = (Map<String, Object>) args[i];
		}
		this.currCollectionIntoDB.add(maps);
		this.currCollectionName = collectionName;
		this.collection = db.getCollection(currCollectionName);
		int batchSize = (Integer) this.collectionBatchSizeMap.get(collectionName);
		if (this.currCollectionIntoDB.size() >= batchSize) {
			try {
				this.persist();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				this.currCollectionIntoDB.clear();
			}
		}
	}

	protected void persist() throws Exception {
		Iterator<Map<String, Object>[]> it = this.currCollectionIntoDB.iterator();
		List<Document> docList = new ArrayList<Document>();
		try {
			while (it.hasNext()) {
				Document doc = new Document();
				Map<String, Object>[] maps = it.next();
				for(int i=0;i<maps.length;i++){
					Iterator<Entry<String, Object>> entryIt = maps[i].entrySet().iterator();
					while (entryIt.hasNext()) {
						Entry<String, Object> entry = entryIt.next();
						String propName = entry.getKey();
						Object propVal = entry.getValue();
						doc.append(propName, propVal);
					}
				}
				docList.add(doc);
			}
			
			
			if(docList.size() > 0){
				collection.insertMany(docList);
			}
		} finally {
			this.currCollectionIntoDB.clear();
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		this.mongoClient = (MongoClient)args[0];
		this.db = mongoClient.getDatabase((String)args[1]);
	}
	
	//{{collectionName,batchSize},{...}}
	public void addPropsCollection(Object... args){
		for (int i = 0; i < args.length; i = i + 2) {
			this.collectionBatchSizeMap.put((String) args[i], (Integer) args[i + 1]);
			this.propsCollectionMap.put((String) args[i], new ArrayList<Map<String, Object>[]>());
		}
	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub

	}

	public void flush(String collectionName) {
		
		this.currCollectionIntoDB = propsCollectionMap.get(collectionName);
		if(currCollectionIntoDB == null) return;
		if(currCollectionIntoDB.size() > 0){
			try{
				this.collection = db.getCollection(collectionName);
				this.persist();
				currCollectionIntoDB.clear();
			}
			catch(Exception e){e.printStackTrace();}
			System.out.println(collectionName+" flush done!");
			//finally{
			//}
		}
	}
	
	@Override
	public void flush() {
		Iterator<Entry<String, List<Map<String, Object>[]>>> it =
				this.propsCollectionMap.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, List<Map<String, Object>[]>> entry = 
					it.next();
			String collectionName = entry.getKey();
			this.flush(collectionName);
		}
		System.out.println("all flush done!");
	}
}