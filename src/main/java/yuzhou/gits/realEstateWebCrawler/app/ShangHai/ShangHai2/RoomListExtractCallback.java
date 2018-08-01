package yuzhou.gits.realEstateWebCrawler.app.ShangHai.ShangHai2;

import java.io.InputStream;
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
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class RoomListExtractCallback extends DefaultRealEstateCrawlingCallback {
	private String roomCollectionName = "shanghai_room";

	public void init(Object... args) throws Exception {
		super.init(args);
		this.roomCollectionName += this.datasetSuffix;
		this.mongoDBCallback.addPropsCollection(this.roomCollectionName, 100);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.roomCollectionName);
	}

	@Override
	protected void extracting(String respStr) throws Exception {
		Document baseDoc = Jsoup.parse(respStr);
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;
		Map<String, String> project = (Map<String, String>) this.crawlTask.getPathContext().getAttr("project");
		String buildingName = (String) this.crawlTask.getPathContext().getAttr("buildingName");
		// String[] areas =
		// (String[])this.crawlTask.getPathContext().getAttr("areaVals");
		Map<String, String> presale = (Map<String, String>) this.crawlTask.getPathContext().getAttr("presale");
		String highLowRefPrice = (String) this.crawlTask.getPathContext().getAttr("highLowRefPrice");

		Elements floorsE = baseDoc.select(ShangHai2Config.floorsSelector);
		Iterator<Element> floorsEIt = floorsE.iterator();
		while (floorsEIt.hasNext()) {
			Element floorE = floorsEIt.next();
			Elements roomsE = floorE.select("td:nth-child(n+2):nth-last-child(n+1)");
			Iterator<Element> roomsEIt = roomsE.iterator();
			while (roomsEIt.hasNext()) {
				try {
					Element roomE = roomsEIt.next();
					String titleStr = roomE.attr("title");
					String[] areaVals = this.getAreaVals(new String(titleStr.getBytes("ISO-8859-1"),"GB2312"));
					Element roomDetailUrlE = roomE.selectFirst("a");
					String floorNo = new String(roomE.parent().child(0).text().getBytes("iso-8859-1"),"gb2312");
					if (roomDetailUrlE != null) {
						String roomDetailURL = ShangHai2Config.siteDomain + "/" + roomE.selectFirst("a").attr("href");
						// System.out.println(roomDetailURL);
						WebCrawlingTask roomDetailTask = this.deriveNewTask("", false, roomDetailURL,
								new DefaultRealEstateCrawlingCallback() {
									@Override
									protected void extracting(String respStr) throws Exception {
										Map<String, String> roomDetailPropsMap = new HashMap<String, String>();
										try{
											Document baseDoc = Jsoup.parse(respStr);
											extractor.extractDataByCssSelectors(ShangHai2Config.roomDetailSelectorMap,
													baseDoc, roomDetailPropsMap,"gb2312");
											roomDetailPropsMap.put("floorNo", floorNo);
										}catch(Exception e){
											extractor.fillPropsNullVal(ShangHai2Config.roomDetailSelectorMap, roomDetailPropsMap);
											String roomNo = roomE.text();
											roomDetailPropsMap.put("roomNo", roomNo);
											roomDetailPropsMap.put("floorNo", floorNo);
										}
										// todo:save
										Object[] callbackArgs = { RoomListExtractCallback.this.roomCollectionName, "",
												currPageNo, currCount, roomDetailPropsMap };
										roomDetailPropsMap.put("projName", project.get("projName"));
										roomDetailPropsMap.put("presaleLicenseNo", presale.get("preSaleLicense"));
										roomDetailPropsMap.put("highLowRefPrice", highLowRefPrice);
										roomDetailPropsMap.put("buildingName", buildingName);
										roomDetailPropsMap.put("predictArea", areaVals[0]);
										roomDetailPropsMap.put("realArea", areaVals[1]);
										roomDetailPropsMap.put("floorNo", floorNo);
										this.mongoDBCallback.doCallback(callbackArgs);
									}
								});
						this.executor.execute(roomDetailTask);
					} else {
						Map<String, String> roomDetailPropsMap = new HashMap<String, String>();
						extractor.fillPropsNullVal(ShangHai2Config.roomDetailSelectorMap, roomDetailPropsMap);
						String roomNo = new String(roomE.ownText().getBytes("ISO-8859-1"),"gb2312");
						roomDetailPropsMap.put("roomNo", roomNo);
						// todo:save
						Object[] callbackArgs = { RoomListExtractCallback.this.roomCollectionName, "", currPageNo,
								currCount, roomDetailPropsMap };
						roomDetailPropsMap.put("floorNo", floorNo);
						roomDetailPropsMap.put("projName", project.get("projName"));
						roomDetailPropsMap.put("presaleLicenseNo", presale.get("preSaleLicense"));
						roomDetailPropsMap.put("highLowRefPrice", highLowRefPrice);
						roomDetailPropsMap.put("buildingName", buildingName);
						roomDetailPropsMap.put("predictArea", areaVals[0]);
						roomDetailPropsMap.put("realArea", areaVals[1]);
						
						this.mongoDBCallback.doCallback(callbackArgs);
					}
				} catch (Exception _1) {
					_1.printStackTrace();
				}
			}
		}
	}
	final static Pattern p1 = Pattern.compile("预测面积：([\\d]{0,}[.]{0,1}[\\d]{0,})");
	final static Pattern p2 = Pattern.compile("实测面积:([\\d]{0,}[.]{0,1}[\\d]{0,})");
	private  String[] getAreaVals(String titleStr) {
		String[] vals = { "", "" };
		Matcher m1 = p1.matcher(titleStr);
		if (m1.find()) {
			vals[0] = m1.group(1);
		}
		Matcher m2 = p2.matcher(titleStr);
		if (m2.find()) {
			vals[1] = m2.group(1);
		}
		return vals;
	}
}
