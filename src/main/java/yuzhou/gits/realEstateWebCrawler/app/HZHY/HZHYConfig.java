package yuzhou.gits.realEstateWebCrawler.app.HZHY;

import java.util.HashMap;
import java.util.Map;
import yuzhou.gits.crawler.crawl.BaseCfg;

public class HZHYConfig extends BaseCfg{
	public final static String siteDomain = "http://218.14.121.21:8080/ysw/";
	public final static String nowSaleBaseURL = "http://218.14.121.21:8080/ysw/outyssearch.aspx";
	public final static String projListDataSelector = "#GridView1 >tbody>tr:nth-child(n+2):nth-last-child(n+2)";
	 
	public static Map<String,String> nowProjListDataItemsSelectorMap =
			new HashMap<String,String>();
	static {
		nowProjListDataItemsSelectorMap.put("projName","tr td:nth-child(1)");
		nowProjListDataItemsSelectorMap.put("licenseNo", "tr td:nth-child(2)");
		nowProjListDataItemsSelectorMap.put("developer", "tr td:nth-child(3) a");
		nowProjListDataItemsSelectorMap.put("projState", "tr td:nth-child(4)");
		nowProjListDataItemsSelectorMap.put("pzDate", "tr td:nth-child(5)");
	}
	
	public static Map<String,String> gtDetailSelectorMap = 
			new HashMap<String,String>();
	static{
		gtDetailSelectorMap.put("gtzh", "td:nth-child(1)");
		gtDetailSelectorMap.put("tdyt", "td:nth-child(2)");
		gtDetailSelectorMap.put("beginDate", "td:nth-child(3)");
		gtDetailSelectorMap.put("yearCount", "td:nth-child(4)");
		gtDetailSelectorMap.put("endDate", "td:nth-child(5)");
	}
	
	public static final String projDetailURLSelector = "td:nth-child(6) > a";
	
	public static Map<String,String> nowBuildListSelectorMap = 
			new HashMap<String,String>();
	static{
		nowBuildListSelectorMap.put("buildName", "td:nth-child(1)");
		nowBuildListSelectorMap.put("upFloor", "td:nth-child(2)");
		nowBuildListSelectorMap.put("underFloor", "td:nth-child(3)");
		nowBuildListSelectorMap.put("constrDate", "td:nth-child(4)");
	}
	public final static String roomListSelector = ".listtable>tbody:nth-child(2)>tr";
	 
}
