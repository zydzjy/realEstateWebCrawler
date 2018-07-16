package yuzhou.gits.realEstateWebCrawler.app.HZBL;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.crawler.http.URLEncoder;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class ProjectListExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected BuildingListExtractCallback buildingListExtractCallback;
	protected String projCollectionName = "hzbl_project20180710";
	protected String presaleCollectionName = "hzbl_presalelicense20180710";

	public ProjectListExtractCallback() {
		this.buildingListExtractCallback = new BuildingListExtractCallback();
	}

	@Override
	public void extracting(String respStr) throws Exception {
		HZBLConfig config = (HZBLConfig) this.stub.getBaseCfg();
		String projectType = config.projectType;

		int currPageNo = (Integer) this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);
		Document baseDoc = Jsoup.parse(respStr);
		int currCount = 1;
		Elements projListE = baseDoc.select(HZBLConfig.projListDataSelector);
		if (projListE == null)
			return;
		Iterator<Element> projListIt = projListE.iterator();
		while (projListIt.hasNext()) {
			Element projListItemE = projListIt.next();

			Map<String, String> projPropsMap = new HashMap<String, String>();
			this.extractor.extractDataByCssSelectors(HZBLConfig.projListDataItemsSelectorMap, projListItemE,
					projPropsMap);
			if ("PRE".equals(projectType)) {
				projPropsMap.put("preOrNow", "预售");
			}else{
				projPropsMap.put("preOrNow", "现售");
			}
			System.out.println(projPropsMap.get("projName") + "(" + crawlerId + "," + currPageNo + "," + (currCount) + ")");
			Object[] callbackArgs = { this.projCollectionName,"", currPageNo,currCount, projPropsMap };
			this.mongoDBCallback.doCallback(callbackArgs);
			this.crawlTask.getPathContext().setAttr(CURR_COUNT_ATTR, currCount);
			this.crawlTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, currPageNo);
			this.crawlTask.getPathContext().setAttr("project", projPropsMap);
			// extract presale license
			if ("PRE".equals(projectType)) {
				String url = URLEncoder.encode(
						HZBLConfig.preSaleLicenseDetailURL.replace("[LICENSENO]", 
								projPropsMap.get("licenseNo")), "gbk");
				WebCrawling presaleCrawling = new WebCrawling(HttpMethod.GET, null,
						url, 600000, 600000, 600000);
				WebCrawlingTask preSaleCrawlTask = this.deriveNewTask("", false, 
						presaleCrawling,new DefaultRealEstateCrawlingCallback(){
							@Override
							public void init(Object... args) throws Exception {}
							@Override
							public void clean(Object... args) throws Exception {}
							@Override
							protected void extracting(String respStr) throws Exception {
								int currCount = (Integer)this.crawlTask.getPathContext().getAttr(CURR_COUNT_ATTR);
								
								Document preSaleLicenseNoPageDoc = Jsoup.parse(respStr);
								Map<String,String> licensePropsMap = new HashMap<String,String>();
								extractor.extractDataByCssSelectors(HZBLConfig.preSaleLicenseSelector, 
										preSaleLicenseNoPageDoc,licensePropsMap);
								licensePropsMap.put("preSaleLicense",projPropsMap.get("licenseNo"));
								licensePropsMap.put("projName",projPropsMap.get("projName"));
								licensePropsMap.put("projLocation",projPropsMap.get("projLocation"));
								licensePropsMap.put("developer",projPropsMap.get("developer"));
								// TODO:fire callback
								Object[] callbackArgs = { presaleCollectionName,"", 
										currPageNo,currCount, licensePropsMap };
								this.mongoDBCallback.doCallback(callbackArgs);
							}
				});
				this.executor.execute(preSaleCrawlTask);
			}
			
			String buildingURL = HZBLConfig.siteDomain+"web/"+projListItemE.selectFirst(HZBLConfig.buildingURLSelector).attr("href");
			buildingURL = URLEncoder.encode(buildingURL, "gbk");
			WebCrawling buildingCrawling = new WebCrawling(HttpMethod.GET, null,
					buildingURL, 600000, 600000, 600000);
			WebCrawlingTask buildingCrawlTask = this.deriveNewTask("", false, 
					buildingCrawling,this.buildingListExtractCallback);
			this.executor.execute(buildingCrawlTask);
			
			currCount++;
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		this.mongoDBCallback.addPropsCollection(this.projCollectionName, 10);
		this.mongoDBCallback.addPropsCollection(this.presaleCollectionName,10);
		this.buildingListExtractCallback.init(args);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.projCollectionName);
		this.mongoDBCallback.flush(this.presaleCollectionName);
		this.buildingListExtractCallback.clean(args);
	}
}