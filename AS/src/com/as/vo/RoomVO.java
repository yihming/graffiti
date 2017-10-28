package com.as.vo;
/*************************************************************
 * Version 1.5: Alter the type of "room_intro" from Clob to String;
 * 			    Change the return type of getRoom_intro() & the argument type of setRoom_intro().
 * 2009-8-27 15:46 Bauer Yung
 * ****************************************************
 * Version 1.6: Add tv_broken, pc_broken, pro_broken member of RoomVO;
 * 				Add 2 operations for each modified member.
 * 2009-8-28 11:59 Bauer Yung
 ************************************************************/



public class RoomVO {
	private int room_id;	// ID of room.
	private int room_size;  // The maximum number of people that room can contain.
	private String room_addr;	// The address of room.
	private String room_name;	// The name of room.
	private String room_intro;	// The introduction of room.
	private int room_tv;	// The number of TV that room has.
	private int room_pc;	// The number of PC that room has.
	private int room_projector;		// The number of projectors that room has.
	private int tv_broken;
	private int pc_broken;
	private int pro_broken;
	
	public int getRoom_id() {
		return room_id;
	}
	
	public void setRoom_id(int room_id) {
		this.room_id = room_id;
	}
	
	public int getRoom_size() {
		return room_size;
	}
	
	public void setRoom_size(int room_size) {
		this.room_size = room_size;
	}
	
	public String getRoom_addr() {
		return room_addr;
	}
	
	public void setRoom_addr(String room_addr) {
		this.room_addr = room_addr;
	}
	
	public String getRoom_name() {
		return room_name;
	}
	
	public void setRoom_name(String room_name) {
		this.room_name = room_name;
	}
	
	public String getRoom_intro() {
		return room_intro;
	}
	
	public void setRoom_intro(String room_intro) {
		this.room_intro = room_intro;
	}
	
	public int getRoom_tv() {
		return room_tv;
	}
	
	public void setRoom_tv(int room_tv) {
		this.room_tv = room_tv;
	}
	
	public int getRoom_pc() {
		return room_pc;
	}
	
	public void setRoom_pc(int room_pc) {
		this.room_pc = room_pc;
	}
	
	public int getRoom_projector() {
		return room_projector;
	}
	
	public void setRoom_projector(int room_projector) {
		this.room_projector = room_projector;
	}
	
	public int getTv_broken() {
		return tv_broken;
	}
	
	public void setTv_broken(int tv_broken) {
		this.tv_broken = tv_broken;
	}
	
	public int getPc_broken() {
		return pc_broken;
	}
	
	public void setPc_broken(int pc_broken) {
		this.pc_broken = pc_broken;
	}
	
	public int getPro_broken() {
		return pro_broken;
	}
	
	public void setPro_broken(int pro_broken) {
		this.pro_broken = pro_broken;
	}

	
}
