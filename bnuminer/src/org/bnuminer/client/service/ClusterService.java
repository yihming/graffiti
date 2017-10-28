package org.bnuminer.client.service;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("cluster")
public interface ClusterService extends RemoteService {
	String doing_cluster(String clusterer_name, String clusterer_options, int explorer_open_mode, int file_id, String[] ignoreAttributes);
	
	String doing_cluster_with_testdataset(String clusterer_name, String clusterer_options, int explorer_open_mode,
											int trainingFileId, int testingFileId, String[] ignoreAttributes);
	
	/**
	 * 获取当前打开文件的属性列表
	 */
	List<BaseModel> getAttributes(int open_mode, int file_id);
}
