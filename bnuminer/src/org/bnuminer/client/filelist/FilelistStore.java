package org.bnuminer.client.filelist;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;

public class FilelistStore extends ListStore<BaseModel> {
	public FilelistStore() {
		super(new FilelistLoader());
	}
}
