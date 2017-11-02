package untils;

import java.util.HashMap;
import java.util.Map;

public class Cache {
	//解析JTL文件：
	public static Map<String,Map<String,String>> jtlMap=new HashMap<String,Map<String,String>>();//存放解析后的jtl数据的队列：
	public static long[] pointer=new long[ConfigData.jtlFileNum];
	
	//解析监控数据：
	public static Map<String,Map<String,String>> monitorMaps=new HashMap<String,Map<String,String>>();//存放解析后的监控数据的缓存
	public static String[] monitorMapKeyArray=new String[ConfigData.appNum];//monitorMaps的Key数组
	public static int monitorMapKeyArrayIndex=0;
//	public static String[] monitorItemMapKeyArray;//解析过后的监控项的Key数组
	
	/**
	 * 初始化
	 */
	public static void init(){
//		//初始化monitorMapKeyArray数组
//		for(int i=0;i<ConfigData.appNum;i++){
//			String[] array=ConfigData.monHostMap.get(""+i);
//			String ip=array[0].split(":")[1].replace("//", "");
//			String appKeyWords=array[2];
//			monitorMapKeyArray[i]=ip+"_"+appKeyWords;
//		}
		
		//初始化pointer数组
		for(int i=0;i<ConfigData.jtlFileNum;i++)pointer[i]=0;
	}
	
	public static synchronized void addMonitorMapsEle(String key,Map<String,String> map){
		monitorMaps.put(key, map);
	}
}