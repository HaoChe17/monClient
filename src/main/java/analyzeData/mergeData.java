package analyzeData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import untils.ConfigData;
import untils.GetExceptionDetalInfo;

/**
 * 数据合并，把两个文件（csv、jtl）进行合并，方便excel绘图
 * 合并方式：两个文件A、B（文件类型只能是jtl或csv），把A的前n列与B的前m列合并在一起，放在前面。A/B剩余的其他列合并在一起，放在其后，然后输出一个csv文件。
 * 注意：目前仅支持一个csv和一个jtl文件合并，时间戳以csv为准。
 * @author chehao
 * @version 2017年10月31日 下午8:12:51
 */
public class mergeData implements Runnable{
	



	
	private Logger log=Logger.getRootLogger();
	private String oldFile1;
	private String oldFile2;
	private String newFile;
	private int merge_firstColumnNum;
	private int merge_secondColumnNum;
	private BufferedWriter newFileWriter=null;
	private BufferedReader oldFile1Reader=null;
	private BufferedReader oldFile2Reader=null;
	private GetExceptionDetalInfo gd=new GetExceptionDetalInfo();
	private String lineSeparator=System.getProperty("line.separator");
	
	public mergeData(String oldFile1,String oldFile2,int merge_firstColumnNum,int merge_secondColumnNum,String newFile){
		this.oldFile1=oldFile1;
		this.oldFile2=oldFile2;
		this.merge_firstColumnNum=merge_firstColumnNum;
		this.merge_secondColumnNum=merge_secondColumnNum;
		this.newFile=newFile;
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		merge();
	}
	
	private void merge(){
		
		try {
			newFileWriter=new BufferedWriter(new FileWriter(newFile));
			oldFile1Reader=new BufferedReader(new FileReader(oldFile1));
			oldFile2Reader=new BufferedReader(new FileReader(oldFile2));
		} catch (IOException e) {
			log.error(gd.getExceptionDetail(e));
		}
		
		if(oldFile1.endsWith(".csv")&&oldFile2.endsWith(".csv")){
			mergeCsvAndCsv();
		}else if(oldFile1.endsWith(".jtl")&&oldFile2.endsWith(".jtl")){
			mergeJtlAndJtl();
		}else if((oldFile1.endsWith(".jtl")&&oldFile2.endsWith(".csv")) || (oldFile1.endsWith(".csv")&&oldFile2.endsWith(".jtl"))){
			mergeCsvAndJtl();
		}else log.error(new StringBuffer("the file type is wrong:").append(oldFile1).append(",").append(oldFile2));
	}
	
	/**
	 * 合并csv和jtl文件
	 */
	private void mergeCsvAndJtl(){
		BufferedReader csvReader;
		BufferedReader jtlReader;
		int csvMergeColumnNum;
		int jtlMergeColumnNum;
		String tmpCsvFile;
		String oldJtlFile;
		String oldCsvFile;
		if(oldFile1.endsWith(".jtl")){
			jtlReader=oldFile1Reader;
			csvReader=oldFile2Reader;
			oldJtlFile=oldFile1;
			oldCsvFile=oldFile2;
			csvMergeColumnNum=merge_secondColumnNum;
			jtlMergeColumnNum=merge_firstColumnNum;
			tmpCsvFile=oldFile1.replace(".jtl", "_tmp.csv");
		}else{
			jtlReader=oldFile2Reader;
			csvReader=oldFile1Reader;
			oldJtlFile=oldFile2;
			oldCsvFile=oldFile1;
			jtlMergeColumnNum=merge_secondColumnNum;
			csvMergeColumnNum=merge_firstColumnNum;
			tmpCsvFile=oldFile2.replace(".jtl", "_tmp.csv");
		}
		
		//1.根据csv的第二行数据的时间戳，先把jtl解析为新csv文件；
		long csvStartTime=0;
		int readLineNum=0;
		String line;
		String[] csvFirstLineArr=null;
		try {
			while((line=csvReader.readLine())!=null){
				if(++readLineNum==2){
					//时间格式处理
					SimpleDateFormat sdf = new SimpleDateFormat(ConfigData.dateFormat);
					System.out.println(line.split(",")[0]);
					csvStartTime=sdf.parse(line.split(",")[0]).getTime();
					break;
				}
				csvReader.mark(8*1024);
				csvFirstLineArr=line.split(",");
			}
			csvReader.reset();
			if(csvStartTime<=0)log.error(new StringBuffer("get ").append(oldCsvFile).append(" 's first timeStamp failed.the timeStamp is:").append(csvStartTime));
			//创建临时的csv文件
			new JtlToCsv(oldJtlFile, tmpCsvFile, csvStartTime).jtlToCsv();
			
		} catch (NumberFormatException e) {
			log.error(gd.getExceptionDetail(e));
		} catch (IOException e) {
			log.error(gd.getExceptionDetail(e));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//2.读取两个文件的首行，重新组装首行内容（使用List，确保顺序）
		BufferedReader tmpCsvReader=null;
		String[] tmpCsvFirstLineArr=null;
		try {
			tmpCsvReader=new BufferedReader(new FileReader(tmpCsvFile));
			if((tmpCsvFirstLineArr=tmpCsvReader.readLine().split(","))==null)log.error(new StringBuffer("the first line of ").append(tmpCsvFile).append(" is null!"));
		} catch (IOException e) {
			log.error(gd.getExceptionDetail(e));
		}
		//组装新csv文件的首行
		StringBuffer csvFirstLinePrefixBuf=new StringBuffer();
		StringBuffer csvFirstLinePostBuf=new StringBuffer();
		int i=0;
		for(String str:csvFirstLineArr){
			if(i++<=csvMergeColumnNum)csvFirstLinePrefixBuf.append(str).append(",");
			else csvFirstLinePostBuf.append(str).append(",");
		}
		
		StringBuffer tmpCsvFirstLinePrefixBuf=new StringBuffer();
		StringBuffer tmpCsvFirstLinePostBuf=new StringBuffer();
		i=1;
		for(String str:tmpCsvFirstLineArr){
			if(i>1){
				if(i<=jtlMergeColumnNum+1)tmpCsvFirstLinePrefixBuf.append(str).append(",");
				else tmpCsvFirstLinePostBuf.append(str).append(",");
			}
			i++;
		}
		String newCsvFirstLine=csvFirstLinePrefixBuf.append(tmpCsvFirstLinePrefixBuf).append(csvFirstLinePostBuf).append(tmpCsvFirstLinePostBuf).append(lineSeparator).toString();
		System.out.println("firstLine:"+newCsvFirstLine);
		try {
			newFileWriter.write(newCsvFirstLine);
		} catch (IOException e) {
			log.error(gd.getExceptionDetail(e));
		}
		
		//数据合并
		String[] csvLineArr=null;
		String[] tmpCsvLineArr=null;
		StringBuffer tmpCsvLinePrefixBuf=new StringBuffer();
		StringBuffer tmpCsvLinePostBuf=new StringBuffer();
		StringBuffer csvLinePrefixBuf=new StringBuffer();
		StringBuffer csvLinePostBuf=new StringBuffer();
		int csvLineArrLen=1;
		int tmpCsvLineArrLen=1;
		String newCsvLine=null;
		String tmpCsvLine=null;
		String csvLine=null;
		int count=1;
		while(tmpCsvLineArrLen!=0 || csvLineArrLen!=0 ){
			try {
				tmpCsvLine=tmpCsvReader.readLine();
				csvLine=csvReader.readLine();
			} catch (IOException e) {
				log.error(gd.getExceptionDetail(e));
			}
			
			if(tmpCsvLine==null){
				tmpCsvLineArr=new String[0];
			}else tmpCsvLineArr=tmpCsvLine.split(",");
			if(csvLine==null){
				csvLineArr=new String[0];
			}else csvLineArr=csvLine.split(",");
			
			for(String str:tmpCsvLineArr){
				if(count>1){
					if(count<=jtlMergeColumnNum+1)tmpCsvLinePrefixBuf.append(str).append(",");
					else tmpCsvLinePostBuf.append(str).append(",");
				}
				count++;
			}
			count=0;
			for(String str:csvLineArr){
				if(count++<=csvMergeColumnNum)csvLinePrefixBuf.append(str).append(",");
				else csvLinePostBuf.append(str).append(",");
			}
			newCsvLine=csvLinePrefixBuf.append(tmpCsvLinePrefixBuf).append(csvLinePostBuf).append(tmpCsvLinePostBuf).append(lineSeparator).toString();
			try {
				newFileWriter.write(newCsvLine);
			} catch (IOException e) {
				log.error(gd.getExceptionDetail(e));
			}
			
			tmpCsvLinePrefixBuf.delete(0, tmpCsvLinePrefixBuf.length());
			tmpCsvLinePostBuf.delete(0, tmpCsvLinePostBuf.length());
			csvLinePrefixBuf.delete(0, csvLinePrefixBuf.length());
			csvLinePostBuf.delete(0, csvLinePostBuf.length());
			tmpCsvLineArrLen=tmpCsvLineArr.length;
			csvLineArrLen=csvLineArr.length;
			count=1;
			
		}
	}
	
	private void mergeCsvAndCsv(){
		
	}
	
	private void mergeJtlAndJtl(){
		
	}


}
