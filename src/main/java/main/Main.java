package main;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import analyzeData.AnalyzeJtl;
import analyzeData.AnalyzeMon;
import analyzeData.IfJtlFileExit;
import untils.Cache;
import untils.ConfigData;
import untils.GetExceptionDetalInfo;

public class Main {
	public static void main(String[] args) {
		
		//初始化
		PropertyConfigurator.configure("log4j.properties");
		ConfigData.init();
		Cache.init();
		Logger log=Logger.getRootLogger();
		
		//初步判断参数是否合法
		String[] para={null,null,null};
		if(args.length>4 || args.length<1){
			log.error("the parameter is wrong.the parameter's  number must less than 5 and more than 0. ");
			System.exit(0);
		}
		int paraIndex=0;
		for(String str:args){
			if(paraIndex>0)para[paraIndex-1]=str;
			paraIndex++;
		}
		
		//进行任务类型识别并执行
		Controller.startWork(args[0], para);
//		Controller.startWork("mon", para);
		
		//判断指定的jtl文件是否都已存在：
//		new Thread(new IfJtlFileExit()).start();
//		new IfJtlFileExit();
//		String resultPath="C:\\Users\\Shinelon\\Desktop\\jtl";
		//创建线程池池：
//		ThreadPoolExecutor pool=new ThreadPoolExecutor(ConfigData.coreThreadNum, ConfigData.maxThreadNum, ConfigData.idleTime, 
//				TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
		
	//	pool.execute(new AnalyzeMon(ConfigData.monHostMap.get(""+1),123254L,resultPath));
	//	new AnalyzeMon(ConfigData.monHostMap.get(""+1),123254L,resultPath).run();
		//获取并解析监控数据
//		for(int j=0;j<ConfigData.appNum;j++){
//			pool.execute(new AnalyzeMon(ConfigData.monHostMap.get(""+j),"123254",resultPath));
//		}
//		
//		long startTime = System.currentTimeMillis()/1000;
		
		//获取并解析监控数据、jtl数据
//		long taskNum=0;//任务数
//		while(true){
//			long timeStampMillis=System.currentTimeMillis();
//			System.out.println("time:"+timeStampMillis);
//			long startTimePer=timeStampMillis/1000;
//			//获取并解析jtl数据
//			if(ConfigData.exitJtlFlag==1){
//				for(int i=0;i<ConfigData.jtlFileNum;i++){
//					pool.execute(new AnalyzeJtl(ConfigData.jtlFileArray[i],i));
//					taskNum++;
//				}
//			}
//			
//			//获取并解析监控数据
//			for(int j=0;j<ConfigData.appNum;j++){
//				pool.execute(new AnalyzeMon(ConfigData.monHostMap.get(""+j),System.currentTimeMillis(),resultPath));
//				taskNum++;
//			}
//			
//			//等待所有线程执行完成
////			System.out.println("finish taskNum1:"+pool.getCompletedTaskCount());
////			System.out.println("1monitorMapsNum:"+Cache.monitorMaps.size()+",appNum:"+ConfigData.appNum);
//			while(taskNum!=pool.getCompletedTaskCount() && Cache.monitorMaps.size()!=ConfigData.appNum){
//				if(System.currentTimeMillis()/1000-startTimePer>=ConfigData.intervalTime)break;
//			}
////			System.out.println("finish taskNum2:"+pool.getCompletedTaskCount());
////			System.out.println("2monitorMapsNum:"+Cache.monitorMaps.size()+",appNum:"+ConfigData.appNum);
//
//			
//			//测试代码-start
//			
//			for(String key:Cache.monitorMapKeyArray){
//				System.out.println(key+":");
//				Map<String,String> map=Cache.monitorMaps.get(key);
//				for(String dataType:ConfigData.getDataTypeArray){
//					System.out.println(dataType+":"+map.get(dataType));
//				}
//			}
//			
//			//测试代码-end
//			
//			
//			//是否退出
//			if(System.currentTimeMillis()/1000-startTime>=ConfigData.durationTime)break;
//			else{
//				try {
//					long duration=System.currentTimeMillis()/1000-startTimePer;
////					System.out.println(startTimePer+","+System.currentTimeMillis()/1000+",3:"+duration);
//					if(duration<ConfigData.intervalTime)Thread.sleep((ConfigData.intervalTime-duration)*1000);
//					System.out.println("4");
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					log.error(new GetExceptionDetalInfo().getExceptionDetail(e));
//				}
//				//再次初始化
//				Cache.jtlMap=new HashMap<String,Map<String,String>>();
//				Cache.monitorMapKeyArrayIndex=0;
//				Cache.monitorMaps=new HashMap<String,Map<String,String>>();
//			}
//		}
		
//		System.exit(0);
		
		
	}

}