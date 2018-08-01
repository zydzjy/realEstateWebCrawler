package yuzhou.gits.realEstateWebCrawler.tools.DataItemMapper;

import java.util.HashMap;
import java.util.Map;

public class WZMapper implements DataMapper {

	public static Map<String,String> roomColorMap = new  HashMap<String,String>();
	static {
		roomColorMap.put("G1", "正常发售");
		roomColorMap.put("B7", "合同已登记");
		roomColorMap.put("B4", "非出售");
		roomColorMap.put("G6", "已签合同");
		roomColorMap.put("G2", "安置房");
		roomColorMap.put("G10", "已认购");
		roomColorMap.put("G4", "非出售");
		roomColorMap.put("B", "不在项目内");
		roomColorMap.put("B6", "已签合同");
		roomColorMap.put("B1", "正常发售");
		roomColorMap.put("G7", "合同已登记");
		roomColorMap.put("B10", "已认购");
		roomColorMap.put("B2", "安置房");
		roomColorMap.put("G5", "已签预订协议");
		roomColorMap.put("B5", "已签预订协议");
		roomColorMap.put("G", "非出售");
		
	}
	@Override
	public String[] map(String tableName,String[] origins) {
		String roomColor = (roomColorMap.get(origins[origins.length-1]) == null ? "" : 
			roomColorMap.get(origins[origins.length-1]));
		origins[origins.length-1] = roomColor;
		return origins;
	}
}
