package com.as.vo;

public class Agency_priVO {
  private int agency_pri_id;
  private int agency_id;
  private String agency_url;
  private int agency_add;
  private int agency_delete;
  private int agency_update;
  private int agency_select;
public int getAgency_add() {
	return agency_add;
}
public void setAgency_add(int agency_add) {
	this.agency_add = agency_add;
}
public int getAgency_delete() {
	return agency_delete;
}
public void setAgency_delete(int agency_delete) {
	this.agency_delete = agency_delete;
}
public int getAgency_id() {
	return agency_id;
}
public void setAgency_id(int agency_id) {
	this.agency_id = agency_id;
}
public int getAgency_pri_id() {
	return agency_pri_id;
}
public void setAgency_pri_id(int agency_pri_id) {
	this.agency_pri_id = agency_pri_id;
}
public int getAgency_select() {
	return agency_select;
}
public void setAgency_select(int agency_select) {
	this.agency_select = agency_select;
}
public int getAgency_update() {
	return agency_update;
}
public void setAgency_update(int agency_update) {
	this.agency_update = agency_update;
}
public String getAgency_url() {
	if(this.agency_url == null){
		this.agency_url = "";
	}
	return agency_url;
}
public void setAgency_url(String agency_url) {
	this.agency_url = agency_url;
}
  
  
}
