package main;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import analyzeData.AnalyzeMon;
import analyzeData.JtlToCsv;
import analyzeData.mergeData;
import untils.ConfigData;

public class Controller {
	private static Logger log=Logger.getRootLogger();
	/**
	 * 根据传入的参数，开始执行不同的任务
	 * @param workType 工作类型：mon-表示监控服务，merge-表示合并文件
	 * @param para 各个工作类型需要的参数
	 */
	public static void startWork(String workType,String[] para){
		//创建线程池池：
		ThreadPoolExecutor pool=new ThreadPoolExecutor(ConfigData.coreThreadNum, ConfigData.maxThreadNum, ConfigData.idleTime, 
						TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
		
		//判断参数类型，执行相应的工作
		if(workType.equals("mon")){//执行监控
			
			//当传入的para不为空时，使用para作为rmi配置参数，否则使用配置文件中指定的参数
			if(para[0]!=null)ConfigData.monResultPath=para[0];//监控数据存放的目录
			if(para[1]!=null)ConfigData.reaultFileKeyWords=para[1];//监控数据文件命名的关键字
			if(para[2]!=null)ConfigData.initMonAppConfig(para[2]);//rmi远程调用配置参数
			
			//获取并解析监控数据
			for(int j=0;j<ConfigData.appNum;j++){
				pool.execute(new AnalyzeMon(ConfigData.monHostMap.get(""+j),ConfigData.reaultFileKeyWords,ConfigData.monResultPath));
			}
			
		}else if(workType.equals("merge")){//执行文件合并
			//当传入的para不为空时，使用para作为解析合并文件的配置参数，否则使用配置文件中指定的参数
			if(para[0]!=null)ConfigData.initMergeFileConfig(para[0]);
			
			//合并文件
			String[] mergeFileConfig;
			for(int i=0;i<ConfigData.mergeCoupleNum;i++){
				mergeFileConfig=ConfigData.mergeFileMap.get(""+i);
				pool.execute(new mergeData(mergeFileConfig[0],
						mergeFileConfig[1],
						Integer.parseInt(mergeFileConfig[2]),
						Integer.parseInt(mergeFileConfig[3]),
						mergeFileConfig[4]));
			}
		}else if(workType.equals("jtc")){//执行jtl转换为csv文件
			//当传入的para不为空时，使用para作为解析合并文件的配置参数，否则使用配置文件中指定的参数
			if(para[0]!=null)ConfigData.initJtlToCsvFileConfig(para[0]);
			
			//转换文件
			String[] jtlToCsvFileConfig;
			for(int i=0;i<ConfigData.jtlToCsvCoupleNum;i++){
				
				jtlToCsvFileConfig=ConfigData.jtlToCsvFileMap.get(""+i);
				long startTime=0;
				if(jtlToCsvFileConfig.length>2 && jtlToCsvFileConfig[2]!=null)startTime=Long.parseLong(jtlToCsvFileConfig[2]);
				pool.execute(new JtlToCsv(jtlToCsvFileConfig[0],
						jtlToCsvFileConfig[1],
						startTime
						));
			}
		}else{
			log.error(new StringBuffer("the workType is wrong！workType=").append(workType).append(".the value is 'mon' or 'merge'"));
		}
		
		//等待所有任务执行完成，关闭线程池
		pool.shutdown();
	}
	
	/**
	 * 返回监控服务时的rmi配置
	 * @return
	 */
	private static String readMonConfig(){
		return "rmiUrl";
	}
	
	 
}
