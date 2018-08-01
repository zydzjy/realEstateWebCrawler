package yuzhou.gits.realEstateWebCrawler.app.SJZ;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class SJZConfig extends BaseCfg{
	public static final String contentURL="http://www.sjzfgj.gov.cn/plus/";
	public final static String projectListBaseURL = "http://www.sjzfgj.gov.cn/plus/scxx_zxlp.php?pageno=[page]&type=[type]";
	public final static String projectListSelector = "#geren >table>tbody>tr:nth-child(n+3):nth-last-child(n+2)";
	public final static String projectURL="http://www.sjzfgj.gov.cn/plus/scxx_xmdetail.php?id=[id]";
	public final static Pattern pageInfoP = Pattern.compile("/(\\d+)");
	public final static String projPageInfoSelector = ".fenye";
	
	public static Map<String, String> projectListFirstSelectorsMap = new HashMap<String, String>();
	static {
		projectListFirstSelectorsMap.put("projName", "td:nth-child(1)>a");
		projectListFirstSelectorsMap.put("developer", "td:nth-child(2)");
		projectListFirstSelectorsMap.put("projAddr", "td:nth-child(3)");
		projectListFirstSelectorsMap.put("contactPhone", "td:nth-child(4)");
	}
	public final static String projectSelector = "table >tbody";
	public static Map<String, String> projectListSencodSelectorsMap = new HashMap<String, String>();
	static {
		projectListSencodSelectorsMap.put("rjl", "tr:nth-child(10)>td:nth-child(2)");
		projectListSencodSelectorsMap.put("tdyxqDate", "tr:nth-child(5)>td:nth-child(2)");
		projectListSencodSelectorsMap.put("sgksDate", "tr:nth-child(15)>td:nth-child(2)");
		projectListSencodSelectorsMap.put("sgjsDate", "tr:nth-child(15)>td:nth-child(4)");
		projectListSencodSelectorsMap.put("sjdw", "tr:nth-child(12)>td:nth-child(4)");
		projectListSencodSelectorsMap.put("sgdw", "tr:nth-child(13)>td:nth-child(4)");
		projectListSencodSelectorsMap.put("jldw", "tr:nth-child(14)>td:nth-child(4)");
		projectListSencodSelectorsMap.put("zjzmj", "tr:nth-child(25)>td:nth-child(4)");
	}
	
	public static final String leftMenuURL="http://www.sjzfgj.gov.cn/plus/cxda_ys_json_menu.php?id=[id]";
	
	public final static String bulidDetailSelector = "table:nth-child(3)>tbody";
	
	public static Map<String, String> buildDetailSelectorsMap = new HashMap<String, String>();
	static {
		buildDetailSelectorsMap.put("bulidName", "tr:nth-child(2)>td:nth-child(2)");
		buildDetailSelectorsMap.put("buildNum", "tr:nth-child(3)>td:nth-child(2)");
		buildDetailSelectorsMap.put("floorCount", "tr:nth-child(4)>td:nth-child(2)");
		buildDetailSelectorsMap.put("floorHigh", "tr:nth-child(5)>td:nth-child(2)");
		buildDetailSelectorsMap.put("kqDate", "tr:nth-child(13)>td:nth-child(4)");
		buildDetailSelectorsMap.put("rzDate", "tr:nth-child(14)>td:nth-child(4)");
		buildDetailSelectorsMap.put("tcw", "tr:nth-child(15)>td:nth-child(4)");
		buildDetailSelectorsMap.put("wyglf", "tr:nth-child(16)>td:nth-child(4)");
	}
	
	public final static String floorListSelector = "table>tbody>tr";
	

	public final static String roomListSelector = "td:nth-child(n+2)";
	
	public static Map<String, String> roomSelectorsMap = new HashMap<String, String>();
	static {
		roomSelectorsMap.put("roomNum", "td");
	}
	
	
	
	/*public final static String projectListBaseURL = "http://www.sjzfgj.gov.cn/plus/scxx_zxlp.php";
	public final static String prjectListPageURL = "http://wsfc.nsae.tyfdc.gov.cn/Firsthand/tyfc/publish/ProjListForPassed.do";
	public final static Map<String,String> basePostParams = new HashMap<String,String>();
	static {
		basePostParams.put("PerYear",""); basePostParams.put("PerFlowNO",""); basePostParams.put("PerType","0"); 
		basePostParams.put("ProType",""); basePostParams.put("ProName",""); basePostParams.put("OrgName",""); 
		basePostParams.put("HouseType","0"); basePostParams.put("Region","0"); basePostParams.put("ProAddress",""); 
		basePostParams.put("pageNo","1"); basePostParams.put("pageSize","15");
	}
	public final static String proTypeSelector = "#ProType option";
	public final static String projectPageInfoSelector = "#my_worklist_page";
	public final static String projectPageListSelector = "#dg tr:nth-child(n+2):nth-last-child(n+1)";
	
	public final static String projectBaseURL = "http://wsfc.nsae.tyfdc.gov.cn/Firsthand/tyfc/publish/p/ProjInfo.do?propid=[PROJECTID]";
	public final static String buildingPageURL = "http://wsfc.nsae.tyfdc.gov.cn/Firsthand/tyfc/publish/ProNBList.do";
	public static final String buildingDataListSelector = "#dg tr:nth-child(n+2):nth-last-child(n+1)";
	public static Map<String, String> cssSelectorsMap = new HashMap<String, String>();
	static {
		cssSelectorsMap.put("area", "td:nth-child(n+3)");
		cssSelectorsMap.put("developer", "td:last-child>a>span");
		cssSelectorsMap.put("planType", "td:nth-child(n+4)");
		cssSelectorsMap.put("licenseNo", "td:nth-child(n+5)");
		cssSelectorsMap.put("projId", "td:nth-child(n+4)");
		
		cssSelectorsMap.put("projName", "tr:has(td:matches(项目名称)) :eq(1)");
		cssSelectorsMap.put("projLocation", "tr:has(td:matches(项目座落)) :eq(1)");
		cssSelectorsMap.put("projAdvertiseName", "tr:has(td:matches(项目推广名)) :eq(3)");
		cssSelectorsMap.put("totalArea", "tr:has(td:matches(总规划建筑面积)) :eq(1)");
		cssSelectorsMap.put("greenRate", "tr:has(td:matches(绿化率)) :eq(1)");
		cssSelectorsMap.put("volumeRate", "tr:has(td:matches(容积率)) :eq(3)");
		cssSelectorsMap.put("startDate", "tr:has(td:matches(开工日期)) :eq(1)");
		cssSelectorsMap.put("finishDate", "tr:has(td:matches(竣工日期)) :eq(3)");
		cssSelectorsMap.put("avgPrice", "tr:has(td:matches(参考均价)) :eq(3)");
		cssSelectorsMap.put("decrationLevel", "tr:has(td:matches(装修标准)) :eq(1)");
		
		//cssSelectorsMap.put("_SUB_LINK_buildings", "a:matches(房产数据信息)");
		
		cssSelectorsMap.put("measureNo", "td:nth-child(n+2)>a>span");
		cssSelectorsMap.put("buildName", "td:nth-child(n+3)>a>span");
		cssSelectorsMap.put("floors", "td:nth-child(n+5)");
		cssSelectorsMap.put("buildLocation", "td:nth-child(n+6)");
		
		cssSelectorsMap.put("roomFloor", "span");
		cssSelectorsMap.put("roomNo", "span");
		cssSelectorsMap.put("saleState", "span");
		
		cssSelectorsMap.put("unitName", "#HouseInfo>table tr:nth-child(n+1) td:nth-child(n+1)");
		cssSelectorsMap.put("roomLocation", "#HouseInfo>table tr:nth-child(n+1) td:nth-child(n+1)");
		cssSelectorsMap.put("roomId", "#HouseInfo>table tr:nth-child(n+2) td:nth-child(n+1)");
		cssSelectorsMap.put("roomCode", "#HouseInfo>table tr:nth-child(n+2) td:nth-child(n+3)");
		cssSelectorsMap.put("constructionArea", "#HouseInfo>table tr:nth-child(n+3) td:nth-child(n+3)");
		cssSelectorsMap.put("innerArea", "#HouseInfo>table tr:nth-child(n+4) td:nth-child(n+1)");
		cssSelectorsMap.put("shareArea", "#HouseInfo>table tr:nth-child(n+4) td:nth-child(n+3)");
		cssSelectorsMap.put("roomUsage", "#HouseInfo>table tr:nth-child(n+5) td:nth-child(n+1)");
		cssSelectorsMap.put("roomType", "#HouseInfo>table tr:nth-child(n+5) td:nth-child(n+3)");
		cssSelectorsMap.put("zdArea", "#HouseInfo>table tr:nth-child(n+7) td:nth-child(n+3)");
		cssSelectorsMap.put("buildYear", "#HouseInfo>table tr:nth-child(n+6) td:nth-child(n+3)");
		
	}
	
	public final static String roomPageURL = "http://wsfc.nsae.tyfdc.gov.cn/Firsthand/tyfc/publish/probld/NBView.do?projectid=[PROJECTID]&nid=[NBID]";
	public final static String roomDetailURL = "http://wsfc.nsae.tyfdc.gov.cn/Firsthand/tyfc/publish/p/HouseBaseInfo.do?HID=[ROOMID]";
	public final static String bldlistSelector = "#bldlist span";*/
}
