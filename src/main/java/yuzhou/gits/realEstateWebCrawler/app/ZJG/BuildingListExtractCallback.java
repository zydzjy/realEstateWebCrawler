package yuzhou.gits.realEstateWebCrawler.app.ZJG;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.SingletonMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.app.IntoMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class BuildingListExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected IntoMongoDBCallback mongoDBCallback;
	protected String buildingCollectionName = "zjg_building";
	protected RoomExtractCallback roomCallback = new RoomExtractCallback();

	public BuildingListExtractCallback() {
		this.mongoDBCallback = SingletonMongoDBCallback.singleton;
	}

	@Override
	public void init(Object... args) throws Exception {
		super.init(args);
		this.buildingCollectionName=this.buildingCollectionName+this.datasetSuffix;
		this.mongoDBCallback.addPropsCollection(this.buildingCollectionName, 60);
		this.roomCallback.init(args);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.buildingCollectionName);
		this.roomCallback.clean(args);
	}

	public void mapExtendMap(Map<String, String> fatherMap, Map<String, Object> sonMap) {
		Iterator<Map.Entry<String, String>> it = fatherMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			String propName = entry.getKey();
			String propVal = entry.getValue();
			sonMap.put(propName, propVal);
		}
	}

	private JsonArray getHouseJosnArray(String houseListStr) {
		JsonElement jelement = new JsonParser().parse(houseListStr.trim());
		String coutomStr = jelement.getAsJsonObject().get("custom").getAsString();
		JsonElement coutomlement = new JsonParser().parse(coutomStr.trim());
		return coutomlement.getAsJsonArray();
	}

	private JsonObject getProjDetailJsonObject(String projectDetailStr) {
		JsonElement jelement = new JsonParser().parse(projectDetailStr.trim());
		String coutomStr = jelement.getAsJsonObject().get("custom").getAsString();
		JsonElement coutomlement = new JsonParser().parse(coutomStr.trim());
		return coutomlement.getAsJsonObject();
	}

	public JsonArray getProjListJsonArray(String jsonStr) {
		JsonElement jelement = new JsonParser().parse(jsonStr.trim());
		String coutomStr = jelement.getAsJsonObject().get("custom").getAsString();
		JsonElement coutomlement = new JsonParser().parse(coutomStr.trim());
		return coutomlement.getAsJsonObject().get("Table").getAsJsonArray();
	}

	private String getValueByKey(JsonObject jsonObject, String key) {
		String value = "";
		try {
			value = jsonObject.get(key).getAsString();
		} catch (Exception e) {

		}
		return value;
	}

	public String getDocumentStr(String url) throws Exception {
		final Map<String, String> map = new HashMap<String, String>();
		WebCrawling urlCrawling = new WebCrawling(HttpMethod.GET, null, url, 600000, 600000, 600000);
		WebCrawlingTask projectDetailCrawlTask = this.deriveNewTask("", true, urlCrawling,
				new WebResourceCrawlingCallback() {
					@Override
					public void doCallback(WebCrawlingTask task) throws Exception {
						String docStr = (String) (task.getCrawling().getResponse());
						map.put("docStr", docStr);
					}

					@Override
					public void init(Object... args) throws Exception {
					}

					@Override
					public void clean(Object... args) throws Exception {
					}
				});
		this.executor.execute(projectDetailCrawlTask);
		return map.get("docStr");
	}

	@Override
	protected void extracting(String respStr) throws Exception {
		int currPageNo = (Integer) this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);

		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;
		JsonArray arrayObj = getProjListJsonArray(respStr);
		if (arrayObj == null) {
			return;
		}
		try {
			for (int i = 0; i < arrayObj.size(); i++) {
				JsonObject jOb = arrayObj.get(i).getAsJsonObject();
				// 遍历
				Map<String, String> parentBuild = new HashMap<String, String>();
				parentBuild.put("pageXuhao", getValueByKey(jOb, "num"));
				parentBuild.put("yszh", getValueByKey(jOb, "yszh"));
				parentBuild.put("projName", getValueByKey(jOb, "projectname"));
				System.out.println(getValueByKey(jOb, "projectname") + "(" + crawlerId + "," + currPageNo + ","
						+ (currCount) + ")");
				parentBuild.put("taoshuCount", getValueByKey(jOb, "cjts"));
				parentBuild.put("areaCount", getValueByKey(jOb, "cjmj"));
				String projecturl = getValueByKey(jOb, "projecturl").replaceAll("&amp;", "&");
				if (!projecturl.contains("spfguid") || !projecturl.contains("yszguid")) {
					System.out.println("projecturl中不包含spfguid或者不包含yszguid" + projecturl);
					continue;
				}
				String spfguid = projecturl.split("[?]")[1].split("&")[0].split("=")[1];
				String yszguid = projecturl.split("[?]")[1].split("&")[1].split("=")[1];
				parentBuild.put("spfguid", spfguid);
				parentBuild.put("yszguid", yszguid);
				String projDetailUrl = ZJGConfig.projDetailURL.replace("[spfguid]", spfguid).replace("[yszguid]",
						yszguid);
				String projectDetailStr = getDocumentStr(projDetailUrl);
				JsonObject projectDetail = this.getProjDetailJsonObject(projectDetailStr);
				parentBuild.put("yszh2", getValueByKey(projectDetail, "yszh"));
				parentBuild.put("projAddress", getValueByKey(projectDetail, "projectaddress"));
				parentBuild.put("projName2", getValueByKey(projectDetail, "projectname"));
				parentBuild.put("fzjg", getValueByKey(projectDetail, "ouname"));
				parentBuild.put("developer", getValueByKey(projectDetail, "deptname"));
				parentBuild.put("fzDate", getValueByKey(projectDetail, "opendate"));
				parentBuild.put("yskyh", getValueByKey(projectDetail, "openbank"));
				parentBuild.put("yskjgzh", getValueByKey(projectDetail, "openaccount"));
				parentBuild.put("xsPhone", getValueByKey(projectDetail, "deptphone"));
				String houseListUrl = ZJGConfig.houseListURL.replace("[spfguid]", spfguid).replace("[yszguid]",
						yszguid);
				String buildListStr = getDocumentStr(houseListUrl);
				JsonArray buildArray = this.getHouseJosnArray(buildListStr);
				if (buildArray == null) {
					System.out.println("houseArray为空转换错误");
					continue;
				}
				for (int j = 0; j < buildArray.size(); j++) {
					JsonObject buildO = buildArray.get(j).getAsJsonObject();
					Map<String, Object> building = new HashMap<String, Object>();
					mapExtendMap(parentBuild, building);
					building.put("sellCount", getValueByKey(buildO, "alreadysell"));
					building.put("noSellCount", getValueByKey(buildO, "cansell"));
					building.put("buildName", getValueByKey(buildO, "housenum"));
					Object[] callbackArgs = { buildingCollectionName, crawlerId, currPageNo, currCount, building };
					mongoDBCallback.doCallback(callbackArgs);
					String buildUrl = getValueByKey(buildO, "houseurl").replaceAll("&amp;", "&");
					if (!buildUrl.contains("guid=")) {
						System.out.println("buildUrl中不包含guid" + buildUrl);
						continue;
					}
					String buildGuid = buildUrl.split("guid=")[1];

					String roomUrl = ZJGConfig.roomListURL.replace("[guid]", buildGuid);

					WebCrawling roomCrawling = new WebCrawling(HttpMethod.GET, null, roomUrl, 600000, 600000, 600000);
					WebCrawlingTask roomCrawlTask = this.deriveNewTask("", true, roomCrawling, roomCallback);
					roomCrawlTask.getPathContext().setAttr("building", building);
					roomCrawlTask.getPathContext().setAttr("currCount", currCount);
					roomCrawlTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, currPageNo);

					this.executor.execute(roomCrawlTask);
				}
				currCount++;
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		
	}

}
