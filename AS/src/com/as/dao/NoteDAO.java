package com.as.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.as.db.DbConnection;
import com.as.function.AS;
import com.as.vo.NoteVO;

public class NoteDAO {
	private DbConnection dbc ;
	
	/**
	 * 插入便签
	 * @param user_id
	 * @param note_intro
	 * @return
	 */
	public boolean setAddNote(int user_id,String note_intro){
		boolean setAddFlag = false;
		String sql = new String();
		sql = "insert into note values(null,"+user_id+",'"+note_intro+"')";
		int affectedRows = 0;
		dbc = new DbConnection();
		try {
			affectedRows = dbc.st.executeUpdate(sql);
			if(affectedRows>0)
				setAddFlag = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dbc.closeConnection();
		return setAddFlag;
	}
	
	/**
	 * 获取便签内容
	 * @param user_id
	 * @return
	 */
	public List getAllNote(int user_id){
		List noteList = new ArrayList();
		String sql = new String();
		AS as = new AS();
		sql = "select * from note where user_id="+user_id;	
		dbc = new DbConnection();
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			while(dbc.rs.next()){
				NoteVO notevo = new NoteVO();
				notevo.setNote_id(dbc.rs.getInt("note_id"));
				notevo.setUser_id(dbc.rs.getInt("user_id"));
				notevo.setNote_intro(as.Clob_To_String(dbc.rs.getClob("note_intro")));		
				noteList.add(notevo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dbc.closeConnection();
		return noteList;
	}
	/**
	 * 删除便签
	 * @param note_id
	 * @return
	 */
	public boolean setDelNote(int note_id){
		boolean setDelFlag = false;
		String sql = new String();
		sql = "delete from note where note_id="+note_id;
		int affectedRows = 0;
		dbc = new DbConnection();
		try {
			affectedRows = dbc.st.executeUpdate(sql);
			if(affectedRows>0)
				setDelFlag = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return setDelFlag;
	}
}
