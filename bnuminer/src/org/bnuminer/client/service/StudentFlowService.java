package org.bnuminer.client.service;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("studentflow")
public interface StudentFlowService extends RemoteService {
	String doWekaFlow(int datasetFileId, String foldsNum, String seedNum,
			String displayModelInOldFormat, String maxIterations, String minStdDev, String numClusters,
			String seed);
	
	Boolean writeToXML(String taskId, String datasetFileName, String foldsNum, String seedNum,
			String displayModelInOldFormat, String maxIterations, String minStdDev, String numClusters,
			String seed);
}
