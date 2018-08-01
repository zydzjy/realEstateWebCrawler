package yuzhou.gits.realEstateWebCrawler.app.ShangHai.ShangHai2;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class ShangHai2Config extends BaseCfg {
	public static final String siteDomain = "http://www.fangdi.com.cn";
	public static String  projListSelector = "body > center > table:nth-child(6) > tbody > tr:nth-child(2n+2):nth-last-child(n+2)";
	public static final String roomListDataSelector = "body > form > table > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	public static final String pageInfoSelector = "#Table7 > tbody > tr > td:nth-child(2)";
	public static final Pattern pageInfoP = Pattern.compile("(\\d+)");
	public static Map<String,String> projListDataSelectorMap = new HashMap<String,String>();
	static {
		projListDataSelectorMap.put("projName", "td:nth-child(2)>a");
		projListDataSelectorMap.put("projAddr", "td:nth-child(3)");
		projListDataSelectorMap.put("totalUnits", "td:nth-child(4)");
		projListDataSelectorMap.put("totalArea", "td:nth-child(5)");
		projListDataSelectorMap.put("adminArea", "td:nth-child(6)");
		projListDataSelectorMap.put("saleState", "td:nth-child(1)");
	}
	public static final String preSaleURL = "http://www.fangdi.com.cn/Presell.asp";
	public static final String preSaleListSelector = "body > table > tbody > tr:nth-child(n+2):nth-child(n+1)";
	public static Map<String,String> preSaleSelectorMap = new HashMap<String,String>();
	static {
		/*preSaleJsonMap.put("saleAddr", "bookingoffice");
		preSaleJsonMap.put("salePhone", "bookingphone");*/
		preSaleSelectorMap.put("preSaleNo", "td:nth-child(1)");
		preSaleSelectorMap.put("preSaleLicense", "td:nth-child(2)>a>span");
		preSaleSelectorMap.put("openDate", "td:nth-child(3)");
		preSaleSelectorMap.put("totalUnits", "td:nth-child(4)");
		preSaleSelectorMap.put("totalHouseUnits", "td:nth-child(5)");
	}
	public static final String buildingListSelector = "body > table > tbody > tr > td > table > tbody > tr > td > table > tbody > tr:nth-child(n+2):nth-child(n+1)";
	
	public static final String floorsSelector = "#Table1 > tbody > tr > td > table:nth-child(2) > tbody > tr";
	public static final String roomDetailSelector = "#Table1 > tbody > tr > td > table > tbody";
	public static Map<String,String> roomDetailSelectorMap = new HashMap<String,String>();
	static {
		//roomDetailSelectorMap.put("floorNo", "tr:nth-child(2)>td:nth-child(2)");
		roomDetailSelectorMap.put("roomNo", "tr:nth-child(3)>td:nth-child(2)");
		roomDetailSelectorMap.put("buildingType", "tr:nth-child(4)>td:nth-child(2)");
		roomDetailSelectorMap.put("flatStyle", "tr:nth-child(5)>td:nth-child(2)");
		roomDetailSelectorMap.put("predictConstruct", "tr:nth-child(6)>td:nth-child(2)");
		roomDetailSelectorMap.put("predictInner", "tr:nth-child(7)>td:nth-child(2)");
		roomDetailSelectorMap.put("predictShare", "tr:nth-child(8)>td:nth-child(2)");
		roomDetailSelectorMap.put("realConstruct", "tr:nth-child(10)>td:nth-child(2)");
		roomDetailSelectorMap.put("realInner", "tr:nth-child(11)>td:nth-child(2)");
		roomDetailSelectorMap.put("realShare", "tr:nth-child(12)>td:nth-child(2)");
		roomDetailSelectorMap.put("roomState", "tr:nth-child(14)>td:nth-child(2)");
	}
}