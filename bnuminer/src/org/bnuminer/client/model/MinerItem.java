package org.bnuminer.client.model;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class MinerItem extends BaseTreeModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MinerItem() {
		
	}
	
	public MinerItem(String name) {
		set("name", name);
	}
	
	public MinerItem(String name, String default_options) {
		set("name", name);
		set("default_options", default_options);
	}
	
	public String getName() {
		return (String) get("name");
	}
	
	public String getDefaultOptions() {
		return (String) get("default_options");
	}
	
	public String toString() {
		return getName();
	}
}
