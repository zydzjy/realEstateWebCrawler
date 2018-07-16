package yuzhou.gits.realEstateWebCrawler.app.HZBL;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class HZBLConfig extends BaseCfg {
	public String projectType = "";
	public static final String siteDomain = "http://119.146.67.130:8083/";
	public final static String nowSaleBaseURL = "http://119.146.67.130:8083/web/nowonsale.jsp";
	public final static String preSaleBaseURL = "http://119.146.67.130:8083/web/presale.jsp";
	public final static String projPageInfoSelector = "#Searchresults > table > tbody > tr:last-child > td";
	public final static Pattern pageInfoP = Pattern.compile("共(\\d+)页");
	public final static String projListDataSelector = "#Searchresults > table > tbody > tr:nth-child(n+2):nth-last-child(n+2)";

	public final static String preSaleLicenseDetailURL = "http://119.146.67.130:8083/web/salepermitinfo.jsp?ctemp=[LICENSENO]";
	public static Map<String, String> projListDataItemsSelectorMap = new HashMap<String, String>();
	static {
		projListDataItemsSelectorMap.put("licenseNo", "tr td:nth-child(n+2) a");
		projListDataItemsSelectorMap.put("projName", "tr td:nth-child(n+3) a");
		projListDataItemsSelectorMap.put("developer", "tr td:nth-child(n+4) a");
		projListDataItemsSelectorMap.put("projLocation", "tr td:nth-child(n+5) a");
	}
	public static final String buildingURLSelector = "td:nth-child(3) > a";

	public static Map<String, String> preSaleLicenseSelector = new HashMap<String, String>();
	static {
		// preSaleLicenseSelector.put("preSaleLicense","#Searchform > div >
		// table > tbody > tr:nth-child(1) > td");
		// preSaleLicenseSelector.put("developer", "#Searchform > div > table >
		// tbody > tr:nth-child(2) > td:nth-child(2)");
		preSaleLicenseSelector.put("developerCerti",
				"#Searchform > div > table > tbody > tr:nth-child(2) > td:nth-child(4)");
		// preSaleLicenseSelector.put("projName", "#Searchform > div > table >
		// tbody > tr:nth-child(3) > td");
		// preSaleLicenseSelector.put("projLocation", "#Searchform > div > table
		// > tbody > tr:nth-child(3) > td");
		preSaleLicenseSelector.put("landUsage", "#Searchform > div > table > tbody > tr:nth-child(5) > td");
		preSaleLicenseSelector.put("preSaleArea",
				"#Searchform > div > table > tbody > tr:nth-child(6) > td:nth-child(2)");
		preSaleLicenseSelector.put("currUnits",
				"#Searchform > div > table > tbody > tr:nth-child(6) > td:nth-child(4)");
		preSaleLicenseSelector.put("houseArea",
				"#Searchform > div > table > tbody > tr:nth-child(7) > td:nth-child(3)");
		preSaleLicenseSelector.put("houseBuildingNo",
				"#Searchform > div > table > tbody > tr:nth-child(7) > td:nth-child(5)");
		preSaleLicenseSelector.put("buildingShop",
				"#Searchform > div > table > tbody > tr:nth-child(8) > td:nth-child(2)");
		preSaleLicenseSelector.put("buildingShopNo",
				"#Searchform > div > table > tbody > tr:nth-child(8) > td:nth-child(4)");
		preSaleLicenseSelector.put("othersArea",
				"#Searchform > div > table > tbody > tr:nth-child(9) > td:nth-child(2)");
		preSaleLicenseSelector.put("mortage", "#Searchform > div > table > tbody > tr:nth-child(9) > td:nth-child(4)");
		preSaleLicenseSelector.put("validateSDate",
				"#Searchform > div > table > tbody > tr:nth-child(10) > td:nth-child(2)");
		preSaleLicenseSelector.put("validateEDate",
				"#Searchform > div > table > tbody > tr:nth-child(10) > td:nth-child(4)");
		preSaleLicenseSelector.put("issuingUnit",
				"#Searchform > div > table > tbody > tr:nth-child(11) > td:nth-child(2)");
		preSaleLicenseSelector.put("issuingDate",
				"#Searchform > div > table > tbody > tr:nth-child(11) > td:nth-child(4)");
		preSaleLicenseSelector.put("moneyBankNo", "#Searchform > div > table > tbody > tr:nth-child(12) > td");
		preSaleLicenseSelector.put("remark", "#Searchform > div > table > tbody > tr:nth-child(13) > td");

	}

	public static final String buildingListSelector = "#Searchform > div > table > tbody > tr:nth-child(10) > td > table > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	public static Map<String, String> projectDetailSelectorMap = new HashMap<String, String>();
	static {
		// buildingListDataItemsSelectorMap.put("projName", "#Searchform > div >
		// table > tbody > tr:nth-child(1) > td");
		// buildingListDataItemsSelectorMap.put("projLocation", "#Searchform >
		// div > table > tbody > tr:nth-child(2) > td");
		// buildingListDataItemsSelectorMap.put("developer", "#Searchform > div
		// > table > tbody > tr:nth-child(3) > td");
		projectDetailSelectorMap.put("occupyArea",
				"#Searchform > div > table > tbody > tr:nth-child(4) > td:nth-child(2)");
		projectDetailSelectorMap.put("constructionArea",
				"#Searchform > div > table > tbody > tr:nth-child(4) > td:nth-child(4)");
		projectDetailSelectorMap.put("contactor",
				"#Searchform > div > table > tbody > tr:nth-child(5) > td:nth-child(2)");
		projectDetailSelectorMap.put("contactPhone",
				"#Searchform > div > table > tbody > tr:nth-child(5) > td:nth-child(4)");
		projectDetailSelectorMap.put("certiNo",
				"#Searchform > div > table > tbody > tr:nth-child(6) > td:nth-child(2)");
		projectDetailSelectorMap.put("adminDivision",
				"#Searchform > div > table > tbody > tr:nth-child(6) > td:nth-child(4)");
		projectDetailSelectorMap.put("openDate",
				"#Searchform > div > table > tbody > tr:nth-child(7) > td:nth-child(2)");
		projectDetailSelectorMap.put("salePhone",
				"#Searchform > div > table > tbody > tr:nth-child(7) > td:nth-child(4)");
		projectDetailSelectorMap.put("totalUnits",
				"#Searchform > div > table > tbody > tr:nth-child(8) > td:nth-child(2)");
		projectDetailSelectorMap.put("saleAvgPrice",
				"#Searchform > div > table > tbody > tr:nth-child(8) > td:nth-child(4)");
		projectDetailSelectorMap.put("landCertiNo", "#Searchform > div > table > tbody > tr:nth-child(9) > td");
	}

	public static Map<String, String> buildingListDataItemsSelectorMap = new HashMap<String, String>();
	static {
		buildingListDataItemsSelectorMap.put("buildingName", "td:nth-child(1)");
		buildingListDataItemsSelectorMap.put("buildingNo", "td:nth-child(2)");
		buildingListDataItemsSelectorMap.put("buildingStruct", "td:nth-child(3)");
		buildingListDataItemsSelectorMap.put("buildingUnits", "td:nth-child(4)");
		buildingListDataItemsSelectorMap.put("buildingFloors", "td:nth-child(5)");
	}
	public final static String roomListPageURLSelector = "td:nth-child(6)>a";

	public final static String roomListSelector = "#Form1 > table > tbody > tr > td > div[style='cursor:hand']";// "#Form1
																												// >
																												// table:nth-child(3)
																												// >
																												// tbody
																												// >
																												// tr
																												// >
																												// td
																												// >
																												// div:nth-child(2)";
	public final static String roomColorSelector = "img";

	public final static String roomPageDetailURL = "http://119.146.67.130:8083/web/House.jsp?id=[id]&lcStr=[lcStr]";
	public final static Map<String, String> roomDetailSelectorMap = new HashMap<String, String>();

	static {
		roomDetailSelectorMap.put("buildingFloor",
				"#Searchform > div > table > tbody > tr:nth-child(5) > td:nth-child(2)");
		roomDetailSelectorMap.put("roomNo", "#Searchform > div > table > tbody > tr:nth-child(3) > td:nth-child(2)");
		roomDetailSelectorMap.put("planningUtil",
				"#Searchform > div > table > tbody > tr:nth-child(3) > td:nth-child(4)");
		roomDetailSelectorMap.put("roomUsage", "#Searchform > div > table > tbody > tr:nth-child(4) > td:nth-child(2)");
		roomDetailSelectorMap.put("roomType", "#Searchform > div > table > tbody > tr:nth-child(4) > td:nth-child(4)");
		roomDetailSelectorMap.put("floorHeight",
				"#Searchform > div > table > tbody > tr:nth-child(5) > td:nth-child(4)");
		roomDetailSelectorMap.put("roomDirection",
				"#Searchform > div > table > tbody > tr:nth-child(6) > td:nth-child(2)");
		roomDetailSelectorMap.put("roomStruct",
				"#Searchform > div > table > tbody > tr:nth-child(6) > td:nth-child(4)");
		roomDetailSelectorMap.put("roomPublic",
				"#Searchform > div > table > tbody > tr:nth-child(7) > td:nth-child(2)");
		roomDetailSelectorMap.put("roomMoveback",
				"#Searchform > div > table > tbody > tr:nth-child(7) > td:nth-child(4)");
		roomDetailSelectorMap.put("roomIsSelfUse",
				"#Searchform > div > table > tbody > tr:nth-child(8) > td:nth-child(2)");
		roomDetailSelectorMap.put("preSaleState",
				"#Searchform > div > table > tbody > tr:nth-child(8) > td:nth-child(4)");
		roomDetailSelectorMap.put("priceList", "#Searchform > div > table > tbody > tr:nth-child(9) > td");
		roomDetailSelectorMap.put("closeBalcony",
				"#Searchform > div > table > tbody > tr:nth-child(11) > td:nth-child(2)");
		roomDetailSelectorMap.put("openBalcony",
				"#Searchform > div > table > tbody > tr:nth-child(11) > td:nth-child(4)");
		roomDetailSelectorMap.put("kitchen", "#Searchform > div > table > tbody > tr:nth-child(12) > td:nth-child(2)");
		roomDetailSelectorMap.put("bathroom", "#Searchform > div > table > tbody > tr:nth-child(12) > td:nth-child(4)");
		roomDetailSelectorMap.put("predictionArea",
				"#Searchform > div > table > tbody > tr:nth-child(14) > td:nth-child(2)");
		roomDetailSelectorMap.put("actualArea",
				"#Searchform > div > table > tbody > tr:nth-child(14) > td:nth-child(4)");
		roomDetailSelectorMap.put("predictionInnerArea",
				"#Searchform > div > table > tbody > tr:nth-child(15) > td:nth-child(2)");
		roomDetailSelectorMap.put("actualInnerArea",
				"#Searchform > div > table > tbody > tr:nth-child(15) > td:nth-child(4)");
		roomDetailSelectorMap.put("predictionShareArea",
				"#Searchform > div > table > tbody > tr:nth-child(16) > td:nth-child(2)");
		roomDetailSelectorMap.put("actualShareArea",
				"#Searchform > div > table > tbody > tr:nth-child(16) > td:nth-child(4)");
		roomDetailSelectorMap.put("mortage", "#Searchform > div > table > tbody > tr:nth-child(18) > td:nth-child(2)");
		roomDetailSelectorMap.put("sealUp", "#Searchform > div > table > tbody > tr:nth-child(18) > td:nth-child(4)");
	}

	/*
	 * ../web/images/house/bks.gif 红色+不可售 
	 * ../web/images/house/xfds.gif 绿色+现房待售
	 * ../web/images/house/ba.gif 黄色+已备案 
	 * ../web/images/house/ybz.gif 浅蓝+已办证
	 * ../web/images/house/bzz.gif 橘黄+办证中
	 *  ../web/images/house/xfyqy.gif 蓝色+现房已签约
	 * ../web/images/house/zhxs.gif 灰色+暂缓销售 
	 * ../web/images/house/xs.gif 银色+空
	 * ../web/images/house/qfyqy.gif 蓝色+期房已签约 
	 * ../web/images/house/qfds.gif 绿色+期房待售
	 */
}
