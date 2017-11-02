package analyzeData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import untils.ConfigData;
import untils.GetExceptionDetalInfo;

public class JtlToCsv implements Runnable{

//	public static void main(String[] args)   {
//		
////		Date start=new Date("1508638654826");
////		Date end=new Date("1508638654827");
//		try {
//			new JtlToCsv().jtlToCsv("C:\\Users\\Shinelon\\Desktop\\jtl\\order_dishList_20171022101731.jtl", "C:\\Users\\Shinelon\\Desktop\\jtl\\new.csv", 0);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	private Logger log=Logger.getRootLogger();
	private BufferedWriter csvWriter=null;
	String[] csvFirstLineArr=null;
	private Set<String> lableSet=new HashSet<String>();;
	private Map<String,Map<String,Long>> lineMap=new HashMap<String,Map<String,Long>>();
	private ArrayList<String> lableList=new ArrayList<String>();
	private int interval=ConfigData.intervalTime;
	private String lineSeparator=System.getProperty("line.separator");
	private String targetJtlFile;
	private String csvFile;
	private long startTime;
	
	public JtlToCsv(String targetJtlFile,String csvFile,long  startTime){
		this.csvFile=csvFile;
		this.targetJtlFile=targetJtlFile;
		this.startTime=startTime;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			jtlToCsv();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(new GetExceptionDetalInfo().getExceptionDetail(e));
		}
	}
	
	public void jtlToCsv() throws IOException{
		log.info("start to analysize "+targetJtlFile+" to csv file!");
		
		BufferedReader jtlReader=new BufferedReader(new FileReader(targetJtlFile));
		csvWriter=new BufferedWriter(new FileWriter(csvFile));
		
		//获取jtl文件中最小/最大的时间戳
		long lineNum=1;
		long minTime=9999638654827L;
		long maxTime=0;
		String jtlLine="";
		long time=0;
		long nexLimitTime;
		if(startTime>0){
			nexLimitTime=startTime+1;
			minTime=startTime-interval*1000;
			while((jtlLine=jtlReader.readLine())!=null){
				if(lineNum>1){
					String[] arr=jtlLine.split(ConfigData.jtlDelimiter);
					time=Long.parseLong(arr[0]);
					if(time>maxTime)maxTime=time;
					lableSet.add(arr[2]);
				}
				lineNum++;
			}
		}else{
			while((jtlLine=jtlReader.readLine())!=null){
				if(lineNum>1){
					String[] arr=jtlLine.split(ConfigData.jtlDelimiter);
					time=Long.parseLong(arr[0]);
					if(minTime>time)minTime=time;
					if(time>maxTime)maxTime=time;
					lableSet.add(arr[2]);
				}
				lineNum++;
			}
			nexLimitTime=minTime+interval*1000+1;
		}
		jtlReader.close();
		jtlReader=new BufferedReader(new FileReader(targetJtlFile));
		
		
		//写入首行csv数据
		StringBuffer csvFirstLine=new StringBuffer();
		csvFirstLine.append("timeStamp,All-Tps-success,All-RT-success,All-Count-fail,");
		for(String lable:lableSet){
			lableList.add(lable);
			csvFirstLine.append(lable).append("-Tps-success").append(",").
			append(lable).append("-RT-success").append(",").
			append(lable).append("--Count-fail").append(",");
		}
		csvWriter.write(csvFirstLine.append(lineSeparator).toString());
		
		//获取指定时间间隔内的所有jtl数据行，并解析为csv数据
		
		long minTimePer=minTime;
		long maxTimePer=0;
		boolean continueWhile=true;
		time=minTime;
		ArrayList<String> lineList;
		while(continueWhile){
			if(nexLimitTime>maxTime)continueWhile=false;
			lineNum=1;
			lineList=new ArrayList<String>();
			jtlLine=null;
			while((jtlLine = jtlReader.readLine())!=null){
				if(lineNum>1){
					String[] lineArr=jtlLine.split(ConfigData.jtlDelimiter);
					time=Long.parseLong(lineArr[0]);
					if(nexLimitTime>time && time>=minTimePer){
						lineList.add(jtlLine);
						if(maxTimePer<time)maxTimePer=time;
						log.debug("time from "+minTimePer+" to "+nexLimitTime+" :"+jtlLine);
//						System.out.println("time from "+minTimePer+" to "+nexLimitTime+" :"+jtlLine);
						log.debug(new StringBuffer().append("time from ").append(minTimePer).append(" to ").append(nexLimitTime).append(" :").append(jtlLine).toString());
					}else if(nexLimitTime+1000*ConfigData.limitInterval<=time)break;
				}
				lineNum++;
			}
			
			//处理数据
			if(lineList.size()!=0)writeToCsv(lineList,maxTimePer);
			jtlReader.close();
			jtlReader=new BufferedReader(new FileReader(targetJtlFile));
			minTimePer=nexLimitTime;
			nexLimitTime=minTimePer+interval*1000+1;
		}
		
		jtlReader.close();
		csvWriter.close();
		log.info("analysize "+targetJtlFile+" finish!,the csv file is:"+csvFile);
	}
	
	/**
	 * 把时间段内传入的jtl数据行，解析为csv文件
	 * @param list
	 * @param unxiTime
	 * @throws IOException
	 */
	private void writeToCsv(ArrayList<String> list,long unxiTime) throws IOException{
		String timeStamp=new SimpleDateFormat(ConfigData.dateFormat).format(new Date(unxiTime));
		HashMap<String,Long> valueModel=new HashMap<String,Long>();
		valueModel.put("Count-success", new Long(0));
		valueModel.put("Count-fail", new Long(0));
		valueModel.put("RT-success", new Long(0));
		for(String lable:lableSet){
			lineMap.put(lable, valueModel);
		}
		
		//把jtl数据中，不同的lable，分类整理
		String lable;
		String[] lineArr;
		Map<String,Long> valueMap;
		String isSuccess;
		int allSuccessCount=0;
		int allSuccessRT=0;
		int allFailCount=0;
		for(String line:list){
			lineArr=line.split(",");
			lable=lineArr[2];
			for(String oldLable:lableSet){
				if(oldLable.equals(lable)){
					valueMap=lineMap.get(lable);
					isSuccess=lineArr[7];
					if(isSuccess.equalsIgnoreCase("true")){
						long RT=Long.parseLong(lineArr[1]);
						valueMap.put("Count-success", valueMap.get("Count-success")+1);
						valueMap.put("RT-success", valueMap.get("RT-success")+RT);
						allSuccessCount++;
						allSuccessRT+=RT;
					}else if(isSuccess.equalsIgnoreCase("false")){
						valueMap.put("Count-fail", valueMap.get("Count-fail")+1);
						allFailCount++;
					}
				}
			}
		}
		
		//解析所有lable的汇总数据
		StringBuffer csvLine=new StringBuffer();
		if(allSuccessCount==0){
			csvLine.append(timeStamp).append(",").
			append(allSuccessCount/(double)interval).append(",").
			append(-1).append(",").
			append(allFailCount).append(",");
		}else{
			csvLine.append(timeStamp).append(",").
			append(new BigDecimal(allSuccessCount/(double)interval).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()).append(",").
			append(new BigDecimal(allSuccessRT/(double)allSuccessCount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()).append(",").
			append(allFailCount).append(",");
		}
		
		//分开解析不同lable的所有数据，并写入csv文件中
		long countSuccess;
		for(String str:lableList){
			Map<String, Long> map=lineMap.get(str);
			countSuccess=map.get("Count-success");
			if(countSuccess==0){
				csvLine.
				append(new BigDecimal(countSuccess/(double)interval).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()).append(",").
				append(-1).append(",").
				append(map.get("Count-fail")).append(",");
			}else{
				csvLine.
				append(new BigDecimal(countSuccess/(double)interval).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()).append(",").
				append(new BigDecimal(map.get("RT-success")/(double)countSuccess).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()).append(",").
				append(map.get("Count-fail")).append(",");
			}
		}
		csvWriter.write(csvLine.append(lineSeparator).toString());
	}

	
}
