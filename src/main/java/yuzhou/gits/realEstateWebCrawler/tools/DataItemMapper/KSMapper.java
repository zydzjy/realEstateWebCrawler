package yuzhou.gits.realEstateWebCrawler.tools.DataItemMapper;

import java.util.HashMap;
import java.util.Map;

public class KSMapper implements DataMapper {

	public static Map<String,String> roomColorMap = new  HashMap<String,String>();
	static {
		roomColorMap.put("../images/loupan/bg-1.gif", "");
		roomColorMap.put("../images/loupan/bg-2.gif", "");
		roomColorMap.put("../images/loupan/bg-3.gif", "");
		roomColorMap.put("../images/loupan/bg-4.gif", "");
		roomColorMap.put("../images/loupan/bg-5.gif", "");
		roomColorMap.put("../images/loupan/bg-6.gif", "");
		roomColorMap.put("../images/loupan/bg-7.gif", "");
	}
	@Override
	public String[] map(String tableName,String[] origins) {
		String roomColor = (roomColorMap.get(origins[4]) == null ? "+" : 
			roomColorMap.get(origins[4]) + "+") + origins[14];
		
		return origins;
	}
}
