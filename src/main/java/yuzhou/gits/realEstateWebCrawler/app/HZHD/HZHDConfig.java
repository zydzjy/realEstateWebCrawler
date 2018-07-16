package yuzhou.gits.realEstateWebCrawler.app.HZHD;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class HZHDConfig extends BaseCfg {
	public final static String siteDomain = "http://202.104.194.190:88/";
	public final static String preSaleBaseURL = "http://202.104.194.190:88/web/presale.jsp";
	public final static String projPageInfoSelector = "#Searchresults > table > tbody > tr:last-child > td";
	public final static Pattern pageInfoP = Pattern.compile("共(\\d+)页");
	public final static String projListDataSelector = "#Searchresults > table > tbody > tr:nth-child(n+2):nth-last-child(n+2)";
	 
	public final static String preSaleLicenseDetailURLSelector = "td:nth-child(2) > a";
	public static Map<String,String> projListDataItemsSelectorMap =
			new HashMap<String,String>();
	static {
		projListDataItemsSelectorMap.put("licenseNo","tr td:nth-child(n+2) a");
		projListDataItemsSelectorMap.put("projName", "tr td:nth-child(n+3) a");
		projListDataItemsSelectorMap.put("developer", "tr td:nth-child(n+4) a");
		projListDataItemsSelectorMap.put("projLocation", "tr td:nth-child(n+5) a");
	}
	public static final String buildingURLSelector = "td:nth-child(3) > a";
	
	public static Map<String,String> preSaleLicenseSelector =
			new HashMap<String,String>();
	static {
		//preSaleLicenseSelector.put("preSaleLicense","#Searchform > div > table > tbody > tr:nth-child(1) > td");
		//preSaleLicenseSelector.put("developer", "#Searchform > div > table > tbody > tr:nth-child(2) > td:nth-child(2)");
		preSaleLicenseSelector.put("developerCerti", "#Searchform > div > table > tbody > tr:nth-child(2) > td:nth-child(4)");
		//preSaleLicenseSelector.put("projName", "#Searchform > div > table > tbody > tr:nth-child(3) > td");
		//preSaleLicenseSelector.put("projLocation", "#Searchform > div > table > tbody > tr:nth-child(3) > td");
		preSaleLicenseSelector.put("landUsage", "#Searchform > div > table > tbody > tr:nth-child(5) > td");
		preSaleLicenseSelector.put("preSaleArea", "#Searchform > div > table > tbody > tr:nth-child(6) > td:nth-child(2)");
		preSaleLicenseSelector.put("currUnits", "#Searchform > div > table > tbody > tr:nth-child(6) > td:nth-child(4)");
		preSaleLicenseSelector.put("houseArea", "#Searchform > div > table > tbody > tr:nth-child(7) > td:nth-child(3)");
		preSaleLicenseSelector.put("houseBuildingNo", "#Searchform > div > table > tbody > tr:nth-child(7) > td:nth-child(5)");
		preSaleLicenseSelector.put("buildingShop", "#Searchform > div > table > tbody > tr:nth-child(8) > td:nth-child(2)");
		preSaleLicenseSelector.put("buildingShopNo", "#Searchform > div > table > tbody > tr:nth-child(8) > td:nth-child(4)");
		preSaleLicenseSelector.put("othersArea", "#Searchform > div > table > tbody > tr:nth-child(9) > td:nth-child(2)");
		preSaleLicenseSelector.put("mortage", "#Searchform > div > table > tbody > tr:nth-child(9) > td:nth-child(4)");
		preSaleLicenseSelector.put("validateSDate", "#Searchform > div > table > tbody > tr:nth-child(10) > td:nth-child(2)");
		preSaleLicenseSelector.put("validateEDate", "#Searchform > div > table > tbody > tr:nth-child(10) > td:nth-child(4)");
		preSaleLicenseSelector.put("issuingUnit", "#Searchform > div > table > tbody > tr:nth-child(11) > td:nth-child(2)");
		preSaleLicenseSelector.put("issuingDate", "#Searchform > div > table > tbody > tr:nth-child(11) > td:nth-child(4)");
		preSaleLicenseSelector.put("moneyBankNo", "#Searchform > div > table > tbody > tr:nth-child(12) > td");
		preSaleLicenseSelector.put("remark", "#Searchform > div > table > tbody > tr:nth-child(13) > td");
		
	
	}
	
	public static final String buildingListSelector = 
			"#Searchform > div > table > tbody > tr:nth-child(10) > td > table > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	public static Map<String,String> projDetailSelectorMap = 
			new HashMap<String,String>();
	static{
		//buildingListDataItemsSelectorMap.put("projName", "#Searchform > div > table > tbody > tr:nth-child(1) > td");
		//buildingListDataItemsSelectorMap.put("projLocation", "#Searchform > div > table > tbody > tr:nth-child(2) > td");
		//buildingListDataItemsSelectorMap.put("developer", "#Searchform > div > table > tbody > tr:nth-child(3) > td");
		projDetailSelectorMap.put("occupyArea", "#Searchform > div > table > tbody > tr:nth-child(4) > td:nth-child(2)");
		projDetailSelectorMap.put("constructionArea", "#Searchform > div > table > tbody > tr:nth-child(4) > td:nth-child(4)");
		projDetailSelectorMap.put("contactor", "#Searchform > div > table > tbody > tr:nth-child(5) > td:nth-child(2)");
		projDetailSelectorMap.put("contactPhone", "#Searchform > div > table > tbody > tr:nth-child(5) > td:nth-child(4)");
		projDetailSelectorMap.put("certiNo", "#Searchform > div > table > tbody > tr:nth-child(6) > td:nth-child(2)");
		projDetailSelectorMap.put("adminDivision", "#Searchform > div > table > tbody > tr:nth-child(6) > td:nth-child(4)");
		projDetailSelectorMap.put("openDate", "#Searchform > div > table > tbody > tr:nth-child(7) > td:nth-child(2)");
		projDetailSelectorMap.put("salePhone", "#Searchform > div > table > tbody > tr:nth-child(7) > td:nth-child(4)");
		projDetailSelectorMap.put("totalUnits", "#Searchform > div > table > tbody > tr:nth-child(8) > td:nth-child(2)");
		projDetailSelectorMap.put("saleAvgPrice", "#Searchform > div > table > tbody > tr:nth-child(8) > td:nth-child(4)");
		projDetailSelectorMap.put("landCertiNo", "#Searchform > div > table > tbody > tr:nth-child(9) > td:nth-child(2)");
	}
	public static Map<String,String> buildingListDataItemsSelectorMap = 
			new HashMap<String,String>();
	static{
		buildingListDataItemsSelectorMap.put("buildingName", "td:nth-child(1)");
		buildingListDataItemsSelectorMap.put("buildingNo", "td:nth-child(2)");
		buildingListDataItemsSelectorMap.put("buildingStruct", "td:nth-child(3)");
		buildingListDataItemsSelectorMap.put("buildingUnits", "td:nth-child(4)");
		buildingListDataItemsSelectorMap.put("buildingFloors", "td:nth-child(5)");
	}
	public final static String roomListPageURLSelector = "td:nth-child(6)>a";
	
	public final static String roomListSelector = "#Form1 > table > tbody > tr > td > div[style='cursor:hand']";//"#Form1 > table:nth-child(3) > tbody > tr > td > div:nth-child(2)";
	public final static String roomColorSelector = 
			"img";
	
	public final static String roomPageDetailURL = "http://202.104.194.190:88/web/House.jsp?id=[id]&lcStr=[lcStr]";
	public final static Map<String,String> roomDetailSelectorMap = 
			new HashMap<String,String>();
	static {
		roomDetailSelectorMap.put("roomNo", "#Searchform > div > table > tbody > tr:nth-child(3) > td:nth-child(2)");
		roomDetailSelectorMap.put("planningUtil", "#Searchform > div > table > tbody > tr:nth-child(3) > td:nth-child(4)");
		//roomDetailSelectorMap.put("roomUsage", "#Searchform > div > table > tbody > tr:nth-child(4) > td:nth-child(2)");
		roomDetailSelectorMap.put("roomType", "#Searchform > div > table > tbody > tr:nth-child(4) > td:nth-child(4)");
		roomDetailSelectorMap.put("buildingFloor", "#Searchform > div > table > tbody > tr:nth-child(5) > td:nth-child(2)");
		roomDetailSelectorMap.put("floorHeight", "#Searchform > div > table > tbody > tr:nth-child(5) > td:nth-child(4)");
		roomDetailSelectorMap.put("roomDirection", "#Searchform > div > table > tbody > tr:nth-child(6) > td:nth-child(2)");
		roomDetailSelectorMap.put("roomStruct", "#Searchform > div > table > tbody > tr:nth-child(6) > td:nth-child(4)");
		roomDetailSelectorMap.put("roomPublic", "#Searchform > div > table > tbody > tr:nth-child(7) > td:nth-child(2)");
		roomDetailSelectorMap.put("roomMoveback", "#Searchform > div > table > tbody > tr:nth-child(7) > td:nth-child(4)");
		roomDetailSelectorMap.put("roomIsSelfUse", "#Searchform > div > table > tbody > tr:nth-child(8) > td:nth-child(2)");
		roomDetailSelectorMap.put("preSaleState", "#Searchform > div > table > tbody > tr:nth-child(8) > td:nth-child(4)");
		roomDetailSelectorMap.put("priceList", "#Searchform > div > table > tbody > tr:nth-child(10) > td");
		roomDetailSelectorMap.put("closeBalcony", "#Searchform > div > table > tbody > tr:nth-child(12) > td:nth-child(2)");
		roomDetailSelectorMap.put("openBalcony", "#Searchform > div > table > tbody > tr:nth-child(12) > td:nth-child(4)");
		roomDetailSelectorMap.put("kitchen", "#Searchform > div > table > tbody > tr:nth-child(13) > td:nth-child(2)");
		roomDetailSelectorMap.put("bathroom", "#Searchform > div > table > tbody > tr:nth-child(13) > td:nth-child(4)");
		roomDetailSelectorMap.put("predictionArea", "#Searchform > div > table > tbody > tr:nth-child(15) > td:nth-child(2)");
		roomDetailSelectorMap.put("actualArea", "#Searchform > div > table > tbody > tr:nth-child(15) > td:nth-child(4)");
		roomDetailSelectorMap.put("predictionInnerArea", "#Searchform > div > table > tbody > tr:nth-child(16) > td:nth-child(2)");
		roomDetailSelectorMap.put("actualInnerArea", "#Searchform > div > table > tbody > tr:nth-child(16) > td:nth-child(4)");
		roomDetailSelectorMap.put("predictionShareArea", "#Searchform > div > table > tbody > tr:nth-child(17) > td:nth-child(2)");
		roomDetailSelectorMap.put("actualShareArea", "#Searchform > div > table > tbody > tr:nth-child(17) > td:nth-child(4)");
		roomDetailSelectorMap.put("mortage", "#Searchform > div > table > tbody > tr:nth-child(19) > td:nth-child(2)");
		roomDetailSelectorMap.put("sealUp", "#Searchform > div > table > tbody > tr:nth-child(19) > td:nth-child(4)");
	}
	
	
	
	/*
	../web/images/house/ks.gif -- 绿色+可售
	../web/images/house/ysqy.gif --蓝色+已签预售合同
	../web/images/house/bks.gif -- 红色+不可售
	../web/images/house/ba.gif -- 黄色+已备案
	../web/images/house/xfqy.gif --橙色+现房已签约
	../web/images/house/ybz.gif -- 湖蓝色+已办证
	*/
}
