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
		// TODO Auto-generated method stub
		//初始化
		PropertyConfigurator.configure("log4j.properties");
		ConfigData.init("config.properties");
		Logger log=Logger.getRootLogger();
		
		//判断指定的jtl文件是否都已存在：
		new Thread(new IfJtlFileExit()).start();
		//创建线程池池：
		ThreadPoolExecutor pool=new ThreadPoolExecutor(ConfigData.coreThreadNum, ConfigData.maxThreadNum, ConfigData.idleTime, 
				TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
		
		
		
		long startTime = System.currentTimeMillis()/1000;
		
		//获取并解析监控数据、jtl数据
		long taskNum=0;//任务数
		while(true){
			long startTimePer=System.currentTimeMillis()/1000;
			//获取并解析jtl数据
			if(ConfigData.exitJtlFlag==1){
				for(int i=0;i<ConfigData.jtlFileNum;i++){
					pool.execute(new AnalyzeJtl(ConfigData.jtlFileNameKeyWordsArray[i]));
					taskNum++;
				}
			}
			
			//获取并解析监控数据
			for(int j=0;j<ConfigData.appNum;j++){
				pool.execute(new AnalyzeMon(ConfigData.monHostMap.get(""+j)));
				taskNum++;
			}
			
			//等待所有线程执行完成
//			System.out.println("finish taskNum1:"+pool.getCompletedTaskCount());
//			System.out.println("1monitorMapsNum:"+Cache.monitorMaps.size()+",appNum:"+ConfigData.appNum);
			while(taskNum!=pool.getCompletedTaskCount() && Cache.monitorMaps.size()!=ConfigData.appNum){
				if(System.currentTimeMillis()/1000-startTimePer>=ConfigData.intervalTime)break;
			}
//			System.out.println("finish taskNum2:"+pool.getCompletedTaskCount());
//			System.out.println("2monitorMapsNum:"+Cache.monitorMaps.size()+",appNum:"+ConfigData.appNum);

			
			//测试代码-start
			
			for(String key:Cache.monitorMapKeyArray){
				System.out.println(key+":");
				Map<String,String> map=Cache.monitorMaps.get(key);
				for(String dataType:ConfigData.getDataTypeArray){
					System.out.println(dataType+":"+map.get(dataType));
				}
			}
			
			//测试代码-end
			
			
			//是否退出
			if(System.currentTimeMillis()/1000-startTime>=ConfigData.durationTime)break;
			else{
				try {
					long duration=System.currentTimeMillis()/1000-startTimePer;
//					System.out.println(startTimePer+","+System.currentTimeMillis()/1000+",3:"+duration);
					if(duration<ConfigData.intervalTime)Thread.sleep((ConfigData.intervalTime-duration)*1000);
					System.out.println("4");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					log.error(new GetExceptionDetalInfo().getExceptionDetail(e));
				}
				//再次初始化
				Cache.jtlMap=new HashMap<String,Map<String,String>>();
				Cache.monitorMapKeyArrayIndex=0;
				Cache.monitorMaps=new HashMap<String,Map<String,String>>();
			}
		}
		
		System.exit(0);
		
		
	}

}