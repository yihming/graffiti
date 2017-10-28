package com.as.vo;

import java.sql.Clob;
import java.sql.Date;

import com.as.function.AS;

public class TaskVO {
	private int task_id;
	private int user_id;
	private String task_title;
	private String task_intro;
	private Date task_start;
	private Date task_end;
	public Date getTask_end() {
		return task_end;
	}
	public void setTask_end(Date task_end) {
		this.task_end = task_end;
	}
	public int getTask_id() {
		return task_id;
	}
	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}
	public String getTask_intro() {
		return task_intro;
	}
	public void setTask_intro(String task_intro) {
		this.task_intro = task_intro;
	}
	public Date getTask_start() {
		return task_start;
	}
	public void setTask_start(Date task_start) {
		this.task_start = task_start;
	}
	public String getTask_title() {
		return task_title;
	}
	public void setTask_title(String task_title) {
		this.task_title = task_title;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
}
