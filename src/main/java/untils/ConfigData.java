package untils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
/**
 * 设置配置数据的类
 * @author chehao
 * @version 2017年10月15日 上午10:51:30
 */
public class ConfigData {
	//全局配置：
	public static int durationTime=10;//需要执行的时间，单位：秒
	public static int intervalTime=5;//解析数据的间隔时间，单位：秒
	
	//线程池配置：
	public static int maxThreadNum=200;
	public static int coreThreadNum=8;
	public static Long idleTime=60L;//单位：秒
	
	//解析monAgent端数据的相关配置
	public static Map<String,String[]> monHostMap=new HashMap<String,String[]>();//rmi调用monAgent时的参数配置
	public static int appNum=2;//被监控的应用的数量
	public static String appConfigDelitimer="\\},\\{";//解析远程调用monAgent方法参数的分隔符
	public static String[] getDataTypeArray=null;//绘制图表需要的数据类型
	public static String osCpuAvgUseRateFlag="os_cpuAvgUse";//系统CPU平均使用率的标签
	public static String osMemUseRateFlag="os_memUse";//系统内存使用率的标签
	public static String osDiskSpaceUseRateFlag="os_diskSpaceUse";//系统磁盘空间使用率的标签
	public static String osDiskBusynessRateFlag="os_diskBusyness";//系统繁忙度的使用率的标签
	
	//json格式监控数据的键
	public static final String firstLevel_monInfoKey="monitorInfo"; 
	
	public static final String secondLevel_osInfoKey="osInfo";
	public static final String secondLevel_jvmInfoKey="jvmInfo";
	public static final String secondLevel_appStatInfoKey="appStatInfo";
	
	public static final String thirdLevel_disksInfoKey="disksInfo";
	public static final String thirdLevel_memsInfoKey="memsInfo";
	public static final String thirdLevel_cpuInfoKey="cpuInfo";
	
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
	public static String jtlFilePath="C:\\Users\\Shinelon\\Desktop\\jtl";
	public static int jtlFileNum=jtlFileNameKeyWordsArray.length;//jtl文件的数量
	public static int exitJtlFlag=0;//指定文件是否存在的标识。1，代表所有指定文件已存在；其他，至少一个指定文件不存在
	
	
	
	private static Logger log=Logger.getRootLogger();
	
	/**
	 * 初始化配置
	 * @param configFile
	 */
	@SuppressWarnings("unused")
	public static void init(String configFile){
		
		String monAppConfig="{\"rmi://172.16.31.77:12121/mi\",\"/usr/java/jdk1.8.0_65\",\"tomcat_pinpoint_collector\",\"jvm-app-os\"},{\"rmi://172.16.31.75:12121/mi\",\"/usr/java/jdk1.8.0_65\",\"tomcat_pinpoint_collector\",\"jvm-app-os\"}";
		if(monAppConfig==null){
			log.error("monAppConfig is null! monClient has stopped!");
			System.exit(0);
		}
		log.debug("monAppConfig:"+monAppConfig);
		initMonAppConfig(monAppConfig);
		
		String getDataType="os_cpuAvgUse,os_memUse,os_diskSpaceUse,os_diskBusyness";
		getDataTypeArray=getDataType.split(",");
	}
	
	/**
	 * 解析rmi远程方法的参数
	 * @param monAppConfig
	 */
	private static void initMonAppConfig(String monAppConfig){
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
}