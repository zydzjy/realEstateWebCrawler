package yuzhou.gits.realEstateWebCrawler.tools;

public class MongoDBSourceFactory implements DataSourceFactory {

	MongoDBDataSource dbSource = null;
	@Override
	public void prepareSource(Object... args) throws Exception {
		String dbName = (String) args[0];
		String collectionName =(String)args[1];
		if(args.length>2){
			String filterStr = (String)args[2];
			this.dbSource.findCollection(dbName, collectionName,filterStr);
		}else{
			this.dbSource.findCollection(dbName, collectionName);
		}
	}

	@Override
	public DataSource getDataSrc() throws Exception {
		return this.dbSource;
	}

	@Override
	public void cleanSource() throws Exception {
		this.dbSource.destroy();
		this.dbSource = null;
	}

	@Override
	public void init(Object... args) throws Exception {
		this.dbSource = new MongoDBDataSource();
		this.dbSource.init(args);
	}

}
