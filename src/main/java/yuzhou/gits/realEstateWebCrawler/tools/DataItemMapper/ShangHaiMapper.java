package yuzhou.gits.realEstateWebCrawler.tools.DataItemMapper;

import java.util.HashMap;
import java.util.Map;

public class ShangHaiMapper implements DataMapper {

	public static Map<String,String> projStateMap = new  HashMap<String,String>();
	static {
		projStateMap.put("1", "即将开售");
		projStateMap.put("2", "在售");
		projStateMap.put("3", "即将开售,在售");
		projStateMap.put("4", "售完");
		projStateMap.put("5", "即将开售,售完");
		projStateMap.put("6", "在售,售完");
		projStateMap.put("7", "即将开售,在售,售完");
		projStateMap.put("8", "暂停销售");
		projStateMap.put("9", "即将开售,暂停销售");
		projStateMap.put("10", "在售,暂停销售");
		projStateMap.put("11", "即将开售,在售,暂停销售");
		projStateMap.put("12", "售完,暂停销售");
		projStateMap.put("13", "即将开售,售完,暂停销售");
		projStateMap.put("14", "在售,售完,暂停销售");
		projStateMap.put("15", "即将开售,在售,售完,暂停销售");
	}
	public static Map<String,String> roomStateMap = new  HashMap<String,String>();
	static {
		roomStateMap.put("3", "已登记");
		roomStateMap.put("2", "已签");
		roomStateMap.put("4", "可售");
		roomStateMap.put("8", "已付定金");
		roomStateMap.put("9", "未纳入网上销售");
	}
	@Override
	public String[] map(String tableName,String[] origins) {
		if("表1-楼盘列表".equalsIgnoreCase(tableName)){
			mapProject(origins);
		}else if("表3-房号".equalsIgnoreCase(tableName)){
			mapRoom(origins);
		}
		return origins;
	}
	
	public void mapProject(String[] origins) {
		origins[1] = projStateMap.get(origins[1]);
	}
	
	public void mapRoom(String[] origins) {
		origins[6] = roomStateMap.get(origins[6]);
	}
}
