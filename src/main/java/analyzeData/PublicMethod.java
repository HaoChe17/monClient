package analyzeData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import untils.ConfigData;

public class PublicMethod {
	
	private long differ=0;
	private long deviationTime=ConfigData.deviationTime;
	private boolean isFindLine=false;
	private int jtlLineNum=0;
	private int monLineNum=0;
	private long monTime=0;
	private Logger log=Logger.getRootLogger();
//	public static void main(String[] args) {
//		Map<String,String> map=null;
//		try {
//			map=new PublicMethod().getStartLineNum("C:\\Users\\Shinelon\\Desktop\\jtl\\1.jtl", "C:\\Users\\Shinelon\\Desktop\\jtl\\2.jtl");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(ConfigData.startLineNumMap_jtlKey+":"+map.get(ConfigData.startLineNumMap_jtlKey)+","+ConfigData.startLineNumMap_monKey+":"+map.get(ConfigData.startLineNumMap_monKey));
//	}
	/**
	 * 获取jtl与监控数据的时间戳相差在指定范围内的行号
	 * @param jtlFile
	 * @param monFile
	 * @return
	 * @throws IOException 
	 */
	public Map<String,String> getStartLineNum(String jtlFile,String monFile) throws IOException{
		BufferedReader jtlReader=new BufferedReader(new FileReader(jtlFile));
		BufferedReader monReader=new BufferedReader(new FileReader(monFile));
		Map<String,String> lineNumMap=new HashMap<String,String>();
		
		String jtlLine="";
		long jtlTime=0;
		boolean isReadMon=true;
		while((jtlLine=jtlReader.readLine()) != null){
			jtlTime=new Long(jtlLine.split(ConfigData.jtlDelimiter)[ConfigData.jtlTimeStampIndex]);
			while(true){
				if(isReadMon)getMonTime(monReader);
				isReadMon=true;
				differ=jtlTime-monTime;
				if(differ>deviationTime)continue;
				else if(Math.abs(differ)<=deviationTime){
					isFindLine=true;
					break;
				}else{
					isReadMon=false;
					break;
				}
			}
			
			jtlLineNum++;
			if(isFindLine)break;
		}
		
		jtlReader.close();
		monReader.close();
		log.debug(jtlFile+"'s initLineNum is:"+jtlLineNum);
		log.debug(monFile+"'s initLineNum is:"+monLineNum);
		lineNumMap.put(ConfigData.startLineNumMap_jtlKey, jtlLineNum+"");
		lineNumMap.put(ConfigData.startLineNumMap_monKey, monLineNum+"");
		return lineNumMap;
	}
	
	private void getMonTime(BufferedReader monReader) throws IOException{
		String monLine="";
		if((monLine=monReader.readLine()) != null){
			monTime=new Long(monLine.split(ConfigData.monDelimiter)[ConfigData.monTimeStampIndex]);
			monLineNum++;
		}
	}
}
