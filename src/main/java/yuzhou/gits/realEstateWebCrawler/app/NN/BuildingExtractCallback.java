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

import yuzhou.gits.realEstateWebCrawler.configuration.NN.NNConfig;
import yuzhou.gits.realEstateWebCrawler.http.HttpStrategy;
import yuzhou.gits.realEstateWebCrawler.realEstateCrawl.DefaultRealEstateCrawlingCallback;

public class BuildingExtractCallback extends DefaultRealEstateCrawlingCallback {
	static final Pattern projDetailUrlP = Pattern.compile("\'(.*)\'");
	
	@Override
	protected void extracting(String respStr) throws Exception {
		Document projDetailPageDoc = Jsoup.parse(respStr);
		
		Elements buildingListE = projDetailPageDoc
				.select(NNConfig.buildingListSelector);
		Iterator<Element> buildingListEIt = 
				buildingListE.iterator();
		while(buildingListEIt.hasNext()){
			Element e = buildingListEIt.next();
			Map<String,String> buildPropValsMap =
					new HashMap<String,String>();
			this.extractor.extractDataByCssSelectors
				(NNConfig.buildingListDataSelectorMap, 
						e, buildPropValsMap);
			String roomsDetailPageUrlStr = e.attr("onclick");
			Matcher m = projDetailUrlP.matcher(roomsDetailPageUrlStr);
			m.find();
			String roomsDetailPageUrl = m.group(1).trim();
			/*Document roomsDetailPageDoc =
					Jsoup.parse((String)this.jsoupHttp
							.get(roomsDetailPageUrl, HttpStrategy.RespType.HTML
									,this.defaultHeaders));
			this.extractRooms(projPropValsMap,
					buildPropValsMap,roomsDetailPageDoc);*/
		}
	}

}
