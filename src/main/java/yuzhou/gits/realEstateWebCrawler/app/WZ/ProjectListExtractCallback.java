package yuzhou.gits.realEstateWebCrawler.app.WZ;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class ProjectListExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected BuildingExtractCallback buildingExtractCallback = new BuildingExtractCallback();
	
	public ProjectListExtractCallback() {
	}
	public static final Pattern projDetailUrlP = Pattern.compile("'(.*)'");
	static Map<String,String> projNamesMap = new HashMap<String,String>();
	static{
		projNamesMap.put("金典时代","1");
		projNamesMap.put("金轩嘉园","1");
		projNamesMap.put("新国光商住广场","1");
		projNamesMap.put("站南商贸城C、EF幢","1");
	}
	@Override
	public void extracting(String respStr) throws Exception {
		Document baseDoc = Jsoup.parse((String) respStr);
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;
		Elements projListDataE = baseDoc.select(WZConfig.projectListSelector);
		Iterator<Element> projListDataEIt = projListDataE.iterator();
		while (projListDataEIt.hasNext()) {
			try {
				Element projDataE = projListDataEIt.next();
				Map<String, String> projPropsMap = new HashMap<String, String>();
				this.extractor.extractDataByCssSelectors(WZConfig.projListDataSelectorMap, 
						projDataE, projPropsMap);
				/*if(projNamesMap.get(projPropsMap.get("projName")) == null){
					currCount++;
					continue;
				}*/
				System.out.println(projPropsMap.get("projName") + "(" + crawlerId + "," + currPageNo + "," + (currCount) + ")");
				Matcher m = projDetailUrlP.matcher(projDataE.attr("onclick"));
				if(m.find()){
					String projDetailPageURL = WZConfig.siteDomain
							+ "/realweb/stat/"+m.group(1);
					WebCrawlingTask buildingTask = this.deriveNewTask("", true, projDetailPageURL, this.buildingExtractCallback);
					buildingTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, this.currPageNo);
					buildingTask.getPathContext().setAttr("project", projPropsMap);
					buildingTask.getPathContext().setAttr("currCount", currCount);
					this.executor.execute(buildingTask);
				}
			}catch(Exception  _1){_1.printStackTrace();} 
			currCount++;
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		this.buildingExtractCallback.init(args);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.buildingExtractCallback.clean(args);
	}
}