package com.as.vo;

public class FriendClassVO {
	private int f_class_id;
	private int user_id;
	private String f_class_name;
	
	public FriendClassVO(){
		f_class_id = 0;
		user_id = 0;
		f_class_name = "";
	}
	
	public int getF_class_id() {
		return f_class_id;
	}
	public void setF_class_id(int f_class_id) {
		this.f_class_id = f_class_id;
	}
	public String getF_class_name() {
		return f_class_name;
	}
	public void setF_class_name(String f_class_name) {
		if(f_class_name!=null) this.f_class_name = f_class_name;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	
	

}
