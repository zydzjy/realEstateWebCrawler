package yuzhou.gits.realEstateWebCrawler.app.HZHY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.SingletonMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.app.IntoMongoDBCallback;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class ProjectListExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected BuildingListExtractCallback buildingListExtractCallback;
	protected IntoMongoDBCallback mongoDBCallback;
	protected String projCollectionName = "hzhy_project";

	public ProjectListExtractCallback() {
		this.buildingListExtractCallback = new BuildingListExtractCallback();
		this.mongoDBCallback = SingletonMongoDBCallback.singleton;
	}

	@Override
	public void init(Object... args) throws Exception {
		super.init(args);
		this.projCollectionName=this.projCollectionName+this.datasetSuffix;
		this.mongoDBCallback.addPropsCollection(this.projCollectionName, 10);
		this.buildingListExtractCallback.init(args);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.projCollectionName);
		this.buildingListExtractCallback.clean(args);
	}

	@Override
	protected void extracting(String respStr) throws Exception {
		Document baseDoc = Jsoup.parse( respStr);
		int currPageNo = (Integer) this.crawlTask.getPathContext().getAttr(Constants.PAGE_CURRENT_NO);
		String __VIEWSTATE=baseDoc.selectFirst("#__VIEWSTATE").val();
		String __EVENTVALIDATION=baseDoc.selectFirst("#__EVENTVALIDATION").val();
		HZHYNowSaleCrawler.set__EVENTVALIDATION(__EVENTVALIDATION);
		HZHYNowSaleCrawler.set__VIEWSTATE(__VIEWSTATE);
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;
		try {
			if (baseDoc != null) {
				Elements projListE = baseDoc.select(HZHYConfig.projListDataSelector);
				Iterator<Element> projListIt = projListE.iterator();
				while (projListIt.hasNext()) {
					try {
						Element projListItemE = projListIt.next();
						Map<String, String> projFirstMap = new HashMap<String, String>();
						this.extractor.extractDataByCssSelectors(HZHYConfig.nowProjListDataItemsSelectorMap,
								projListItemE, projFirstMap);

						// save building first,then project for get location
						System.out.println(projFirstMap.get("projName") + "(" + crawlerId + "," + currPageNo + ","
								+ (currCount) + ")");
						
						// extract projectDetail
						String projectDetailURL = HZHYConfig.siteDomain
								+ projListItemE.selectFirst(HZHYConfig.projDetailURLSelector).attr("href");
						WebCrawling projDetailCrawling = new WebCrawling(HttpMethod.GET, null, projectDetailURL, 600000,
								600000, 600000);
						final int countCurr=currCount;
						final Map<String,String> projPreMap=projFirstMap;
						final List<Map<String,String>> buildList=new ArrayList<Map<String,String>>();
						WebCrawlingTask projectDetailCrawlTask = this.deriveNewTask("", true, projDetailCrawling,
								new WebResourceCrawlingCallback() {
									@Override
									public void doCallback(WebCrawlingTask task) throws Exception {
										Document projDetailDoc = Jsoup
												.parse((String) (task.getCrawling().getResponse()));
										//地址/座落
										String projLocation=projDetailDoc.selectFirst("#TxtProjectaddress").val();
										//地区
										String dq=projDetailDoc.selectFirst("#DDLareaname option[selected=selected]").text();
										//占地面积（㎡）
										String Txtproportion=projDetailDoc.selectFirst("#Txtproportion").val();
										//总建筑面积（㎡）
										String Txtproportion2=projDetailDoc.selectFirst("#Txtproportion2").val();
										//开盘时间
										String Txtsellstardate=projDetailDoc.selectFirst("#Txtsellstardate").val();
										//售楼电话
										String Txtsellphone=projDetailDoc.selectFirst("#Txtsellphone").val();
										//总套数
										String Txthosenumber=projDetailDoc.selectFirst("#Txthosenumber").val();
										//售楼均价
										String TxtsellPrice=projDetailDoc.selectFirst("#TxtsellPrice").val();
										//开户银行
										String TxtbankName=projDetailDoc.selectFirst("#TxtbankName").val();
										//监控账号
										String TxtbankAccount=projDetailDoc.selectFirst("#TxtbankAccount").val();
										//建设工程规划许可证
										String Txtmofno=projDetailDoc.selectFirst("#Txtmofno").val();
										//建设用地规划许可证
										String TxtmofSoilNO=projDetailDoc.selectFirst("#TxtmofSoilNO").val();
										//施工许可证
										String Txtcofno=projDetailDoc.selectFirst("#Txtcofno").val();
										Elements gtListE = projDetailDoc.select("#GridView2>tbody>tr:nth-child(n+2)");
										Iterator<Element> gtListIt = gtListE.iterator();
										while(gtListIt.hasNext()){
											Element gtE=gtListIt.next();
											Map<String, String> projDetailMap = new HashMap<String, String>();
											extractor.extractDataByCssSelectors(HZHYConfig.gtDetailSelectorMap,
													gtE, projDetailMap);
											projDetailMap.put("projName", projPreMap.get("projName"));
											projDetailMap.put("licenseNo", projPreMap.get("licenseNo"));
											projDetailMap.put("developer", projPreMap.get("developer"));
											projDetailMap.put("projState", projPreMap.get("projState"));
											projDetailMap.put("pzDate", projPreMap.get("pzDate"));
											
											projDetailMap.put("projLocation",projLocation );
											projDetailMap.put("dq",dq );
											projDetailMap.put("Txtproportion",Txtproportion );
											projDetailMap.put("Txtproportion2",Txtproportion2 );
											projDetailMap.put("Txtsellstardate",Txtsellstardate );
											projDetailMap.put("Txtsellphone",Txtsellphone );
											projDetailMap.put("Txthosenumber",Txthosenumber );
											projDetailMap.put("TxtsellPrice",TxtsellPrice );
											projDetailMap.put("TxtbankName", TxtbankName);
											projDetailMap.put("TxtbankAccount",TxtbankAccount );
											projDetailMap.put("Txtmofno", Txtmofno);
											projDetailMap.put("TxtmofSoilNO", TxtmofSoilNO);
											projDetailMap.put("Txtcofno", Txtcofno);
											Object[] callbackArgs = { projCollectionName, crawlerId, currPageNo, countCurr,
													projDetailMap };
											mongoDBCallback.doCallback(callbackArgs);
										}
										
										Elements buildListE = projDetailDoc.select("#GridView1>tbody>tr:nth-child(n+2)");
										Iterator<Element> buildListIt = buildListE.iterator();
										while(buildListIt.hasNext()){
											Element buildE=buildListIt.next();
											Map<String, String> buildFirstMap = new HashMap<String, String>();
											extractor.extractDataByCssSelectors(HZHYConfig.nowBuildListSelectorMap,
													buildE, buildFirstMap);
											String roomListUrl=buildE.selectFirst("td:nth-child(5)>a").attr("href");
											buildFirstMap.put("roomListUrl", roomListUrl);
											buildList.add(buildFirstMap);
										}
										
									}
									@Override
									public void init(Object... args) throws Exception {
									}
									@Override
									public void clean(Object... args) throws Exception {
									}
								});
						this.executor.execute(projectDetailCrawlTask);
						
						for(int i=0;i<buildList.size();i++){
							String buildingURL=HZHYConfig.siteDomain + buildList.get(i).get("roomListUrl");
							WebCrawling buildingsCrawling = new WebCrawling(HttpMethod.GET, null,
									buildingURL, 600000, 600000, 600000);
							WebCrawlingTask buildingsCrawlTask = this.deriveNewTask("", true, 
									buildingsCrawling, buildingListExtractCallback);
							buildingsCrawlTask.getPathContext().setAttr("building", buildList.get(i));
							buildingsCrawlTask.getPathContext().setAttr("project", projPreMap);
							buildingsCrawlTask.getPathContext().setAttr("currCount", currCount);
							buildingsCrawlTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, 
									currPageNo);
							
							this.executor.execute(buildingsCrawlTask);
						}		
						currCount++;
					} catch (Exception _1) {
						_1.printStackTrace();
					}
				}
			}
		} catch (Exception _2) {
			_2.printStackTrace();
		}

	}

}
