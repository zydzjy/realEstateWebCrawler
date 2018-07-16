package yuzhou.gits.realEstateWebCrawler.app.XZ;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class XZConfig extends BaseCfg {
	public final static String siteDomain = "http://www.xzsfdc.com";
	public final static String baseURL = "http://www.xzsfdc.com/shangpflist.aspx";
	public final static String projPageInfoSelector = "#ctl00_ContentPlaceHolder1_TableNavigator1_lbl_Pages";
	public final static String projListSelector = "#ctl00_ContentPlaceHolder1_houseList > dl > dd > h3 > a";
	public final static Pattern pageInfoP = Pattern.compile("共(\\d+)页");
	
	public static final Map<String,String> projectDetailSelectorMap = 
			new HashMap<String,String>();
	static {
		projectDetailSelectorMap.put("projName", "#ctl00_ContentPlaceHolder1_lblProjectname");
		projectDetailSelectorMap.put("projLocation", "#ctl00_ContentPlaceHolder1_lblsitnumgather");
		projectDetailSelectorMap.put("developer", "#ctl00_ContentPlaceHolder1_lbldeveloper");
		projectDetailSelectorMap.put("adminArea", "#ctl00_ContentPlaceHolder1_lblxingzqx");
		projectDetailSelectorMap.put("propertyCompany", "#ctl00_ContentPlaceHolder1_lblwuygs");
		projectDetailSelectorMap.put("propertyFee", "#ctl00_ContentPlaceHolder1_lblwuyglf");
		projectDetailSelectorMap.put("constructionArea", "#ctl00_ContentPlaceHolder1_lblArea");
		projectDetailSelectorMap.put("rjl", "#ctl00_ContentPlaceHolder1_lblrongjl");
		projectDetailSelectorMap.put("certiNo", "#ctl00_ContentPlaceHolder1_lblzizzs");
		projectDetailSelectorMap.put("avgPrice", "#ctl00_ContentPlaceHolder1_lbljj");
		projectDetailSelectorMap.put("certiLevel", "#ctl00_ContentPlaceHolder1_lblzizdj");
		projectDetailSelectorMap.put("salePhone", "#ctl00_ContentPlaceHolder1_lblxiaosdh");	
	}
	
	public static final String planningLicenseListSelector = "#ctl00_ContentPlaceHolder1_gv_layout > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	public static final Map<String,String> planningLicenseDataSelectorMap = 
			new HashMap<String,String>();
	static {
		planningLicenseDataSelectorMap.put("planningLicenseNo", "td");
	}
	
	public static final String carryoutLicenseListSelector = "#ctl00_ContentPlaceHolder1_gv_cons > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	public static final Map<String,String> carryoutLicenseDataSelectorMap = 
			new HashMap<String,String>();
	static {
		carryoutLicenseDataSelectorMap.put("license", "td");
	}
	
	public static final String preSaleLicenseListSelector = "#ctl00_ContentPlaceHolder1_gv_precert > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	public static final Map<String,String> preSaleLicenseDataSelectorMap = 
			new HashMap<String,String>();
	static {
		preSaleLicenseDataSelectorMap.put("preSaleLicenseNo", "td:nth-child(1)");
		preSaleLicenseDataSelectorMap.put("applyDate", "td:nth-child(2)");
		preSaleLicenseDataSelectorMap.put("approveDate", "td:nth-child(3)");
		preSaleLicenseDataSelectorMap.put("startDate", "td:nth-child(4)");
		preSaleLicenseDataSelectorMap.put("endDate", "td:nth-child(5)");
		preSaleLicenseDataSelectorMap.put("preSaleArea", "td:nth-child(6)");
	}
	
	
	public static final String landLicenseListSelector = "#ctl00_ContentPlaceHolder1_gv_landcert > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	public static final Map<String,String> landLicenseDataSelectorMap = 
			new HashMap<String,String>();
	static {
		landLicenseDataSelectorMap.put("landNo", "td:nth-child(1)");
		landLicenseDataSelectorMap.put("landLicenseType", "td:nth-child(2)");
		landLicenseDataSelectorMap.put("landLicenseNo", "td:nth-child(3)");
		landLicenseDataSelectorMap.put("landLocation", "td:nth-child(4)");
	}
	
	public static final String buildingListSelector = "#Td1 > p > a:nth-child(n+1):nth-last-child(n+1)";
	
	public static final String roomListSelector = "#ctl00_ContentPlaceHolder1_assemblex > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	public static final Map<String,String> roomDataSelectorMap = 
			new HashMap<String,String>();
	static {
		roomDataSelectorMap.put("unitNo", "td:nth-child(1)");
		roomDataSelectorMap.put("roomNo", "td:nth-child(2)>a");
		roomDataSelectorMap.put("_roomUsage", "td:nth-child(3)");
		roomDataSelectorMap.put("_roomType", "td:nth-child(4)");
		roomDataSelectorMap.put("totalArea", "td:nth-child(5)");
		roomDataSelectorMap.put("innerArea", "td:nth-child(6)");
		roomDataSelectorMap.put("saleState", "td:nth-child(7)");
	}
	
	public static final Map<String,String> roomDetailSelectorMap = 
			new HashMap<String,String>();
	static {
		roomDetailSelectorMap.put("roomLocation", "#ctl00_ContentPlaceHolder1_lblhouse_sitnumgather");
		roomDetailSelectorMap.put("unit", "#ctl00_ContentPlaceHolder1_lblhouse_unitnum");
		roomDetailSelectorMap.put("floorNum", "#ctl00_ContentPlaceHolder1_lblhouse_floornum");
		roomDetailSelectorMap.put("roomUsage", "#ctl00_ContentPlaceHolder1_lblhouse_houseusage");
		roomDetailSelectorMap.put("roomType", "#ctl00_ContentPlaceHolder1_lblhouse_housetype");
		roomDetailSelectorMap.put("constructionArea", "#ctl00_ContentPlaceHolder1_lblhouse_archarea");
		roomDetailSelectorMap.put("_innerArea", "#ctl00_ContentPlaceHolder1_lblhouse_roomarea");
		roomDetailSelectorMap.put("shareArea", "#ctl00_ContentPlaceHolder1_lblhouse_apportarea");
		roomDetailSelectorMap.put("eWall", "#ctl00_ContentPlaceHolder1_lblhouse_east");
		roomDetailSelectorMap.put("wWall", "#ctl00_ContentPlaceHolder1_lblhouse_west");
		roomDetailSelectorMap.put("sWall", "#ctl00_ContentPlaceHolder1_lblhouse_south");
		roomDetailSelectorMap.put("nWall", "#ctl00_ContentPlaceHolder1_lblhouse_north");
	}
	public static final String roomDetailPageURL = "td:nth-child(2)>a";
}

