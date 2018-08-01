/*package yuzhou.gits.realEstateWebCrawler.app;

import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class PropsMapInToDBCallback extends InToDBCallback {

	public PropsMapInToDBCallback(int batchSize, int division) {
		super(batchSize, division);
	}

	@Override
	protected String getPsSql() {
		return null;
	}
	
	
	protected void _propsToCols(Object bean, PreparedStatement ps) {
		boolean isArray = bean.getClass().isArray();
		Map<String,String>[] beanArr = null;
		if(isArray){
			beanArr = (Map<String,String>[])bean;
		}else{
			beanArr = (Map<String, String>[])new Map[1];
			beanArr[0] = (Map<String, String>) bean;
		}
		for(int i=0;i<beanArr.length;i++){
			Map<String,String> propsMap = (Map<String,String>)beanArr[i];
			Iterator<Entry<String, String>> fields = propsMap.entrySet().iterator();
			while (fields.hasNext()) {
				Entry<String, String> propEntry = fields.next();
				String propertyVal = null;
				String propertyName = propEntry.getKey();
				try {
					propertyVal = propEntry.getValue();
					int idx = colNameToIdxMap.get(propertyName);
					ps.setString(idx, propertyVal);
				} catch (Exception e) {e.printStackTrace();}
			}
		}
	}
}
*/