package yuzhou.gits.realEstateWebCrawler.app.TC;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class ProjectListExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected BuildingPageExtractCallback buildingPageCallback = new BuildingPageExtractCallback();
	public ProjectListExtractCallback() {
	}

	int currPageNo = 0;
	
	@Override
	public void extracting(String respStr) throws Exception {
		
		int currPageNo = (Integer) this.crawlTask.getPathContext()
				.getAttr(Constants.PAGE_CURRENT_NO);
		Document baseDoc = Jsoup.parse((String) respStr);
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;

		Elements projListE = baseDoc.select(TCConfig.projListSelector);
		Iterator<Element> projListIt = projListE.iterator();
		while (projListIt.hasNext()) {
			try {
				Element projListDataItemE = projListIt.next();
				
				Map<String, String> projListDataPropsMap = new HashMap<String, String>();
				extractor.extractDataByCssSelectors(TCConfig.projListDataSelectorMap, projListDataItemE,
						projListDataPropsMap);
				// TODO: save projList
				System.out.println(
						projListDataPropsMap.get("projName") + "(" + crawlerId + "," 
								+ currPageNo + "," + (currCount) + ")");
				Element projDetailLinkE = projListDataItemE.selectFirst(TCConfig.projDetailPageURLSelector);
				String projDetaiPageURLStr = projDetailLinkE.attr("onclick");
				Matcher m = TCConfig.projDetailPageURLPattern.matcher(projDetaiPageURLStr);
				if (m.find()) {
					String projId = m.group(1);
					String projDetailPageURL = TCConfig.siteDomain + "/" + projId;
					WebCrawling buildingCrawling = new WebCrawling(HttpMethod.GET, null,
							projDetailPageURL, 600000, 600000, 600000);
					WebCrawlingTask buildingCrawlTask = this.deriveNewTask("", true, 
							buildingCrawling,this.buildingPageCallback);
					buildingCrawlTask.getPathContext().setAttr("projectListData", projListDataPropsMap);
					buildingCrawlTask.getPathContext().setAttr("projId", projId);
					buildingCrawlTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, currPageNo);
					buildingCrawlTask.getPathContext().setAttr(DefaultRealEstateCrawlingCallback.CURR_COUNT_ATTR, 
							currCount);
					this.executor.execute(buildingCrawlTask);
				}
				currCount++;
			} catch (Exception _1) {
				_1.printStackTrace();
			}
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		this.buildingPageCallback.init(args);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.buildingPageCallback.clean(args);
	}
}