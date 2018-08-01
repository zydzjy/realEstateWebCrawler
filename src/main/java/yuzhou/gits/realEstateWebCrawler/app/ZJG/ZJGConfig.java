package yuzhou.gits.realEstateWebCrawler.app.ZJG;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class ZJGConfig extends BaseCfg{
	public final static String pageUrl="http://218.4.91.246:81/EpointWebBuilder_zjgfcj/zjgfcj/spf/projectList.jspx";
	public final static String contentURL = "http://218.4.91.246:81";
	public final static String projListURL = "http://218.4.91.246:81/EpointWebBuilder_zjgfcj/zjgFcjWebAction.action?cmd=getSpfList&pageIndex=[pageIndex]&pageSize=10&pname=/EpointWebBuilder_zjgfcj";
	public final static String projDetailURL="http://218.4.91.246:81/EpointWebBuilder_zjgfcj/zjgFcjWebAction.action?cmd=getSpfDetail&spfguid=[spfguid]&yszguid=[yszguid]";
	public final static String houseListURL="http://218.4.91.246:81/EpointWebBuilder_zjgfcj/zjgFcjWebAction.action?cmd=getHouseList&spfguid=[spfguid]&yszguid=[yszguid]";
	public final static String roomListURL="http://218.4.91.246:81/EpointWebBuilder_zjgfcj/zjgFcjWebAction.action?cmd=getRoomList&guid=[guid]";
	public final static Pattern pageInfoP = Pattern.compile("/(\\d+)");
	public final static String projPageInfoSelector = ".pg_maxpagenum";
	public final static String houserTypeListSelector = "body>table";
	public final static String floorListSelector = "table>tbody>tr:has(table)";
	public final static String bulidTypeSelector = "td:nth-last-child(3)";
	public final static String roomFloorSelector = "td:nth-last-child(2)";
	public final static String roomListDetailSelector = "td:nth-last-child(1)>table>tbody>tr>td";
	public static Map<String,String> roomListSelectorMap =
			new HashMap<String,String>();
	static {
		roomListSelectorMap.put("xuhao","td:nth-child(1)");
		roomListSelectorMap.put("yszh", "td:nth-child(2) a");
		roomListSelectorMap.put("projName", "td:nth-child(3)");
		roomListSelectorMap.put("taoshuCount", "td:nth-child(4)");
		roomListSelectorMap.put("areaCount", "td:nth-child(5)");
	}
}
