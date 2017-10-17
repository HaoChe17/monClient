package analyzeData;

/**
 * 解析JTL文件数据
 * @author chehao
 * @version 2017年10月15日 上午11:42:29
 */
public class AnalyzeJtl implements Runnable{
	String jtlFile;
	public AnalyzeJtl(String jtlFile){
		this.jtlFile=jtlFile;
	}
	//解析JTL文件数据
	public void run(){
		System.out.println(Thread.currentThread().getName()+"解析JTL");
	}
}
