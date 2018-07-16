package yuzhou.gits.realEstateWebCrawler.app.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import  java.lang.annotation.RetentionPolicy;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CrawlerAnnotation {
	public String crawlerCityName();
	public String crawlerCfgClzName();
	
}