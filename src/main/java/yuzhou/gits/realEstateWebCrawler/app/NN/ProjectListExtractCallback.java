package yuzhou.gits.realEstateWebCrawler.app.NN;

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
	Pattern projDetailUrlP = Pattern.compile("\'(.*)\'");
	
	public ProjectListExtractCallback() {
	}
	
	@Override
	public void extracting(String respStr) throws Exception {
		Document baseDoc = Jsoup.parse((String) respStr);
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;
		Elements projListDataE = baseDoc.select(NNConfig.projectListSelector);
		Iterator<Element> projListDataEIt = projListDataE.iterator();
		while (projListDataEIt.hasNext()) {
			try {
				Element projDataE = projListDataEIt.next();
				Map<String, String> projPropsMap = new HashMap<String, String>();
				this.extractor.extractDataByCssSelectors(NNConfig.projListDataSelector, 
						projDataE, projPropsMap);
				System.out.println(projPropsMap.get("projName") + "(" + crawlerId + "," + currPageNo + "," + (currCount) + ")");
				String projDetailUrlStr = projDataE.attr("onclick");
				Matcher m = projDetailUrlP.matcher(projDetailUrlStr);
				m.find();
				String projDetailUrl = m.group(1).trim();
				WebCrawlingTask buildingTask = 
						this.deriveNewTask("", true, projDetailUrl, this.buildingExtractCallback); 
				buildingTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, this.currPageNo);
				buildingTask.getPathContext().setAttr("project", projPropsMap);
				this.executor.execute(buildingTask);
				
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