package yuzhou.gits.realEstateWebCrawler.app.CS;

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
	protected BuildingExtractCallback buildingCallback = new BuildingExtractCallback();
	String projectCollectionName =  "cs_project";
	String presaleCollectionName = "cs_presalelicense";
	public ProjectListExtractCallback() {}
	
	static Pattern locationP = Pattern.compile("([^\\[查看地图\\]]*)");
	
	@Override
	public void extracting(String respStr) throws Exception {
		Document baseDoc = Jsoup.parse((String) respStr);
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;
		Elements projListE = baseDoc.select(CSConfig.projListDataSelector);
		Iterator<Element> projListIt = projListE.iterator();
		while (projListIt.hasNext()) {
			try {
				Element projDiv = projListIt.next();
				Element detailUrlE = projDiv
						.selectFirst(CSConfig.projDetailPageURLSelector);
				String projDetailDocURL = CSConfig.siteDomain+"NewHouse/"+detailUrlE.attr("href");
				WebCrawlingTask projectTask = this.deriveNewTask("", true, projDetailDocURL,new DefaultRealEstateCrawlingCallback(){
					@Override
					protected void extracting(String respStr) throws Exception {
						Document projDetailPageDoc = Jsoup.parse(respStr);
						Map<String,String> projDataPropsMap = new HashMap<String,String>();
						try {
							this.extractor.extractDataByCssSelectors(CSConfig.projDetailDataSelectorMap, 
									projDetailPageDoc, projDataPropsMap);
							String projLocation = projDataPropsMap.get("projLocation");
							if(projLocation!=null){
								Matcher m = locationP.matcher(projLocation);
								if(m.find()){
									projDataPropsMap.put("projLocation", m.group(0));
								}
							}
							//TODO:do callback
							System.out.println(projDataPropsMap.get("projName") + "(" + crawlerId + "," 
											+ currPageNo + "," +  ")");
							//
							Object[] callbackArgs = { projectCollectionName,"", 
									currPageNo,1, projDataPropsMap };
							this.mongoDBCallback.doCallback(callbackArgs);
							this.crawlTask.getPathContext().setAttr("project", projDataPropsMap);
							this.crawlTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO,this.currPageNo);
							//presale licenses
							Elements preSaleLicenseListE = projDetailPageDoc.select(
									CSConfig.preSaleLicensePageURLsSelector);
							Iterator<Element> preSaleLicenseEIt = preSaleLicenseListE.iterator();
							while(preSaleLicenseEIt.hasNext()){
								try{
									Element e = preSaleLicenseEIt.next();
									String presaleLicenseDetailPageURL = CSConfig.siteDomain+"NewHouse/"+e.attr("href");
									WebCrawlingTask presaleLicenseDetailTask = this.deriveNewTask("", false, 
											presaleLicenseDetailPageURL, 
											new DefaultRealEstateCrawlingCallback(){
												@Override
												protected void extracting(String respStr) throws Exception {
													Document presaleLicenseDetailPageDoc = Jsoup.parse(respStr);
													Map<String,String> preSaleLicensePropsMap = new HashMap<String,String>();
													this.extractor.extractDataByCssSelectors(CSConfig.preSaleLicenseSelectorMap, 
															presaleLicenseDetailPageDoc, preSaleLicensePropsMap);
													//TODO:do callback
													Object[] callbackArgs = { presaleCollectionName,"", 
															currPageNo,1, preSaleLicensePropsMap };
													this.mongoDBCallback.doCallback(callbackArgs);
												}
									});
									this.executor.execute(presaleLicenseDetailTask);
								}catch(Exception e){e.printStackTrace();}
							}
							//builds
							Elements buildListE = projDetailPageDoc.select(CSConfig.buildListSelector);
							Iterator<Element> buildListEIt = buildListE.iterator();
							while(buildListEIt.hasNext()){
								try{
									Map<String,String> mainBuildPropsMap = new HashMap<String,String>();
									Element e = buildListEIt.next();
									this.extractor.extractDataByCssSelectors(CSConfig.buildMainSelectorMap, 
											e, mainBuildPropsMap);
									Element buildingListPageE = e.selectFirst(CSConfig.buildPageURLsSelector);
									String buildDetailPageURL = CSConfig.siteDomain+"NewHouse/"+buildingListPageE.attr("href");
									WebCrawlingTask buildDetailTask = this.deriveNewTask("", false, buildDetailPageURL, 
											ProjectListExtractCallback.this.buildingCallback);
									buildDetailTask.getPathContext().setAttr("mainBuildPropsMap", mainBuildPropsMap);
									this.executor.execute(buildDetailTask);
								}catch(Exception e){e.printStackTrace();}
							}
							
						}catch(Exception e){e.printStackTrace();}
					}
				});
				projectTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO,this.currPageNo);
				this.executor.execute(projectTask);
				currCount++;
			}catch(Exception e){e.printStackTrace();}
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		super.init(args);
		this.projectCollectionName = this.projectCollectionName + this.datasetSuffix;
		this.presaleCollectionName = this.presaleCollectionName + this.datasetSuffix;
		this.mongoDBCallback.addPropsCollection(this.projectCollectionName,10);
		this.mongoDBCallback.addPropsCollection(this.presaleCollectionName,10);
		this.buildingCallback.init(args);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.buildingCallback.clean(args);
		this.mongoDBCallback.flush(projectCollectionName);
		this.mongoDBCallback.flush(presaleCollectionName);
	}
}