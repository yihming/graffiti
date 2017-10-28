package com.as.vo;
/*********************************************************************
 * Version 1.3: Create class MeetingVO. (2009-8-29 12:16 Bauer Yung)
 * Version 1.4: Change the return type of getM_state() & the parameter type of setM_state(). (2009-8-31 10:45 Bauer Yung)
 *********************************************************************/
import com.as.dao.RoomDAO;

public class MeetingVO {
	private int m_id;  // ID of the meeting.
	private int room_id;  // The room the meeting will use.
	private String m_title;  // The topic of the meeting.
	private String m_intro;  // The introduction of the meeting.
	private String m_start;  // The start time of the meeting.
	private String m_end;  // The end time of the meeting.
	private int m_num;   // The total number of participants.
	private int m_state;   // The state of the meeting.
	/*m_state - description:
	 * 0   -   Waiting for approval.（待审）
	 * 1   -   Approved to be succeed.（审成）
	 * 2   -   Approved to be rejected.(拒绝)
	 * 3   -   Exception happened.（异常）
	 */
	
	public int getM_id() {
		return m_id;
	}
	
	public void setM_id(int m_id) {
		this.m_id = m_id;
	}
	
	public int getRoom_id() {
		return room_id;
	}
	
	public void setRoom_id(int room_id) {
		this.room_id = room_id;
	}
	
	public String getM_title() {
		if (m_title == null) {
			return "无";
		} else return m_title;
	}
	
	public void setM_title(String m_title) {
		this.m_title = m_title;
	}
	
	public String getM_intro() {
		if (m_intro == null) {
			return "无";
		} else return m_intro;
	}
	
	public void setM_intro(String m_intro) {
		this.m_intro = m_intro;
	}
	
	public String getM_start() {
		return m_start;
	}
	
	public void setM_start(String m_start) {
		this.m_start = m_start;
	}
	
	public String getM_end() {
		return m_end;
	}
	
	public void setM_end(String m_end) {
		this.m_end = m_end;
	}
	
	public int getM_num() {
		return m_num;
	}
	
	public void setM_num(int m_num) {
		this.m_num = m_num;
	}
	
	public int getM_state() {
		return m_state;
	}
	
	public void setM_state(int m_state) {
		this.m_state = m_state;
	}
	
	public String getRoom_name(){
		RoomDAO roomdao=new RoomDAO();
		return roomdao.getRoomById(this.room_id).getRoom_name();
	}
}
