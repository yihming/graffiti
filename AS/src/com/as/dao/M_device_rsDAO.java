package com.as.dao;
/************************************************************************
 * Version 1.1: Add getCheckDevice(). (2009-8-30 11:49 Bauer Yung)
 * Version 1.4: Add getUsedDeviceByM_id(). (2009-8-31 16:45 Bauer Yung)
 * Version 1.5: Correct one Bug in getUsedDeviceByM_id(). (2009-8-31 17:03 Bauer Yung)
 * Version 1.6: Add setDelRecords(). (2009-8-31 18:56 Bauer Yung)
 ***********************************************************************/
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.as.db.DbConnection;
import com.as.vo.M_device_rsVO;
import com.as.vo.DeviceVO;

public class M_device_rsDAO {
	public DbConnection dbc;
	
	public boolean getCheckDevice(DeviceVO device, int required) { // Judge the validity of the chosen device.
		boolean judge = false;
		dbc = new DbConnection();
		try {
			PreparedStatement pst = dbc.conn.prepareStatement("SELECT SUM(used_device_num) sum FROM m_device_rs WHERE device_id = ? ");
			pst.setInt(1, device.getDevice_id());
			dbc.rs = pst.executeQuery();
			if (pst.execute()) {
				dbc.rs = pst.getResultSet();
				int used = dbc.rs.getInt("sum");
				int total = device.getDevice_count();
				int damaged = device.getDevice_damage();
				if (total - damaged - used >= required) {
					judge = true;
				}
				
			} else {
				judge = true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
		}
		
		return judge;
		
	} // End of getCheckDevice().
	
	public List getUsedDeviceByM_id(int m_id) {
		dbc = new DbConnection();
		List released_devices = new ArrayList();
		
		PreparedStatement pst;
		try {
			pst = dbc.conn.prepareStatement("SELECT device_id, used_device_num FROM m_device_rs WHERE m_id = ? ");
			pst.setInt(1, m_id);
			
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				while (dbc.rs.next()) {
					DeviceVO extra_device = new DeviceVO();
					
					extra_device.setDevice_id(dbc.rs.getInt("device_id"));
					extra_device.setDevice_valid(dbc.rs.getInt("used_device_num"));
					
					released_devices.add(extra_device);
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
		
		return released_devices;
		
	} // End of getUsedDeviceByM_id().
	
	public boolean setDelRecords(int m_id) {
		dbc = new DbConnection();
		boolean flag = false;
		
		try {
			PreparedStatement pst = dbc.conn.prepareStatement("DELETE FROM m_device_rs WHERE m_id = ? ");
			pst.setInt(1, m_id);
			
			int count = pst.executeUpdate();
			flag = true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
		}
		
		return flag;
		
	} // End of setDelRecords().
	
	public boolean setAddRecords(int m_id, List m_device_rs) {
		dbc = new DbConnection();
		int count = 0;
		
		if (m_device_rs != null) {
			Iterator it = m_device_rs.iterator();
			while (it.hasNext()) {
				M_device_rsVO m_device = (M_device_rsVO) it.next();
				
				PreparedStatement pst;
				try {
					pst = dbc.conn.prepareStatement("INSERT INTO m_device_rs(m_device_rs_id, m_id, device_id, used_device_num) VALUES(null, ?, ?, ?)");
					pst.setInt(1, m_id);
					pst.setInt(2, m_device.getDevice_id());
					pst.setInt(3, m_device.getUsed_device_num());
					
					int i = pst.executeUpdate();
					if (i != 0) {
						++count;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				
			}
		}
		
		if (dbc.conn != null) {
			dbc.closeConnection();
		}
		
		if (count == m_device_rs.size()) {
			return true;
			
		} else return false;
	
	} // End of setAddRecords.
	
	public List getUsed_deviceNameByM_id(int m_id){
		dbc = new DbConnection();
		List used_device = new ArrayList();
		
		try {
			PreparedStatement pst = dbc.conn.prepareStatement("SELECT device_name, used_device_num FROM m_device_rs, device WHERE m_device_rs.device_id = device.device_id AND m_id = ? ");
			pst.setInt(1, m_id);
			
			if (pst.execute()) {
				dbc.rs = pst.executeQuery();
				
				while (dbc.rs.next()) {
					DeviceVO device = new DeviceVO();
					
					device.setDevice_name(dbc.rs.getString("device_name"));
					device.setDevice_valid(dbc.rs.getInt("used_device_num"));
					
					used_device.add(device);
					
				}
			} else {
				used_device = null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
		}
		
		return used_device;
		
	} // End of getUsed_deviceByM_id().
	
}
