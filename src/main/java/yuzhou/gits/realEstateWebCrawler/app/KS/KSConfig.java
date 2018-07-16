package yuzhou.gits.realEstateWebCrawler.app.KS;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class KSConfig extends BaseCfg {
	public static final String siteDomain = "http://ksfc.kscein.gov.cn";
	public static final String baseURL = "http://ksfc.kscein.gov.cn/lp/search_loupan.aspx";
	public static final String projCtxPath = "/lp";
	public static final String projPageInfoSelector = "#lblcount";
	public static final Pattern pageInfoP = Pattern.compile("共(\\d+)页");
	public static final String projListSelector = 
			"#DataGrid1 > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	
	public static final Map<String,String> projListDataSelectorMap= 
			new HashMap<String,String>();
	static {
		projListDataSelectorMap.put("projName", "td:nth-child(1)>a>>attr=title");
		projListDataSelectorMap.put("projLocation", "td:nth-child(2)>>attr=title");
		projListDataSelectorMap.put("area", "td:nth-child(3)");
		projListDataSelectorMap.put("arrovedSaleTotals", "td:nth-child(4)");
		projListDataSelectorMap.put("saledTotals", "td:nth-child(5)");
		
	}
	
	public static final String projDetailPageURLSelector = "td:nth-child(1) > a";
	public static final String projDetailPage2URLSelector =
			"#search_loupan > map > area:nth-child(2)";
	public static final Map<String,String> projDataSelector = 
			new HashMap<String,String>();
	static {
		projDataSelector.put("projName", "#lblitemname");
		projDataSelector.put("projLocation", "#lblitemaddress");
		projDataSelector.put("developer", "#HyperLink1");
		projDataSelector.put("currSaleTotalArea", "#lblcanarea");
		projDataSelector.put("currSaleTotalUnits", "#lblcancount");
		projDataSelector.put("avgPrice", "#lbljg");
		projDataSelector.put("adminDivision", "#lblreg");
		projDataSelector.put("salePhone", "#Label9");
		projDataSelector.put("saleAddr", "#Label10");
		projDataSelector.put("remark", "#Label11");
	}
	public static final Map<String,String> projData2Selector = 
			new HashMap<String,String>();
	static {
		projData2Selector.put("projType", "#lblkind");
		projData2Selector.put("totalLandArea", "#lbljzarea");
		projData2Selector.put("totalConstructionArea", "#lbltotalarea");
		projData2Selector.put("tracffic", "#lblpjadress");
		projData2Selector.put("salePhone", "#Label6");
		projData2Selector.put("startTime", "#lblkgdate");
		projData2Selector.put("endTime", "#lbljgdate");
		projData2Selector.put("developer", "#Hyperlink4");
		projData2Selector.put("projDesc", "#Label9");
		projData2Selector.put("utilDecoration", "#Label10");
		projData2Selector.put("exeProgress", "#Label11");
		projData2Selector.put("utility", "#Label12");
		projData2Selector.put("aroudTracffic", "#Label13");
		projData2Selector.put("greenRate", "#Label14");
		projData2Selector.put("rjRate", "#Label15");
		projData2Selector.put("saleLocation", "#Label16");
	}
	
	public static final String buildingListPageURLSelector = 
			"#search_loupan > map > area:nth-child(3)";
	public static final String buildingListSelector = 
			"#DataGrid1 > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	public static final Map<String,String> buildingListDataSelector = 
			new HashMap<String,String>();
	static {
		buildingListDataSelector.put("gaBuildingNo", "td:nth-child(1) > a");
		buildingListDataSelector.put("buildingNo", "td:nth-child(2)");
		buildingListDataSelector.put("referencePrice", "td:nth-child(3)");
		buildingListDataSelector.put("saleUnits", "td:nth-child(4)");
		buildingListDataSelector.put("saledUnits", "td:nth-child(5)");
	}
	public static final String buildingDetailPageURLSelector = "td > a";
	public static final Pattern buildingListInfoStrP = Pattern.compile("innerHTML=\"([\\w\\W]*)\"");
	public static final String unitListSelector = "body > table[class=\"td2\"] > tbody > tr > td";
	public static final String roomTdSelector = "tbody td";
	public static final Map<String,String> roomDataSelectorsMap = 
			new HashMap<String,String>();
	static {
		roomDataSelectorsMap.put("unitName", "#lbldy");
		roomDataSelectorsMap.put("roomNo", "#lblfh");
		roomDataSelectorsMap.put("roomUsage", "#lblkind");
		roomDataSelectorsMap.put("roomType", "#lblfzstruct");
		roomDataSelectorsMap.put("predicatedConstructionArea", 
				"#lblzjarea");
		roomDataSelectorsMap.put("predicatedInnerArea", 
				"#lbltmarea");
		roomDataSelectorsMap.put("predicatedShareArea", 
				"#lblftarea");
		roomDataSelectorsMap.put("ytType", "#lblyt");
		roomDataSelectorsMap.put("roomStruct", "#lbljg");
		roomDataSelectorsMap.put("areaSrc", "#lblmj");
		roomDataSelectorsMap.put("state", "#lblstatus");
	}
}
