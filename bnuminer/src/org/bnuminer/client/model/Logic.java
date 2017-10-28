package org.bnuminer.client.model;

import com.extjs.gxt.ui.client.data.BaseModel;

public class Logic extends BaseModel {
	private static final long serialVersionUID = 1L;
	
	public Logic() {
		
	}
	
	public Logic(String name, int value) {
		setName(name);
		setValue(value);
	}
	
	public String getName() {
		return get("name");
	}
	
	public void setName(String name) {
		set("name", name);
	}
	
	public int getValue() {
		return get("value");
	}
	
	public void setValue(int value) {
		set("value", value);
	}
}
