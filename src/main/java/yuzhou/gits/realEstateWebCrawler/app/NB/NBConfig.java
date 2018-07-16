package yuzhou.gits.realEstateWebCrawler.app.NB;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class NBConfig extends BaseCfg {
	public final static String siteDomain = "http://old.newhouse.cnnbfdc.com";
	public final static String baseURL = "/Lpxx.aspx?Region=&ProjectName=";
	public final static String projListPageURL = "/Lpxx.aspx?Region=&ProjectName=&p=";
	public final static String projPageInfoSelector = "body > table:nth-child(7) > tbody > tr > td:nth-child(1) > table > tbody > tr:nth-child(2) > td > table > tbody > tr:nth-child(20) > td > div > a:last-child";
	public final static String pageInfoP = "p=(\\d+)";
	public final static String projectListSelector =
			"body > table:nth-child(7) > tbody > tr > td:nth-child(1) > table > tbody > tr:nth-child(2) > td > table > tbody > tr:nth-child(n+2):nth-last-child(n+2)";
	public final static Map<String,String> projDataSelectorMap = 
			new HashMap<String,String>();
	static {
		projDataSelectorMap.put("projName", "td:nth-child(2)>a");
		projDataSelectorMap.put("adminArea", "td:nth-child(3)>span");
		projDataSelectorMap.put("projAddr", "td:nth-child(4)");
	}
	public final static Map<String,String> projDetailSelectorMap = 
			new HashMap<String,String>();
	static {
		projDetailSelectorMap.put("bankName", "table.sp_sck + table > tbody > tr:nth-child(1) > td:nth-child(2)");
		projDetailSelectorMap.put("bankNo", "table.sp_sck + table > tbody > tr:nth-child(1) > td:nth-child(4)");
		projDetailSelectorMap.put("projState", "table.sp_sck + table > tbody > tr:nth-child(2) > td:nth-child(4)>span");
		projDetailSelectorMap.put("developer", "table.sp_sck + table > tbody > tr:nth-child(4) > td:nth-child(2)");
		projDetailSelectorMap.put("salelicenseName", "table.sp_sck + table > tbody > tr:nth-child(5) > td:nth-child(2)");
		projDetailSelectorMap.put("salelicenseNo", "table.sp_sck + table > tbody > tr:nth-child(5) > td:nth-child(4)");
		
	} 
	
	public final static Map<String,String> roomDetailSelectorMap = 
			new HashMap<String,String>();
	static {
		roomDetailSelectorMap.put("roomNo", "div:nth-child(2)>table>tbody>tr:nth-child(2)>td:nth-child(2)");
		roomDetailSelectorMap.put("realFloor", "div:nth-child(2)>table>tbody>tr:nth-child(2)>td:nth-child(4)");
		roomDetailSelectorMap.put("roomType", "div:nth-child(2)>table>tbody>tr:nth-child(3)>td:nth-child(2)");
		roomDetailSelectorMap.put("roomStruct", "div:nth-child(2)>table>tbody>tr:nth-child(3)>td:nth-child(4)");
		roomDetailSelectorMap.put("roomUsage", "div:nth-child(2)>table>tbody>tr:nth-child(4)>td:nth-child(2)");
		roomDetailSelectorMap.put("predictedConstrustArea", "div:nth-child(2)>table>tbody>tr:nth-child(4)>td:nth-child(4)");
		roomDetailSelectorMap.put("predictedInnerArea", "div:nth-child(2)>table>tbody>tr:nth-child(5)>td:nth-child(2)");
		roomDetailSelectorMap.put("predictedShareArea", "div:nth-child(2)>table>tbody>tr:nth-child(5)>td:nth-child(4)");
		roomDetailSelectorMap.put("realConstrustArea", "div:nth-child(2)>table>tbody>tr:nth-child(6)>td:nth-child(2)");
		roomDetailSelectorMap.put("realInnerArea", "div:nth-child(2)>table>tbody>tr:nth-child(6)>td:nth-child(4)");
		roomDetailSelectorMap.put("realShareArea", "div:nth-child(2)>table>tbody>tr:nth-child(7)>td:nth-child(2)");
		//roomDetailSelectorMap.put("saleState", "table.sp_sck + table > tbody > tr:nth-child(5) > td:nth-child(4)");
	} 
}
