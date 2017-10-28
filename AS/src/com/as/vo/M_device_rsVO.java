package com.as.vo;
/*******************************************************************************
 * Version 1.2: Create M_devce_rsVO class. (2009-8-30 10:26 Bauer Yung)
 ******************************************************************************/
import java.util.*;

public class M_device_rsVO {
	private int m_device_rs_id;  // ID of this relationship.
	private int m_id;    // ID of meeting.
	private int device_id;    // ID of chosen device.
	private int used_device_num;    // The total number for this device.
	
	
	public int getDevice_id() {
		return device_id;
	}
	public void setDevice_id(int device_id) {
		this.device_id = device_id;
	}
	public int getM_device_rs_id() {
		return m_device_rs_id;
	}
	public void setM_device_rs_id(int m_device_rs_id) {
		this.m_device_rs_id = m_device_rs_id;
	}
	public int getM_id() {
		return m_id;
	}
	public void setM_id(int m_id) {
		this.m_id = m_id;
	}
	public int getUsed_device_num() {
		return used_device_num;
	}
	public void setUsed_device_num(int used_device_num) {
		this.used_device_num = used_device_num;
	}
	
	
}
