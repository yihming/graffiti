package org.bnuminer.client.filelist;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class FilelistLoader extends BasePagingLoader<PagingLoadResult<BaseModel>> {
	public FilelistLoader() {
		super(new FilelistRpcProxy());
	}
}
