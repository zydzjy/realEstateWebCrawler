package yuzhou.gits.realEstateWebCrawler.app.HZHY;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
	protected String buildingCollectionName = "hzhy_building";
	public BuildingListExtractCallback(){
		this.mongoDBCallback = SingletonMongoDBCallback.singleton;
	}
	@Override
	public void init(Object... args) throws Exception {
		super.init(args);
		this.buildingCollectionName=this.buildingCollectionName+this.datasetSuffix;
		this.mongoDBCallback.addPropsCollection(this.buildingCollectionName,60);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.buildingCollectionName);
	}

	
	private String getRoomColor(String colorCss) {
		String color = "";
		switch (colorCss) {
		case "#ff0000":
			color = "红色不可售";
			break;
		case "#0000ff":
			color = "蓝色已售";
			break;
		case "#804040":
			color = "棕色已查封";
			break;
		case "#C26060":
			color = "棕红色已抵押";
			break;
		case "#000000":
			color = "黑色已抵押,已查封";
			break;
		case "#009900":
			color = "深绿色可售";
			break;
		case "#00cc00":
			color = "浅绿色现房可售";
			break;
		default:
			color = "未知颜色";
			break;
		}
		return color;
	}
	@Override
	protected void extracting(String respStr) throws Exception {
		Document buildDetailDoc = Jsoup.parse((String)respStr);
		int currPageNo = (Integer)this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);
		int currCount = (Integer)this.crawlTask.getPathContext().getAttr("currCount");
		Map<String,String> building=(Map<String, String>) this.crawlTask.getPathContext().getAttr("building");
		Map<String,String> project=(Map<String, String>) this.crawlTask.getPathContext().getAttr("project");
		Elements roomListE = buildDetailDoc.select(HZHYConfig.roomListSelector);
		Iterator<Element> roomListIt = roomListE.iterator();
		while(roomListIt.hasNext()){
			Element floorListE=roomListIt.next();
			Elements roomList=floorListE.select("td:nth-child(n+2)");
			Iterator<Element> roomsIt = roomList.iterator();
			while(roomsIt.hasNext()){
				Element roomE=roomsIt.next();
				String roomColor=getRoomColor(roomE.selectFirst("td").attr("bgcolor"));
				String roomDetailUrl=roomE.selectFirst("td a").attr("href");
				String roomNo=roomE.selectFirst("td a font").text();
				if(!roomDetailUrl.contains("buildingid")){
					roomDetailUrl=roomDetailUrl.replace("&=", "&buildingid=");
				}
				String roomDetailURL = HZHYConfig.siteDomain+ roomDetailUrl;
				WebCrawling roomDetailCrawling = new WebCrawling(HttpMethod.GET, null, roomDetailURL, 600000,
						600000, 600000);
				final int countCurr=currCount;
				WebCrawlingTask roomDetailCrawlTask = this.deriveNewTask("", true, roomDetailCrawling,
						new WebResourceCrawlingCallback() {
							@Override
							public void doCallback(WebCrawlingTask task) throws Exception {
								Document roomDetailDoc = Jsoup
										.parse((String) (task.getCrawling().getResponse()));
								Map<String,String> roomMap=new HashMap<String,String>();
								roomMap.put("projName",project.get("projName"));
								roomMap.put("licenseNo",project.get("licenseNo"));
								
								roomMap.put("buildName",building.get("buildName"));
								roomMap.put("upFloor",building.get("upFloor"));
								roomMap.put("underFloor",building.get("underFloor"));
								roomMap.put("constrDate",building.get("constrDate"));
								
								roomMap.put("roomNo",roomNo);
								roomMap.put("roomColor",roomColor);
								//楼栋名称
								roomMap.put("TextBox1",roomDetailDoc.selectFirst("#TextBox1").val());
								//层高
								roomMap.put("TxtRoomhigh",roomDetailDoc.selectFirst("#TxtRoomhigh").val());
								//楼层
								roomMap.put("Txthsfloor",roomDetailDoc.selectFirst("#Txthsfloor").val());
								//房号
								roomMap.put("Txthsnumber2",roomDetailDoc.selectFirst("#Txthsnumber2").val());
								//户型
								roomMap.put("DdlModel",roomDetailDoc.selectFirst("#DdlModel option[selected=selected]").text());
								//房屋朝向
								roomMap.put("DDLtrend",roomDetailDoc.selectFirst("#DDLtrend option[selected=selected]").text());
								//房屋功能
								roomMap.put("DDLhsfunction",roomDetailDoc.selectFirst("#DDLhsfunction option[selected=selected]").text());
								//具体功能
								roomMap.put("DDLhsfunction2",roomDetailDoc.selectFirst("#DDLhsfunction2 option[selected=selected]").text());
								//房屋用途
								roomMap.put("DDLpurpose",roomDetailDoc.selectFirst("#DDLpurpose option[selected=selected]").text());
								//房屋结构
								roomMap.put("DDLstructure",roomDetailDoc.selectFirst("#DDLstructure option[selected=selected]").text());
								//是否回迁
								roomMap.put("DDLmovestate",roomDetailDoc.selectFirst("#DDLmovestate option[selected=selected]").text());
								//是否自用
								roomMap.put("DDLoneselfstate",roomDetailDoc.selectFirst("#DDLoneselfstate option[selected=selected]").text());
								//是否公建配套
								roomMap.put("DDLpublicState",roomDetailDoc.selectFirst("#DDLpublicState option[selected=selected]").text());
								//预售总建筑面积
								roomMap.put("Txtproportion",roomDetailDoc.selectFirst("#Txtproportion").val());
								//预售套内面积
								roomMap.put("Txtproportion2",roomDetailDoc.selectFirst("#Txtproportion2").val());							
								//预售分摊面积
								roomMap.put("Txtproportion3",roomDetailDoc.selectFirst("#Txtproportion3").val());
								//实测建筑面积
								roomMap.put("TxtXfp1",roomDetailDoc.selectFirst("#TxtXfp1").val());
								//实测套内建筑面积
								roomMap.put("TxtXfp2",roomDetailDoc.selectFirst("#TxtXfp2").val());
								//实测分摊面积
								roomMap.put("TxtXfp3",roomDetailDoc.selectFirst("#TxtXfp3").val());
								//封闭阳台
								roomMap.put("TxtterraceCount",roomDetailDoc.selectFirst("#TxtterraceCount").val());
								//非封闭阳台
								roomMap.put("Txtterrace2count",roomDetailDoc.selectFirst("#Txtterrace2count").val());
								//厨房
								roomMap.put("TxtkitchenCount",roomDetailDoc.selectFirst("#TxtkitchenCount").val());
								//卫生间
								roomMap.put("TxthygieneCount",roomDetailDoc.selectFirst("#TxthygieneCount").val());
								//房屋状态
								roomMap.put("DDLState",roomDetailDoc.selectFirst("#DDLState option[selected=selected]").text());
								//备案价格
								roomMap.put("Txtbaprice",roomDetailDoc.selectFirst("#Txtbaprice").val());
								//抵押信息
								roomMap.put("Txtholdnote",roomDetailDoc.selectFirst("#Txtholdnote").val());
								//查封信息
								roomMap.put("Txtsealupnote",roomDetailDoc.selectFirst("#Txtsealupnote").val());
								Object[] callbackArgs = { buildingCollectionName, "", currPageNo, countCurr,
										roomMap };
								mongoDBCallback.doCallback(callbackArgs);
							}

							@Override
							public void init(Object... args) throws Exception {								
							}

							@Override
							public void clean(Object... args) throws Exception {						
							}});
				
				this.executor.execute(roomDetailCrawlTask);
			}	
		}	
	}
}
