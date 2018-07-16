package yuzhou.gits.realEstateWebCrawler.app.NB;

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
	protected RoomsExtractCallback roomsExtractCallback = new RoomsExtractCallback();

	public ProjectListExtractCallback() {
	}

	public static final Pattern p = Pattern.compile("qrykey=\\d+");

	@Override
	public void extracting(String respStr) throws Exception {
		Document baseDoc = Jsoup.parse((String) respStr);
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;
		Elements projListDataE = baseDoc.select(NBConfig.projectListSelector);
		Iterator<Element> projListDataEIt = projListDataE.iterator();
		while (projListDataEIt.hasNext()) {
			try {
				Element projDataE = projListDataEIt.next();
				Map<String, String> projPropsMap = new HashMap<String, String>();
				this.extractor.extractDataByCssSelectors(NBConfig.projDataSelectorMap, projDataE, projPropsMap);
				System.out.println(
						projPropsMap.get("projName") + "(" + crawlerId + "," + currPageNo + "," + (currCount) + ")");
				String projDetailURL = NBConfig.siteDomain + "/"
						+ projDataE.selectFirst("td:nth-child(2)>a").attr("href");
				WebCrawlingTask projDetailTask = this.deriveNewTask("", true, projDetailURL,
						new DefaultRealEstateCrawlingCallback() {
							@Override
							protected void extracting(String respStr) throws Exception {
								Document projDetailDoc = Jsoup.parse(respStr);
								Map<String, String> projDetailPropsMap = new HashMap<String, String>();
								this.extractor.extractDataByCssSelectors(NBConfig.projDetailSelectorMap, projDetailDoc,
										projDetailPropsMap);
								this.crawlTask.getPathContext().setAttr("projectDetail", projDetailPropsMap);
								Elements buildingListE = projDetailDoc.select("td:has(table.sp_sck)>table:nth-child(4)"
										+ ">tbody>tr:nth-child(n+2):nth-last-child(n+1)");
								Iterator<Element> buildingListEIt = buildingListE.iterator();
								while (buildingListEIt.hasNext()) {
									Element buildingE = buildingListEIt.next();
									String buildingName = buildingE.selectFirst("td>a>font").ownText();
									String roomsUrlStr = buildingE.selectFirst("td>a").attr("onclick");
									Matcher m = p.matcher(roomsUrlStr);
									if (m.find()) {
										String roomsURL = NBConfig.siteDomain + "/GetHouseTable.aspx?" + m.group(0);
										WebCrawlingTask roomsTask = this.deriveNewTask("", false, roomsURL,
												ProjectListExtractCallback.this.roomsExtractCallback);
										this.crawlTask.getPathContext().setAttr("buildingName", buildingName);
										this.executor.execute(roomsTask);
									}
								}
							}
						});
				projDetailTask.getPathContext().setAttr(Constants.PAGE_CURRENT_NO, this.currPageNo);
				projDetailTask.getPathContext().setAttr("project", projPropsMap);
				this.executor.execute(projDetailTask);
			} catch (Exception _1) {
				_1.printStackTrace();
			}

			currCount++;
		}
	}

	@Override
	public void init(Object... args) throws Exception {
		this.roomsExtractCallback.init(args);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.roomsExtractCallback.clean(args);
	}
}