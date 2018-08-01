package yuzhou.gits.realEstateWebCrawler.app.XT;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class XTConfig {
	public final static String siteDomain = "http://floor.0731fdc.com";
	public final static String baseURL = "/search.php?ssqy=1&ssbkid=a&subway=a&zxzt=&price=&q=";
	public final static String pageInfoSelector = "#page > div > ul";
	public final static Pattern pageInfoP = Pattern.compile("共(\\d+)页");
	
	public final static String projListPageURL = "/search.php?page=[PAGENO]&q=&xmdd=&wylx=&ssbkid=a&price=&tjw=&ssqy=1&kfgs=&zxzt=&hmts=&subway=a&zt=&tj=&tg=&dsf=&xhx=&dz=&bs=&code=&floorid=&xy_type=&qu_type=&leitype=&sort=";
	public final static String projListDataSelector = "#ddd > div";
	public final static String projDetailPageUrlSelector = "a";
	public static Map<String,String> projDetailSelectorMap =
			new HashMap<String,String>();
	static{
		projDetailSelectorMap.put("projName", "body > div.container > div.box.clearfix > div.floor_info > div.info_c > div > h2");
		projDetailSelectorMap.put("adminArea", "body  div.floor-fr.mt12 > div.info > p:nth-child(2) > span:nth-child(2)");
		projDetailSelectorMap.put("openDate", "body  div.floor-fr.mt12 > div.info > p:nth-child(2) > span:nth-child(1)");
		projDetailSelectorMap.put("occupyArea", "body  div.floor-fr.mt12 > div.info > p:nth-child(3) > span:nth-child(1)");
		projDetailSelectorMap.put("totalConstructionArea", "body  div.floor-fr.mt12 > div.info > p:nth-child(3) > span:nth-child(2)");
		projDetailSelectorMap.put("rjl", "body  div.floor-fr.mt12 > div.info > p:nth-child(4) > span:nth-child(1)");
		projDetailSelectorMap.put("greenRate", "body  div.floor-fr.mt12 > div.info > p:nth-child(4) > span:nth-child(2)");
		projDetailSelectorMap.put("propertyFee", "body  div.floor-fr.mt12 > div.info > p:nth-child(5) > span:nth-child(1)");
		projDetailSelectorMap.put("propertyCompany", "body  div.floor-fr.mt12 > div.info > p:nth-child(5) > span:nth-child(2)");
		projDetailSelectorMap.put("developer", "body  div.floor-fr.mt12 > div.info > p:nth-child(6)");
		projDetailSelectorMap.put("projLocation", "body  div.floor-fr.mt12 > div.info > p:nth-child(7)");
	}
	
	public static final String buildingListSelector = "body div.import_div dd";
	public static Map<String,String> buildingDataSelectorMap =
			new HashMap<String,String>();
	static{
		buildingDataSelectorMap.put("buildingName", "span:nth-child(1)");
		buildingDataSelectorMap.put("presalelicenseno", "span:nth-child(2)");
		buildingDataSelectorMap.put("issueDate", "span:nth-child(3)");
		buildingDataSelectorMap.put("approveArea", "span:nth-child(4)");
		buildingDataSelectorMap.put("landCerti", "span:nth-child(5)");
		buildingDataSelectorMap.put("projPlanningLicense", "span:nth-child(6)");
		buildingDataSelectorMap.put("landPlanningLicense", "span:nth-child(7)");
		buildingDataSelectorMap.put("carryoutLicense", "span:nth-child(8)");
		buildingDataSelectorMap.put("updateDate", "span:nth-child(9)");
	}
	
	public static final String roomListSelector = "div.m-import-h > div.scroll > tr";
	
	public static final String roomListURL = "/index.php?action=ajaxhslist";
	
	public static Map<String,String> roomSelectorMap = new HashMap<String,String>();
	static {
		roomSelectorMap.put("roomNo", "td:nth-child(1)");
		roomSelectorMap.put("floorNo", "td:nth-child(2)");
		roomSelectorMap.put("roomUsage", "td:nth-child(3)");
		roomSelectorMap.put("roomType", "td:nth-child(4)");
		roomSelectorMap.put("decorationLevel", "td:nth-child(5)");
		roomSelectorMap.put("constructionArea", "td:nth-child(6)");
		roomSelectorMap.put("innerArea", "td:nth-child(7)");
		roomSelectorMap.put("shareArea", "td:nth-child(8)");
		roomSelectorMap.put("saleState", "td:nth-child(9)");
	}
}