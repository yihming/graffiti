package org.bnuminer.client.service;


import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StudentFlowServiceAsync {

	void doWekaFlow(int datasetFileId, String foldsNum, String seedNum,
			String displayModelInOldFormat, String maxIterations,
			String minStdDev, String numClusters, String seed,
			AsyncCallback<String> callback);

	void writeToXML(String taskId, String datasetFileName, String foldsNum,
			String seedNum, String displayModelInOldFormat, String maxIterations,
			String minStdDev, String numClusters, String seed,
			AsyncCallback<Boolean> callback);


}
