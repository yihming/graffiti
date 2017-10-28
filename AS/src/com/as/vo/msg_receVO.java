package com.as.vo;

/**
 * 消息接收关系类
 * @author chaohua
 *
 */

public class msg_receVO {
	private int msg_rece_id;
	private int msg_id;		
	private int user_id;
	private int msg_rece_state;		//该收件人已读？
	
	public int getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(int msg_id) {
		this.msg_id = msg_id;
	}
	public int getMsg_rece_id() {
		return msg_rece_id;
	}
	public void setMsg_rece_id(int msg_rece_id) {
		this.msg_rece_id = msg_rece_id;
	}
	public int getMsg_rece_state() {
		return msg_rece_state;
	}
	public void setMsg_rece_state(int msg_rece_state) {
		this.msg_rece_state = msg_rece_state;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
}
