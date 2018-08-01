package yuzhou.gits.realEstateWebCrawler.app.KMCG;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class KMCGConfig extends BaseCfg {
	public static final String siteDomain = "http://pre.chghouse.org";
	public static final String pageInfoSelector = "body > table:nth-child(8) > tbody > tr:nth-child(2) > td > strong";
	public static final Pattern pageInfoP = Pattern.compile("/(\\d+)");
	public static String  projListSelector = "body > table:nth-child(8) > tbody > tr:nth-child(1) > td > div > form > table:nth-child(n+2):nth-last-child(n-1) > tbody > tr > td > a";
	public static final String roomListDataSelector = "body > form > table > tbody > tr:nth-child(n+2):nth-last-child(n+1)";
	public static Map<String,String> roomDataSelectorMap = new HashMap<String,String>();
	static {
		roomDataSelectorMap.put("unitNo", "td:nth-child(2)");
		roomDataSelectorMap.put("roomNo", "td:nth-child(3)>font");
		roomDataSelectorMap.put("construction", "td:nth-child(4)");
		roomDataSelectorMap.put("roomUsage", "td:nth-child(5)");
		roomDataSelectorMap.put("roomType", "td:nth-child(6)");
		roomDataSelectorMap.put("constructionArea", "td:nth-child(7)");
		roomDataSelectorMap.put("innerArea", "td:nth-child(8)");
		roomDataSelectorMap.put("saleState", "td:nth-child(9)>font");
		roomDataSelectorMap.put("mortageState", "td:nth-child(10)");
		roomDataSelectorMap.put("openUnitPrice", "td:nth-child(11)");
		roomDataSelectorMap.put("openTotalPrice", "td:nth-child(12)");
	}
}