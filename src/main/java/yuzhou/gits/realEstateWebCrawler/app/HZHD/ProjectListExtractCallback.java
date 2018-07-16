package yuzhou.gits.realEstateWebCrawler.app.HZHD;

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
import yuzhou.gits.realEstateWebCrawler.SingletonMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.app.IntoMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class ProjectListExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected String preSaleCollectionName = "hzhd_prelicense20180710";
	protected String projCollectionName = "hzhd_project20180710";
	protected BuildingListExtractCallback buildingListExractCallback = new BuildingListExtractCallback();
	public ProjectListExtractCallback() {
	}
	
	@Override
	public void init(Object... args) throws Exception {
		 this.mongoDBCallback.addPropsCollection(this.projCollectionName,10);
		 this.mongoDBCallback.addPropsCollection(this.preSaleCollectionName,10);
		 this.buildingListExractCallback.init(args);
	}

	@Override
	public void clean(Object...args) throws Exception {
		this.mongoDBCallback.flush(this.projCollectionName);
		this.mongoDBCallback.flush(this.preSaleCollectionName);
		this.buildingListExractCallback.clean(args);
	}

	@Override
	protected void extracting(String respStr) throws Exception {
		if (respStr != null) {
			Document baseDoc = Jsoup.parse(respStr);
			Elements projListE = baseDoc.select(HZHDConfig.projListDataSelector);
			if(projListE == null) return;
			int currCount = 1;
			Iterator<Element> projListIt = projListE.iterator();
			while (projListIt.hasNext()) {
				Element projListItemE = projListIt.next();
				Map<String,String> projPropsMap = new HashMap<String,String>();
				this.extractor.extractDataByCssSelectors(
						HZHDConfig.projListDataItemsSelectorMap, 
						projListItemE, projPropsMap);
				// TODO:fire project into database callback
				System.out.println(projPropsMap.get("projName") + "(" + 
						crawlerId + "," + currPageNo + "," + (currCount) + ")");
				Object[] callbackArgs = { this.projCollectionName,
						crawlerId, currPageNo,currCount, projPropsMap };
				this.mongoDBCallback.doCallback(callbackArgs);
				// extract presale license
				{
					String preSaleUrl = HZHDConfig.siteDomain+"web/"+URLEncoder.encode(
							projListItemE.selectFirst(HZHDConfig.preSaleLicenseDetailURLSelector)
							.attr("href"), "gbk");
					WebCrawling preSaleLicenseCrawling = new WebCrawling(HttpMethod.GET, null,
							preSaleUrl, 600000, 600000, 600000);
					WebCrawlingTask preSaleLicenseCrawlTask = this.deriveNewTask("", true, 
							preSaleLicenseCrawling,new DefaultRealEstateCrawlingCallback(){
								@Override
								public void init(Object... args) throws Exception {}
								@Override
								public void clean(Object... args) throws Exception {}
								@Override
								protected void extracting(String respStr) throws Exception {
									int currCount = (Integer) this.crawlTask.getPathContext().getAttr(CURR_COUNT_ATTR);
									Document preSaleLicenseNoPageDoc = (Document)Jsoup.parse(respStr);
									Map<String,String> licensePropsMap = new HashMap<String,String>();
									licensePropsMap.put("preSaleLicense", projPropsMap.get("licenseNo"));
									licensePropsMap.put("projName", projPropsMap.get("projName"));
									licensePropsMap.put("projLocation", projPropsMap.get("projLocation"));
									licensePropsMap.put("developer", projPropsMap.get("developer"));
									this.extractor.extractDataByCssSelectors(HZHDConfig.preSaleLicenseSelector, 
											preSaleLicenseNoPageDoc,
											licensePropsMap);
									// TODO:fire callback
									Object[] callbackArgs = { preSaleCollectionName,
											crawlerId, currPageNo,currCount,licensePropsMap };
									mongoDBCallback.doCallback(callbackArgs);
								}
					});
					preSaleLicenseCrawlTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, 
							currPageNo);
					preSaleLicenseCrawlTask.getPathContext().setAttr(CURR_COUNT_ATTR, 
							currCount);
					this.executor.execute(preSaleLicenseCrawlTask);
				}
				
				//extracting buildings
				String buildingURL = HZHDConfig.siteDomain+"web/"+projListItemE.selectFirst(HZHDConfig.buildingURLSelector).attr("href");
				//TODO：fix bug
				int idx1 = buildingURL.lastIndexOf("&");
				int idx2 = buildingURL.lastIndexOf("=");
				String paramVal = buildingURL.substring(idx2);
				String qryStr = "ProjectCode"+paramVal;
				buildingURL = buildingURL.substring(0, idx1+1)+qryStr;
				buildingURL = URLEncoder.encode(buildingURL, "gbk");
				WebCrawling buildingsCrawling = new WebCrawling(HttpMethod.GET, null,
						buildingURL, 600000, 600000, 600000);
				WebCrawlingTask buildingsCrawlTask = this.deriveNewTask("", true, 
						buildingsCrawling,this.buildingListExractCallback);
				buildingsCrawlTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, 
						currPageNo);
				buildingsCrawlTask.getPathContext().setAttr(CURR_COUNT_ATTR, 
						currCount);
				buildingsCrawlTask.getPathContext().setAttr("project", 
						projPropsMap);
				this.executor.execute(buildingsCrawlTask);
				currCount++;
			}
		}
	}
}