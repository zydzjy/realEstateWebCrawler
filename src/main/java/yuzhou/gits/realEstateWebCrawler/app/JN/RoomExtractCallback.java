package yuzhou.gits.realEstateWebCrawler.app.JN;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import yuzhou.gits.crawler.crawl.WebCrawling;
import yuzhou.gits.crawler.crawl.WebCrawlingTask;
import yuzhou.gits.crawler.crawl.WebResourceCrawlingCallback;
import yuzhou.gits.crawler.crawl.WebCrawling.HttpMethod;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class RoomExtractCallback extends DefaultRealEstateCrawlingCallback {

	public RoomExtractCallback(String collectionName, DefaultRealEstateCrawlingCallback nextCallback) {
		super(collectionName, nextCallback);
	}

	@Override
	public void extracting(String respStr) throws Exception {
		int currCount = (Integer) this.crawlTask.getPathContext().getAttr("currCount");
		String bnoId = (String) this.crawlTask.getPathContext().getAttr("bnoId");
		String buildName = (String) this.crawlTask.getPathContext().getAttr("buildName");
		String projName = (String) this.crawlTask.getPathContext().getAttr("projName");
		Document roomDoc = Jsoup.parse(respStr);
		Element roomTrE = roomDoc.selectFirst(JNConfig.roomFirstSelectorMap);
		Map<String, String> roomFirstMap = new HashMap<String, String>();
		this.extractor.extractDataByCssSelectors(JNConfig.firstRoomSelectorMap, roomTrE, roomFirstMap);

		String url = JNConfig.roomListURL.replace("[bnoId]", bnoId);
		String roomListStr = _get(url);

		extractZzRoomList(bnoId, roomFirstMap, roomListStr, currCount, projName, buildName);
		extractNoZzRoomList(bnoId, roomFirstMap, roomListStr, currCount, projName, buildName);

	}

	private void extractZzRoomList(String bnoId, Map<String, String> roomFirstMap, String roomListStr, int currCount,
			String projName, String buildNo) {
		try {
			Document roomListDoc = Jsoup.parse(roomListStr);
			Elements dys = roomListDoc.select(JNConfig.dySelectorList);
			Iterator<Element> dyIt = dys.iterator();
			Elements xhs = roomListDoc.select(JNConfig.xhSelectorList);
			Iterator<Element> xhIt = xhs.iterator();
			List<Map<String, String>> dyList = new ArrayList<Map<String, String>>();
			List<Map<String, String>> xhList = new ArrayList<Map<String, String>>();
			while (dyIt.hasNext()) {
				Map<String, String> dyMap = new HashMap<String, String>();
				Element dyE = dyIt.next();
				dyMap.put("dyName", dyE.selectFirst("td").text());
				dyMap.put("colspan",
						dyE.selectFirst("td").attr("colspan").equals("") ? "1" : dyE.selectFirst("td").attr("colspan"));
				dyList.add(dyMap);
			}
			int xhLength = 1;
			int dyListIndex = 1;
			while (xhIt.hasNext() && dyList.size() >= dyListIndex) {
				int dyXhCount = Integer.parseInt(dyList.get(dyListIndex - 1).get("colspan"));
				Map<String, String> xhMap = new HashMap<String, String>();
				Element xhE = xhIt.next();
				xhMap.put("dyName", dyList.get(dyListIndex - 1).get("dyName"));
				xhMap.put("colspan", dyList.get(dyListIndex - 1).get("colspan"));
				xhMap.put("sxh", xhE.selectFirst("td").text());
				xhList.add(xhMap);
				if (xhLength == dyXhCount) {
					xhLength = 1;
					dyListIndex++;
				} else {
					xhLength++;
				}
			}

			Elements floorS = roomListDoc.select(JNConfig.floorTableSelectorList);
			Iterator<Element> floorIt = floorS.iterator();
			String floorNo="";
			while (floorIt.hasNext()) {
				int roomIndex = 1;
				Element floorE = floorIt.next();
				String floorNoTmp = floorE.selectFirst("td").text();
				if(!StringUtil.isBlank(floorNoTmp)){
					floorNo=floorNoTmp;
				}
				Elements roomS = floorE.select("td:nth-child(n+2)");
				Iterator<Element> roomIt = roomS.iterator();
				while (roomIt.hasNext() && xhList.size() >= roomIndex) {
					Map<String, String> preMap = xhList.get(roomIndex - 1);
					Element room = roomIt.next();
					String roomNo = room.selectFirst("td").text();
					String hid = room.selectFirst("td").attr("id");
					if (StringUtil.isBlank(hid)) {
						roomIndex++;
						continue;
					}
					String roomDetailUrl = JNConfig.roomDetailURL.replace("[hid]", hid) + new Date();
					String roomJsonStr = getDocumentStr(roomDetailUrl);
					if (StringUtil.isBlank(roomJsonStr)) {
						continue;
					}
					Map<String, String> roomMap = getRoomMap(roomJsonStr);
					getRoomState(roomMap.get("housestatus"), roomMap);
					Map<String, Object> docR = new HashMap<String, Object>();
					docR.put("projName", projName);
					docR.put("buildNo", buildNo);
					mapExtendMap(roomFirstMap, docR);
					mapExtendMap(preMap, docR);
					mapExtendMap(roomMap, docR);
					docR.put("floorNo", floorNo);
					docR.put("roomNo", roomNo);
					docR.put("city", "济南");
					Object[] callbackArgs = { collectionNames.get(0), crawlerId, currPageNo, currCount, docR };
					mongoDBCallback.doCallback(callbackArgs);
					roomIndex++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 非住宅没有单元也没有序号
	private void extractNoZzRoomList(String bnoId, Map<String, String> roomFirstMap, String roomListStr, int currCount,
			String projName, String buildNo) {
		try {
			Document roomListDoc = Jsoup.parse(roomListStr);
			Elements floorS = roomListDoc.select(JNConfig.bussTableSelectorList);
			Iterator<Element> floorIt = floorS.iterator();
			String floorNo="";
			while (floorIt.hasNext()) {
				Element floorE = floorIt.next();
				String floorNoTmp = floorE.selectFirst("td").text();
				if(StringUtil.isBlank(floorNoTmp)){
					floorNo=floorNoTmp;
				}
				Elements roomS = floorE.select("td:nth-child(n+2)");
				Iterator<Element> roomIt = roomS.iterator();
				while (roomIt.hasNext()) {
					Element room = roomIt.next();
					String roomNo = room.selectFirst("td").text();
					String hid = room.selectFirst("td").attr("id");
					if (StringUtil.isBlank(hid)) {
						continue;
					}
					String roomDetailUrl = JNConfig.roomDetailURL.replace("[hid]", hid) + new Date();
					String roomJsonStr = getDocumentStr(roomDetailUrl);
					if (StringUtil.isBlank(roomJsonStr)) {
						continue;
					}
					Map<String, String> roomMap = getRoomMap(roomJsonStr);
					getRoomState(roomMap.get("housestatus"), roomMap);
					org.bson.Document docR = new org.bson.Document();
					docR.put("projName", projName);
					docR.put("buildNo", buildNo);
					mapExtendMap(roomFirstMap, docR);
					mapExtendMap(roomMap, docR);
					docR.put("floorNo", floorNo);
					docR.put("roomNo", roomNo);
					docR.put("city", "济南");
					Object[] callbackArgs = { collectionNames.get(0), crawlerId, currPageNo, currCount, docR };
					mongoDBCallback.doCallback(callbackArgs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected String _get(String url) throws Exception {
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		// 屏蔽日志信息
		/*LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);*/
		// 支持JavaScript
		webClient.getOptions().setJavaScriptEnabled(true);// 启用JS解释器，默认为true
		webClient.getOptions().setCssEnabled(false);// 禁用css支持
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);// js运行错误时，是否抛出异常
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setTimeout(10000);// 设置连接超时时间 ，这里是10S。如果为0，则无限期等待
		HtmlPage rootPage = webClient.getPage(url);
		// 设置一个运行JavaScript的时间
		webClient.waitForBackgroundJavaScript(10000);
		String html = rootPage.asXml();
		return html;
	}

	public String getDocumentStr(String url) throws Exception {
		final Map<String, String> map = new HashMap<String, String>();
		WebCrawling urlCrawling = new WebCrawling(HttpMethod.GET, null, url, 600000, 600000, 600000);
		WebCrawlingTask projectDetailCrawlTask = this.deriveNewTask("", true, urlCrawling,
				new WebResourceCrawlingCallback() {
					@Override
					public void doCallback(WebCrawlingTask task) throws Exception {
						String docStr = (String) (task.getCrawling().getResponse());
						map.put("docStr", docStr);
					}

					@Override
					public void init(Object... args) throws Exception {
					}

					@Override
					public void clean(Object... args) throws Exception {
					}
				});
		this.executor.execute(projectDetailCrawlTask);
		return map.get("docStr");
	}

	private Map<String, String> getRoomMap(String roomJsonStr) {
		Map<String, String> roomMap = new HashMap<String, String>();
		JsonElement jelement = new JsonParser().parse(roomJsonStr.trim());
		String housestatus = jelement.getAsJsonObject().get("housestatus").getAsString();// 房屋状态
		String unitarea = jelement.getAsJsonObject().get("unitarea").getAsString();// 套内面积
		String apportioarea = jelement.getAsJsonObject().get("apportioarea").getAsString();// 公摊面积
		String usedtypeno = jelement.getAsJsonObject().get("usedtypeno").getAsString();// 房屋用途
		String housearea = jelement.getAsJsonObject().get("housearea").getAsString();// 住房面积
		roomMap.put("housestatus", housestatus);
		roomMap.put("unitarea", unitarea);
		roomMap.put("apportioarea", apportioarea);
		roomMap.put("usedtypeno", usedtypeno);
		roomMap.put("housearea", housearea);
		return roomMap;
	}

	private Map<String, String> getRoomState(String state, Map<String, String> map) {
		String color = "";
		String roomState = "";
		switch (state) {
		case "15701":
			color = "绿色";
			roomState = "可售";
			break;
		case "15702":
			color = "粉红色";
			roomState = "已预订";
			break;
		case "15703":
			color = "淡黄色";
			roomState = "已备案";
			break;
		case "15704":
			color = "淡蓝色";
			roomState = "已签约";
			break;
		case "15705":
			color = "紫色";
			roomState = "可租赁";
			break;
		case "15707":
			color = "红色";
			roomState = "不可租售";
			break;
		case "15709":
			color = "红褐色";
			roomState = "查封";
			break;
		case "15710":
			color = "灰色";
			roomState = "冻结";
			break;
		default:
			color = "未知颜色";
			roomState = "未知状态";
			break;
		}
		map.put("color", color);
		map.put("roomState", roomState);
		return map;
	}

	public void mapExtendMap(Map<String, String> fatherMap, Map<String, Object> sonMap) {
		Iterator<Map.Entry<String, String>> it = fatherMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			String propName = entry.getKey();
			String propVal = entry.getValue();
			sonMap.put(propName, propVal);
		}
	}
}