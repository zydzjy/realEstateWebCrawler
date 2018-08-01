package yuzhou.gits.realEstateWebCrawler.app.ShangHai;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class ProjectListExtractCallback extends DefaultRealEstateCrawlingCallback {
	PresaleExtractCallback presaleExtractCallback = new PresaleExtractCallback();
	private String projectCollectionName = "shanghai_project";
	public ProjectListExtractCallback() {
	}
	static Pattern p = Pattern.compile("'(.*)'");
	@Override
	public void init(Object... args) throws Exception {
		super.init(args);
		this.projectCollectionName += this.datasetSuffix;
		this.mongoDBCallback.addPropsCollection(this.projectCollectionName,1);
		this.presaleExtractCallback.init(args);
	}
	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.projectCollectionName);
		this.presaleExtractCallback.clean(args);
	}
	@Override
	public void extracting(String respStr) throws Exception {
		JsonElement respJson = new JsonParser().parse(respStr);
		Document baseDoc = Jsoup.parse(respJson.getAsJsonObject().get("htmlView").getAsString());
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;
		Elements projListE = baseDoc.select(ShangHaiConfig.projListSelector);
		Iterator<Element> projListIt = projListE.iterator();
		while (projListIt.hasNext()) {
			try {
				Element projListDataItemE = projListIt.next();
				Map<String, String> projListDataPropsMap = new HashMap<String, String>();
				extractor.extractDataByCssSelectors(ShangHaiConfig.projListDataSelectorMap, 
						projListDataItemE,projListDataPropsMap);
				
				/*if(!"上海五角世贸商城".equals(projListDataPropsMap.get("projName"))){
					currCount++;
					continue;
				}*/
				System.out.println(projListDataPropsMap.get("projName") + "(" + crawlerId + "," 
						+ currPageNo + "," + (currCount) + ")");
				Object[] callbackArgs = { this.projectCollectionName,"", currPageNo,
						currCount, projListDataPropsMap};
				this.mongoDBCallback.doCallback(callbackArgs);
				//extract project detail
				//houseDetail('a126c35004f85e63')
				String houseDetailStr = 
						projListDataItemE.selectFirst("td:nth-child(2)>a").attr("onclick");
				Matcher m = p.matcher(houseDetailStr);
				if(m.find()){
					String houseId = m.group(1);
					String[] postFormData = {"projectID",houseId};
					WebCrawlingTask projDetailTask = 
							this.deriveNewTask("", true, ShangHaiConfig.projectDetailURL, 
									new DefaultRealEstateCrawlingCallback(){
										@Override
										protected void extracting(String respStr) throws Exception {
											String developer = "";
											//System.out.println(respStr);
											try{
												JsonElement projJson = new JsonParser().parse(respStr);
												if(projJson != null && projJson.isJsonNull() == false){
													developer = projJson.getAsJsonObject().get("project")
															.getAsJsonObject().get("name").getAsString();
												}
											}catch(Exception e){e.printStackTrace();}
											//presale,buidlings
											WebCrawlingTask presaleTask = 
													this.deriveNewTask("", true, ShangHaiConfig.presaleURL, 
															ProjectListExtractCallback.this.presaleExtractCallback);
											presaleTask.getCrawling().setHttpMethod(HttpMethod.POST);
											presaleTask.getCrawling().setPostFormData(postFormData);
											presaleTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, this.currPageNo);
											presaleTask.getPathContext().setAttr("project", projListDataPropsMap);
											presaleTask.getPathContext().setAttr("developer", developer);
											this.executor.execute(presaleTask); 
										}
							});
					projDetailTask.getCrawling().setHttpMethod(HttpMethod.POST);
					projDetailTask.getCrawling().setPostFormData(postFormData);
					projDetailTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, this.currPageNo);
					projDetailTask.getPathContext().setAttr("project", projListDataPropsMap);
					this.executor.execute(projDetailTask); 
					
					
				}
			}catch(Exception e){e.printStackTrace();}
			currCount++;
		}
	}
}