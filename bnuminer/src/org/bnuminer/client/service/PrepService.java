package org.bnuminer.client.service;

import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("prep")
public interface PrepService extends RemoteService {
	Map<String, String> db_init_configs();
	
	Boolean db_connect_test(String url, String user, String password);
	
	List<BaseModel> get_History(List<String> history, String option);
	
	Map<String, Object> db_query(String url, String user, String password, String sql);
	
	Map<String, Object> prep_statistics(int open_mode, int file_id); 
	
	Map<String, Object> displaySelectedAttribute(int attribute_index, int open_mode, int file_id);
	
	List<BaseModel> getAttributeList(int open_mode, int file_id);
}
