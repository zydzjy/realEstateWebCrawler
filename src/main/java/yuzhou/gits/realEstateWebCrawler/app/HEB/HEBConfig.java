package yuzhou.gits.realEstateWebCrawler.app.HEB;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class HEBConfig extends BaseCfg {
	protected String roomCollectionBaseName;
	protected int roomBatchSize = 1;
	
	public String getRoomCollectionBaseName() {
		return roomCollectionBaseName;
	}
	public void setRoomCollectionBaseName(String roomCollectionBaseName) {
		this.roomCollectionBaseName = roomCollectionBaseName;
	}
	public int getRoomBatchSize() {
		return roomBatchSize;
	}
	public void setRoomBatchSize(int roomBatchSize) {
		this.roomBatchSize = roomBatchSize;
	}
	public final static String siteDomain = "http://spfmmbj.hrbszfc.com:5399/";
	public static Pattern pageInfoP = Pattern.compile("共(\\d+)页");
	//public final static String baseURL = "http://spfmmbj.hrbszfc.com:5399/mmbjquery/jsp/queryPresale.action?toptype=WSFDC";
	public final static String projPageURL = "http://spfmmbj.hrbszfc.com:5399/mmbjquery/queryPresale.action?developer=&projectname=&district=99&prosit=&toptype=WSFDC&bustype=%3Cs%3Aproperty+value%3D%27bustype%27+%2F%3E&pager.offset=[OFFSET]&pageNo=[PAGENO]";
	public final static String projectListSelector = ".BRC2Tab > table tr:nth-child(n+2):nth-last-child(n+2)";
	public final static String projPageInfoSelector = "body > div.BRCentent2 > div.BRC2Tab > table > tbody > tr:last-child > td > div > a:nth-child(5) > nobr";
	public final static String projectDetailURL = "http://spfmmbj.hrbszfc.com:5399/mmbjquery/Lpb-lpbShow.action";
	
	public static final Map<String, String> projListDataSelector = new HashMap<String,String>();
	static{
		projListDataSelector.put("developer", "td:eq(0)");
		projListDataSelector.put("projName", "td:eq(1) >> attr=title");
		projListDataSelector.put("preSaleLicenseNo", "td:eq(2)");
		projListDataSelector.put("area", "td:eq(3)");
		projListDataSelector.put("projLocation", "td:eq(4)");
	}
	public static final String buildingBaseURL = 
			"http://spfmmbj.hrbszfc.com:5399/mmbjquery/Lpb-lpbShow.action?toptype=WSFDC";
		
	public static final String floorsSelector = "#lpbTable > tbody > tr";
	public static final String unitsNameSelector = "td:nth-child(2) > table > tbody > tr > td";
	public static final String floorNoESelector = ">td:first-child";
	public static final String roomsPerUnitSelectStr = ">td:nth-child(2) > table > tbody > tr > td";
	public static final String roomsSelectStr = "table>tbody>tr>td";
	 
}
