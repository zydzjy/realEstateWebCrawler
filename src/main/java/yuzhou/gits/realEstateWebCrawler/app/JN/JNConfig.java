package yuzhou.gits.realEstateWebCrawler.app.JN;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class JNConfig extends BaseCfg{
	public static final String contentURL="http://124.128.246.22:8090/onsaling/index[page].shtml";
	public final static String projectListSelector = ".project_table >tbody>tr:nth-child(n+2):nth-last-child(n+2)";
	public static Map<String, String> projectSelectorMap = new HashMap<String, String>();
	public final static Pattern pageInfoP = Pattern.compile("/(\\d+)页");
	public final static String projPageInfoSelector = ".project_table >tbody>tr:nth-last-child(1)>td>span";
	static {
		projectSelectorMap.put("canSellCount", "td:nth-child(5)");
	}
	
	public final static String projectDetailURL="http://124.128.246.22:8090/onsaling/show[buildPage].shtml?prjno=[prjnoId]";
	public final static String buildFirstSelectorMap = ".message_table >tbody";
	public static Map<String, String> firstBuildSelectorMap = new HashMap<String, String>();
	static {
		firstBuildSelectorMap.put("projName", "tr:nth-child(n+2)>td:nth-child(2)");//项目名称
		firstBuildSelectorMap.put("projAddr", "tr:nth-child(n+2)>td:nth-child(4)");//项目地址
		firstBuildSelectorMap.put("developer", "tr:nth-child(n+3)>td:nth-child(2)");//企业名称
		firstBuildSelectorMap.put("county", "tr:nth-child(n+3)>td:nth-child(4)");//所在区县
		firstBuildSelectorMap.put("projScale", "tr:nth-child(n+4)>td:nth-child(2)");//项目规模
		firstBuildSelectorMap.put("buildCount", "tr:nth-child(n+4)>td:nth-child(4)");//总栋数
		firstBuildSelectorMap.put("sellAddr", "tr:nth-child(n+5)>td:nth-child(2)");//售楼地址
		firstBuildSelectorMap.put("sellPhone", "tr:nth-child(n+5)>td:nth-child(4)");//售楼电话
		firstBuildSelectorMap.put("propertyCompany", "tbody>tr:nth-child(n+6)>td:nth-child(2)");//物业公司
	}
	
	public final static String buildSelectorList = ".project_table >tbody>tr:nth-child(n+2):nth-last-child(n+2)";
	public static Map<String, String> secondBuildSelectorMap = new HashMap<String, String>();
	static {
		//secondBuildSelectorMap.put("buildName", "td:nth-child(2)>a");//楼盘名称 会截取
		secondBuildSelectorMap.put("ysxkz", "td:nth-child(3)");//预售许可证
		secondBuildSelectorMap.put("count", "td:nth-child(4)");//总套数
		secondBuildSelectorMap.put("countArea", "td:nth-child(5)");//总面积
	}
	
	public final static String buildDetailURL="http://124.128.246.22:8090/onsaling/bshow.shtml?bno=[bnoId]";

	public final static String roomFirstSelectorMap = ".message_table >tbody";
	public static Map<String, String> firstRoomSelectorMap = new HashMap<String, String>();
	static {
		firstRoomSelectorMap.put("buildName", "tr:nth-child(n+2)>td:nth-child(2)>span>span");//楼盘名称
		firstRoomSelectorMap.put("jzArear", "tr:nth-child(n+5)>td:nth-child(4)");//建筑面积（万㎡）
		firstRoomSelectorMap.put("ysxkz", "tr:nth-child(n+8)>td:nth-child(2)");//商品预售许可证
		firstRoomSelectorMap.put("tdsyz", "tr:nth-child(n+8)>td:nth-child(4)");//国有土地使用证
		firstRoomSelectorMap.put("ghxkz", "tr:nth-child(n+9)>td:nth-child(2)");//建设工程规划许可证
		firstRoomSelectorMap.put("sgxkz", "tr:nth-child(n+9)>td:nth-child(4)");//建设工程施工许可证
	}
	
	public final static String roomListURL="http://124.128.246.22:8090/onsaling/viewhouse.shtml?fmid=[bnoId]";

	public final static String dySelectorList = "#floorTable >tbody>tr:nth-child(1)>td:nth-child(n+2)";
	public final static String xhSelectorList = "#floorTable >tbody>tr:nth-child(2)>td:nth-child(n+2)";
	public final static String floorTableSelectorList = "#floorTable >tbody>tr:nth-child(n+3)";
	public final static String bussTableSelectorList = "#bussTable >tbody>tr";
	public final static String roomDetailURL="http://124.128.246.22:8090/onsaling/viewDiv.shtml?fid=[hid]&rd=";
	
}
