package yuzhou.gits.realEstateWebCrawler.app.ShangHai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class PresaleExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected BuildingExtractCallback buildingExtractCallback = 
			new BuildingExtractCallback();
	private String presaleCollectionName = "shanghai_presale";
	@Override
	public void init(Object... args) throws Exception {
		super.init(args);
		this.presaleCollectionName += this.datasetSuffix;
		this.mongoDBCallback.addPropsCollection(this.presaleCollectionName,10);
		this.buildingExtractCallback.init(args);
	}
	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.presaleCollectionName );
		this.buildingExtractCallback.clean(args);
	}
	@Override
	protected void extracting(String respStr) throws Exception {
		JsonElement baseJson = new JsonParser().parse(respStr);
		int currCount = 1;
		
		if(baseJson != null && baseJson.isJsonNull() == false){
			Map<String,String> project = (Map<String,String>)this.crawlTask.getPathContext()
					.getAttr("project");
			String developer = (String)this.crawlTask.getPathContext()
					.getAttr("developer");
			Map<String,List<JsonObject>> buidlingListMap = new HashMap<String,
					List<JsonObject>>();
			JsonElement priceInformationListJson = baseJson.getAsJsonObject().get("priceInformationList");
			if(priceInformationListJson != null && priceInformationListJson.isJsonNull() == false){
				JsonArray priceInformationList = priceInformationListJson.getAsJsonArray();
				for(int i=0;i<priceInformationList.size();i++){
					JsonElement arr = priceInformationList.get(i);
					if(arr != null && arr.isJsonNull() == false){ 
						JsonArray oArr = arr.getAsJsonArray();
						for(int j=0;j<oArr.size();j++){
							JsonObject o = oArr.get(j).getAsJsonObject();
							String start_id = o.get("start_id").getAsString();
							List<JsonObject> list = buidlingListMap.get(start_id);
							if(list == null){
								list = new ArrayList<JsonObject>();
								buidlingListMap.put(start_id, list);
							}
							list.add(o);
						}
					}
				}
			}
			
			JsonElement startUnitListJson = baseJson.getAsJsonObject().get("startUnitList");
			if(startUnitListJson != null && startUnitListJson.isJsonNull() == false){
				JsonArray startUnitList = startUnitListJson.getAsJsonArray();
				for(int i=0;i<startUnitList.size();i++){
					JsonObject o = startUnitList.get(i).getAsJsonObject();
					Map<String,String> preSalePropsMap = new HashMap<String,String>();
					//System.out.println(o.toString());
					this.extractor.extractDataByJson(ShangHaiConfig.preSaleJsonMap, o, 
							preSalePropsMap);
					preSalePropsMap.put("adminArea", project.get("adminArea"));
					preSalePropsMap.put("developer", developer);
					preSalePropsMap.put("projName", project.get("projName"));
					Object[] callbackArgs = { this.presaleCollectionName,"", currPageNo,
							currCount, preSalePropsMap};
					this.mongoDBCallback.doCallback(callbackArgs);
					
					String start_id = o.get("start_id").getAsString();
					List<JsonObject> buildingList = buidlingListMap.get(start_id);
					if(buildingList != null){
						Iterator<JsonObject> it = buildingList.iterator();
						while(it.hasNext()){
							JsonObject json = it.next();
							String building_id = json.get("building_id").getAsString();
							String building_name = json.get("building_name").getAsString();
							String hign_refprice =  json.get("hign_refprice") != null ? 
									(json.get("hign_refprice").isJsonNull() ? "" : json.get("hign_refprice").getAsString()) : "";
							String low_refprice = json.get("low_refprice") != null ? 
									(json.get("low_refprice").isJsonNull() ? "" : json.get("low_refprice").getAsString()) : "";
							String reference_price = json.get("reference_price") != null ? 
											(json.get("reference_price").isJsonNull() ? "" : json.get("reference_price").getAsString()) : "";
							String highLowRefPrice = !"".equals(reference_price) ? reference_price :
								hign_refprice + "/" + low_refprice;
							WebCrawlingTask buidlingTask = this.deriveNewTask("", false, ShangHaiConfig.buildingURL, 
									this.buildingExtractCallback); 
							String[] postFormData = {"buildingID", building_id};
							buidlingTask.getCrawling().setPostFormData(postFormData);
							buidlingTask.getPathContext().setAttr("buildingName", building_name);
							buidlingTask.getPathContext().setAttr("presale", preSalePropsMap);
							buidlingTask.getPathContext().setAttr("highLowRefPrice", highLowRefPrice);
							this.executor.execute(buidlingTask);
							
						}
					}
				}
			}
		}
	}
}
