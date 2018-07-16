package yuzhou.gits.realEstateWebCrawler.app.CS;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class CSConfig extends BaseCfg {
	public final static String siteDomain = "http://www.jscsfc.com/";
	public final static String baseURL = "http://www.jscsfc.com/NewHouse/index.aspx";
	public final static String projPageInfoSelector = "#PageNavigator_NewHouse1_LblPageCount";
	public final static Pattern pageInfoP = Pattern.compile("(\\d+)");
	
	public final static String projListDataSelector = "#es_content > div > div > div:nth-child(n+1):nth-last-child(n+2)";
	public final static String projDetailPageURLSelector = "div.es_message_box_left > h3 > a";

	public static Map<String,String> projDetailDataSelectorMap =
			new HashMap<String,String>();
	static {
		projDetailDataSelectorMap.put("projName", "#xqc1 > div.lp_article_box_content > h3");
		projDetailDataSelectorMap.put("projLocation", "#xqc1 > div.lp_article_box_content > div > ul > li:nth-child(3)");
		projDetailDataSelectorMap.put("developer", "#xqc1 > div.lp_article_box_content > div > ul > li:nth-child(2)");
		projDetailDataSelectorMap.put("salePhone", "#xqc1 > div.lp_article_box_content > div > ul > li:nth-child(16)");
		projDetailDataSelectorMap.put("saleAddr", "#xqc1 > div.lp_article_box_content > div > ul > li:nth-child(4)");
		projDetailDataSelectorMap.put("adminDivision", "#xqc1 > div.lp_article_box_content > div > ul > li:nth-child(5)");
		projDetailDataSelectorMap.put("greenRate", "#xqc1 > div.lp_article_box_content > div > ul > li:nth-child(7)");
		projDetailDataSelectorMap.put("rjl", "#xqc1 > div.lp_article_box_content > div > ul > li:nth-child(8)");
		projDetailDataSelectorMap.put("totalArea", "#xqc1 > div.lp_article_box_content > div > ul > li:nth-child(9)");
		projDetailDataSelectorMap.put("totalConstructArea", "#xqc1 > div.lp_article_box_content > div > ul > li:nth-child(10)");
		projDetailDataSelectorMap.put("landGetWay", "#xqc1 > div.lp_article_box_content > div > ul > li:nth-child(11)");
		projDetailDataSelectorMap.put("carSpots", "#xqc1 > div.lp_article_box_content > div > ul > li:nth-child(12)");
		projDetailDataSelectorMap.put("carSpotsRate", "#xqc1 > div.lp_article_box_content > div > ul > li:nth-child(13)");
		projDetailDataSelectorMap.put("propertyCompany", "#xqc1 > div.lp_article_box_content > div > ul > li:nth-child(14)");
	}
	
	public static final String preSaleLicensePageURLsSelector = "#es_content > div > div.es_xiaoqu > div:nth-child(11) > div.lp_article_box_message2 > table > tbody > tr:nth-child(n+2):nth-last-child(n+1) td:nth-child(2) > a";
	public static Map<String,String> preSaleLicenseSelectorMap =
			new HashMap<String,String>();
	static {
		preSaleLicenseSelectorMap.put("developer", "#YSZ_KFS_MC");
		preSaleLicenseSelectorMap.put("projLocation", "#YSZ_xmzl");
		preSaleLicenseSelectorMap.put("preSaleLicense", "#YSZ_FullXKZ");
		preSaleLicenseSelectorMap.put("licenseArea", "#YSZ_ZMJ");
		preSaleLicenseSelectorMap.put("monitorBankName", "#YSZ_JGYH");
		preSaleLicenseSelectorMap.put("projName", "#YSZ_name");
		preSaleLicenseSelectorMap.put("preSaleBuildNames", "#YSZ_zh");
		preSaleLicenseSelectorMap.put("planningUsage", "#YSZ_YTXZ");
		preSaleLicenseSelectorMap.put("issueDate", "#YSZ_FZRQ");
		preSaleLicenseSelectorMap.put("monitorBankNo", "#YSZ_YHZH");
	}
	
	public static final String buildListSelector = "#es_content > div > div.es_xiaoqu > div:nth-child(7) > div.lp_article_box_message2 > table > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	public static final String buildPageURLsSelector = "td:nth-child(1) > a:nth-child(2)";
	public static final String buildListDataSelector = "#es_content > div > div.es_xiaoqu > div:nth-child(6) > div.lp_article_box_message2 > table > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	public static final String buildDetailPageURLSelector = "td:nth-child(7) > a";
	public static final String roomsSelecotr = "#tbRoomTable > tbody > tr:nth-child(n+2):nth-last-child(n+1) > td:nth-child(2) > table > tbody > tr > td";
	public static final String roomItemsSelector = "span > a";
	public static Map<String,String> buildMainSelectorMap =
			new HashMap<String,String>();
	static {
		buildMainSelectorMap.put("buildName", "td:nth-child(1) > a:nth-child(2)");
		buildMainSelectorMap.put("adminDivision", "td:nth-child(2)");
		buildMainSelectorMap.put("projLocation", "td:nth-child(3)");
		buildMainSelectorMap.put("property", "td:nth-child(4)");
	}
	
	public static Map<String,String> buildSelectorMap =
			new HashMap<String,String>();
	static {
		
		buildSelectorMap.put("carryoutNo", "td:nth-child(1)");
		buildSelectorMap.put("gaNo", "td:nth-child(2)");
		buildSelectorMap.put("totalUnits", "td:nth-child(3)");
		buildSelectorMap.put("totalBuildArea", "td:nth-child(4)");
	}
	public static final Map<String,Pattern> roomDetailRegExprs = new HashMap<String,Pattern>();
	static {
		roomDetailRegExprs.put("roomNo", Pattern.compile("室号：([^\r\n]*)"));
		roomDetailRegExprs.put("roomType", Pattern.compile("户型：([^\r\n]*)"));
		roomDetailRegExprs.put("floorNo", Pattern.compile("所在层：([^\r\n]*)"));
		roomDetailRegExprs.put("unitNo", Pattern.compile("所在单元：([^\r\n]*)"));
		roomDetailRegExprs.put("saleState", Pattern.compile("状态：([^\r\n]*)"));
		roomDetailRegExprs.put("constructArea", Pattern.compile("建筑面积：([^\r\n]*)"));
		roomDetailRegExprs.put("roomUsage", Pattern.compile("房屋用途：([^\r\n]*)"));
		roomDetailRegExprs.put("roomProperty", Pattern.compile("房屋属性：([^\r\n]*)"));
	}
	
	//css_YS--黄色+
	//css_ZJDY--宝石蓝+
	//css_ZJ--浅绿+
	//css_QY--
	
	//css_XS--浅蓝+
	//css_YD--紫色+
	//css_YSYQ--橘色+
	//css_YDJ--红色+
	//css_DJ--土黄+
	//css_XSYQ--蓝色+
	//css_YL--白色+
	//css_ZLY--银色+
}
