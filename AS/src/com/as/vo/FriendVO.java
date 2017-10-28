package com.as.vo;

public class FriendVO {
	private int friend_id;

	private int f_class_id;

	private String friend_name;

	private int friend_sex;

	private String friend_phone;

	private String friend_addr;

	private String friend_birth;

	private String friend_mail;

	private String friend_intro;

	
	public FriendVO(){
		friend_id = 0;
		f_class_id = 0;
		friend_name = "";
		friend_sex = -1;
		friend_phone = "";
		friend_addr = "";
		friend_birth = "";
		friend_mail = "";
		friend_intro = "";
		
	}
	public int getF_class_id() {
		return f_class_id;
	}

	public void setF_class_id(int f_class_id) {
		this.f_class_id = f_class_id;
	}

	public String getFriend_addr() {
		return friend_addr;
	}

	public void setFriend_addr(String friend_addr) {
		if(friend_addr!=null) this.friend_addr = friend_addr;
	}

	public String getFriend_birth() {
		return friend_birth;
	}

	public void setFriend_birth(String friend_birth) {
		if(friend_birth!=null) this.friend_birth = friend_birth;
	}

	public int getFriend_id() {
		return friend_id;
	}

	public void setFriend_id(int friend_id) {
		this.friend_id = friend_id;
	}

	public String getFriend_intro() {
		return friend_intro;
	}

	public void setFriend_intro(String friend_intro) {
		if(friend_intro!=null) this.friend_intro = friend_intro;
	}

	public String getFriend_mail() {
		return friend_mail;
	}

	public void setFriend_mail(String friend_mail) {
		if(friend_mail!=null) this.friend_mail = friend_mail;
	}

	public String getFriend_name() {
		return friend_name;
	}

	public void setFriend_name(String friend_name) {
		if(friend_name!=null) this.friend_name = friend_name;
	}

	public String getFriend_phone() {
		return friend_phone;
	}

	public void setFriend_phone(String friend_phone) {
		if(friend_phone!=null) this.friend_phone = friend_phone;
	}

	public int getFriend_sex() {
		return friend_sex;
	}

	public void setFriend_sex(int friend_sex) {
		this.friend_sex = friend_sex;
	}

}
