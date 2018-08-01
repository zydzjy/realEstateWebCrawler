package yuzhou.gits.realEstateWebCrawler.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseSourceFactory implements DataSourceFactory {
	protected Connection dbConn = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	ConsecutiveResultSetsDataSource ds = null;
	static C3P0DataSource dataSourceInstance = new C3P0DataSource();
	public void init(Object...args){
		dataSourceInstance.initMethod((String)args[0], (String)args[1], 
				(String)args[2], (String)args[3]);
		this.dbConn = dataSourceInstance.getConnection();
	}
	@Override
	public void prepareSource(Object... args) throws Exception {
		String[] qrySelects = new String[args.length];
		for(int i=0;i<args.length;i++){
			qrySelects[i] = (String)args[i];
		}
		this.ds = new ConsecutiveResultSetsDataSource(this.dbConn,qrySelects);
	}

	@Override
	public void cleanSource() throws Exception {
		
	}

	@Override
	public DataSource getDataSrc() throws Exception {
		return this.ds;
	}
}
