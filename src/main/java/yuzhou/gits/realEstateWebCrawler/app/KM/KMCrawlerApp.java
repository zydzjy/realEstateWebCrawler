/*package yuzhou.gits.realEstateWebCrawler.app.KM;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.realEstateWebCrawler.app.Bootstrap;
import yuzhou.gits.realEstateWebCrawler.app.CrawlerApp;
import yuzhou.gits.realEstateWebCrawler.beans.KM.Building;
import yuzhou.gits.realEstateWebCrawler.beans.KM.Project;
import yuzhou.gits.realEstateWebCrawler.beans.KM.Room;
import yuzhou.gits.realEstateWebCrawler.configuration.KM.KMConfig;
import yuzhou.gits.realEstateWebCrawler.dataExtractor.BeanExtractedCallback;
import yuzhou.gits.realEstateWebCrawler.dataExtractor.DataExtractor;
import yuzhou.gits.realEstateWebCrawler.dataExtractor.DataExtractorJsoupImpl;
import yuzhou.gits.realEstateWebCrawler.http.HttpStrategy;
import yuzhou.gits.realEstateWebCrawler.http.OK3Http;

//http://www.kmhouse.org/lqt/ProjectIndex2.asp(昆明)
public class KMCrawlerApp implements CrawlerApp {
	private int batchSize = 500;
	List<String> allIds=new ArrayList<String>();
	private int beginPage=1;
	public void startup(Map<String, Object> env) throws Exception {
		if(env.get(Bootstrap.ENV_START_PAGENO)!=null){
			beginPage=Integer.parseInt(env.get(Bootstrap.ENV_START_PAGENO).toString());
		}
		extractProjects();
		this.projCallback.flush();
		this.buildCallback.flush();
		this.roomCallback.flush();
	}

	public KMCrawlerApp() {
		projCallback = new ProjectInDBCallback(1,1);
		buildCallback = new BuildingInDBCallback(batchSize,1);
		roomCallback = new RoomInDBCallback(batchSize,1);
		try {
			this.projCallback.init();
			this.buildCallback.init();
			this.roomCallback.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	HttpStrategy http = new OK3Http(10, 666666, 50);

	protected BeanExtractedCallback<Object> projCallback;
	protected BeanExtractedCallback<Object> buildCallback;
	protected BeanExtractedCallback<Object> roomCallback;
	DataExtractor dataExtractor = new DataExtractorJsoupImpl();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	public void extractProjects() {
		System.out.println("start connecting site:" + KMConfig.baseURL);
		int countPage=137;
		int preSaleLicense=3001;
		int bulidNo=3000001;
		int roomTypeNo=3000001;
		int currentPage=beginPage;
		String charset="gb2312";
		try{
			for(;currentPage<=countPage;currentPage++){
				System.out.println("访问页数"+(currentPage));
				String url = KMConfig.baseURL.replace("[page]",
						String.valueOf(currentPage));
				String  baseDocStr=(String)http.get(url, HttpStrategy.RespType.HTML,charset);
				Document baseDoc = Jsoup.parse(baseDocStr);
				Elements projListE = baseDoc.select(KMConfig.projListDataSelector);
				Iterator<Element> projListIt = projListE.iterator();
				while (projListIt.hasNext()) {
					try {
						Element e = projListIt.next();
						Map<String,String> proj =new HashMap<String,String>();
						dataExtractor.extractDataByCssSelectors(KMConfig.baseSelectorMap, e,proj);					
						String pid=e.select("tr td:nth-child(1)>a").attr("href");
						if(pid.contains("id=")){
							pid=pid.split("id=")[1].split("&")[0];
							proj.put("pid_attr_", pid);
						}else{
							continue;
						}	
						String operDate=sdf.format(new Date());
						proj.put("operDate", operDate);
						org.bson.Document docP=new org.bson.Document();
						dataExtractor.mapToDocument(proj, docP);
						docP.put("xuhao", preSaleLicense++);
						this.projCallback.doMongoCallback(docP);
						url = KMConfig.projectDetailBaseURL.replace("[pid]",
								pid);
						System.out.println(url);
						Document detailDoc = Jsoup.parse((String) http.get(url, HttpStrategy.RespType.HTML,charset));
						Elements bulidListE = detailDoc.select(KMConfig.bulidingTale).get(1).select(KMConfig.buildingListSelector);
						Iterator<Element> bulidListIt = bulidListE.iterator();
						while(bulidListIt.hasNext()){
							try{
								Element bulid = bulidListIt.next();
								Map<String,String> building=new HashMap<String,String>();
								dataExtractor.extractDataByCssSelectors(KMConfig.projectDetailSelectorMap, bulid, building);
								String bid=bulid.select("tr td:nth-child(1) > a").attr("href");
								if(bid.contains("BId=")){
									bid=bid.split("BId=")[1];
									building.put("bid_attr_", bid);
								}else{
									continue;
								}
								Map<String,Integer> returnMap=this.extractBuildingList(building,pid,proj.get("projName"),bulidNo,roomTypeNo);
								bulidNo=returnMap.get("bulidNo");
								roomTypeNo=returnMap.get("roomTypeNo");
							}
							catch(Exception ee){
								ee.printStackTrace();
							}
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	protected Map<String,Integer> extractBuildingList(Map<String,String> projDetail,String pid,String projectName,int bulidNo, int roomTypeNo) {
		 Map<String,Integer> returnMap=new HashMap<String,Integer>();
			Map<String,String> headMap=new HashMap<String,String>();
		 String bid=projDetail.get("bid_attr_");
		String url = KMConfig.buildingListBaseURL.replace("[bid]",bid).replace("[pid]",pid);
		System.out.println(url);
		try {
			Document buildingListPageDoc =Jsoup.parse((String) http.get(url, HttpStrategy.RespType.HTML,"gb2312"));
			Elements buildingTrE = buildingListPageDoc.select(KMConfig.buildingSelector);
			Elements roomTypeTrE = buildingListPageDoc.select(KMConfig.roomTypeSelector);
			Iterator<Element> buildingTrIt = buildingTrE.iterator();
			Iterator<Element> roomTypeTrIt = roomTypeTrE.iterator();
			while (buildingTrIt.hasNext()) {
				Element e = buildingTrIt.next();
				Map<String,String> building=new HashMap<String,String>();
				dataExtractor.extractDataByCssSelectors(KMConfig.bulidDetailSelectorMap, e, building);
				building.put("projectName",projectName);
				building.put("bulidName",projDetail.get("bulidName"));
				building.put("bid_attr_",bid);
				building.put("pid_attr_",pid);
				String operDate=sdf.format(new Date());
				building.put("operDate", operDate);
				org.bson.Document docB=new org.bson.Document();
				dataExtractor.mapToDocument(building, docB);
				docB.put("xuhao", bulidNo++);
				this.buildCallback.doMongoCallback(docB);
			}
			while(roomTypeTrIt.hasNext()){
				Element e = roomTypeTrIt.next();
				String id=e.select("tr> td>a").attr("href");
				String hxArea=e.select("tr> td:nth-child(2)").text().split("�")[0].split("户型面积:")[1];
				if(id.contains("Id=")){
					id=id.split("Id=")[1].split("&")[0];
				}else{
					continue;
				}
				roomTypeNo=this.extractRoomList(id,roomTypeNo,pid,projectName,projDetail.get("bulidName"),hxArea);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnMap.put("bulidNo", bulidNo);
		returnMap.put("roomTypeNo", roomTypeNo);
		return returnMap;
	}
	
	private int extractRoomList(String  roomId, int roomTypeNo,String pid,String projName,String buildName,String hxArea) {
		String url = KMConfig.roomURL.replace("[pid]",pid).replace("[id]", roomId);
		Map<String,String> headMap=new HashMap<String,String>();
		try {
			Document roomDoc = Jsoup.parse((String) http.get(url, HttpStrategy.RespType.HTML,"gb2312"));
			Elements roomTrE = roomDoc.select(KMConfig.roomDetailSelector);
			Iterator<Element> roomTrIt = roomTrE.iterator();
			while (roomTrIt.hasNext()) {
				Element e = roomTrIt.next();
				Map<String,String> tmpRoom=new HashMap<String,String>();
				dataExtractor.extractDataByCssSelectors(KMConfig.roomDetailSelectorMap, e, tmpRoom);
				tmpRoom.put("hxArea",hxArea);
				tmpRoom.put("inArea",tmpRoom.get("inArea").toString().split("�")[0]);
				tmpRoom.put("poolArea",tmpRoom.get("poolArea").toString().split("�")[0]);
				tmpRoom.put("projName",projName);
				tmpRoom.put("buildName",buildName);
				tmpRoom.put("id_attr_",roomId);
				tmpRoom.put("pid_attr_",pid);
				String operDate=sdf.format(new Date());
				tmpRoom.put("operDate", operDate);
				org.bson.Document docR=new org.bson.Document();
				docR.put("xuhao",roomTypeNo++);
				dataExtractor.mapToDocument(tmpRoom, docR);
				this.roomCallback.doMongoCallback(docR);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roomTypeNo;
	}
}
*/