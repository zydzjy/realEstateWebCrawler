package yuzhou.gits.realEstateWebCrawler.tools;

public interface DataSource {

	public void init(Object...args) throws Exception;
	public void destroy() throws Exception;
	public Object getSrc() throws Exception;
	public Object[] getObjVal(String...objNames) throws Exception;
	public boolean next() throws Exception;
}
