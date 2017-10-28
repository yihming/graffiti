package com.as.vo;

public class DeviceVO {
	private int device_id;			//设备编号
	private String device_name;		//设备名称
	private String device_intro;	//设备简介
	private int device_count;		//设备总数目
	private int device_damage;		//设备损坏数
	private int device_valid;       //设备可用数
	
	public int getDevice_id(){
		return device_id;
	}
	
	public String getDevice_name(){
		return device_name;
	}
	
	public String getDevice_intro(){
		return device_intro;
	}
	
	public int getDevice_count(){
		return device_count;
	}
	
	public int getDevice_damage(){
		return device_damage;
	}
	
	public void setDevice_id(int device_id){
		this.device_id=device_id;
	}
	
	public void setDevice_name(String device_name){
		this.device_name=device_name;		
	}
	
	public void setDevice_intro(String device_intro){
		this.device_intro=device_intro;
	}
	
	public void setDevice_count(int device_count){
		this.device_count=device_count;
	}
	
	public void setDevice_damage(int device_damage){
		this.device_damage=device_damage;
	}
	
	public int getDevice_valid() {
		return device_valid;
	}
	
	public void setDevice_valid(int device_valid) {
		this.device_valid = device_valid;
	}

}
