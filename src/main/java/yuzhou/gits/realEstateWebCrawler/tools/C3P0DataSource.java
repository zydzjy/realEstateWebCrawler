package yuzhou.gits.realEstateWebCrawler.tools;

import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0DataSource implements DataBaseSource {
	
	private ComboPooledDataSource comboPooledDataSource;
	public void initMethod(){
		try {
			comboPooledDataSource = new ComboPooledDataSource();
			comboPooledDataSource.setDriverClass("com.mysql.jdbc.Driver");
			comboPooledDataSource.setJdbcUrl("jdbc:mysql://localhost:3306/realeastatecrawler?useUnicode=true&characterEncoding=utf8");
			comboPooledDataSource.setUser("root");
			comboPooledDataSource.setPassword("12345678");
		} catch (Exception ex1) {
			ex1.printStackTrace();
		}
    }
	
	public C3P0DataSource() {
		
	}
	
	public static C3P0DataSource dataSourceInstance;
	static {
		dataSourceInstance = new C3P0DataSource();
		dataSourceInstance.initMethod();
	}
	
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