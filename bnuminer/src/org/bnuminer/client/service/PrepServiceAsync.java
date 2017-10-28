package org.bnuminer.client.service;

import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PrepServiceAsync {

	void db_init_configs(AsyncCallback<Map<String, String>> callback);

	void db_connect_test(String url, String user, String password,
			AsyncCallback<Boolean> callback);

	void get_History(List<String> history, String option,
			AsyncCallback<List<BaseModel>> callback);

	void db_query(String url, String user, String password, String sql,
			AsyncCallback<Map<String, Object>> callback);

	void prep_statistics(int open_mode, int file_id,
			AsyncCallback<Map<String, Object>> callback);

	void displaySelectedAttribute(int attribute_index, int open_mode,
			int file_id, AsyncCallback<Map<String, Object>> callback);

	void getAttributeList(int open_mode, int file_id,
			AsyncCallback<List<BaseModel>> callback);

	


}
