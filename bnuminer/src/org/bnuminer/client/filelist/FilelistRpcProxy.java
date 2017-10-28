package org.bnuminer.client.filelist;

import org.bnuminer.client.service.BnuMinerService;
import org.bnuminer.client.service.BnuMinerServiceAsync;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FilelistRpcProxy extends RpcProxy<PagingLoadResult<BaseModel>> {
	@Override
	protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<BaseModel>> callback) {
		BnuMinerServiceAsync service = GWT.create(BnuMinerService.class);
		service.getFilelist((PagingLoadConfig)loadConfig, callback);
	}
}
