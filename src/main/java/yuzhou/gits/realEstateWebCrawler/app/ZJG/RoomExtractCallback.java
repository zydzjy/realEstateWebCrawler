package yuzhou.gits.realEstateWebCrawler.app.ZJG;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.SingletonMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class RoomExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected String roomCollectionName = "zjg_room";

	public RoomExtractCallback() {
		this.mongoDBCallback = SingletonMongoDBCallback.singleton;
	}

	@Override
	public void extracting(String respStr) throws Exception {
		int currPageNo = (Integer)this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);
		int currCount = (Integer)this.crawlTask.getPathContext().getAttr("currCount");
		Map<String,String> building=(Map<String, String>) this.crawlTask.getPathContext().getAttr("building");
		
		JsonElement jelement = new JsonParser().parse(respStr.trim());
		String coutomStr=jelement.getAsJsonObject().get("custom").getAsString();
		Document coutomlement = Jsoup.parse(coutomStr.trim());
		Elements houserTypeListE = coutomlement.select(ZJGConfig.houserTypeListSelector);
		Iterator<Element> houserTypeListIt = houserTypeListE.iterator();
		while(houserTypeListIt.hasNext()){
			Element houseType = houserTypeListIt.next();
			//因为结构相同所以分布取
			Element bulidTypeE=houseType.selectFirst(ZJGConfig.bulidTypeSelector);//用于获取bulidType楼栋类型
			Elements floorListE=houseType.select(ZJGConfig.floorListSelector);
			if(floorListE==null){
				System.out.println("floorListE为空");
				continue;
			}
			Iterator<Element> floorListEt = floorListE.iterator();
			while(floorListEt.hasNext()){
				Element floor = floorListEt.next();
				Element roomFloor=floor.selectFirst(ZJGConfig.roomFloorSelector);
				String roomFloorStr=roomFloor.ownText();
				Elements roomList=floor.select(ZJGConfig.roomListDetailSelector);
				Iterator<Element> roomListDeatilIt = roomList.iterator();
				while(roomListDeatilIt.hasNext()){
					Element roomDeatil=roomListDeatilIt.next();
					String roomNum=roomDeatil.ownText();
					String roomColor=roomDeatil.attr("class");
					if(roomColor.equals("room_ks")){
						roomColor="未售黄色";
					}else if(roomColor.equals("room_ys")){
						roomColor="已售绿色";
					}
					String title=roomDeatil.attr("title");
					String jzAera=title.split("建筑面积：")[1].split("\n")[0];
					String tnArea=title.split("套内面积：")[1].split("\n")[0];
					String ftArea=title.split("分摊面积：")[1].split("\n")[0];
					String houseContrus="";
					if(title.split("房型结构：").length>1){
						houseContrus=title.split("房型结构：")[1];
					}
					Map<String,Object> room=new HashMap<String,Object>();
					room.put("yszh",building.get("yszh"));
					room.put("projName",building.get("projName"));
					room.put("buildName",building.get("buildName"));
					room.put("buildType",bulidTypeE.ownText());
					room.put("roomNum",roomNum);
					room.put("roomColor",roomColor);
					room.put("jzAera",jzAera);
					room.put("ftArea",ftArea);
					room.put("tnArea",tnArea);
					room.put("houseContrus",houseContrus);
					room.put("floorNum",roomFloorStr);
					Object[] callbackArgs = { roomCollectionName, crawlerId, currPageNo, currCount, room };
					mongoDBCallback.doCallback(callbackArgs);
				}		
			} 
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		super.init(args);
		this.roomCollectionName=this.roomCollectionName+this.datasetSuffix;
		this.mongoDBCallback.addPropsCollection(this.roomCollectionName,500);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.roomCollectionName);
	}

}