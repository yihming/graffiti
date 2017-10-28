package com.as.vo;
/**
 * 站内消息类
 * @author chaohua
 * 
 */
import java.sql.Clob;
//import java.util.Date;

import com.as.function.AS;

public class msgVO {
	private int msg_id;			//auto increment key
	private int user_id;		//
	private String msg_sender;	//发件人姓名
	private String msg_title;	//标题
	private String msg_time;		//时间
	private String msg_intro;	//内容
	private int msg_count;	//收件人数
	private int msg_sender_del;//发件人delete？
	private int msg_reply_to_id;	//消息所回复的消息直接id
	private int msg_first_id;	//回复针对的初始邮件
	
	public int getMsg_count() {
		return msg_count;
	}
	public void setMsg_count(int msg_count) {
		this.msg_count = msg_count;
	}
	public int getMsg_first_id() {
		return msg_first_id;
	}
	public void setMsg_first_id(int msg_first_id) {
		this.msg_first_id = msg_first_id;
	}
	public int getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(int msg_id) {
		this.msg_id = msg_id;
	}
	public String getMsg_intro() {
		return msg_intro;
	}
	public void setMsg_intro(String msg_intro) {
		this.msg_intro = msg_intro;
	}
	public int getMsg_reply_to_id() {
		return msg_reply_to_id;
	}
	public void setMsg_reply_to_id(int msg_reply_to_id) {
		this.msg_reply_to_id = msg_reply_to_id;
	}
	public String getMsg_sender() {
		return msg_sender;
	}
	public void setMsg_sender(String msg_sender) {
		this.msg_sender = msg_sender;
	}
	public int getMsg_sender_del() {
		return msg_sender_del;
	}
	public void setMsg_sender_del(int msg_sender_del) {
		this.msg_sender_del = msg_sender_del;
	}
	public String getMsg_time() {
		return msg_time;
	}
	public void setMsg_time(String msg_time) {
		this.msg_time = msg_time;
	}
	public String getMsg_title() {
		return msg_title;
	}
	public void setMsg_title(String msg_title) {
		this.msg_title = msg_title;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	
}
