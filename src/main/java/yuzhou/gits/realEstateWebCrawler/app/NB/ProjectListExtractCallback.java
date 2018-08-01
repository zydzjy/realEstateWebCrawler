package yuzhou.gits.realEstateWebCrawler.app.NB;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.crawler.dataExtractor.Constants;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class ProjectListExtractCallback extends DefaultRealEstateCrawlingCallback {
	protected RoomPageExtractCallback roomPageExtractCallback = new RoomPageExtractCallback();

	public ProjectListExtractCallback() {
	}

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
						+ projDataE.selectFirst("td:nth-child(1)>a").attr("href");
				WebCrawlingTask projDetailTask = this.deriveNewTask("", true, projDetailURL,
						new DefaultRealEstateCrawlingCallback() {
							@Override
							protected void extracting(String respStr) throws Exception {
								int currCount = (Integer) this.crawlTask.getPathContext()
										.getAttr(DefaultRealEstateCrawlingCallback.CURR_COUNT_ATTR);
								Document projDetailDoc = Jsoup.parse(respStr);
								String url = projDetailDoc
										.selectFirst("body > div.layout-center > div > main > nav > ol > li:nth-child(3) > a")
										.attr("href");
								Map<String, String> projDetailPropsMap = new HashMap<String, String>();
								this.extractor.extractDataByCssSelectors(NBConfig.projDetailSelectorMap, projDetailDoc,
										projDetailPropsMap);
								this.crawlTask.getPathContext().setAttr("projDetail", projDetailPropsMap);
								WebResourceCrawlingCallback[] callbacks = new WebResourceCrawlingCallback[ProjectListExtractCallback.this.roomPageExtractCallback
										.getCallbacksInPage().length + 1];
								System.arraycopy(
										ProjectListExtractCallback.this.roomPageExtractCallback.getCallbacksInPage(), 0,
										callbacks, 0, callbacks.length - 1);
								callbacks[callbacks.length
										- 1] = ProjectListExtractCallback.this.roomPageExtractCallback;
								Elements buildingListE = projDetailDoc.select(NBConfig.buildingListSelector);
								Iterator<Element> buildingListEIt = buildingListE.iterator();
								while (buildingListEIt.hasNext()) {
									Element buildingE = buildingListEIt.next();
									Map<String, String> buildingPropsMap = new HashMap<String, String>();
									this.extractor.extractDataByCssSelectors(NBConfig.buildingSelectorMap,
											buildingE, buildingPropsMap);
									this.crawlTask.getPathContext().setAttr("building", buildingPropsMap);
									String roomPageURL = buildingE.selectFirst("div.views-field.views-field-nid>span>a").attr("href");
									roomPageURL = NBConfig.siteDomain + roomPageURL;
									roomPageURL = roomPageURL.replace("diagram", "table");
									roomPageURL = roomPageURL.replace("buildingId", "unit_building");
									//System.out.println(roomPageURL);
									this.crawlTask.getPathContext().setAttr(Constants.PAGE_START_NO, 0);
									this.crawlTask.getPathContext().setAttr(Constants.PAGE_END_NO, -1);
									this.executor.execute(this.deriveNewTask("", false, roomPageURL, callbacks));
								}
							}
						});
				projDetailTask.getPathContext().setAttr(DefaultRealEstateCrawlingCallback.CURR_COUNT_ATTR, currCount);
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
		this.roomPageExtractCallback.init(args);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.roomPageExtractCallback.clean(args);
	}
}