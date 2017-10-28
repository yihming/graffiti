package com.as.vo;
/**
 * 这个vo只是存储代办种类的
 * 就是管理员来做的东西
 * @author kebebrar
 *
 */
public class Agency_listVO {
	private int agency_list_id;
	private String ag_list_name;
	private String ag_list_url;
	private int ag_list_add;
	private int ag_list_delete;
	private int ag_list_update;
	private int ag_list_select;
 
	public int getAg_list_add() {
		return ag_list_add;
	}
	public void setAg_list_add(int ag_list_add) {
		this.ag_list_add = ag_list_add;
	}
	public int getAg_list_delete() {
		return ag_list_delete;
	}
	public void setAg_list_delete(int ag_list_delete) {
		this.ag_list_delete = ag_list_delete;
	}
	
	public String getAg_list_name() {
		return ag_list_name;
	}
	public void setAg_list_name(String ag_list_name) {
		this.ag_list_name = ag_list_name;
	}
	public int getAg_list_select() {
		return ag_list_select;
	}
	public void setAg_list_select(int ag_list_select) {
		this.ag_list_select = ag_list_select;
	}
	public int getAg_list_update() {
		return ag_list_update;
	}
	public void setAg_list_update(int ag_list_update) {
		this.ag_list_update = ag_list_update;
	}
	public String getAg_list_url() {
		return ag_list_url;
	}
	public void setAg_list_url(String ag_list_url) {
		this.ag_list_url = ag_list_url;
	}
	public int getAgency_list_id() {
		return agency_list_id;
	}
	public void setAgency_list_id(int agency_list_id) {
		this.agency_list_id = agency_list_id;
	}

}
