package yuzhou.gits.realEstateWebCrawler.app.ZH;

import java.util.HashMap;
import java.util.Map;

import yuzhou.gits.crawler.crawl.BaseCfg;

public class ZHConfig extends BaseCfg {

	public static final String siteDomain = "http://219.131.222.106:30090";
	public static Map<String,String> projJsonMap = 
			new HashMap<String,String>();
	static{
		projJsonMap.put("presaleLicenseNo", "SaleNumber");
		projJsonMap.put("projName", "BPrjName");
		projJsonMap.put("developer", "saleOrgName");
		projJsonMap.put("mortage", "sfdy");
		projJsonMap.put("constructionArea", "SaleAreaAll");
		projJsonMap.put("houseArea", "SaleHouseArea");
		projJsonMap.put("bizArea", "SaleBusinessArea");
		projJsonMap.put("issueDate", "DateOfIssue");
	}
	
	public static Map<String,String> presaleLicenseSelectorMap = 
			new HashMap<String,String>();
	static{
		//presaleLicenseSelectorMap.put("projName", "#InfoPathDataForm1_ViewBody > div > table > tbody > tr:nth-child(4) > td:nth-child(2) > div > textarea");
		//presaleLicenseSelectorMap.put("presaleLicenseNo", "#InfoPathDataForm1_ViewBody > div > table > tbody > tr:nth-child(3) > td:nth-child(2) > div > font > textarea");
		//presaleLicenseSelectorMap.put("developer", "#InfoPathDataForm1_ViewBody > div > table > tbody > tr:nth-child(3) > td:nth-child(2) > div > font > textarea");
	    presaleLicenseSelectorMap.put("projLocation", "#InfoPathDataForm1_ViewBody > div > table > tbody > tr:nth-child(4) > td:nth-child(4)  div textarea");
		presaleLicenseSelectorMap.put("remark", "#InfoPathDataForm1_ViewBody > div > table > tbody > tr:nth-child(4) > td:nth-child(5) div textarea");
		presaleLicenseSelectorMap.put("buildInfo", "#InfoPathDataForm1_ViewBody > div > table > tbody > tr:nth-child(6) > td:nth-child(2) > div  input  >> attr=value");
		presaleLicenseSelectorMap.put("houseArea", "#InfoPathDataForm1_ViewBody > div > table > tbody > tr:nth-child(7) > td:nth-child(3)   div  input  >> attr=value");
		presaleLicenseSelectorMap.put("houseCnts", "#InfoPathDataForm1_ViewBody > div > table > tbody > tr:nth-child(7) > td:nth-child(5)   div  input  >> attr=value");
		presaleLicenseSelectorMap.put("bizArea", "#InfoPathDataForm1_ViewBody > div > table > tbody > tr:nth-child(8) > td:nth-child(2)   div  input  >> attr=value");
		presaleLicenseSelectorMap.put("bizCnts", "#InfoPathDataForm1_ViewBody > div > table > tbody > tr:nth-child(8) > td:nth-child(4)   div  input  >> attr=value");
		presaleLicenseSelectorMap.put("officeArea", "#InfoPathDataForm1_ViewBody > div > table > tbody > tr:nth-child(9) > td:nth-child(2)  div  input  >> attr=value");
		presaleLicenseSelectorMap.put("officeCnts", "#InfoPathDataForm1_ViewBody > div > table > tbody > tr:nth-child(9) > td:nth-child(4)   div  input  >> attr=value");
		presaleLicenseSelectorMap.put("otherArea", "#InfoPathDataForm1_ViewBody > div > table > tbody > tr:nth-child(10) > td:nth-child(2)  div  input  >> attr=value");
		presaleLicenseSelectorMap.put("otherCnts", "#InfoPathDataForm1_ViewBody > div > table > tbody > tr:nth-child(10) > td:nth-child(4)  div  input  >> attr=value");
		presaleLicenseSelectorMap.put("bankNum", "#InfoPathDataForm1_ViewBody > div > table > tbody > tr:nth-child(11) > td:nth-child(2)  div  textarea");
	}
	public static Map<String,String> roomSelectorMap = 
			new HashMap<String,String>();
	static{
		roomSelectorMap.put("buildNo", "td:nth-child(2)>nobr");
		roomSelectorMap.put("origiBuildNo", "td:nth-child(3)>nobr");
		roomSelectorMap.put("roomNo", "td:nth-child(4)>nobr");
		roomSelectorMap.put("unitNo", "td:nth-child(5)>nobr");
		roomSelectorMap.put("floorNo", "td:nth-child(6)>nobr");
		roomSelectorMap.put("origiRoomNo", "td:nth-child(7)>nobr");
		roomSelectorMap.put("roomType", "td:nth-child(8)>nobr");
		roomSelectorMap.put("constructionArea", "td:nth-child(9)>nobr");
		roomSelectorMap.put("innerArea", "td:nth-child(10)>nobr");
		roomSelectorMap.put("roomUsage", "td:nth-child(11)>nobr");
		roomSelectorMap.put("propertyFee", "td:nth-child(13)>nobr");
	}
}
/*
 * background-color: rgb(109, 228, 152); -- 可售
 */
