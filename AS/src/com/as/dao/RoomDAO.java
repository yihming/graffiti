package com.as.dao;
/************************************************
 * Version: 1.5 : Remove RoomDAO(), Add getAllRooms()
 * 2009-8-27 By Bauer Yung
 * ****************************************************
 * Version 1.7: Add setCheckRoomName(), Add setAddRoom(); Revise a BUG: "dbc" should be initialized in each function.
 * 2009-8-27 15:44 Bauer Yung
 * ***************************************************
 * Version 1.8: Change setAddRoom() synchronized with changes in Database--Add "broken" columns of each fixed device.
 * 2009-8-27 21:31 Bauer Yung
 * ***************************************************
 * Version 1.11: Add getAllRooms() which return a List object to add_device.jsp
 * 2009-8-28 11:37 Bauer Yung
 * ***************************************************
 * Version 1.12: Reconstruct setAddRoom() with PreparedStatement Class instead of Statement Class.
 * 2009-8-28 12:03 Bauer Yung
 * ***************************************************
 * Version 1.14: Add getRoom_PageHelp()
 * 2009-8-28 20:21 Bauer Yung
 * ***************************************************
 * Version 1.15: Add delRoom(). (2009-8-28 21:24 Bauer Yung)
 * Version 1.18: Add updRoom(). (2009-8-29 11:50 Bauer Yung)
 * Version 1.19: Add dbc.closeConnection() in getRoomById(), delRoom() & updRoom(). (2009-8-29 13:25 Bauer Yung)
 * Version 1.20: Addd getRooms_User(). (2009-8-30 19:12 Bauer Yung)
 * Version 1.21: Correction some Bugs in getRooms_User(). (2009-8-30 21:03 Bauer Yung)
 * Version 1.23: Correct one Bug in getRooms_User(): Add the 3rd SQL Statement. (2009-8-31 9:17 Bauer Yung)
 * Version 1.24: Correct one Bug in getRoomById(): Add room_id to the returned RoomVO object. (2009-8-31 10:52 Bauer Yung)
 ***********************************************/

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.as.db.*;
import com.as.function.PageHelp;
import com.as.vo.RoomVO;

public class RoomDAO {
	public DbConnection dbc = null;
	
	public List getAllRooms() {  // Get information of all rooms. As the interface for add_device.jsp.
		
		dbc=new DbConnection();
		List li = new ArrayList();
		PreparedStatement pst;
		try {
			pst = dbc.conn.prepareStatement("SELECT * FROM room");
			
			if (pst.executeQuery() != null) {
				dbc.rs = pst.executeQuery();
				
				while (dbc.rs.next()) {
					RoomVO roomvo = new RoomVO();
					roomvo.setRoom_id(dbc.rs.getInt("room_id"));
					roomvo.setRoom_addr(dbc.rs.getString("room_addr"));
					roomvo.setRoom_intro(dbc.rs.getString("room_intro"));
					roomvo.setRoom_name(dbc.rs.getString("room_name"));
					roomvo.setRoom_pc(dbc.rs.getInt("room_pc"));
					roomvo.setRoom_projector(dbc.rs.getInt("room_projector"));
					roomvo.setRoom_size(dbc.rs.getInt("room_size"));
					roomvo.setRoom_tv(dbc.rs.getInt("room_tv"));
					
					li.add(roomvo);
				}
			}
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
		}
			
		return li;
	}// End of getAllRooms().
	
	public PageHelp getRoom_PageHelp(String cond, int currentPage) { // Get information of all rooms, displaying with multiple pages.
		
		dbc = new DbConnection();
		int recordcount = 0;    // Count the whole records in table "room".
		PageHelp pagehelp = null;
		
		try {
			// Get the total number of records in table "room".
			PreparedStatement pst = dbc.conn.prepareStatement("SELECT COUNT(*) recordcount FROM room WHERE 1=1");
			dbc.rs = pst.executeQuery();
			
			if (dbc.rs.next()) {
				recordcount = dbc.rs.getInt("recordcount");
			}
			
			int startNum = (currentPage - 1) * PageHelp.pagesize + 1;  // Set the start number of records.(From 1)
			int endNum = currentPage * PageHelp.pagesize + 1;   // Set the end number of records.
			
			// Select records in the currentPage.
			StringBuffer querySQL = new StringBuffer();
			querySQL.append("SELECT * FROM ( SELECT first.*, rownum rc FROM ( SELECT * FROM room WHERE 1=1")
					.append(cond)
					.append(" ORDER BY room_id DESC ) first WHERE rownum < ")
					.append(endNum)
					.append(") second WHERE rc >= ")
					.append(startNum);
			
			pst = dbc.conn.prepareStatement(querySQL.toString());
			dbc.rs = pst.executeQuery();
			List rooms = new ArrayList();
			RoomVO roomvo = null;
			
			while (dbc.rs.next()) {
				roomvo = new RoomVO();
				roomvo.setRoom_id(dbc.rs.getInt("room_id"));
				roomvo.setRoom_name(dbc.rs.getString("room_name"));
				roomvo.setRoom_addr(dbc.rs.getString("room_addr"));
				roomvo.setRoom_size(dbc.rs.getInt("room_size"));
				roomvo.setRoom_intro(dbc.rs.getString("room_intro"));
				roomvo.setRoom_pc(dbc.rs.getInt("room_pc"));
				roomvo.setRoom_tv(dbc.rs.getInt("room_tv"));
				roomvo.setRoom_projector(dbc.rs.getInt("room_projector"));
				roomvo.setPc_broken(dbc.rs.getInt("pc_broken"));
				roomvo.setTv_broken(dbc.rs.getInt("tv_broken"));
				roomvo.setPro_broken(dbc.rs.getInt("pro_broken"));
				
				rooms.add(roomvo);
			}
			
			pagehelp = new PageHelp(currentPage, recordcount, cond, rooms);
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
		}
		
		return pagehelp;
	} // End of getRoom_PageHelp().
	
	public PageHelp getRooms_Available(List room_ids, int currentPage) {
		int recordcount = room_ids.size();
		PageHelp pagehelp = null;
		RoomDAO roomdao = new RoomDAO();
		
		int startNum = (currentPage - 1) * PageHelp.pagesize + 1;
		int endNum = currentPage * PageHelp.pagesize + 1;
		List rooms = new ArrayList();
		Iterator it = room_ids.iterator();
		while (it.hasNext()) {
			RoomVO roomvo = new RoomVO();
			Integer res = (Integer) it.next();
			roomvo = roomdao.getRoomById(res.intValue());
			rooms.add(roomvo);
		}
		String cond = "";
		
		pagehelp = new PageHelp(currentPage, recordcount, cond, rooms);
		
		return pagehelp;
	}
	
	
	public boolean getCheckRoomName(String name) { // Check the name in the set of room_name.
		boolean flag = false;
		dbc = new DbConnection();  // Create a new Database Connection.
		
		try {
			
			// Execute the SQL Statement.
			PreparedStatement pst = dbc.conn.prepareStatement("SELECT * FROM room WHERE room_name = ? ");
			pst.setString(1, name);
			
			if (pst.executeQuery() != null) {
				dbc.rs = pst.executeQuery();
				
				if (dbc.rs.next()) {
					if (dbc.rs.getString("room_name").equals(name)) {
						flag = true;
					}
				}
				
			} else {
				flag = true;
			}
		} catch (SQLException e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
			
		} finally {
			// Close the database connection.
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
		}
			
			
		
		return flag;
		
	} // End of getCheckRoomName().
	
	public RoomVO getRoomById(int id) { // Get information of the room by searching for its ID.
		dbc = new DbConnection();
		RoomVO room = null;
		
		try {
			PreparedStatement pst = dbc.conn.prepareStatement("SELECT * FROM room WHERE room_id = ? ");
			pst.setInt(1, id);
			
			if (pst.executeQuery() != null) {
				dbc.rs = pst.executeQuery();
				while (dbc.rs.next()) {
					room = new RoomVO();
					room.setRoom_id(id);
					room.setRoom_name(dbc.rs.getString("room_name"));
					room.setRoom_addr(dbc.rs.getString("room_addr"));
					room.setRoom_size(dbc.rs.getInt("room_size"));
					room.setRoom_intro(dbc.rs.getString("room_intro"));
					room.setRoom_pc(dbc.rs.getInt("room_pc"));
					room.setRoom_tv(dbc.rs.getInt("room_tv"));
					room.setRoom_projector(dbc.rs.getInt("room_projector"));
					room.setPc_broken(dbc.rs.getInt("pc_broken"));
					room.setPro_broken(dbc.rs.getInt("pro_broken"));
					room.setTv_broken(dbc.rs.getInt("tv_broken"));
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (dbc.conn != null) {
				// Close the database connection.
				dbc.closeConnection();
			}
		}
		
		return room;
	} // End of getRoomById().
	
	public List getRooms_User(String m_start, String m_end, int m_num, int m_projector, int m_tv, int m_pc) {
		dbc = new DbConnection();		// Create a new database connection.
		StringBuffer sql1 = new StringBuffer();		// The first SQL Statement.
		sql1.append("SELECT room_id FROM room WHERE room_id IN ")
			.append("(SELECT room_id FROM meeting WHERE m_end <= to_date('")
			.append(m_start)
			.append("', 'yyyy-mm-dd hh24-mi') AND room_id IN ")
			.append("(SELECT room_id FROM room WHERE room_size >= ")
			.append(m_num)
			.append(" AND room_pc - pc_broken >= ")
			.append(m_pc)
			.append(" AND room_tv - tv_broken >= ")
			.append(m_tv)
			.append(" AND room_projector - pro_broken >= ")
			.append(m_projector)
			.append(" ))");
		
		StringBuffer sql2 = new StringBuffer();		// The second SQL Statement.
		sql2.append("SELECT room_id FROM room WHERE room_id IN ")
			.append("(SELECT room_id FROM meeting WHERE m_start >= to_date('")
			.append(m_end)
			.append("', 'yyyy-mm-dd hh24-mi') ")
			.append("AND room_id IN ")
			.append("(SELECT room_id FROM room WHERE room_size >= ")
			.append(m_num)
			.append(" AND room_pc - pc_broken >= ")
			.append(m_pc)
			.append(" AND room_tv - tv_broken >= ")
			.append(m_tv)
			.append(" AND room_projector - pro_broken >= ")
			.append(m_projector)
			.append(" ))");
		
		String sql3 = "SELECT room_id FROM room WHERE room_id NOT IN (SELECT room_id FROM meeting)";
		
		List res = new ArrayList();			// The final result List.
		
		try {
			// Execute the first query.
			PreparedStatement pst = dbc.conn.prepareStatement(sql1.toString());
			
			if (pst.execute()) { // If the query returns results.
				dbc.rs = pst.executeQuery();
				while (dbc.rs.next()) {
					// Add each result to the List.
					
					Integer room_id = new Integer(dbc.rs.getInt("room_id"));
					
					res.add(room_id);
				}
			}
			
			// Execute the second query.
			pst = dbc.conn.prepareStatement(sql2.toString());
			
			if (pst.execute()) { // If the second query returns results.
				ResultSet rs2 = pst.executeQuery();
				
				while (rs2.next()) {
					Integer tmp = new Integer(rs2.getInt("room_id"));
					
					if (!res.contains(tmp)) { // If this room_id does not exist in the List.
						
						// Add it to the List.
						res.add(tmp);
					}
				}
			}
			
			// Execute the third query.
			pst = dbc.conn.prepareStatement(sql3);
			
			if (pst.execute()) {	// If the third query returns results.
				ResultSet rs3 = pst.executeQuery();
				
				while (rs3.next()) {
					Integer tmp = new Integer(rs3.getInt("room_id"));
					
					if (!res.contains(tmp)) {  // If this room_id does not exist in the List.
						
						// Add it to the List.
						res.add(tmp);
					}
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// Close the database connection.
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
		}
		
		return res;
		
	} // End of getRooms_User().
	
	public int setAddRoom(RoomVO room) { // Add a room record into database.
		
		dbc = new DbConnection();  // Create a new Database Connection.
		String name = room.getRoom_name();  // Gain the room name.
		int count = 0;  // Count the number of lines influenced by INSERT operation.
		
		
		if (!this.getCheckRoomName(name)) { // This room name is available.
			
			// Set the SQL statement.
			dbc = new DbConnection();
			
			String sql = "INSERT INTO room(room_id, room_size, room_addr, room_name, room_intro, room_tv, room_pc, room_projector, tv_broken, pc_broken, pro_broken) ";
				  sql += "VALUES(null, ?, ?, ?, ?, ?, ?, ?, 0, 0, 0)";
			
			try {
				PreparedStatement pst = dbc.conn.prepareStatement(sql);
				pst.setInt(1, room.getRoom_size());
				pst.setString(2, room.getRoom_addr());
				pst.setString(3, room.getRoom_name());
				pst.setString(4, room.getRoom_intro());
				pst.setInt(5, room.getRoom_tv());
				pst.setInt(6, room.getRoom_pc());
				pst.setInt(7, room.getRoom_projector());
				
				count = pst.executeUpdate();
				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				if (dbc.conn != null) {
					dbc.closeConnection();
				}
			}
			
				  
		} 
		
		// Return the number of lines which was changed by INSERT operation.
		return count;
		
	} // End of setAddRoomName().
	
	public int delRoom(int id) { // Delete a record in table "room".
		dbc = new DbConnection();
		int count = 0;
		
		PreparedStatement pst;
		try {
			pst = dbc.conn.prepareStatement("DELETE FROM room WHERE room_id = ?");
			pst.setInt(1, id);
			
			count = pst.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (dbc.conn != null) {
				// Close the database connection.
				dbc.closeConnection();
			}
		}
		
		return count;
		
		
	} // End of delRoom().
	
	public int updRoom(RoomVO room) { // Update room information.
		int count = 0;  // Count on lines influenced by UPDATE operation.
		dbc = new DbConnection();  // Create a new database connection.
		
		// The SQL statement.
		String sql = "UPDATE room SET room_name = ?, room_addr = ?, room_size = ?, room_intro = ?, room_pc = ?, room_tv = ?, room_projector = ?, tv_broken = ?, pc_broken = ?, pro_broken = ? WHERE room_id = ? ";
		
		try {
			// Set parameters.
			PreparedStatement pst = dbc.conn.prepareStatement(sql);
			pst.setString(1, room.getRoom_name());
			pst.setString(2, room.getRoom_addr());
			pst.setInt(3, room.getRoom_size());
			pst.setString(4, room.getRoom_intro());
			pst.setInt(5, room.getRoom_pc());
			pst.setInt(6, room.getRoom_tv());
			pst.setInt(7, room.getRoom_projector());
			pst.setInt(8, room.getTv_broken());
			pst.setInt(9, room.getPc_broken());
			pst.setInt(10, room.getPro_broken());
			pst.setInt(11, room.getRoom_id());
			
			// Execute the SQL statement.
			count = pst.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (dbc.conn != null) {
				// Close the database connection.
				dbc.closeConnection();
			}
		}
		
		return count;
		
	} // End of updRoom().
	
	public RoomVO getRoomByM_id(int m_id) {
		dbc = new DbConnection();
		RoomVO room_info = new RoomVO();
		
		PreparedStatement pst;
		try {
			pst = dbc.conn.prepareStatement("SELECT room.* FROM room, meeting WHERE meeting.room_id = room.room_id AND meeting.m_id = ? ");
			pst.setInt(1, m_id);
			
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				
				while (dbc.rs.next()) {
					room_info.setRoom_id(dbc.rs.getInt("room_id"));
					room_info.setRoom_name(dbc.rs.getString("room_name"));
					room_info.setRoom_addr(dbc.rs.getString("room_addr"));
					room_info.setRoom_size(dbc.rs.getInt("room_size"));
					room_info.setRoom_pc(dbc.rs.getInt("room_pc"));
					room_info.setRoom_projector(dbc.rs.getInt("room_projector"));
					room_info.setRoom_tv(dbc.rs.getInt("room_tv"));
					room_info.setPc_broken(dbc.rs.getInt("pc_broken"));
					room_info.setPro_broken(dbc.rs.getInt("pro_broken"));
					room_info.setTv_broken(dbc.rs.getInt("tv_broken"));
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
		}
		
		return room_info;
		
	} // End of getRoomByM_id().
	
}
