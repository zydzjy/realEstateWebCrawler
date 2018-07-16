package yuzhou.gits.realEstateWebCrawler.app.HZDYW;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.DefaultCrawlerStub;
import yuzhou.gits.crawler.crawl.DefaultCrawlingCallback;
import yuzhou.gits.crawler.crawl.DefaultCrawlingTask;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawlingExecutor;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.crawler.dataExtractor.DataExtractor;
import yuzhou.gits.crawler.dataExtractor.DataExtractorJsoupImpl;
import yuzhou.gits.crawler.http.URLEncoder;
import yuzhou.gits.realEstateWebCrawler.SingletonMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.app.IntoMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlerStub;

public class PreSaleListExtractCallback extends DefaultCrawlingCallback {
	protected BuildingListExtractCallback buildingListExtractCallback;
	protected IntoMongoDBCallback mongoDBCallback;
	protected String projCollectionName = "hzdyw_project20180709";
	protected String presalelicenseCollectionName = "hzdyw_presalelicense20180709";

	public PreSaleListExtractCallback() {
		this.buildingListExtractCallback = new BuildingListExtractCallback();
		this.mongoDBCallback = SingletonMongoDBCallback.singleton;
	}

	@Override
	public void _doCallback(WebCrawlingTask task) throws Exception {
		String resp = (String) (task.getCrawling().getResponse());
		Document baseDoc = Jsoup.parse((String) resp);
		int currPageNo = (Integer) this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);

		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;
		try {
			Elements preSaleLicenseListE = baseDoc.select(HZDYWConfig.preSaleLicenseListDataSelector);
			Iterator<Element> preSaleLicenseListEIt = preSaleLicenseListE.iterator();
			while (preSaleLicenseListEIt.hasNext()) {
				try {
					Element preSaleLicenseE = preSaleLicenseListEIt.next();
					String detailURL = HZDYWConfig.siteDomain + "web/"
							+ preSaleLicenseE.selectFirst(HZDYWConfig.preSaleLicenseDetailURLSelector).attr("href");
					detailURL = URLEncoder.encode(detailURL, "gbk");
					// extract project
					Map<String,String> preProjPropsMap = new HashMap<String,String>();
					this.extractor.extractDataByCssSelectors(HZDYWConfig.preProjListDataItemsSelectorMap,
							preSaleLicenseE, preProjPropsMap);
					// TODO:fire callback
					// System.out.println(preProj);
					System.out.println(
							preProjPropsMap.get("projName") + "(" + crawlerId + "," + currPageNo + "," + (currCount) + ")");
					
					// extract building
					String buildingURL = HZDYWConfig.siteDomain + "web/"
							+ preSaleLicenseE.selectFirst(HZDYWConfig.buildingURLSelector).attr("href");
					buildingURL = URLEncoder.encode(buildingURL, "gbk");
					WebCrawling buildingsCrawling = new WebCrawling(HttpMethod.GET, null,
							buildingURL, 600000, 600000, 600000);
					WebCrawlingTask buildingsCrawlTask = this.deriveNewTask("", true, 
							buildingsCrawling, buildingListExtractCallback);
					buildingsCrawlTask.getPathContext().setAttr("project", preProjPropsMap);
					buildingsCrawlTask.getPathContext().setAttr("currCount", currCount);
					buildingsCrawlTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, 
							currPageNo);
					executor.execute(buildingsCrawlTask);
					preProjPropsMap.put("projLocation", (String)buildingsCrawlTask.getPathContext()
							.getAttr("projLocation"));
					Object[] projCallbackArgs = { this.projCollectionName,
							crawlerId, currPageNo,currCount, preProjPropsMap };
					this.mongoDBCallback.doCallback(projCallbackArgs);
					
					//extract presalelicense
					WebCrawling preSaleCrawling = new WebCrawling(HttpMethod.GET, null,
							detailURL, 600000, 600000, 600000);
					WebCrawlingTask preSaleCrawlingTask = this.deriveNewTask("", true, 
							preSaleCrawling, new  DefaultCrawlingCallback() {
								@Override
								public void init(Object... args) throws Exception {}
								@Override
								public void clean(Object... args) throws Exception {}
								@Override
								protected void _doCallback(WebCrawlingTask _task) throws Exception {
									String _resp = (String) (_task.getCrawling().getResponse());
									Document _baseDoc = Jsoup.parse((String) _resp);
									int currCount = (Integer)this.crawlTask.getPathContext().getAttr("currCount");
									
									Map<String,String> preSaleLicensePropsMap = new HashMap<String,String>();
									this.extractor.extractDataByCssSelectors(HZDYWConfig.preSaleLicenseSelector, _baseDoc,
											preSaleLicensePropsMap);
									Object[] preSaleLicenseCallbackArgs = { presalelicenseCollectionName,
											crawlerId, currPageNo,currCount, preSaleLicensePropsMap };
									mongoDBCallback.doCallback(preSaleLicenseCallbackArgs);
								}
					});
					preSaleCrawlingTask.getPathContext().setAttr("project", preProjPropsMap);
					preSaleCrawlingTask.getPathContext().setAttr("currCount", currCount);
					preSaleCrawlingTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, 
							currPageNo);
					executor.execute(preSaleCrawlingTask);
					currCount++;
				} catch (Exception _1) {
					_1.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		mongoDBCallback.addPropsCollection(presalelicenseCollectionName, 10);
		this.mongoDBCallback.addPropsCollection(this.projCollectionName, 10);
		this.buildingListExtractCallback.init(args);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.projCollectionName);
		mongoDBCallback.flush(presalelicenseCollectionName);
		this.buildingListExtractCallback.clean(args);
	}
}