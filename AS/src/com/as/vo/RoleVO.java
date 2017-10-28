package com.as.vo;

import java.util.Date;

public class RoleVO {
    private int role_id;
    private String role_name;
    private Date create_time;
    private String role_intro;
    private int meeting_agree;
    private int device_mag;
    private int assign_work;
    private int account_add_del;
    private int account_update;
    private int new_power1;
    private int new_power2;
    
	private String user_name;
	private String user_true_name;
	
	private int user_role_rs_id;
	private int user_id;
	

	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public int getUser_role_rs_id() {
		return user_role_rs_id;
	}
	public void setUser_role_rs_id(int user_role_rs_id) {
		this.user_role_rs_id = user_role_rs_id;
	}
	public String getUser_true_name() {
		return user_true_name;
	}
	public void setUser_true_name(String user_true_name) {
		this.user_true_name = user_true_name;
	}
	public int getAccount_add_del() {
		return account_add_del;
	}
	public void setAccount_add_del(int account_add_del) {
		this.account_add_del = account_add_del;
	}
	public int getAccount_update() {
		return account_update;
	}
	public void setAccount_update(int account_update) {
		this.account_update = account_update;
	}
	public int getAssign_work() {
		return assign_work;
	}
	public void setAssign_work(int assign_work) {
		this.assign_work = assign_work;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public int getDevice_mag() {
		return device_mag;
	}
	public void setDevice_mag(int device_mag) {
		this.device_mag = device_mag;
	}
	public int getMeeting_agree() {
		return meeting_agree;
	}
	public void setMeeting_agree(int meeting_agree) {
		this.meeting_agree = meeting_agree;
	}
	public int getNew_power1() {
		return new_power1;
	}
	public void setNew_power1(int new_power1) {
		this.new_power1 = new_power1;
	}
	public int getNew_power2() {
		return new_power2;
	}
	public void setNew_power2(int new_power2) {
		this.new_power2 = new_power2;
	}
	public int getRole_id() {
		return role_id;
	}
	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}
	public String getRole_intro() {
		return role_intro;
	}
	public void setRole_intro(String role_intro) {
		this.role_intro = role_intro;
	}
	public String getRole_name() {
		return role_name;
	}
	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}
	public static void main(String args[]){
		
	}
}
