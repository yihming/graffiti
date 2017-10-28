package com.as.vo;

public class AgencyVO {
    private int agency_id;
    private int user_id;
    private int doer_id;
    private String agency_intro;
    private String agency_begin;
    private String agency_end;
    private int agency_state;
    private String user_name;
    private String doer_name;
	public String getDoer_name() {
		return doer_name;
	}
	public void setDoer_name(String doer_name) {
		this.doer_name = doer_name;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getAgency_begin() {
		if(agency_begin==null){
			agency_begin = "";
		}
		return agency_begin;
	}
	public void setAgency_begin(String agency_begin) {
		this.agency_begin = agency_begin;
	}
	public String getAgency_end() {
		if(agency_end == null){
			agency_end = "";
		}
		return agency_end;
	}
	public void setAgency_end(String agency_end) {
		this.agency_end = agency_end;
	}
	public int getAgency_id() {
		return agency_id;
	}
	public void setAgency_id(int agency_id) {
		this.agency_id = agency_id;
	}
	public String getAgency_intro() {
		if(agency_intro == null){
			agency_intro = "";
		}
		return agency_intro;
	}
	public void setAgency_intro(String agency_intro) {
		this.agency_intro = agency_intro;
	}
	public int getAgency_state() {
		return agency_state;
	}
	public void setAgency_state(int agency_state) {
		this.agency_state = agency_state;
	}
	public int getDoer_id() {
		return doer_id;
	}
	public void setDoer_id(int doer_id) {
		this.doer_id = doer_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
}
