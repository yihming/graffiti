/**
 * Version: 1.10: All Rights Reserved to AlphaSphere R&D Group
 * 2009-8-26 Llj
 * 
 * Version: 1.11
 * 2009-8-28
 * All functions have been tested to be accurate
 * Guo Kai
 * 
 * Version: 1.12
 * 2009-8-29
 * Guo Kai: add getAllDeptList
 */
package com.as.dao;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.as.db.DbConnection;
import com.as.function.AS;
import com.as.vo.DeptVO;

public class DeptDAO {
	private DbConnection dbc;

	private String dept = "dept";

	/**
	 * 功能：UserDAO构造函数，连接数据库 
	 * 参数：null 
	 * 返回值：NULL
	 */
	public DeptDAO() {
		dbc = new DbConnection();
	}

	/**
	 * 功能：根据部门ID查找部门表 
	 * 参数：i 
	 * 返回值：DeptVO类
	 */
	public DeptVO getDeptById(int i) {
		DeptVO deptvo = new DeptVO();
		AS as_clob = new AS();
		String sql = "select * from " + dept + " where dept_id = " + i;

		try {
			dbc.rs = dbc.st.executeQuery(sql);
			if (dbc.rs.next()) {

				deptvo.setDept_name((String) dbc.rs.getString("dept_name"));
				deptvo.setDept_intro((String) as_clob
						.Clob_To_String((Clob) dbc.rs.getClob("dept_intro")));
				
				deptvo.setDept_id((int) dbc.rs.getInt("dept_id"));

				return deptvo;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;

	}

	/**
	 * 功能：根据部门名称查找部门ID
	 * 参数：字符串name
	 * 返回值：int型 Id序号
	 */
	public int getDeptIdByname(String name) {
		String sql = "select dept_id from " + dept + " where dept_name = "
				+"'" +name+"'";
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			if (dbc.rs.next()) {
				return dbc.rs.getInt("dept_id");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		return 0;
	}

	/**
	 * 功能：增加一个部门
	 * 参数：DeptVO类型对象
	 * 返回值：布尔型
	 */
	public boolean setAddDept(DeptVO deptvo) {
		String name = deptvo.getDept_name();
		String intro = deptvo.getDept_intro();
		String sql = "insert into " + dept + "(dept_id,dept_name,dept_intro) " +
				"values(NULL,'"+name+"','"+intro+"')";

		try {
			if (this.getDeptIdByname(name) == 0) {
				// This dept_name is available
				dbc.st.executeUpdate(sql);
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false ;
	}

	/**
	 * 功能：删除一个部门
	 * 参数：部门ID
	 * 返回值：int型
	 */
	public boolean setDelDept(int id) {
		int i = 0;
		String sql = "delete from " + dept + " where dept_id=" + id;
//		System.out.println(sql);
		try {
			i = dbc.st.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;

	}

	/**
	 * 功能：修改一个部门
	 * 参数：DeptVO型对象,deptvo.id 是要更新的部门的id号
	 * 返回值：int型
	 */
	public boolean setUdpDept(DeptVO deptvo) {
		int i = 0;

		String sql = "update " + dept + " set dept_name='"
				+ deptvo.getDept_name() + "',dept_intro='"
				+ deptvo.getDept_intro()+"'";
		sql += "where dept_id = "+deptvo.getDept_id();
//		System.out.println(sql);
		
		try {

			i = dbc.st.executeUpdate(sql);			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 功能：获得所有的部门列表
	 * 参数：NULL
	 * 返回值：list
	 * 备注：通过测试
	 * 
	 */
	
	public List getAllDeptList(){
		String sql = "select * from dept";
		AS as = new AS();
		List list = new ArrayList();
		
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			while(dbc.rs.next()){
				DeptVO deptvo = new DeptVO();
				deptvo.setDept_id(dbc.rs.getInt("dept_id"));
				deptvo.setDept_name(dbc.rs.getString("dept_name"));
				deptvo.setDept_intro(as.Clob_To_String(dbc.rs.getClob("dept_intro")));
				list.add(deptvo);
			}
			return list;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 功能：获取某个员工的部门信息
	 * 参数：user_id
	 * 返回值：DeptVO
	 * 备注：通过测试
	 * @param id
	 * @return
	 */
	public DeptVO getDeptByUserId(int id){
		String sql = "select * from dept,userinfo where userinfo.dept_id=dept.dept_id and userinfo.user_id = "+id;
		DeptVO deptvo = new DeptVO();
		AS as = new AS();
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			if(dbc.rs.next()){
				deptvo.setDept_name((String) dbc.rs.getString("dept_name"));
				deptvo.setDept_intro((String) as
						.Clob_To_String((Clob) dbc.rs.getClob("dept_intro")));
				
				deptvo.setDept_id((int) dbc.rs.getInt("dept_id"));
			}
			return deptvo;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		
	}
	
/*
	public static void main(String[] args){
		DeptDAO deptdao = new DeptDAO();
		DeptVO deptvo = new DeptVO();
		List list = deptdao.getAllDeptList();
		Iterator it = list.iterator();
//		while(it.hasNext()){
//		deptvo = (DeptVO)it.next();
//			System.out.println(deptvo.getDept_name());
//		}
		deptvo = deptdao.getDeptByUserId(51);
		System.out.println(deptvo.getDept_name());
		
	}
*/
	
	
	
	
	
	
}
