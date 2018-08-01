package yuzhou.gits.realEstateWebCrawler.app.NB;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class NBConfig extends BaseCfg {
	public final static String siteDomain = "https://newhouse.cnnbfdc.com";
	public final static String baseURL = "/Lpxx.aspx?Region=&ProjectName=";
	public final static String projListPageURL = "/Lpxx.aspx?Region=&ProjectName=&p=";
	public final static String projPageInfoSelector = "body > div.layout-center > div > main > div > div.item-list > ul > li.pager-last.last > a";
	public final static String pageInfoP = "(\\d+)";
	public final static String projectListSelector =
			"body > div.layout-center > div > main > div > div.view-content > table > tbody > tr:nth-child(n+1):nth-last-child(n-1)";
	public final static Map<String,String> projDataSelectorMap = 
			new HashMap<String,String>();
	static {
		projDataSelectorMap.put("licenseNo", "td:nth-child(1)>a");
		projDataSelectorMap.put("licenseDate", "td:nth-child(2)>span");
		projDataSelectorMap.put("projName", "td:nth-child(3)");
		projDataSelectorMap.put("adminArea", "td:nth-child(4)");
		projDataSelectorMap.put("developer", "td:nth-child(5)");
	}
	public final static Map<String,String> projDetailSelectorMap = 
			new HashMap<String,String>();
	static {
		projDetailSelectorMap.put("nickName", "div.project-detail__name > div");
		projDetailSelectorMap.put("projAddr", "div.field.field-name-project-detail-summary > div > div:nth-child(2) > span");
		projDetailSelectorMap.put("saleAddr", "#detail > table > tbody > tr:nth-child(2) > td:nth-child(2)");
		projDetailSelectorMap.put("salePhone", "#detail > table > tbody > tr:nth-child(2) > td:nth-child(4)");
		projDetailSelectorMap.put("buildings", "#detail > table > tbody > tr:nth-child(3) > td:nth-child(2)");
		projDetailSelectorMap.put("totalConstructArea", "#detail > table > tbody > tr:nth-child(3) > td:nth-child(4)");
		projDetailSelectorMap.put("openDate", "#detail > table > tbody > tr:nth-child(4) > td:nth-child(2) > span");
		projDetailSelectorMap.put("bankNO", "#detail > table > tbody > tr:nth-child(4) > td:nth-child(4)");
		projDetailSelectorMap.put("bankName", "#detail > table > tbody > tr:nth-child(5) > td:nth-child(4)");
		projDetailSelectorMap.put("licenseState", "#detail > table > tbody > tr:nth-child(6) > td:nth-child(2)");
		
	}
	public final static String buildingListSelector = "#building-list > div > div.view-content > div:nth-child(n+1):nth-last-child(n-1)";
	public final static Map<String,String> buildingSelectorMap = 
			new HashMap<String,String>();
	static {
		buildingSelectorMap.put("buildingName", "div.views-field.views-field-building-name>div");
		buildingSelectorMap.put("totalFloors", "div.views-field.views-field-building-layers>div");
		buildingSelectorMap.put("totalLicenses", "div.views-field.views-field-building-license-count>div");
	}
	//public final static String roomListPageURL = "https://newhouse.cnnbfdc.com/project/[PROJID]/rooms-table?unit_building=All&page=[PAGENO]";
	public final static String roomListDataSelector = "body > div:nth-child(4) > div > main > div > div.panel-col-bottom.panel-panel > div > div > div > div.view-content > table > tbody > tr:nth-child(n+1):nth-last-child(n-1)";
	public final static Map<String,String> roomDetailSelectorMap = 
			new HashMap<String,String>();
	static {
		roomDetailSelectorMap.put("buildingNo", "td:nth-child(1)");
		roomDetailSelectorMap.put("roomNo", "td:nth-child(2)");
		roomDetailSelectorMap.put("innerArea", "td:nth-child(3)");
		roomDetailSelectorMap.put("constructArea", "td:nth-child(4)");
		roomDetailSelectorMap.put("floorNo", "td:nth-child(5)");
		roomDetailSelectorMap.put("remarkUnitPrice", "td:nth-child(6)");
		roomDetailSelectorMap.put("planningUsage", "td:nth-child(7)");
		roomDetailSelectorMap.put("openUnitPrice", "td:nth-child(8)");
		roomDetailSelectorMap.put("openTotalPrice", "td:nth-child(9)");
		roomDetailSelectorMap.put("roomBathrooms", "td:nth-child(10)");
		roomDetailSelectorMap.put("saleState", "td:nth-child(11)>span");
	} 
}
