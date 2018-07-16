package yuzhou.gits.realEstateWebCrawler.app.TZ;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class TZConfig extends BaseCfg {

	public final static String siteDomain = "http://tz.tmsf.com";
	public final static String baseURL = "/newhouse/property_searchall.htm";
	public final static String projPageInfoSelector = "body > div.search_page > "
			+ "div.searchpage_main > div.pagenumber > div.pagenuber_info > font:nth-child(2)";
	public static final Pattern pageInfoP = Pattern.compile("/(\\d+)");
	public static final String projListSelector = "body > div.search_page > div.searchpage_main > div.searchpageall > div > ul > li";
	public static Map<String,String> projListDataSelectorMap = new HashMap<String,String>();
	static {
		projListDataSelectorMap.put("projName", "div>div.build_txt>h3>a");
		projListDataSelectorMap.put("propertyType", "div>div.build_txt>div:nth-child(2)>p");
		projListDataSelectorMap.put("projAddr", "div > div.build_txt > div:nth-child(3) > p");
		projListDataSelectorMap.put("developer", "div>div.build_txt>div:nth-child(4)>p");
		projListDataSelectorMap.put("salePhone", "div>div.build_txt>div:nth-child(6)>font:nth-child(2)");
	
	}
	public static final String avgPriceSelector = "div > div.build_txt > div.build_txt06 > p.ash1>span";
	public static final String onPricePageUrlSelector = "div>div.build_txt>div:nth-child(8)>a:nth-child(2)";
	public static final Map<String,String> digitsMap = new HashMap<String,String>();
	static{
		digitsMap.put("numbzero", "0");digitsMap.put("numbone", "1");
		digitsMap.put("numbtwo", "2");digitsMap.put("numbthree", "3");
		digitsMap.put("numbfour", "4");digitsMap.put("numbfive", "5");
		digitsMap.put("numbsix", "6");digitsMap.put("numbseven", "7");
		digitsMap.put("numbeight", "8");digitsMap.put("numbnine", "9");
		digitsMap.put("numbdor", ".");
		
		digitsMap.put("numberzero", "0");digitsMap.put("numberone", "1");
		digitsMap.put("numbertwo", "2");digitsMap.put("numberthree", "3");
		digitsMap.put("numberfour", "4");digitsMap.put("numberfive", "5");
		digitsMap.put("numbersix", "6");digitsMap.put("numberseven", "7");
		digitsMap.put("numbereight", "8");digitsMap.put("numbernine", "9");
		digitsMap.put("numberdor", ".");
	}
	public static final String presalesTypeSelector = "#presell_dd > div > a:nth-child(n+2):nth-last-child(n+1)";
	public static final String presalesURL = "/newhouse/NewPropertyHz_createPresellInfo.jspx?sid=[sid]&presellid=[presellid]&propertyid=[propertyid]";
	public static Map<String,String> presaleJsonMap =
			new HashMap<String,String>();
	static{
		presaleJsonMap.put("presalelicense", "pre>persellno");
		presaleJsonMap.put("presaleProjName", "presell>projname");	
		presaleJsonMap.put("openDate", "presell>openingdate");	
		presaleJsonMap.put("saleState", "property>propertystatename");	
		presaleJsonMap.put("saleAddr", "property>selladdress");	
		presaleJsonMap.put("roomSalePhone", "property>selltel");	
	}
	public static final String roomListSelector = "div.onbuildshow_contant.colordg.ft14 > div > table > tbody > tr";
	public static final String roomPageInfoSelector = "div.spagenext > span";
	public static Map<String,String> roomDataSelectorMap =
			new HashMap<String,String>();
	static{
		roomDataSelectorMap.put("buildingNo", "td:nth-child(1)>a");
		roomDataSelectorMap.put("roomNo", "td:nth-child(2)>a>div");	
		roomDataSelectorMap.put("constructionArea", "td:nth-child(3)>a>div");	
		roomDataSelectorMap.put("innerArea", "td:nth-child(4)>a>div");	
		roomDataSelectorMap.put("getRate", "td:nth-child(5)>a>div");	
		roomDataSelectorMap.put("rawPrice", "td:nth-child(6)>a>div");	
		roomDataSelectorMap.put("decorationPrice", "td:nth-child(7)>a>div");
		roomDataSelectorMap.put("totalPrice", "td:nth-child(8)>a>div");
		roomDataSelectorMap.put("roomState", "td:nth-child(9)>div");
	}
}
