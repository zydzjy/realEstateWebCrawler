package yuzhou.gits.realEstateWebCrawler.app.ShangHai;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class BuildingExtractCallback extends DefaultRealEstateCrawlingCallback {
	private String roomCollectionName = "shanghai_room";

	@Override
	public void init(Object... args) throws Exception {
		super.init(args);
		this.roomCollectionName += this.datasetSuffix;
		this.mongoDBCallback.addPropsCollection(this.roomCollectionName, 1000);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.roomCollectionName);
	}

	@Override
	protected void extracting(String respStr) throws Exception {
		JsonElement baseJson = new JsonParser().parse(respStr);
		int currCount = 1;

		if (baseJson != null && baseJson.isJsonNull() == false) {
			Map<String, String> project = (Map<String, String>) this.crawlTask.getPathContext().getAttr("project");
			Map<String, String> presale = (Map<String, String>) this.crawlTask.getPathContext().getAttr("presale");
			String highLowRefPrice = (String) this.crawlTask.getPathContext().getAttr("highLowRefPrice");
			String buildingName = (String) this.crawlTask.getPathContext().getAttr("buildingName");
			/*if(!"钦州北路218-27号".equalsIgnoreCase(buildingName)){
				return;
			}*/
			JsonElement moreInfoList = baseJson.getAsJsonObject().get("moreInfoList");
			if (moreInfoList != null && moreInfoList.isJsonNull() == false) {
				Set<Entry<String, JsonElement>> floors = moreInfoList.getAsJsonObject().entrySet();
				Iterator<Entry<String, JsonElement>> floorsIt = floors.iterator();
				while (floorsIt.hasNext()) {
					Entry<String, JsonElement> entry = floorsIt.next();
					String floorNo = entry.getKey();
					JsonElement rooms = entry.getValue();
					if (rooms != null && rooms.isJsonNull() == false) {
						JsonArray roomArr = rooms.getAsJsonArray();
						for (int i = 0; i < roomArr.size(); i++) {
							JsonObject roomJson = roomArr.get(i).getAsJsonObject();
							Map<String, String> roomPropsMap = new HashMap<String, String>();
							if(roomJson.get("room_number") == null || roomJson.get("room_number").isJsonNull() ||
									"".equals(roomJson.get("room_number").getAsString()))
								continue;
								
							//this.extractor.fillPropsNullVal(ShangHaiConfig.roomDetailJsonMap, roomPropsMap);
							this.extractor.extractDataByJson(ShangHaiConfig.roomDetailJsonMap, roomJson,
									roomPropsMap);
							roomPropsMap.put("floorNo", floorNo);
							roomPropsMap.put("buildingName", buildingName);
							roomPropsMap.put("highLowRefPrice", highLowRefPrice);
							roomPropsMap.put("presaleNo", presale.get("preSaleLicense"));
							roomPropsMap.put("projName", project.get("projName"));
							roomPropsMap.put("adminArea", project.get("adminArea"));
							
							Object[] callbackArgs = {BuildingExtractCallback.this.roomCollectionName, "",
									currPageNo, currCount, roomPropsMap };
							this.mongoDBCallback.doCallback(callbackArgs);
						}
					}
				}
			}
		}
	}
}
