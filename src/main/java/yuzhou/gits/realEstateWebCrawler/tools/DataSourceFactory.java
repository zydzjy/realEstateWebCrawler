package yuzhou.gits.realEstateWebCrawler.tools;

public interface DataSourceFactory {
	void init(Object...args) throws Exception;
	void prepareSource(Object...args) throws Exception;
	DataSource getDataSrc() throws Exception;
	void cleanSource() throws Exception;
}
