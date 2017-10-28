package org.bnuminer.client.service;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ClusterServiceAsync {

	void doing_cluster(String clusterer_name, String clusterer_options, int explorer_open_mode,
			int file_id, String[] ignoreAttributes, AsyncCallback<String> callback);

	void getAttributes(int open_mode, int file_id,
			AsyncCallback<List<BaseModel>> callback);

	void doing_cluster_with_testdataset(String clusterer_name,
			String clusterer_options, int explorer_open_mode,
			int trainingFileId, int testingFileId, String[] ignoreAttributes,
			AsyncCallback<String> callback);

}
