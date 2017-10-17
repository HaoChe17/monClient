package untils;
/**
 * 获取异常信息
 * @author chehao
 * @version 2017年10月15日 下午4:26:07
 */
public class GetExceptionDetalInfo {
	public String getExceptionDetail(Exception e) {  
        StringBuffer stringBuffer = new StringBuffer(e.toString() + "\n");  
        StackTraceElement[] messages = e.getStackTrace();  
        int length = messages.length;  
        for (int i = 0; i < length; i++) {  
            stringBuffer.append("\t"+messages[i].toString()+"\n");  
        }  
        return stringBuffer.toString();  
    }
}
