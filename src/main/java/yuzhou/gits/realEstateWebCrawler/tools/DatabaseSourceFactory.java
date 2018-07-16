package yuzhou.gits.realEstateWebCrawler.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseSourceFactory implements DataSourceFactory {
	protected Connection dbConn = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	ConsecutiveResultSetsDataSource ds = null;
	
	public void init(Object...args){
		this.dbConn = C3P0DataSource.dataSourceInstance.getConnection();
	}
	@Override
	public void prepareSource(Object... args) throws Exception {
		String[] qrySelects = (String[]) args;
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
