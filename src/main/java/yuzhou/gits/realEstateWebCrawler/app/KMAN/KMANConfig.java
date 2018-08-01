package yuzhou.gits.realEstateWebCrawler.app.KMAN;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class KMANConfig extends BaseCfg{
	public final static String contentURL="http://www.anhouse.org/";
	public final  static String baseURL = "http://www.anhouse.org/business/ysxkz?currentPage=[page]&numPerPage=10";
	public final static Pattern pageInfoP = Pattern.compile("共(\\d+)页");
	public final static String projPageInfoSelector = ".pagination>span";
	public final static String projListDataSelector = ".ER-lz table";
	public static final String buildingListSelector = ".xlsj-zongnr2>table>tbody>tr:nth-child(n+2):nth-last-child(n+2)";
	public static final String roomListSelector = ".xlsj-zongnr2>table>tbody>tr:nth-child(n+2)";

	public static final Map<String,String> baseSelectorMap = 
			new HashMap<String,String>();
	
	static{
		baseSelectorMap.put("projName", "tbody >tr:nth-child(1)>td:nth-child(2) > a>span");
		baseSelectorMap.put("projLocation", "tbody >tr:nth-child(2)>td:nth-child(2)>span");
		baseSelectorMap.put("occupyArea", "tbody >tr:nth-child(2)>td:nth-child(4)>span");
		baseSelectorMap.put("fzjg", "tbody >tr:nth-child(3)>td:nth-child(2)>span");
		baseSelectorMap.put("ysxkz", "tbody >tr:nth-child(3)>td:nth-child(4)");
		baseSelectorMap.put("fzDate", "tbody >tr:nth-child(4)>td:nth-child(2)");
	}
	
	public static final Map<String,String> bulidSelectorMap = 
			new HashMap<String,String>();
	static {
		bulidSelectorMap.put("buildName", "tr td:nth-child(1)>a");
		bulidSelectorMap.put("floorCount", "tr td:nth-child(2)");
		bulidSelectorMap.put("buildConstruct", "tr td:nth-child(3)");
		bulidSelectorMap.put("underFloorCount", "tr td:nth-child(4)");
		bulidSelectorMap.put("stage", "tr td:nth-child(5)");
		bulidSelectorMap.put("state", "tr td:nth-child(6)");
		bulidSelectorMap.put("buildNature", "tr td:nth-child(7)");
	}
	
	public static final Map<String,String> roomDetailSelectorMap = 
			new HashMap<String,String>();
	static {
		roomDetailSelectorMap.put("dyNo", "tr td:nth-child(1)");
		roomDetailSelectorMap.put("roomNo", "tr td:nth-child(2)");
		roomDetailSelectorMap.put("roomConstruct", "tr td:nth-child(3)");
		roomDetailSelectorMap.put("roomUse", "tr td:nth-child(4)");
		roomDetailSelectorMap.put("roomNaturet", "tr td:nth-child(5)");
		roomDetailSelectorMap.put("tnArea","tr td:nth-child(6)");
		roomDetailSelectorMap.put("jzArea","tr td:nth-child(7)");
	}
	
	
	
	
	public static final String projectDetailBaseURL = "http://www.kmhouse.org/lqt/ProjectBuild.asp?PId=[pid]&AId=1";
	public static final String buildingListBaseURL = "http://www.kmhouse.org/lqt/projectbuild.asp?PId=[pid]&AId=1&BId=[bid]";
	public static final String roomURL="http://www.kmhouse.org/lqt/HouseTypePic.asp?Id=[id]&PId=[pid]&AId=1";
	
	public static final String bulidingTale="body table";
	
	public static final String buildingSelector = "table[background] >tbody>tr:nth-child(2)>td:nth-child(2)>table>tbody>tr:nth-child(n+2)";
	
	public static final String roomTypeSelector = "table[background] >tbody>tr:nth-child(2)>td:nth-child(3)>table>tbody>tr:nth-child(n+2)>td>table>tbody>tr";
	public static final Map<String,String> bulidDetailSelectorMap = 
			new HashMap<String,String>();
	static {
		bulidDetailSelectorMap.put("roomNum", "tr td:nth-child(1)");
		bulidDetailSelectorMap.put("bulidNum", "tr td:nth-child(2)");
		bulidDetailSelectorMap.put("bulidType", "tr td:nth-child(3)");
	}
	
	public static final String roomDetailSelector = "table[background]>tbody>tr:nth-child(3)>td>table>tbody";

}
