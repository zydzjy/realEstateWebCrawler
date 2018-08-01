package yuzhou.gits.realEstateWebCrawler.app.ShangHai;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class ShangHaiConfig extends BaseCfg {
	public static final String siteDomain = "http://202.109.79.219";
	public static final Pattern pageInfoP = Pattern.compile("(\\d+)");
	public static final String pageInfoSelector = "body > div > div.page_left.f_left > span.page_total > i";
	public static String  projListSelector = "body > table > tbody > tr:nth-child(n+1):nth-last-child(n-1)";
	
	public static Map<String,String> projListDataSelectorMap = new HashMap<String,String>();
	static {
		projListDataSelectorMap.put("projName", "td:nth-child(2)>a");
		projListDataSelectorMap.put("projAddr", "td:nth-child(3)");
		projListDataSelectorMap.put("totalUnits", "td:nth-child(4)");
		projListDataSelectorMap.put("totalArea", "td:nth-child(5)");
		projListDataSelectorMap.put("adminArea", "td:nth-child(6)");
		projListDataSelectorMap.put("saleState", "td:nth-child(1)");
	}
	public final static String projectDetailURL = siteDomain+"/service/freshHouse/queryProjectById.actin";
	public static final String roomListDataSelector = "body > form > table > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	
	public final static String presaleURL = siteDomain+"/service/freshHouse/queryStartUnit.actin";
	public static Map<String,String> preSaleJsonMap = new HashMap<String,String>();
	static {
		preSaleJsonMap.put("saleAddr", "bookingoffice");
		preSaleJsonMap.put("salePhone", "bookingphone");
		preSaleJsonMap.put("preSaleNo", "start_code");
		preSaleJsonMap.put("preSaleLicense", "presell_desc");
		preSaleJsonMap.put("openDate", "start_date");
		preSaleJsonMap.put("totalUnits", "num");
		preSaleJsonMap.put("totalHouseUnits", "z_num");
	}
	
	public final static String buildingURL = siteDomain + "/service/freshHouse/getMoreInfo.action";
	public static Map<String,String> roomDetailJsonMap = new HashMap<String,String>();
	static {
		//roomDetailJsonMap.put("floorNo", "floor");
		roomDetailJsonMap.put("roomNo", "room_number");
		roomDetailJsonMap.put("buildingType", "land_use");
		roomDetailJsonMap.put("flatStyle", "flat_style");
		roomDetailJsonMap.put("predictConstruct", "plan_flarea");
		roomDetailJsonMap.put("predictInner", "plan_priv_flarea");
		roomDetailJsonMap.put("predictShare", "plan_co_flarea");
		roomDetailJsonMap.put("realConstruct", "flarea");
		roomDetailJsonMap.put("realInner", "priv_flarea");
		roomDetailJsonMap.put("realShare", "co_flarea");
		roomDetailJsonMap.put("roomState", "status");
		roomDetailJsonMap.put("planArea", "plan_flarea");
		roomDetailJsonMap.put("realArea", "flarea");
	}
	
	public final static String houseDetailURL = siteDomain + "/service/freshHouse/queryHouseById.action";
}