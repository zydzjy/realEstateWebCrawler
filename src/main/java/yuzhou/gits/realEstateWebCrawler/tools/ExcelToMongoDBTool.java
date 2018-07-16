package yuzhou.gits.realEstateWebCrawler.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import yuzhou.gits.realEstateWebCrawler.app.IntoMongoDBCallback;

public class ExcelToMongoDBTool {
	public static final String newImport = "1>";
	public static final String excels = "2>";
	public static final String sheetCollection = "3>";
	public static final String collectionKeys = "4>";
	public static final String rootDir = "E:\\";

	public void loadCfg(String[] cfgs) throws Exception {
		for (int k = 0; k < cfgs.length; k++) {
			String cfg = cfgs[k];
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(cfg), "GBK"));
			try {
				String aLine = "";
				String currCmd = "";
				MongoClient client = MongoClients.create(MongoClientSettings.builder()
						.applyToClusterSettings(builder -> builder.hosts(Arrays.asList(
										new ServerAddress("localhost", 27027)))).build());
				IntoMongoDBCallback mongodbCallback = 
						new IntoMongoDBCallback();
				mongodbCallback.init(client,"test");
				String[] excels = null;
				//String sheetCollection = null;
				String[] collectionKeys = null;
				String collectionName = null;
				Map<String, Integer> nameIdxMap = null;
				String sheetName = null;
				while ((aLine = buffReader.readLine()) != null) {
					aLine = aLine.trim();
					if (aLine.endsWith(">")) {
						currCmd = aLine;
						if (currCmd.equals(ExcelToMongoDBTool.newImport)) {
							Object[] mongodbAras = { collectionName, 6000 };
							mongodbCallback.addPropsCollection(mongodbAras);
							System.out.println(collectionName);
							this.toMongoDB(excels, sheetName, nameIdxMap, collectionName, mongodbCallback);
						} else {
							continue;
						}
					} else {
						if (currCmd.equals(ExcelToMongoDBTool.excels)) {
							excels = aLine.split(",");
						} else if (currCmd.equals(ExcelToMongoDBTool.sheetCollection)) {
							String[] vals = aLine.split("\\|");
							sheetName = vals[0];
							collectionName = vals[1];
						} else if (currCmd.equals(ExcelToMongoDBTool.collectionKeys)) {
							collectionKeys = aLine.split(",");
							nameIdxMap = new HashMap<String, Integer>();
							for (int i = 0; i < collectionKeys.length; i++) {
								nameIdxMap.put(collectionKeys[i], i + 1);
							}
						} else {}
					}
				}
			} catch (Exception e) {
				throw e;
			} finally {
				buffReader.close();
			}
		}
	}

	public static void main(String[] args) {
		try {
			/*String[] cfgs = { "E:\\crawlerData\\data\\cs", "E:\\crawlerData\\data\\fssd", "E:\\crawlerData\\data\\hzbl", 
					"E:\\crawlerData\\data\\hzdyw", "E:\\crawlerData\\data\\hzhd", "E:\\crawlerData\\data\\tc" };
			*/
			String[] cfgs = {"E:\\tempWorkspace\\codes\\realEstateWebCrawler\\src\\main\\java"
					+ "\\yuzhou\\gits\\realEstateWebCrawler\\tools\\zh"};
			new ExcelToMongoDBTool().loadCfg(cfgs);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void toMongoDB(String[] excels, String sheetName, Map<String, Integer> nameIdxMap, String collectionName,
			IntoMongoDBCallback mongodbCallback) throws Exception {

		DataSource src = new ConsecutiveExcelDataSource(rootDir, excels, sheetName, nameIdxMap);
		src.init();
		while (src.next()) {
			Map<String, String> propsMap = new HashMap<String, String>();
			Iterator<Entry<String, Integer>> it = nameIdxMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Integer> entry = it.next();
				String name = entry.getKey();
				propsMap.put(name, (String) src.getObjVal(name)[0]);
			}
			Object[] maps = {collectionName, "1","1","1",propsMap};
			mongodbCallback.doCallback(maps);
		}
		mongodbCallback.flush();
	}
}