package yuzhou.gits.realEstateWebCrawler.tools;

import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0DataSource implements DataBaseSource {
	
	private ComboPooledDataSource comboPooledDataSource;
	public void initMethod(String driverClzName,String url,String user,String pwd){
		try {
			comboPooledDataSource = new ComboPooledDataSource();
			comboPooledDataSource.setDriverClass(driverClzName);
			//"jdbc:mysql://localhost:3306/realeastatecrawler?useUnicode=true&characterEncoding=utf8
			comboPooledDataSource.setJdbcUrl(url);
			comboPooledDataSource.setUser(user);
			comboPooledDataSource.setPassword(pwd);
		} catch (Exception ex1) {
			ex1.printStackTrace();
		}
    }
	
	public C3P0DataSource() {
		
	}
	
	public static C3P0DataSource dataSourceInstance;
	
	public Connection getConnection() {
		Connection con = null;
		try {
			con = comboPooledDataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
}