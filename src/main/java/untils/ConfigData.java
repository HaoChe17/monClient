package untils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
/**
 * 设置配置数据的类
 * @author chehao
 * @version 2017年10月15日 上午10:51:30
 */
public class ConfigData {
	//全局配置：
	public static int durationTime=1800;//需要执行的时间，单位：秒
	public static int intervalTime=5;//解析数据的间隔时间，单位：秒
	public static String dateFormat="yyyy-MM-dd/HH:mm:ss.SSS";//时间戳格式
	private static String configFile="./config.properties";//配置文件
	
	
	//线程池配置：
	public static int maxThreadNum=200;
	public static int coreThreadNum=8;
	public static Long idleTime=60L;//单位：秒
	public static int deviationTime=5;//单位：毫秒
	
	//数据解析公共配置
	public static String startLineNumMap_jtlKey="initJtlLineNum";//jtl文件解析的起始行的map的key
	public static String startLineNumMap_monKey="initMonLineNum";//mon文件解析的起始行的map的key
	
	//jtl转换为csv配置
	public static String jtlToCsvFile="{\"jtl1\",\"csv1\",\"1508638654827\"},{\"jtl2\",\"csv2\",\"1508638654827\"}";//数据合并的文件配置
	public static Map<String,String[]> jtlToCsvFileMap=new HashMap<String,String[]>();//合并数据时的配置map
	public static int jtlToCsvCoupleNum=1;//需要合并的文件对数量
	
	//文件合并配置：
	public static int merge_firstColumnNum=3;//需要合并的第一个文件要合并的列数
	public static int merge_secondColumnNum=3;//需要合并的第二个文件要合并的列数
	public static String mergeFile="{\"file1\",\"file2\",\"2\",\"3\",\"newFile1\"},{\"file3\",\"file4\",\"3\",\"3\",\"newFile2\"}";//数据合并的文件配置
	public static Map<String,String[]> mergeFileMap=new HashMap<String,String[]>();//合并数据时的配置map
	public static int mergeCoupleNum=1;//需要合并的文件对数量
	
	
	//解析monAgent端数据的相关配置
	private static String monAppConfig="{\"rmi://172.16.31.77:12121/mi\",\"/usr/java/jdk1.8.0_65\",\"tomcat_pinpoint_collector\",\"jvm-app-os\"},{\"rmi://172.16.31.75:12121/mi\",\"/usr/java/jdk1.8.0_65\",\"tomcat_pinpoint_collector\",\"jvm-app-os\"}";
	public static String reaultFileKeyWords="mon";//监控文件命名的关键字
	public static String monResultPath="./";//监控结果存放的目录
	public static Map<String,String[]> monHostMap=new HashMap<String,String[]>();//rmi调用monAgent时的参数配置
	public static int appNum=2;//被监控的应用的数量
	public static String appConfigDelitimer="\\},\\{";//解析远程调用monAgent方法参数的分隔符
	public static int monTimeStampIndex=0;//存储监控数据的文件中时间戳所在的位置，0，表示第一列
	public static String monDelimiter=",";//监控数据文件中的分隔符
	private static String getDataType="%app_cpu,%app_mem,JVM,%os_cpuAvgUse,%os_memUse,%os_diskBusyness,%os_diskSpaceUse";//需要获取的数据类型
	public static String[] getDataTypeArray=getDataType.split(",");//绘制图表需要的数据类型
	//需要监控的数据的标签
	public static String osCpuAvgUseRateFlag="%os_cpuAvgUse";//系统CPU平均使用率的标签
	public static String osMemUseRateFlag="%os_memUse";//系统内存使用率的标签
	public static String osDiskSpaceUseRateFlag="%os_diskSpaceUse";//系统磁盘空间使用率的标签
	public static String osDiskBusynessRateFlag="%os_diskBusyness";//系统繁忙度的使用率的标签
	public static String appJvmInfoFlag="JVM";//系统繁忙度的使用率的标签
	public static String appMemInfoFlag="%app_mem";//系统繁忙度的使用率的标签
	public static String appCpuInfoFlag="%app_cpu";//系统繁忙度的使用率的标签
	
	//json格式监控数据的键
	public static final String firstLevel_monInfoKey="monitorInfo"; 
	
	public static final String secondLevel_osInfoKey="osInfo";
	public static final String secondLevel_jvmInfoKey="jvmInfo";
	public static final String secondLevel_appStatInfoKey="appStatInfo";
	
	public static final String thirdLevel_disksInfoKey="disksInfo";
	public static final String thirdLevel_memsInfoKey="memsInfo";
	public static final String thirdLevel_cpuInfoKey="cpuInfo";
	public static final String thirdLevel_FGCKey="FGC";
	public static final String thirdLevel_FGCTKey="FGCT";
	public static final String thirdLevel_appCpuKey="%CPU";
	public static final String thirdLevel_appMemKey="%MEM";
	
	public static final String fourthLevel_memTotalKey="mem-total";
	public static final String fourthLevel_bufCahcheUsedKey="-/+buf/cac-used";
	public static final String fourthLevel_diskLoadInfoKey="diskLoadInfo";
	public static final String fourthLevel_diskSpaceInfoKey="diskSpaceInfo";
	public static final String fourthLevel_cpuLoadKey="cpuLoad";
	
	public static final String fifthLevel_diskNumKey="diskNum";
	public static final String fifthLevel_allCpuKey="all";
	
	public static final String sixthLevel_diskUtilRateKey="%util";
	public static final String sixthLevel_diskMountedOnKey="Mounted-on";
	public static final String sixthLevel_diskSpaceUsedKey="Use%";
	public static final String sixthLevel_cpuIdleRateKey="%idle";
	
	//解析Jtl文件的相关配置
	public static String[] jtlFileNameKeyWordsArray={"1.jtl","2.jtl"};//存放jtl文件名称的数组
	public static int jtlFileNum=jtlFileNameKeyWordsArray.length;//jtl文件的数量
	public static Map<String,Set<String>> jtlFileMap=new HashMap<String,Set<String>>();//存放文件名含有jtlFileNameKeyWordsArray数组中指定关键字的jtl文件，键为关键字
	public static String jtlFilePath="C:\\Users\\Shinelon\\Desktop\\jtl";
	public static int exitJtlFlag=0;//指定文件是否存在的标识。1，代表所有指定文件已存在；其他，至少一个指定文件不存在
	public static int jtlTimeStampIndex=0;//jtl文件时间戳所在的位置，0，表示第一列
	public static String jtlDelimiter=",";//jtl文件的分隔符
	public static int unitRate=1;//单位比率，1，表示单位为tps；60，表示CPM
	public static int limitInterval=10;//解析jtl文件时，当读取到的时间戳超过本次最大时间戳10秒时，则本次读取完成，进行下一次解析。
	
	
	
	private static Logger log=Logger.getRootLogger();
	private static GetExceptionDetalInfo gd=new GetExceptionDetalInfo();
	
	/**
	 * 初始化配置
	 * @param configFile
	 */
	@SuppressWarnings("unused")
	public static void init(){
		//读取配置文件，初始化此类中的静态配置
		if(configFile!=null && configFile.length()>0 && new File(configFile).exists()){
			readConfig();
		}
		
		
//		String monAppConfig="{\"rmi://172.16.31.77:12121/mi\",\"/usr/java/jdk1.8.0_65\",\"tomcat_pinpoint_collector\",\"jvm-app-os\"},{\"rmi://172.16.31.75:12121/mi\",\"/usr/java/jdk1.8.0_65\",\"tomcat_pinpoint_collector\",\"jvm-app-os\"}";
		if(monAppConfig==null){
			log.error("monAppConfig is null! monClient has stopped!");
			System.exit(0);
		}
		log.debug("monAppConfig:"+monAppConfig);
		
		initMonAppConfig(monAppConfig);
//		initMergeFileConfig(mergeFile);
//		initJtlToCsvFileConfig(jtlToCsvFile);
//		
	}
	

	
	/**
	 * 解析rmi远程方法的参数,解析为map
	 * @param monAppConfig
	 */
	public static void initMonAppConfig(String monAppConfig){
		String[] appConfigArray=monAppConfig.split(appConfigDelitimer);
		int mapKey=0;
		for(String str:appConfigArray){
			String newStr=str.replace("{", "").replace("}", "").replace("\"", "");
			log.debug("monHostMap's key  "+mapKey+"'s value is:"+newStr);
			String[] strArray=newStr.split(",");
			monHostMap.put(mapKey+"", strArray);
			mapKey++;
		}
		appNum=mapKey;
	}
	
	/**
	 * 解析合并文件的配置数据，解析为map
	 * @param mergeFile
	 */
	public static void initMergeFileConfig(String mergeFile){
		String[] mergeFileArray=mergeFile.split(appConfigDelitimer);
		int mapKey=0;
		for(String str:mergeFileArray){
			String newStr=str.replace("{", "").replace("}", "").replace("\"", "");
			log.debug("mergeFileMap's key  "+mapKey+"'s value is:"+newStr);
			String[] strArray=newStr.split(",");
			mergeFileMap.put(mapKey+"", strArray);
			mapKey++;
		}
		mergeCoupleNum=mapKey;
	}
	
	/**
	 * 解析jtl转换为csv的配置数据，解析为map
	 * @param jtlToCsv
	 */
	public static void initJtlToCsvFileConfig(String jtlToCsvFile){
		String[] jtlToCsvFileArray=jtlToCsvFile.split(appConfigDelitimer);
		int mapKey=0;
		for(String str:jtlToCsvFileArray){
			String newStr=str.replace("{", "").replace("}", "").replace("\"", "");
			log.debug("jtlToCsvFileMap's key  "+mapKey+"'s value is:"+newStr);
			String[] strArray=newStr.split(",");
			jtlToCsvFileMap.put(mapKey+"", strArray);
			mapKey++;
		}
		jtlToCsvCoupleNum=mapKey;
	}
	
	/**
	 * 读取配置文件，初始化配置
	 * @param config
	 */
	private static void readConfig(){
		log.info(new StringBuilder("read configFile ").append(configFile).append("……"));
		//加载配置文件
		Properties prop=new Properties();
		try {
			prop.load(new BufferedInputStream(new FileInputStream(configFile)));
		} catch (IOException e) {
			log.error(gd.getExceptionDetail(e));
		}
		
		//全局变量：
		if(prop.containsKey("monAppConfig")){
			monAppConfig=prop.getProperty("monAppConfig");
			log.info(new StringBuilder("monAppConfig=").append(monAppConfig));
		}
		
		if(prop.containsKey("mergeFile")){
			mergeFile=prop.getProperty("mergeFile");
			log.info(new StringBuilder("mergeFile=").append(mergeFile));
		}
		
		if(prop.containsKey("jtlToCsvFile")){
			jtlToCsvFile=prop.getProperty("jtlToCsvFile");
			log.info(new StringBuilder("jtlToCsvFile=").append(jtlToCsvFile));
		}
		
		if(prop.containsKey("monResultPath")){
			monResultPath=prop.getProperty("monResultPath");
			log.info(new StringBuilder("monResultPath=").append(monResultPath));
		}
		
		if(prop.containsKey("reaultFileKeyWords")){
			reaultFileKeyWords=prop.getProperty("reaultFileKeyWords");
			log.info(new StringBuilder("reaultFileKeyWords=").append(reaultFileKeyWords));
			
		}
		
		if(prop.containsKey("durationTime")){
			durationTime=Integer.parseInt(prop.getProperty("durationTime"));
			log.info(new StringBuilder("durationTime=").append(durationTime));
			
		}
		
		if(prop.containsKey("dateFormat")){
			dateFormat=prop.getProperty("dateFormat");
			log.info(new StringBuilder("dateFormat=").append(dateFormat));
		}
		
		if(prop.containsKey("intervalTime")){
			intervalTime=Integer.parseInt(prop.getProperty("intervalTime"));
			log.info(new StringBuilder("intervalTime=").append(intervalTime));
		}
		
		if(prop.containsKey("getDataType")){
			getDataType=prop.getProperty("getDataType");
			log.info(new StringBuilder("getDataType=").append(getDataType));
		}
		
	}
}