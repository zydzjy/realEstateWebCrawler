package yuzhou.gits.realEstateWebCrawler.app.KMAN;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class BuildingListExtractCallback extends DefaultRealEstateCrawlingCallback {


	public BuildingListExtractCallback(String collectionName,DefaultRealEstateCrawlingCallback nextCallback) {
		super(collectionName,nextCallback);
	}


	@Override
	protected void extracting(String respStr) throws Exception {
		Document buildlistDoc = Jsoup.parse(respStr);
		Elements bulidListE = buildlistDoc.select(KMANConfig.buildingListSelector);
		int currPageNo = (Integer) this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);
		int currCount = (Integer) this.crawlTask.getPathContext().getAttr("currCount");
		Map<String,String> projMap =(Map<String,String>) this.crawlTask.getPathContext().getAttr("projMap");
		Iterator<Element> bulidListIt = bulidListE.iterator();
		while (bulidListIt.hasNext()) {
			try {
				Element buildE = bulidListIt.next();
				Map<String, String> buildMap = new HashMap<String, String>();
				this.extractor.extractDataByCssSelectors(KMANConfig.bulidSelectorMap, buildE, buildMap);
				String buildDetailUrl=buildE.selectFirst("tr td:nth-child(1)>a").attr("href");
				if(StringUtil.isBlank(buildDetailUrl)){
					continue;
				}
				buildDetailUrl=KMANConfig.contentURL+buildDetailUrl;
				WebCrawling roomsCrawling = new WebCrawling(HttpMethod.GET, null, buildDetailUrl, 600000, 600000,
						600000);
				WebCrawlingTask roomsCrawlTask = this.deriveNewTask("", true, roomsCrawling,
						nextCallBack);
				roomsCrawlTask.getPathContext().setAttr("currCount", currCount);
				roomsCrawlTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, currPageNo);
				roomsCrawlTask.getPathContext().setAttr("projMap", projMap);
				roomsCrawlTask.getPathContext().setAttr("buildMap", buildMap);
				this.executor.execute(roomsCrawlTask);
				
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}

	}

	public String getDocumentStr(String url) throws Exception {
		final Map<String, String> map = new HashMap<String, String>();
		WebCrawling urlCrawling = new WebCrawling(HttpMethod.GET, null, url, 600000, 600000, 600000);
		WebCrawlingTask projectDetailCrawlTask = this.deriveNewTask("", true, urlCrawling,
				new WebResourceCrawlingCallback() {
					@Override
					public void doCallback(WebCrawlingTask task) throws Exception {
						String docStr = (String) (task.getCrawling().getResponse());
						map.put("docStr", docStr);
					}

					@Override
					public void init(Object... args) throws Exception {
					}

					@Override
					public void clean(Object... args) throws Exception {
					}
				});
		this.executor.execute(projectDetailCrawlTask);
		return map.get("docStr");
	}
}
