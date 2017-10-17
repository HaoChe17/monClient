package rmi;

import java.rmi.Remote;

public interface MonitorInterface extends Remote {
	/**
	 * 
	 * @param jdkPath
	 * @param appKeyWord
	 * @param infoType
	 * @return
	 * @throws java.rmi.RemoteException
	 */
	public String getMonitorInfo(String jdkPath,String appKeyWord,String infoType) throws java.rmi.RemoteException;
}
