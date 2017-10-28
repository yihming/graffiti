package org.bnuminer.client.filelist;

import java.util.ArrayList;
import java.util.List;

import org.bnuminer.client.AppEvents;
import org.bnuminer.client.BnuMiner;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;

public class FilelistGrid extends Grid<BaseModel> {
	private Listener<BaseEvent> refreshListListener = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			reload();
		}
	};
	
	
	public static FilelistGrid create() {
		
		List<ColumnConfig> columnList = new ArrayList<ColumnConfig>();
		columnList.add(new ColumnConfig("file_name", "名称", 100));
		columnList.add(new ColumnConfig("owner_id", "所有者ID", 100));
		columnList.add(new ColumnConfig("file_size", "大小", 100));
		columnList.add(new ColumnConfig("file_type", "类型", 100));
		columnList.add(new ColumnConfig("file_create_time", "创建时间", 200));
		columnList.add(new ColumnConfig("file_modify_time", "修改时间", 200));
		ColumnModel cm = new ColumnModel(columnList);
		return new FilelistGrid(new FilelistStore(), cm);
	}
	
	public FilelistGrid(FilelistStore store, ColumnModel cm) {
		super(store, cm);
		BnuMiner.getCurrent().addAppListener(AppEvents.LoginSuccess, refreshListListener);
		BnuMiner.getCurrent().addAppListener(AppEvents.UploadSuccess, refreshListListener);
		this.setAutoExpandColumn("file_name");
	}
	
	public void reload() {
		getStore().getLoader().load();
	}
}
