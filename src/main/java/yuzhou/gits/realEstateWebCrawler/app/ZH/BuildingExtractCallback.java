package yuzhou.gits.realEstateWebCrawler.app.ZH;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class BuildingExtractCallback extends DefaultRealEstateCrawlingCallback {
	@Override
	public void init(Object... args) throws Exception {

	}

	@Override
	public void clean(Object... args) throws Exception {

	}

	@Override
	protected void extracting(String respStr) throws Exception {
		String buildingListPageDocStr = respStr;
		Pattern p = Pattern
				.compile("Telerik.Web.UI.RadTreeView, ([\\w\\W\\s^]*),[\\w\\W\\s]*\\{\"contextMenuItemClicking\"");
		Matcher m = p.matcher(buildingListPageDocStr);
		if (m.find()) {
			String str = m.group(1);
			// extracting rooms
			JsonElement json = new JsonParser().parse(str);
			JsonArray items = json.getAsJsonObject().get("nodeData").getAsJsonArray().get(0).getAsJsonObject()
					.get("items").getAsJsonArray();
			Iterator<JsonElement> itemsIt = items.iterator();
			while (itemsIt.hasNext()) {
				JsonElement item = itemsIt.next();
				if (item == null)
					continue;
				JsonElement itemsJson = item.getAsJsonObject().get("items");
				if (itemsJson == null)
					continue;
				JsonArray subItems = itemsJson.getAsJsonArray();
				Iterator<JsonElement> subItemsIt = subItems.iterator();
				while (subItemsIt.hasNext()) {
					try {
						JsonElement subItem = subItemsIt.next();
						String OnClickFuncName = subItem.getAsJsonObject().get("attributes").getAsJsonObject()
								.get("OnClickFuncName").getAsString();
						if ("ShowBuildChart".equals(
								OnClickFuncName) /*
													 * || "M_ShowBD".equals(
													 * OnClickFuncName)
													 */) {
							String DatumElementKey = subItem.getAsJsonObject().get("attributes").getAsJsonObject()
									.get("DatumElementKey").getAsString();
							String BusinessInstanceId = subItem.getAsJsonObject().get("attributes").getAsJsonObject()
									.get("BusinessInstanceId").getAsString();
							// System.out.println("building
							// id:"+DatumElementKey);
							// TODO:
							String buildingDetailPageURL = ZHConfig.siteDomain
									+ "/LeadingCP/LCP_Floor/Execute/FloorCommon.ashx";
						}
					} catch (Exception e) {e.printStackTrace();}
				}
			}
		}
	}
}