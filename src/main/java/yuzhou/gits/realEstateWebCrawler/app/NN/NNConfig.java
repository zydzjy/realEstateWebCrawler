package yuzhou.gits.realEstateWebCrawler.app.NN;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class NNConfig extends BaseCfg {

	public final static String projectListBaseURL = "http://www.nnfcxx.com/vipdata/fcj.php?list=xjspf&ctg=0&gsmc=";
	public final static String projPageURL = "http://www.nnfcxx.com/vipdata/fcj.php?list=xjspf&ctg=0&gsmc=&page=[PAGENO]";
	public final static String projectListSelector = "#container > div.main > div.article > div:nth-child(1) > table > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	public final static String pageInfoSelector = "#container > div.main > div.article > div.pages > ul > cite";
	public final static Pattern pageInfoP = Pattern.compile("/(\\d+)页");
	
	public static final Map<String, String> projListDataSelector = new HashMap<String,String>();
	static{
		projListDataSelector.put("developer", "td:eq(0)");
		projListDataSelector.put("projName", "td:eq(1)");
		projListDataSelector.put("projLocation", "td:eq(2)");
		projListDataSelector.put("preSaleLicense", "td:eq(3)");
		projListDataSelector.put("preSaleArea", "td:eq(4)");
		projListDataSelector.put("approveDate", "td:eq(5)");
	}
	
	public static final String buildingListSelector = 
			"#container > div.main > div.article > table.table4 > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	public static final Map<String, String> buildingListDataSelectorMap = 
			new HashMap<String,String>();
	static{
		buildingListDataSelectorMap.put("buildNo", "td:eq(0)");
		buildingListDataSelectorMap.put("units", "td:eq(2)");
	}	
	
	public static final String roomListSelector =
			"#container > div.main > div.article.mt10 > table.houses > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	public static final Map<String, String> roomListDataSelectorMap = 
			new HashMap<String,String>();
	static{
		roomListDataSelectorMap.put("floorNo", "th");
	}
	public static final String roomInfoStrSelect = 
			"td>div>ul>li";
	public static final Map<String,Pattern> roomDetailRegExprs = new HashMap<String,Pattern>();
	static {
		roomDetailRegExprs.put("roomNo", Pattern.compile("房号：([^<br />\r\n]*)"));
		//roomDetailRegExprs.put("saleState", Pattern.compile("房号：([^<br />]*)"));
		roomDetailRegExprs.put("construction", Pattern.compile("结构：([^<br />\r\n]*)"));
		roomDetailRegExprs.put("roomType", Pattern.compile("户型：([^<br />\r\n]*)"));
		roomDetailRegExprs.put("roomUsage", Pattern.compile("用途：([^<br />\r\n]*)"));
		roomDetailRegExprs.put("constructArea", Pattern.compile("建筑面积：([^<br />\r\n]*)"));
		roomDetailRegExprs.put("planningSalePrice", Pattern.compile("拟售单价：([^\r\n]*)"));
	}
	
	/*
	background-color:#F00 --已备案
	background-color:#c00 --已签约
	background-color:#0000FF--签约中
	background-color:#00FFFF--未出售
	background-color:#823813--在建工程抵押
	*/
}
