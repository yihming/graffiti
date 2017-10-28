package com.as.dao;

import java.sql.PreparedStatement;
import java.beans.Statement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.as.db.DbConnection;
import com.as.function.*;
import com.as.vo.DeviceVO;
import com.as.vo.M_device_rsVO;

public class DeviceDAO {
	private DbConnection dbc;
	/*
	 * 功能：构造函数，链接到数据库
	 * 参数：空
	 * 返回值：空
	 * */
	
	/**
	 * 功能：根据部门ID查找设备 
	 * 参数：device_id(接收的设备的ID号)
	 * 返回值：DeviceVO类（查找到的设备）
	 */
	public DeviceVO getDeviceById(int device_id) {
		dbc=new DbConnection();
		DeviceVO device = new DeviceVO();
		AS as_clob = new AS();
		PreparedStatement pst;
		try {
			pst = dbc.conn.prepareStatement(" select * from device where device_id = ?");
			pst.setInt(1,device_id);
			if (pst.executeQuery() != null) {
				dbc.rs = pst.executeQuery();				
				if (dbc.rs.next()) {
					device.setDevice_id((int)dbc.rs.getInt("device_id"));
					device.setDevice_name((String) dbc.rs.getString("device_name"));
					device.setDevice_intro((String) as_clob
							.Clob_To_String((Clob) dbc.rs.getClob("device_intro")));
					device.setDevice_count((int) dbc.rs.getInt("device_count"));
					device.setDevice_damage((int) dbc.rs.getInt("device_damage"));
					device.setDevice_valid((int) dbc.rs.getInt("device_valid"));
					return device;
				}
			}			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * 功能：根据设备名称查找部门ID
	 * 参数：字符串device_name（接收的设备名称）
	 * 返回值：int型 Id序号
	 */
	public int getDeviceIdByname(String device_name) {
		dbc=new DbConnection();
		PreparedStatement pst;
		try {
			pst = dbc.conn.prepareStatement("select * from device where device_name=?");
			pst.setString(1, device_name);
			if(pst.executeQuery()!=null)
			{
				dbc.rs=pst.executeQuery();
				if (dbc.rs.next()) {
					return dbc.rs.getInt("device_id");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		return 0;
	}

	/**
	 * 功能：增加一种设备
	 * 参数：DeviceVO类型对象
	 * 返回值：布尔型
	 */
	public boolean setAddDevice(DeviceVO devicevo) {
		dbc=new DbConnection();
		String name = devicevo.getDevice_name();
		int count = devicevo.getDevice_count();
		int damage = devicevo.getDevice_damage();
		int valid = count - damage;
		String intro = devicevo.getDevice_intro();
		String sql="insert into device (device_id,device_name,device_count,device_damage,device_valid,device_intro) values(null,'"+name+"',"+count+","+damage+"," + valid + ",'"+intro+"')";
		try {
			if(this.getDeviceIdByname(name) == 0) {
				// This device_name is available
				dbc.st.executeUpdate(sql);
				return true;
			}else{
				System.out.println("已经存在名称为'"+name+"'的设备！");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false ;
	}

	/**
	 * 功能：删除一种设备
	 * 参数：设备ID
	 * 返回值：bool型
	 */
	public boolean setDelDevice(int device_id) {
		dbc=new DbConnection();
		PreparedStatement pst;
		try {
				pst = dbc.conn.prepareStatement("delete from device where device_id = ?");
				pst.setInt(1, device_id);
				pst.executeQuery();
				return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public boolean setDelDeviceByList(List released_devices) {
		dbc = new DbConnection();
		boolean flag = false;
		Iterator it = released_devices.iterator();
		int count = 0;
		
		if (released_devices != null) {
			while (it.hasNext()) {
				DeviceVO extra_device = (DeviceVO) it.next();
				int device_id = extra_device.getDevice_id();
				int extra_num = extra_device.getDevice_valid();
				
				try {
					PreparedStatement pst = dbc.conn.prepareStatement("UPDATE device SET device_valid = device_valid + ? WHERE device_id = ? ");
					pst.setInt(1, extra_num);
					pst.setInt(2, device_id);
					int lines = pst.executeUpdate();
					
					if (lines != 0) {
						++count;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
			
			if (count == released_devices.size()) {
				flag = true;
			}
		} else {
			flag = true;
		}
		
		return flag;
		
	} // End of setDelDeviceByList().
	
	/**
	 * 功能：更新一种设备
	 * 参数：DeviceVO型对象
	 * 返回值：Boolean
	 */
	public boolean setUdpDevice(DeviceVO devicevo) {
		dbc=new DbConnection();
		String sql="update device set device_name= '"+devicevo.getDevice_name()+
					"',device_intro='"+devicevo.getDevice_intro()+"',device_count="+
					devicevo.getDevice_count()+",device_damage="+devicevo.getDevice_damage()+
					",device_valid="+devicevo.getDevice_valid()+
					" where device_id="+devicevo.getDevice_id();
		try {
				dbc.st.executeUpdate(sql);
				return true;			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean setUpdValid(List used_devices) {
		dbc = new DbConnection();
		boolean flag = false;
		int count = 0;
		
		if (used_devices != null) {
			Iterator it = used_devices.iterator();
			while (it.hasNext()) {
				DeviceVO m_device = (DeviceVO) it.next();
				int device_id = m_device.getDevice_id();
				int used_num = m_device.getDevice_valid();
				
				String sql = "UPDATE device SET device_valid = device_valid + ? WHERE device_id = ? ";
				PreparedStatement pst;
				try {
					pst = dbc.conn.prepareStatement(sql);
					pst.setInt(1, used_num);
					pst.setInt(2, device_id);
					
					int i = pst.executeUpdate();
					
					if (i != 0) {
						++count;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
			
			if (count == used_devices.size()) {
				flag = true;
			}
			
		} else {
			flag = true;
		}
		
		return flag;
		
	} // End SetUpdValid().
	
	/*
	 * 功能：获取所有的移动设备的列表
	 * 参数：空
	 * 返回值：数据库中移动设备的列表
	 * */
	public List getAllDevice(){
		dbc=new DbConnection();
		List list = new ArrayList();
		DeviceVO devicevo;//=new DeviceVO();
		
		try { 
			dbc.st = dbc.conn.createStatement();
			dbc.rs = dbc.st.executeQuery("SELECT * FROM device ORDER BY device_id");

			while (dbc.rs.next()) {
				devicevo = new DeviceVO();
				devicevo.setDevice_id(dbc.rs.getInt("device_id"));
				devicevo.setDevice_name(dbc.rs.getString("device_name"));
				devicevo.setDevice_count(dbc.rs.getInt("device_count"));
				devicevo.setDevice_damage(dbc.rs.getInt("device_damage"));
				devicevo.setDevice_valid(dbc.rs.getInt("device_valid"));

				AS my_as = new AS();
				String intro = my_as.Clob_To_String(dbc.rs.getClob("device_intro"));
				devicevo.setDevice_intro(intro);
				list.add(devicevo);				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return list;				
	}

	public PageHelp queryDevice(String condi,int currentPage) throws SQLException {
		dbc=new DbConnection();
		dbc.st =dbc.conn.createStatement();

		int recordcount=0;
		String sql="select count(*) recordcount from device where 1=1 "+condi+"";
		dbc.rs=dbc.st.executeQuery(sql);
		if(dbc.rs.next()){
			recordcount=dbc.rs.getInt("recordcount");
		}
		int startNum=(currentPage - 1) * PageHelp.pagesize+1;//由于数据库中没有第0条记录所以要进行+1修正
		int endNum= currentPage* PageHelp.pagesize+1;//查询结束行号
		String pagesql="select * from (" +
										"select a.* ,rownum rc from(" +
																	"select * from device where 1=1 "+condi+" order by device_id desc" +
																") a where rownum<"+endNum+"" +
										") b where rc >="+startNum+"";	
		dbc.rs=dbc.st.executeQuery(pagesql);
		List devices=new ArrayList();
		DeviceVO devicevo =null;
		while(dbc.rs.next()){
			devicevo= new DeviceVO();
			devicevo.setDevice_id(dbc.rs.getInt("device_id"));
			devicevo.setDevice_name(dbc.rs.getString("device_name"));
			devicevo.setDevice_count(dbc.rs.getInt("device_count"));
			devicevo.setDevice_damage(dbc.rs.getInt("device_damage"));
			devicevo.setDevice_valid(dbc.rs.getInt("device_valid"));
			devicevo.setDevice_intro(dbc.rs.getString("device_intro"));

			devices.add(devicevo);
		}
		PageHelp pagehelp= new PageHelp(currentPage,recordcount,condi,devices);
		return pagehelp;
	}
	
	public int getDeviceRecords() { // Return the total quantity of device records.
		dbc = new DbConnection();
		int count = 0;
		try {
			PreparedStatement pst = dbc.conn.prepareStatement("SELECT COUNT(*) record FROM device WHERE 1=1");
			dbc.rs = pst.executeQuery();
			
			if (dbc.rs.next()) {
				count = dbc.rs.getInt("record");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (dbc.conn != null) {
				dbc.closeConnection();
			}
		}
		
		return count;
	} // End of getDeviceRecords().
	
	public List getDeviceIds() { // Get IDs of all device records order by asecentic.
		dbc = new DbConnection();
		List res = new ArrayList();
		
		try {
			PreparedStatement pst = dbc.conn.prepareStatement("SELECT device_id FROM device ORDER BY device_id");
			dbc.rs = pst.executeQuery();
			
			while (dbc.rs.next()) {
				res.add(new Integer(dbc.rs.getInt("device_id")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;
	} // End getDeviceIds().
	
}
