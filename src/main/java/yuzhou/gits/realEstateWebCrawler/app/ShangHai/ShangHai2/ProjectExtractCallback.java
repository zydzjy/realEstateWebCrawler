package yuzhou.gits.realEstateWebCrawler.app.ShangHai.ShangHai2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class ProjectExtractCallback extends DefaultRealEstateCrawlingCallback {
	ProjectDetailExtractCallback projDetailCallback = new ProjectDetailExtractCallback();
	private String projectCollectionName = "shanghai_project";
	@Override
	public void init(Object... args) throws Exception {
		super.init(args);
		this.projectCollectionName += this.datasetSuffix;
		this.mongoDBCallback.addPropsCollection(this.projectCollectionName,1);
		this.projDetailCallback.init(args);
	}
	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.projectCollectionName);
		this.projDetailCallback.clean(args);
	}
	@Override
	protected void extracting(String respStr) throws Exception {
		int currPageNo = (Integer) this.crawlTask.getPathContext()
				.getAttr(Constants.PAGE_CURRENT_NO);
		Document baseDoc = Jsoup.parse(respStr);
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;

		Elements projListE = baseDoc.select(ShangHai2Config.projListSelector);
		Iterator<Element> projListIt = projListE.iterator();
		while (projListIt.hasNext()) {
			try {
				Element projListDataItemE = projListIt.next();
				Map<String, String> projListDataPropsMap = new HashMap<String, String>();
				extractor.extractDataByCssSelectors(ShangHai2Config.projListDataSelectorMap, 
						projListDataItemE,projListDataPropsMap);
				// TODO: save projList
				if(currCount != 3){
					currCount++;
					continue;
				}
				
				System.out.println(projListDataPropsMap.get("projName") + "(" + crawlerId + "," 
								+ currPageNo + "," + (currCount) + ")");
				Object[] callbackArgs = { this.projectCollectionName,"", currPageNo,
						currCount, projListDataPropsMap};
				this.mongoDBCallback.doCallback(callbackArgs);
				String projDetailURL = ShangHai2Config.siteDomain + "/" +
						projListDataItemE.selectFirst("td:nth-child(2)>a").attr("href");
				WebCrawlingTask projDetailTask = 
						this.deriveNewTask("", true, projDetailURL, this.projDetailCallback);
				projDetailTask.getCrawling().setHttpMethod(HttpMethod.GET);
				projDetailTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, this.currPageNo);
				projDetailTask.getPathContext().setAttr("project", projListDataPropsMap);
				this.executor.execute(projDetailTask);
				
				currCount++;
			}catch(Exception _1){_1.printStackTrace();}
		}
	}
}
