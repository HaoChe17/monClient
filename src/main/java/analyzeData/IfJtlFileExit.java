package analyzeData;

import java.io.File;

import untils.ConfigData;

/**
 * 判断指定的jtl文件是否存在
 * @author chehao
 * @version 2017年10月17日 下午8:41:17
 */
public class IfJtlFileExit implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String filePath=ConfigData.jtlFilePath;
		File jtlFile=new File(filePath);
		int jtlExitNum=0;
		while(true){
			if(jtlFile.exists() && jtlFile.isDirectory()){
				String[] fileArray=jtlFile.list();
				for(String fileName:fileArray){
					for(String keyWords:ConfigData.jtlFileNameKeyWordsArray){
						if(fileName.contains(keyWords))jtlExitNum++;
					}
				}
			}
			if(jtlExitNum==ConfigData.jtlFileNum){
				ConfigData.exitJtlFlag=1;
				break;
			}
			jtlExitNum=0;
		}
		
	}

}
