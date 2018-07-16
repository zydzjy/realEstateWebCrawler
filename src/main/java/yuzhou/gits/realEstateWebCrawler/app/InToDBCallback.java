package yuzhou.gits.realEstateWebCrawler.app;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import yuzhou.gits.crawler.dataExtractor.BeanExtractedCallback;
import yuzhou.gits.realEstateWebCrawler.tools.C3P0DataSource;

public abstract class InToDBCallback implements BeanExtractedCallback {

	protected int batchSize = 1;
	protected String dbUrl = "";
	protected String dbUser = "";
	protected String dbPwd = "";
	protected String table = "";
	protected int division = 1;
	public void init(Object...args){
		
	}
	public int getDivision() {
		return division;
	}

	public void setDivision(int division) {
		this.division = division;
	}

	public InToDBCallback(int batchSize,int division){
		this.batchSize = batchSize;
		this.division = division;
	}
	protected Connection dbCon = null;
	public void init() throws Exception {
		//jdbc:mysql://[host1][:port1][,[host2][:port2]]...[/[database]]
		this.dbCon = C3P0DataSource.dataSourceInstance.getConnection();
	}
	
	public void destroy() throws Exception {
		this.dbCon.close();
	}
	protected Map<String,Integer> colNameToIdxMap = 
			new HashMap<String,Integer>();
	protected List<Object> dataTempList = new ArrayList<Object>();
	
	public void doCallback(Object t) throws Exception {
		this.dataTempList.add(t);
		if(this.dataTempList.size() >= this.batchSize){
			try{
				long sTime = System.currentTimeMillis();
				this.persist();
				long eTime = System.currentTimeMillis();
				//System.out.println("into db time elpased:"+(eTime-sTime)/1000.f);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				this.dataTempList.clear();
			}
		}
	}
	
	protected abstract String getPsSql();
	protected void persist() throws Exception {
		
		PreparedStatement ps = this.dbCon.prepareStatement(getPsSql());
		Iterator<Object> it = this.dataTempList.iterator();
		while(it.hasNext()){
			Object bean = it.next();
			this._propsToCols(bean, ps);
			ps.addBatch();
			ps.clearParameters();
		}
		ps.executeBatch();
		//System.out.println(ps.executeBatch().length);
		ps.clearBatch();
		ps.close();
	}
	
	
	public void flush() {
		if(this.dataTempList.size() > 0){
			try{
				this.persist();
				this.dataTempList.clear();
			}
			catch(Exception e){e.printStackTrace();}
			//finally{
			//}
		}
	}
	
	protected void _propsToCols(Object bean, PreparedStatement ps) {
		
		Class<? extends Object> projClz = bean.getClass();
		Field[] fields = projClz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			String propertyVal = null;
			String propertyName = fields[i].getName();
			try {
				/*if(propertyName.startsWith("_"))
					continue;*/
				propertyVal = (String) PropertyUtils.getSimpleProperty(bean, propertyName);
				//System.out.println(propertyName+"="+propertyVal);
				int idx = colNameToIdxMap.get(propertyName);
				ps.setString(idx, propertyVal);
			} catch (Exception e) {
			}
		}
	}
}
