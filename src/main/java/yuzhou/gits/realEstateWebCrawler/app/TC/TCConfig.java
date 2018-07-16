package yuzhou.gits.realEstateWebCrawler.app.TC;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class TCConfig extends BaseCfg {
	public final static String siteDomain = "http://222.92.5.35:8082";
	public final static String baseURL = "http://222.92.5.35:8082/fygl.aspx";
	public final static String projPageInfoSelector = "#AspNetPager1 > table > tbody > tr > td:nth-child(1)";
	public final static Pattern pageInfoP = Pattern.compile("(\\d+)页");
	
	public final static String projListSelector = "#ctl00 > table.ysxk > tbody > tr:nth-child(n+1):nth-last-child(n+1)";
	public final static String projDetailPageURLSelector = "td:nth-child(4) > div > span";
	public final static Pattern projDetailPageURLPattern = 
			Pattern.compile("'([^']*)'");
	public static Map<String,String> projListDataSelectorMap =
			new HashMap<String,String>();
	static {
		projListDataSelectorMap.put("projName", "td:nth-child(2) > div > span");
		projListDataSelectorMap.put("projLocation", "td:nth-child(3) > div > span");
	}
	public static final String buildingPageURL = "http://222.92.5.35:8082/fygl2.aspx?id=";
	
	public static final String buildListSelector = 
			"#ctl00 > table.ysxk > tbody > tr:nth-child(n+1):nth-last-child(n+1)";
	public static final String buildDetailURLSelector = "td:nth-child(7) > div > span";
	public static Map<String,String> buildSelectorMap =
			new HashMap<String,String>();
	static {
		buildSelectorMap.put("buildName", "td:nth-child(2) > div > span");
		buildSelectorMap.put("construction", "td:nth-child(3) > div > span");
		buildSelectorMap.put("floors", "td:nth-child(4) > div > span");
		buildSelectorMap.put("units", "td:nth-child(5) > div > span");
		buildSelectorMap.put("rooms", "td:nth-child(6) > div > span");
	}
	
	public static final String roomsSelector = 
			"#htmlContent > div > table > tbody > tr > td:nth-child(n+2):nth-last-child(n+1) > div";
	public static Map<String,String> roomSelectorMap =
			new HashMap<String,String>();
	static {
		roomSelectorMap.put("roomNo", "#lbl_RoomNo");
		roomSelectorMap.put("unitNo", "#lbl_danyuan");
		//roomSelectorMap.put("floorNo", "#tb_first > tbody > tr:nth-child(2) > td:nth-child(2)");
		roomSelectorMap.put("constructionArea", "#lbl_BuildArea");
		roomSelectorMap.put("innerArea", "#lbl_InnerArea");
		roomSelectorMap.put("shareArea", "#lbl_ShareArea");
		roomSelectorMap.put("floorHeight", "#lbl_floorheight");
		roomSelectorMap.put("roomUsage", "#lbl_roomuse");
		roomSelectorMap.put("roomStruct", "#lbl_Rroomstruct1");
		roomSelectorMap.put("balcony", "#lbl_BalconyDescription");
		roomSelectorMap.put("roomType", "#lbl_Suite");
		roomSelectorMap.put("planningPrice", "#lbl_protocolPrice");
		roomSelectorMap.put("openDate", "#lbl_time");
	}
	
	/*
	tdunsaled -- 绿色
	tdqianding --橘色
	tdunablesale --黄色
	tdzanbushou --棕色
	tdsaled --红色
	tddongjie --灰色
	*/
}
