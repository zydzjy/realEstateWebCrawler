package yuzhou.gits.realEstateWebCrawler.app;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import yuzhou.gits.crawler.crawl.CrawlerStub;
import yuzhou.gits.realEstateWebCrawler.app.annotaion.CrawlerAnnotation;

public class Bootstrap {

	public static void main(String[] args) {
 
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.populateEvnProperties(args);
		bootstrap.startup();
	}
	public Map<String, Object> env;
	public static final String ENV_CITYNAME = "ENV_CITYNAME";
	public static final String ENV_TASK_NUMS = "ENV_TASK_NUMS";
	public static final String ENV_START_PAGE_NO = "ENV_START_PAGE_NO";
	public static final String ENV_END_PAGE_NO = "ENV_END_PAGE_NO";
	public static final String ENV_CONFIG_PATH = "ENV_CONFIG_PATH";
	public static final String ENV_SITE_CRAWLING_EXECUTOR = "ENV_SITE_CRAWLING_EXECUTOR";
	public static final String ENV_DATASET_SUFFIX = "ENV_DATASET_SUFFIX";
	
	public void printUsage(){
		System.out.println("使用方法:\r\njava -jar crawler.jar cityName configPath [taskNums(defaults:1)]");
	}
	
	public Map<String, Object> populateEvnProperties(String... args) {
		
		Map<String, Object> properties = new HashMap<String, Object>();
		if (args.length < 1) {
			System.out.println("错误:缺少要采集城市的编号");
			this.printUsage();
			System.exit(-1);
		}
		String cityName = args[0];
		properties.put(ENV_CITYNAME, cityName);
		if (args.length < 2) {
			System.out.println("错误:缺少配置文件路径");
			this.printUsage();
			System.exit(-1);
		}
		String cfgPath = args[1];
		properties.put(ENV_CONFIG_PATH, cfgPath);
		int taskNums = 1;
		if (args.length < 3) {
			taskNums = 1;
		}else{
			taskNums = Integer.parseInt(args[2]);
		}
		properties.put(ENV_TASK_NUMS, taskNums);
		int startPageNo = 1;
		if (args.length < 4) {
			startPageNo = 1;
		}else{
			startPageNo = Integer.parseInt(args[3]);
		}
		properties.put(ENV_START_PAGE_NO, startPageNo);
		int endPageNo = 1;
		if (args.length < 5) {
			endPageNo = 1;
		}else{
			endPageNo = Integer.parseInt(args[4]);
		}
		properties.put(ENV_END_PAGE_NO, endPageNo);
		
		String datasetSuffix = "";
		if (args.length < 6) {
			datasetSuffix = new SimpleDateFormat("yyyyMMdd").format(new Date());
		}else{
			datasetSuffix = args[5];
		}
		properties.put(ENV_DATASET_SUFFIX, datasetSuffix);
		
		env = properties;
		return properties;
	}

	public static Map<String, String> cityToClassMap = new HashMap<String, String>();
	 
	protected void startup() {
		String cityName = (String) env.get(ENV_CITYNAME);
		String cfgPath = (String) env.get(ENV_CONFIG_PATH);
		try {
			processCrawlerRegistersByAnnotation();
			Class<?> crawlerClz = Class.forName(Bootstrap.cityToClassMap.get(cityName));
			Constructor<?> crawlerConstructor = crawlerClz.getConstructor();
			CrawlerAnnotation annotation = (CrawlerAnnotation) crawlerClz.getAnnotation(CrawlerAnnotation.class);
			String cfgClzName = annotation.crawlerCfgClzName();
			CrawlerStub crawler = (CrawlerStub) crawlerConstructor.newInstance();
			crawler.init(cfgPath,cfgClzName,env);
			crawler.startup(env);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void processCrawlerRegistersByAnnotation() {
		Reflections reflections = new Reflections("yuzhou.gits.realEstateWebCrawler.app");
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(
				CrawlerAnnotation.class);
		if(annotated != null){
			Iterator<Class<?>> clzIt = annotated.iterator();
			while(clzIt.hasNext()){
				Class<?> clz = clzIt.next();
				if(clz.isAnnotationPresent(CrawlerAnnotation.class)){
					CrawlerAnnotation config
						= clz.getAnnotation(CrawlerAnnotation.class);
					String registerCityName = config.crawlerCityName();
					//System.out.println(registerCityName);
					cityToClassMap.put(registerCityName, clz.getName());
				}
			}
		}
	}
}
