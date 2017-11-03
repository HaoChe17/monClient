package analyzeData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import rmi.MonitorInterface;
import untils.ConfigData;
import untils.GetExceptionDetalInfo;
import untils.Cache;

/**
 * 解析监控数据
 * @author chehao
 * @version 2017年10月14日 下午3:44:00
 */
public class AnalyzeMon implements Runnable{
	
	private String[] rmiClientPara;
	private String monData=null;
	private JSONObject jsObject;
	private Map<String,String> monMap=new HashMap<String,String>();
	private List<String> monMapKeyList=new ArrayList<String>();
	private String monitorMapsKey;
	private Logger log=Logger.getRootLogger();
	private long startTime;
	private String resultPath;
	private String csvFile;
	private BufferedWriter monCsvWriter=null;
	private String lineSeparator=System.getProperty("line.separator");
	private long timeStamp=0;
	private String fileNameKeyWords;
	private String ip;
	private String appKeyWords;
	private long count=0;
	
	private long lastFGC=0;
	private float lastFGCT=0;
	
	public AnalyzeMon(String[] rmiClientPara,String fileNameKeyWords,String resultPath){
		this.rmiClientPara=rmiClientPara;
		this.fileNameKeyWords=fileNameKeyWords;
		String fileSeparator=java.io.File.separator;
		if(!resultPath.endsWith(fileSeparator))this.resultPath=new StringBuffer(resultPath).append(fileSeparator).toString();
		else this.resultPath=resultPath;
	}
	
	/**
	 * 获取、并解析监控数据
	 */
	public void run(){
		if(rmiClientPara.length!=4){
			String errorStr="";
			for(int i=0;i<rmiClientPara.length;i++){
				errorStr+=rmiClientPara[i];
				if(i<rmiClientPara.length-1)errorStr+=",";
			}
			log.error("rmi远程调用参数配置有误:"+errorStr);
		}
		
		//获取一些设置数据
		String rmiUrl=rmiClientPara[0];
		String jdkPath=rmiClientPara[1];
		appKeyWords=rmiClientPara[2];
		String monDataType=rmiClientPara[3];
		ip=rmiUrl.split(":")[1].replace("//", "");
		csvFile=new StringBuffer(resultPath).append(ip).append("-").append(appKeyWords).append("-").append(fileNameKeyWords).append(".csv").toString();
		try {
			monCsvWriter=new BufferedWriter(new FileWriter(csvFile));
		} catch (IOException e) {
			log.error(new GetExceptionDetalInfo().getExceptionDetail(e));
		}
		
		//设置monMap集合的key
//		monMapKeyList.add(ConfigData.osCpuAvgUseRateFlag);
//		monMapKeyList.add(ConfigData.osMemUseRateFlag);
//		monMapKeyList.add(ConfigData.osDiskBusynessRateFlag);
//		monMapKeyList.add(ConfigData.osDiskSpaceUseRateFlag);
		
		//设置首行列名，并写入csv文件：
//		StringBuffer csvFirstLineBuf=new StringBuffer("timeStamp,");
//		for(String lable:monMapKeyList){
//			csvFirstLineBuf.append(lable).append(",");
//		}
//		try {
//			monCsvWriter.write(csvFirstLineBuf.append(lineSeparator).toString());
//		} catch (IOException e) {
//			log.error(new GetExceptionDetalInfo().getExceptionDetail(e));
//		}
		
		
		//开始rmi调用,并解析数据，写入csv文件
		timeStamp=System.currentTimeMillis();
		startTime=timeStamp;
		while(true){
			//rmi调用
//			monData="{\"time\":1508035108970,\"monitorInfo\":{\"jvmInfo\":{\"S0C\":\"512.0\",\"S1C\":\"512.0\",\"S0U\":\"448.0\",\"S1U\":\"0.0\",\"EC\":\"25600.0\",\"EU\":\"10425.8\",\"OC\":\"87552.0\",\"OU\":\"67673.2\",\"MC\":\"-\",\"MU\":\"-\",\"CCSC\":\"-\",\"CCSU\":\"-\",\"YGC\":\"19210\",\"YGCT\":\"55.367\",\"FGC\":\"18\",\"FGCT\":\"1.247\",\"GCT\":\"56.614\"},\"osInfo\":{\"disksInfo\":{\"diskLoadInfo\":{\"diskNum\":2,\"vda\":{\"rrqm/s\":\"0.01\",\"wrqm/s\":\"43.36\",\"r/s\":\"1.59\",\"w/s\":\"2.25\",\"rkB/s\":\"104.17\",\"wkB/s\":\"182.46\",\"avgrq-sz\":\"149.21\",\"avgqu-sz\":\"0.21\",\"await\":\"53.45\",\"svctm\":\"1.48\",\"%util\":\"0.57\"},\"vdb\":{\"rrqm/s\":\"0.00\",\"wrqm/s\":\"0.03\",\"r/s\":\"0.07\",\"w/s\":\"0.91\",\"rkB/s\":\"1.03\",\"wkB/s\":\"34.09\",\"avgrq-sz\":\"72.02\",\"avgqu-sz\":\"0.01\",\"await\":\"12.61\",\"svctm\":\"1.15\",\"%util\":\"0.11\"}},\"diskSpaceInfo\":{\"/dev/vda1\":{\"Filesystem\":\"/dev/vda1\",\"Size\":\"40G\",\"Used\":\"9.1G\",\"Avail\":\"29G\",\"Use%\":\"24%\",\"Mounted-on\":\"/\"},\"tmpfs\":{\"Filesystem\":\"tmpfs\",\"Size\":\"7.8G\",\"Used\":\"0\",\"Avail\":\"7.8G\",\"Use%\":\"0%\",\"Mounted-on\":\"/dev/shm\"},\"/dev/vdb1\":{\"Filesystem\":\"/dev/vdb1\",\"Size\":\"500G\",\"Used\":\"24G\",\"Avail\":\"477G\",\"Use%\":\"5%\",\"Mounted-on\":\"/alidata\"}}},\"memsInfo\":{\"mem-total\":\"16334340\",\"mem-used\":\"15575104\",\"mem-free\":\"759236\",\"mem-shared\":\"0\",\"mem-buffers\":\"289500\",\"mem-cached\":\"6396160\",\"-/+buf/cac-used\":\"8889444\",\"-/+buf/cac-free\":\"7444896\",\"Swap-total\":\"0\",\"Swap-used\":\"0\",\"Swap-free\":\"0\"},\"cpuInfo\":{\"cpuFrameWork\":\"_x86_64_\",\"cpuCount\":\"8\",\"cpuLoad\":{\"all\":{\"CPU\":\"all\",\"%usr\":\"0.37\",\"%nice\":\"0.00\",\"%sys\":\"0.12\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.12\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"99.38\"},\"0\":{\"CPU\":\"0\",\"%usr\":\"0.99\",\"%nice\":\"0.00\",\"%sys\":\"0.99\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"98.02\"},\"1\":{\"CPU\":\"1\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"2\":{\"CPU\":\"2\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"3\":{\"CPU\":\"3\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"4\":{\"CPU\":\"4\",\"%usr\":\"0.99\",\"%nice\":\"0.00\",\"%sys\":\"0.99\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"98.02\"},\"5\":{\"CPU\":\"5\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"6\":{\"CPU\":\"6\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"7\":{\"CPU\":\"7\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"}}}},\"appStatInfo\":{\"Time\":\"1508035110\",\"PID\":\"24429\",\"%usr\":\"0.16\",\"%system\":\"0.07\",\"%guest\":\"0.00\",\"%CPU\":\"0.24\",\"CPU\":\"3\",\"minflt/s\":\"0.21\",\"majflt/s\":\"0.00\",\"VSZ\":\"8916456\",\"RSS\":\"545360\",\"%MEM\":\"3.34\",\"kB_rd/s\":\"0.00\",\"kB_wr/s\":\"0.34\",\"kB_ccwr/s\":\"0.00\",\"Command\":\"java\"}}}";
//			if(count==1)monData="{\"time\":1508035108970,\"monitorInfo\":{\"jvmInfo\":{\"S0C\":\"512.0\",\"S1C\":\"512.0\",\"S0U\":\"448.0\",\"S1U\":\"0.0\",\"EC\":\"25600.0\",\"EU\":\"10425.8\",\"OC\":\"87552.0\",\"OU\":\"67673.2\",\"MC\":\"-\",\"MU\":\"-\",\"CCSC\":\"-\",\"CCSU\":\"-\",\"YGC\":\"19210\",\"YGCT\":\"55.367\",\"FGC\":\"22\",\"FGCT\":\"3.785\",\"GCT\":\"56.614\"},\"osInfo\":{\"disksInfo\":{\"diskLoadInfo\":{\"diskNum\":2,\"vda\":{\"rrqm/s\":\"0.01\",\"wrqm/s\":\"43.36\",\"r/s\":\"1.59\",\"w/s\":\"2.25\",\"rkB/s\":\"104.17\",\"wkB/s\":\"182.46\",\"avgrq-sz\":\"149.21\",\"avgqu-sz\":\"0.21\",\"await\":\"53.45\",\"svctm\":\"1.48\",\"%util\":\"0.57\"},\"vdb\":{\"rrqm/s\":\"0.00\",\"wrqm/s\":\"0.03\",\"r/s\":\"0.07\",\"w/s\":\"0.91\",\"rkB/s\":\"1.03\",\"wkB/s\":\"34.09\",\"avgrq-sz\":\"72.02\",\"avgqu-sz\":\"0.01\",\"await\":\"12.61\",\"svctm\":\"1.15\",\"%util\":\"0.11\"}},\"diskSpaceInfo\":{\"/dev/vda1\":{\"Filesystem\":\"/dev/vda1\",\"Size\":\"40G\",\"Used\":\"9.1G\",\"Avail\":\"29G\",\"Use%\":\"24%\",\"Mounted-on\":\"/\"},\"tmpfs\":{\"Filesystem\":\"tmpfs\",\"Size\":\"7.8G\",\"Used\":\"0\",\"Avail\":\"7.8G\",\"Use%\":\"0%\",\"Mounted-on\":\"/dev/shm\"},\"/dev/vdb1\":{\"Filesystem\":\"/dev/vdb1\",\"Size\":\"500G\",\"Used\":\"24G\",\"Avail\":\"477G\",\"Use%\":\"5%\",\"Mounted-on\":\"/alidata\"}}},\"memsInfo\":{\"mem-total\":\"16334340\",\"mem-used\":\"15575104\",\"mem-free\":\"759236\",\"mem-shared\":\"0\",\"mem-buffers\":\"289500\",\"mem-cached\":\"6396160\",\"-/+buf/cac-used\":\"8889444\",\"-/+buf/cac-free\":\"7444896\",\"Swap-total\":\"0\",\"Swap-used\":\"0\",\"Swap-free\":\"0\"},\"cpuInfo\":{\"cpuFrameWork\":\"_x86_64_\",\"cpuCount\":\"8\",\"cpuLoad\":{\"all\":{\"CPU\":\"all\",\"%usr\":\"0.37\",\"%nice\":\"0.00\",\"%sys\":\"0.12\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.12\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"99.38\"},\"0\":{\"CPU\":\"0\",\"%usr\":\"0.99\",\"%nice\":\"0.00\",\"%sys\":\"0.99\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"98.02\"},\"1\":{\"CPU\":\"1\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"2\":{\"CPU\":\"2\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"3\":{\"CPU\":\"3\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"4\":{\"CPU\":\"4\",\"%usr\":\"0.99\",\"%nice\":\"0.00\",\"%sys\":\"0.99\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"98.02\"},\"5\":{\"CPU\":\"5\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"6\":{\"CPU\":\"6\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"},\"7\":{\"CPU\":\"7\",\"%usr\":\"0.00\",\"%nice\":\"0.00\",\"%sys\":\"0.00\",\"%iowait\":\"0.00\",\"%irq\":\"0.00\",\"%soft\":\"0.00\",\"%steal\":\"0.00\",\"%guest\":\"0.00\",\"%idle\":\"100.00\"}}}},\"appStatInfo\":{\"Time\":\"1508035110\",\"PID\":\"24429\",\"%usr\":\"0.16\",\"%system\":\"0.07\",\"%guest\":\"0.00\",\"%CPU\":\"0.24\",\"CPU\":\"3\",\"minflt/s\":\"0.21\",\"majflt/s\":\"0.00\",\"VSZ\":\"8916456\",\"RSS\":\"545360\",\"%MEM\":\"3.34\",\"kB_rd/s\":\"0.00\",\"kB_wr/s\":\"0.34\",\"kB_ccwr/s\":\"0.00\",\"Command\":\"java\"}}}";

			log.info("rmiUrl:"+rmiUrl+",jdkPath:"+jdkPath+",appKeyWords:"+appKeyWords+",monDataType:"+monDataType+",ip:"+ip);
//			System.out.println("rmiUrl:"+rmiUrl+",jdkPath:"+jdkPath+",appKeyWords:"+appKeyWords+",monDataType:"+monDataType+",ip:"+ip);
			try {
				monData=((MonitorInterface)Naming.lookup(rmiUrl)).getMonitorInfo(jdkPath,appKeyWords,monDataType);
			} catch (Exception e) {
				log.error("rmi远程调用异常");
				new GetExceptionDetalInfo().getExceptionDetail(e);
			}
			
			//对rmi返回结果进行处理，把监控数据写入csv文件中
			if(monData==null){
				log.error("获取监控数据失败，monData is null!");
			}else{
				log.debug("获取监控数据成功，monData："+monData);
				//设置该应用监控数据的Map的Key值
				monitorMapsKey=ip+"-"+appKeyWords;
//				synchronized(this){
//					Cache.monitorMapKeyArray[Cache.monitorMapKeyArrayIndex++]=monitorMapsKey;
//				}
				//解析数据，并写入csv文件中
				writeMonToCsv();
			}

			//每次采取数据后，等待指定时间
			long getMonTime=System.currentTimeMillis();
			long waitTime=ConfigData.intervalTime*1000-(getMonTime-timeStamp);
			try {
				if(waitTime>0)Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				log.error(new GetExceptionDetalInfo().getExceptionDetail(e));
			}
			//判读是否已执行指定时间
			timeStamp=System.currentTimeMillis();
			if(timeStamp-startTime>=ConfigData.durationTime*1000)break;
//			System.out.println("timeStamp="+timeStamp+",startTime="+startTime+",duration="+ConfigData.durationTime*1000+",waitTime="+waitTime);
			count++;

		}
//		System.out.println("out");
		try {
			monCsvWriter.close();
		} catch (IOException e) {
			log.error(new GetExceptionDetalInfo().getExceptionDetail(e));
		}
	}

	
	
	/**
	 * 解析监控数据
	 */
	private void writeMonToCsv(){
		String[] dataType=ConfigData.getDataTypeArray;
		jsObject=new JSONObject(monData);
		for(String str:dataType){
			if(str.equals(ConfigData.osCpuAvgUseRateFlag)){
				analyzeOsCpu();
				continue;
			}else if(str.equals(ConfigData.osMemUseRateFlag)){
				analyzeOsMem();
				continue;
			}else if(str.equals(ConfigData.osDiskBusynessRateFlag)){
				analyzeOsDiskBusyness();
				continue;
			}else if(str.equals(ConfigData.osDiskSpaceUseRateFlag)){
				analyzeOsDiskSpace();
				continue;
			}else if(str.equals(ConfigData.appJvmInfoFlag)){
				analyzeJvm();
				continue;
			}else if(str.equals(ConfigData.appMemInfoFlag)){
				analyzeAppMem();
				continue;
			}else if(str.equals(ConfigData.appCpuInfoFlag)){
				analyzeAppCpu();
				continue;
			}
		}
		//存入缓存
//		Cache.addMonitorMapsEle(monitorMapsKey, monMap);
		
		//往csv文件中写入首行
		if(count==0){
			//设置首行列名，并写入csv文件：
			StringBuffer csvFirstLineBuf=new StringBuffer("timeStamp,");
			for(String lable:monMapKeyList){
				csvFirstLineBuf.append(lable).append(",");
			}
			try {
				monCsvWriter.write(csvFirstLineBuf.append(lineSeparator).toString());
			} catch (IOException e) {
				log.error(new GetExceptionDetalInfo().getExceptionDetail(e));
			}
		}
		
		//写入csv文件
		StringBuffer lineBuf=new StringBuffer(new SimpleDateFormat(ConfigData.dateFormat).format(new Date(timeStamp))).append(",");
		for(String lable:monMapKeyList){
			lineBuf.append(monMap.get(lable)).append(",");
		}
		try {
			monCsvWriter.write(lineBuf.append(lineSeparator).toString());
			monCsvWriter.flush();
			log.debug(new StringBuffer(ip).append("-").append(appKeyWords).append("'s monitorData:").append(lineBuf.toString()).toString());
		} catch (IOException e) {
			log.error(new GetExceptionDetalInfo().getExceptionDetail(e));
		}
	}
	
	/**
	 * 解析系统cpu监控数据
	 */
	private void analyzeOsCpu(){
		String cpuAllIdle=jsObject.getJSONObject(ConfigData.firstLevel_monInfoKey).getJSONObject(ConfigData.secondLevel_osInfoKey).getJSONObject(ConfigData.thirdLevel_cpuInfoKey).getJSONObject(ConfigData.fourthLevel_cpuLoadKey).getJSONObject(ConfigData.fifthLevel_allCpuKey).getString(ConfigData.sixthLevel_cpuIdleRateKey);
		String cpuAlluseRate=String.format("%.2f",100.00-Double.parseDouble(cpuAllIdle));
		log.debug(monitorMapsKey+"'s cpuAlluseRate:"+cpuAlluseRate);
		monMap.put(ConfigData.osCpuAvgUseRateFlag, cpuAlluseRate);
		if(count==0)monMapKeyList.add(ConfigData.osCpuAvgUseRateFlag);
	}
	
	/**
	 * 解析系统内存监控数据
	 */
	private void analyzeOsMem(){
		JSONObject memJson=jsObject.getJSONObject(ConfigData.firstLevel_monInfoKey).getJSONObject(ConfigData.secondLevel_osInfoKey).getJSONObject(ConfigData.thirdLevel_memsInfoKey);
		String osAllMem=memJson.getString(ConfigData.fourthLevel_memTotalKey);
		String osUsedMem=memJson.getString(ConfigData.fourthLevel_bufCahcheUsedKey);
		String osUsedMemRate=String.format("%.2f", Double.parseDouble(osUsedMem)/Double.parseDouble(osAllMem)*100);
		log.debug(monitorMapsKey+"'s osUsedMemRate:"+osUsedMemRate);
		monMap.put(ConfigData.osMemUseRateFlag, osUsedMemRate);
		if(count==0)monMapKeyList.add(ConfigData.osMemUseRateFlag);
	}
	
	/**
	 * 解析系统硬盘大小监控数据
	 */
	private void analyzeOsDiskSpace(){
		JSONObject diskJson=jsObject.getJSONObject(ConfigData.firstLevel_monInfoKey).getJSONObject(ConfigData.secondLevel_osInfoKey).getJSONObject(ConfigData.thirdLevel_disksInfoKey).getJSONObject(ConfigData.fourthLevel_diskSpaceInfoKey);
		String osDiskSpaceUsed="";
		Set<String> keySet=diskJson.keySet();
		int keySetSize=keySet.size();
		int times=1;
		for(String key:keySet){
			String diskMountedOn=diskJson.getJSONObject(key).getString(ConfigData.sixthLevel_diskMountedOnKey);
			String diskSpaceUsedRate=diskJson.getJSONObject(key).getString(ConfigData.sixthLevel_diskSpaceUsedKey).replace("%", "");
			String mapKey=new StringBuffer(ConfigData.osDiskSpaceUseRateFlag).append("-").append(diskMountedOn).toString();
			monMap.put(mapKey, diskSpaceUsedRate);
			if(count==0)monMapKeyList.add(mapKey);
			
			osDiskSpaceUsed+=diskMountedOn+":"+diskSpaceUsedRate;
			if(times<keySetSize)osDiskSpaceUsed+=",";
			times++;
		}
		log.debug(monitorMapsKey+"'s osDiskSpaceUsed:"+osDiskSpaceUsed);
//		monMap.put(ConfigData.osDiskSpaceUseRateFlag, osDiskSpaceUsed);
	}
	
	/**
	 * 解析系统硬盘繁忙度监控数据
	 */
	private void analyzeOsDiskBusyness(){
		JSONObject diskJson=jsObject.getJSONObject(ConfigData.firstLevel_monInfoKey).getJSONObject(ConfigData.secondLevel_osInfoKey).getJSONObject(ConfigData.thirdLevel_disksInfoKey).getJSONObject(ConfigData.fourthLevel_diskLoadInfoKey);
		String osDiskBusyness="";
		Set<String> keySet=diskJson.keySet();
		int keySetSize=keySet.size();
		int times=1;
		for(String key:keySet){
			if(!key.equals(ConfigData.fifthLevel_diskNumKey)){
				String utilValue=diskJson.getJSONObject(key).getString(ConfigData.sixthLevel_diskUtilRateKey);
				String mapKey=new StringBuffer(ConfigData.osDiskBusynessRateFlag).append("-").append(key).toString();
				monMap.put(mapKey, utilValue);
				if(count==0)monMapKeyList.add(mapKey);
				
				osDiskBusyness+=key+":"+utilValue;
				if(times<keySetSize-1)osDiskBusyness+=",";
				times++;
			}
		}
		log.debug(monitorMapsKey+"'s osDiskBusyness:"+osDiskBusyness);
//		monMap.put(ConfigData.osDiskBusynessRateFlag, osDiskBusyness);
	}
	
	
	/**
	 * 解析JVM信息
	 */
	private void analyzeJvm(){
		JSONObject jvmJson=jsObject.getJSONObject(ConfigData.firstLevel_monInfoKey).getJSONObject(ConfigData.secondLevel_jvmInfoKey);
		StringBuffer jvmInfoSb=new StringBuffer();
		
		long FGC=Long.parseLong(jvmJson.getString(ConfigData.thirdLevel_FGCKey));
		float FGCT=Float.parseFloat(jvmJson.getString(ConfigData.thirdLevel_FGCTKey));
		String currentFGCT="0.0";
		String currentFGC="0";
		if(lastFGCT>0)currentFGCT=String.format("%.3f", FGCT-lastFGCT);
		if(lastFGC>0)currentFGC=new Long((FGC-lastFGC)).toString();
		lastFGC=FGC;
		lastFGCT=FGCT;
		
		String FGCKey=new StringBuffer(ConfigData.appJvmInfoFlag).append("-").append(ConfigData.thirdLevel_FGCKey).toString();
		String FGCTKey=new StringBuffer(ConfigData.appJvmInfoFlag).append("-").append(ConfigData.thirdLevel_FGCTKey).toString();
		monMap.put(FGCKey, currentFGC);
		if(count==0)monMapKeyList.add(FGCKey);//获取列名
		monMap.put(FGCTKey, currentFGCT);
		if(count==0)monMapKeyList.add(FGCTKey);//获取列名
		
		jvmInfoSb.append("FGC:").append(currentFGC).append(",").append("FGCT:").append(currentFGCT).append(",");

		log.debug(monitorMapsKey+"'s JVMInfo:"+jvmInfoSb.toString());
	}
	
	/**
	 * 解析应用cpu监控数据
	 */
	private void analyzeAppCpu(){
		String appAvgCpu=jsObject.getJSONObject(ConfigData.firstLevel_monInfoKey).getJSONObject(ConfigData.secondLevel_appStatInfoKey).getString(ConfigData.thirdLevel_appCpuKey);
//		String cpuAlluseRate=String.format("%.2f",100.00-Double.parseDouble(cpuAllIdle));
		log.debug(monitorMapsKey+"'s appAvgCpuRate:"+appAvgCpu);
		monMap.put(ConfigData.appCpuInfoFlag, appAvgCpu);
		if(count==0)monMapKeyList.add(ConfigData.appCpuInfoFlag);
	}
	
	/**
	 * 解析应用mem监控数据
	 */
	private void analyzeAppMem(){
		String appAvgMem=jsObject.getJSONObject(ConfigData.firstLevel_monInfoKey).getJSONObject(ConfigData.secondLevel_appStatInfoKey).getString(ConfigData.thirdLevel_appMemKey);
//		String cpuAlluseRate=String.format("%.2f",100.00-Double.parseDouble(cpuAllIdle));
		log.debug(monitorMapsKey+"'s appMemRate:"+appAvgMem);
		monMap.put(ConfigData.appMemInfoFlag, appAvgMem);
		if(count==0)monMapKeyList.add(ConfigData.appMemInfoFlag);
	}
}
