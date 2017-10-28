package org.bnuminer.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bnuminer.DbConnection;
import org.bnuminer.vo.ConfigsVO;

public class ConfigsDAO {
	private DbConnection dbc;
	
	public List<ConfigsVO> getAllConfigs() {
		List<ConfigsVO> result = new ArrayList<ConfigsVO>();
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("SELECT * FROM configs");
			dbc.rs = pst.executeQuery();
			while (dbc.rs.next()) {
				ConfigsVO configsvo = new ConfigsVO();
				configsvo.setItem(dbc.rs.getString("item"));
				configsvo.setContent(dbc.rs.getString("content"));
				result.add(configsvo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return result;
	}
	
	public ConfigsVO getConfig(String item) {
		ConfigsVO result = new ConfigsVO();
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("SELECT * FROM configs WHERE item = ?");
			pst.setString(1, item);
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				if (dbc.rs.next()) {
					result.setItem(dbc.rs.getString("item"));
					result.setContent(dbc.rs.getString("content"));
				}
			} else {
				result = null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return result;
		
	} // End of getConfig().
	
	public boolean setUpdateContent(ConfigsVO configsvo) {
		boolean result = false;
		dbc = new DbConnection();
		PreparedStatement pst;
		
		try {
			pst = dbc.conn.prepareStatement("UPDATE configs SET content = ? WHERE item = ? ");
			pst.setString(1, configsvo.getContent());
			pst.setString(2, configsvo.getItem());
			
			if (pst.executeUpdate() != 0) {
				result = true;
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			dbc.closeConnection();
		}
		
		return result;
	} // End of setUpdateContent().
}
