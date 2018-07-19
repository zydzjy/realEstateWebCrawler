package yuzhou.gits.realEstateWebCrawler.app.KM;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class KMConfig extends BaseCfg{

	public final  static String baseURL = "http://www.kmhouse.org/lqt/ProjectIndex2.asp?Page=[page]";
	public static final String projectDetailBaseURL = "http://www.kmhouse.org/lqt/ProjectBuild.asp?PId=[pid]&AId=1";
	public static final String buildingListBaseURL = "http://www.kmhouse.org/lqt/projectbuild.asp?PId=[pid]&AId=1&BId=[bid]";
	public static final String roomURL="http://www.kmhouse.org/lqt/HouseTypePic.asp?Id=[id]&PId=[pid]&AId=1";
	public final static Pattern pageInfoP = Pattern.compile("/(\\d+) 页");
	public final static String projPageInfoSelector = "div table:eq(2) >tbody > tr:nth-child(2) > td:nth-child(2) > table > tbody >tr:nth-child(2)> td > table > tbody > tr >td";
	public final static String projListDataSelector = "div table:eq(2) >tbody > tr:nth-child(2) > td:nth-child(2) > table > tbody > tr > td > table > tbody > tr > td > table > tbody tr:nth-child(n+2)";
	
	public static final Map<String,String> baseSelectorMap = 
			new HashMap<String,String>();
	
	static{
		baseSelectorMap.put("projName", "tr td:nth-child(1) > a > font");
		baseSelectorMap.put("projLocation", "tr td:nth-child(2) > font");
		baseSelectorMap.put("occupyArea", "tr td:nth-child(3) > a > font");
		baseSelectorMap.put("phone", "tr td:nth-child(4) > font");
	}
	
	public static final String bulidingTale="body table";
	public static final String buildingListSelector = "table[background] > tbody>tr:nth-child(2)>td:nth-child(1)>table>tbody>tr:nth-child(2)>td>table>tbody>tr";
	
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

	public static final Map<String,String> roomDetailSelectorMap = 
			new HashMap<String,String>();
	static {
		roomDetailSelectorMap.put("roomType", "tbody>tr:nth-child(1)>td:nth-child(2)");
		roomDetailSelectorMap.put("inArea", "tbody>tr:nth-child(1)>td:nth-child(4)");
		roomDetailSelectorMap.put("poolArea", "tbody>tr:nth-child(2)>td:nth-child(2)");
	}
}
