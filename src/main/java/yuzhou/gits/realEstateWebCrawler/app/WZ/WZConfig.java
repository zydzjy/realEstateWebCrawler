package yuzhou.gits.realEstateWebCrawler.app.WZ;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class WZConfig extends BaseCfg {
	public final static String siteDomain = "http://www.wzfg.com";
	public final static String baseURL = "/realweb/stat/ProjectSellingList.jsp";
	public final static String pageInfoSelector = "#aButton > table > tbody > tr > td:last-child > a";
	public final static String pageInfoP = "(\\d+)";
	
	public final static String projListPageURL = "http://www.wzfg.com/realweb/stat/ProjectSellingList.jsp?currPage=[PAGENO]&permitNo=&projectName=&projectAddr=&region=&num=[PAGENO]";
	public final static String projectListSelector = "body > table:nth-child(6) > tbody > tr > td > table:nth-child(3) > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	
	public static Map<String,String> projListDataSelectorMap =
			new HashMap<String,String>();
	static{
		projListDataSelectorMap.put("preSaleLicenseNo", "td:nth-child(2)");
		projListDataSelectorMap.put("projName", "td:nth-child(3)");
		projListDataSelectorMap.put("projLocation", "td:nth-child(4)");
		projListDataSelectorMap.put("openDate", "td:nth-child(5)");
		projListDataSelectorMap.put("adminArea", "td:nth-child(6)");
	}
	
	public static Map<String,String> projDetailSelectorMap =
			new HashMap<String,String>();
	static{
		projDetailSelectorMap.put("developer", "#saleInfo>table>tbody>tr:nth-child(1)>td:nth-child(2)>a");
		projDetailSelectorMap.put("projArea", "#saleInfo > table > tbody > tr:nth-child(6) > td:nth-child(2)");
		projDetailSelectorMap.put("saleAddr", "#saleInfo > table > tbody > tr:nth-child(10) > td:nth-child(2)");
		projDetailSelectorMap.put("salePhone", "#saleInfo > table > tbody > tr:nth-child(11) > td:nth-child(2)");
	}
	
	public static final String tdBldListSelector = "#tdBldList > a";
	public static final String tdRoomsBldSelector = "#tdRooms > div > table:nth-child(n+2):nth-last-child(n+1) > tbody";
	
	public static final String roomDetailPageURL = "/realweb/stat/HouseInfoUser5.jsp?houseID=[houseID]&isLimit=&isUni=";
	public static Map<String,String> roomDetailSelectorMap =
			new HashMap<String,String>();
	static{
		roomDetailSelectorMap.put("roomNo","table.biankuang>tbody>tr:nth-child(3)>td:nth-child(2)");
		roomDetailSelectorMap.put("roomLocation","table.biankuang>tbody>tr:nth-child(2)>td:nth-child(2)");
		roomDetailSelectorMap.put("totalArea","table.biankuang>tbody>tr:nth-child(3)>td:nth-child(4)");
		roomDetailSelectorMap.put("innerArea","table.biankuang>tbody>tr:nth-child(4)>td:nth-child(2)");
		roomDetailSelectorMap.put("shareArea","table.biankuang>tbody>tr:nth-child(4)>td:nth-child(4)");
		roomDetailSelectorMap.put("roomUsage","table.biankuang>tbody>tr:nth-child(5)>td:nth-child(2)");
		roomDetailSelectorMap.put("designUsage","table.biankuang>tbody>tr:nth-child(5)>td:nth-child(4)");
		roomDetailSelectorMap.put("structType","table.biankuang>tbody>tr:nth-child(6)>td:nth-child(2)");
		roomDetailSelectorMap.put("onePrice","table.biankuang>tbody>tr:nth-child(6)>td:nth-child(4)");
		roomDetailSelectorMap.put("roomProj","table.biankuang>tbody>tr:nth-child(7)>td:nth-child(2)>a");
		//roomDetailSelectorMap.put("saleState","table.biankuang>tbody>tr:nth-child(8)>td:nth-child(2)");
	}
}