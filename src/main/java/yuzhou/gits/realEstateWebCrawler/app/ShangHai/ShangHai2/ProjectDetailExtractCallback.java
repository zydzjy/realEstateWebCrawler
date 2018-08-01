package yuzhou.gits.realEstateWebCrawler.app.ShangHai.ShangHai2;

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

public class ProjectDetailExtractCallback extends DefaultRealEstateCrawlingCallback {
	private RoomListExtractCallback roomListExtractCallback = new RoomListExtractCallback();
	private String preSaleCollectionName = "shanghai_presale";

	@Override
	public void init(Object... args) throws Exception {
		super.init(args);
		this.preSaleCollectionName += this.datasetSuffix;
		this.mongoDBCallback.addPropsCollection(this.preSaleCollectionName, 6);
		this.roomListExtractCallback.init(args);
	}

	@Override
	public void clean(Object... args) throws Exception {
		this.mongoDBCallback.flush(this.preSaleCollectionName);
		this.roomListExtractCallback.clean(args);
	}

	@Override
	protected void extracting(String respStr) throws Exception {
		Document baseDoc = Jsoup.parse(respStr);
		String crawlerId = "";// (String)_extractContext.get("crawlerId");
		int currCount = 1;
		String developerSelector = "table:nth-child(5) > tbody > tr > td > table:nth-child(2) > "
				+ "tbody > tr > td:nth-child(2) > table > tbody > tr:nth-child(2) > td > table > "
				+ "tbody > tr:nth-child(3) > td:nth-child(2) > a";
		String developer = new String(baseDoc.selectFirst(developerSelector).ownText().getBytes("ISO-8859-1"),"gb2312");;
		String projId = "";
		String thisURL = this.crawlTask.getCrawling().getUrl();
		int idParaStartIdx = thisURL.indexOf("=") + 1;
		projId = thisURL.substring(idParaStartIdx);
		String preSaleURL = ShangHai2Config.preSaleURL + "?projectID=" + projId;
		Map<String, String> project = (Map<String, String>) this.crawlTask.getPathContext().getAttr("project");
		this.executor.execute(this.deriveNewTask("", false, preSaleURL, new DefaultRealEstateCrawlingCallback() {
			@Override
			protected void extracting(String respStr) throws Exception {
				// System.out.println(respStr);
				Document baseDoc = Jsoup.parse(respStr);
				Elements preSaleList = baseDoc.select(ShangHai2Config.preSaleListSelector);
				Iterator<Element> preSaleListIt = preSaleList.iterator();
				while (preSaleListIt.hasNext()) {
					try {
						Element preSaleE = preSaleListIt.next();
						String saleInfo = preSaleE.attr("onclick");
						saleInfo = new String(saleInfo.getBytes("ISO-8859-1"),"gb2312");
						Map<String, String> preSalePropsMap = new HashMap<String, String>();
						String[] _vals = saleInfo.split(",");
						String salePhone = this.getVal(_vals[2]);
						String saleAddr = this.getVal(_vals[3]);
						this.extractor.extractDataByCssSelectors(ShangHai2Config.preSaleSelectorMap, preSaleE,
								preSalePropsMap,"gb2312");
						preSalePropsMap.put("salePhone", salePhone);
						preSalePropsMap.put("developer", developer);
						preSalePropsMap.put("adminArea", project.get("adminArea"));
						preSalePropsMap.put("projName", project.get("projName"));
						preSalePropsMap.put("saleAddr", saleAddr);
						Object[] callbackArgs = { ProjectDetailExtractCallback.this.preSaleCollectionName, "",
								currPageNo, currCount, preSalePropsMap };
						this.mongoDBCallback.doCallback(callbackArgs);
						this.crawlTask.getPathContext().setAttr("presale", preSalePropsMap);
						String buildingListURL = ShangHai2Config.siteDomain + "/"
								+ preSaleE.selectFirst("td:nth-child(2)>a").attr("href");
						System.out.println("buildingListURL:"+buildingListURL);
						WebCrawlingTask buildingListTask = this.deriveNewTask("", false, buildingListURL,
								new DefaultRealEstateCrawlingCallback() {
									@Override
									protected void extracting(String respStr) throws Exception {
										Document baseDoc = Jsoup.parse(respStr);
										/*FileOutputStream out2 = new FileOutputStream("e:\\testTTT.txt");
										out2.write(baseDoc.toString().getBytes());
										out2.close();*/
										Elements buildingListE = baseDoc.select(ShangHai2Config.buildingListSelector);
										Iterator<Element> buildingListEIt = buildingListE.iterator();
										while (buildingListEIt.hasNext()) {
											Element buildingE = buildingListEIt.next();
											String buildingName = "";
											String highLowRefPrice = "";
											Element buildingNameE = buildingE.selectFirst("td:nth-child(1)>a");
											Element highLowRefPriceE = buildingE.selectFirst("td:nth-child(2)");
											buildingName = buildingNameE.ownText();
											buildingName = buildingName != null ? 
													new String(buildingName.getBytes("ISO-8859-1"),"GB2312") : "";
											highLowRefPrice = highLowRefPriceE.ownText();
											highLowRefPrice = highLowRefPrice != null ? 
													new String(highLowRefPrice.getBytes("ISO-8859-1"),"GB2312") : "";
											String roomListURL = ShangHai2Config.siteDomain + "/"
													+ buildingE.selectFirst("td:nth-child(1)>a").attr("href");
											//System.out.println("roomListURL:"+roomListURL);
											WebCrawlingTask roomListTask = this.deriveNewTask("", false, roomListURL,
													ProjectDetailExtractCallback.this.roomListExtractCallback);
											roomListTask.getPathContext().setAttr("buildingName", buildingName);
											roomListTask.getPathContext().setAttr("highLowRefPrice", highLowRefPrice);
											this.executor.execute(roomListTask);
										}
									}
								});
						this.executor.execute(buildingListTask);
					} catch (Exception _1) {
						_1.printStackTrace();
					}
				}
			}

			private String getVal(String val) {
				String _val = "";
				Matcher m = p.matcher(val);
				if (m.find()) {
					_val = m.group(0);
				}
				return _val;
			}

			final Pattern p = Pattern.compile("([^'])+");
		}));
	}
}
