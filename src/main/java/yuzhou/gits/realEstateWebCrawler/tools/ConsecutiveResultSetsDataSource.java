package yuzhou.gits.realEstateWebCrawler.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConsecutiveResultSetsDataSource implements DataSource {
	int qryIdx = 0;
	String[] qrySelects = null;
	ResultSet currRs = null;
	PreparedStatement currPs = null;
	Connection dbConn = null;
	public ConsecutiveResultSetsDataSource(Connection conn,String[] qrySelects) throws Exception{
		this.qrySelects = qrySelects;
		this.dbConn = conn;
		currPs = dbConn.prepareStatement("select * from "+
				this.qrySelects[this.qryIdx++]);
		currRs = currPs.executeQuery();
	}
	@Override
	public boolean next() throws DataSrcDrainedReachedException {
		try {
			if(currRs.next() == false){
				if(this.qryIdx == this.qrySelects.length){
					throw new DataSrcDrainedReachedException();
				}else{
					this.currRs.close();
					this.currPs.close();
					currPs = dbConn.prepareStatement("select * from "+
							this.qrySelects[this.qryIdx++]);
					currRs = currPs.executeQuery();
					return this.currRs.next();
				}
			}else{
				return true;
			}
		} catch (SQLException e) {
			return false;
		}
	}
	@Override
	public Object getSrc() throws Exception {
		return this.currRs;
	}
	@Override
	public Object[] getObjVal(String... objNames) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void init(Object... args) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
