/**
 * Version: 1.1: All Rights Reserved to AlphaSphere R&D Group
 * 2009-8-26 Guo Kai
 * 
 * Version: 1.25: All Rights Reserved to AlphaSphere R&D Group
 * 2009-8-26 Guo Kai
 * add user_number
 * 
 * Version 1.29
 * 2009-8-26
 * Guo Kai : debug in update user information
 * 
 * Version 1.30
 * 2009-8-30
 * Guo Kai: add get user number
 * 
 * Version 1.31
 * 2008-8-30
 * Guo Kai: add getUserListBydept
 */
package com.as.dao;

import java.sql.Clob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.as.db.DbConnection;
import com.as.function.AS;
import com.as.function.PageHelp;
import com.as.vo.DeviceVO;
import com.as.vo.UserVO;

public class UserDAO {
	private DbConnection dbc;

	private String user = "userinfo";

	/**
	 * 功能：UserDAO构造函数，连接数据库 参数：null 返回值：NULL
	 */
	public UserDAO() {
		dbc = new DbConnection();
	}

	/**
	 * 功能：取得id为i的用户 参数：int id 返回值：UserVO类
	 * 
	 * 
	 * @param i
	 * @return
	 */
	public UserVO getUserById(int i) { 
		String sql = "select * from " + user + " where user_id = " + i;
		java.util.Date date = new java.util.Date();
		String strTime = "";
		SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd");
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			if (dbc.rs.next()) {
				UserVO user = new UserVO();
				AS as = new AS();
				// user.setUser_birthday(dbc.rs.getDate("user_birthday"));	

				if(dbc.rs.getTimestamp("user_birthday")!=null)
					date =dbc.rs.getTimestamp("user_birthday"); 
				
				strTime = simpledate.format(date);
				
//				System.out.println(strTime);
			    user.setUser_birthday(strTime);

				user.setUser_bonus((double) dbc.rs.getDouble("user_bonus"));
				user.setUser_card((String) dbc.rs.getString("user_card"));
				user.setUser_directory((String) dbc.rs
						.getString("user_directory"));
				user.setUser_mail((String) dbc.rs.getString("user_mail"));

				if(dbc.rs.getTimestamp("user_entrytime")!=null)
					date =dbc.rs.getTimestamp("user_entrytime");
				
				strTime = simpledate.format(date);
				user.setUser_entrytime(strTime);
				// user.setUser_entrytime((Date)
				// dbc.rs.getDate("user_entrytime"));

				user.setUser_group((int) dbc.rs.getInt("user_group"));
				user.setUser_name((String) dbc.rs.getString("user_name"));
				user.setUser_mobile((String) dbc.rs.getString("user_mobile"));
				user.setUser_true_name((String) dbc.rs
						.getString("user_true_name"));
				user.setUser_phone((String) dbc.rs.getString("user_phone"));
				user.setUser_other((String) dbc.rs.getString("user_other"));
				user.setUser_picture((String) dbc.rs.getString("user_picture"));
				user.setUser_pswd((String) dbc.rs.getString("user_pswd"));
				user.setUser_wage((double) dbc.rs.getDouble("user_wage"));
				user.setUser_intro(as.Clob_To_String((Clob) dbc.rs.getClob("user_intro")));
				user.setUser_sex((int) dbc.rs.getInt("user_sex"));
				user.setUser_id((int) dbc.rs.getInt("user_id"));
				user.setDept_id((int) dbc.rs.getInt("dept_id"));
				user.setUser_fax((String) dbc.rs.getString("user_fax"));
				user.setUser_office((String) dbc.rs.getString("user_office"));
				user.setUser_workage((int) dbc.rs.getInt("user_workage"));
				user.setUser_number((String) dbc.rs.getString("user_number"));

				return user;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	/**
	 * 功能：通过用户名查找用户id
	 * 参数：String user_name
	 * 返回值：int
	 * 备注：通过单步调试
	 * @param name
	 * @return
	 */
	public int getUserIdByName(String name) {
		String sql = "select user_id from " + user + " where user_name = '"
				+ name+"'";
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			if (dbc.rs.next()) {
				return dbc.rs.getInt("user_id");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		return 0;
	}

	/**
	 * 功能：用户登录 参数：用户名，密码 返回值：Boolean
	 * 
	 * @param name
	 * @param pswd
	 * @return
	 */
	public boolean login(String name, String pswd) {
		UserVO user = new UserVO();
		AS as = new AS();
		int id = this.getUserIdByName(name);
		user = this.getUserById(id);
		if (user != null) {
			String password = user.getUser_pswd();
			// System.out.println(password);
			if (as.md5(pswd).equals(password)) {
				return true;
			} else
				return false;
		}
		else return false;
	}

	/**
	 * 功能：新增用户 参数：UserVO 返回值：Boolean
	 * 
	 * @param user
	 * @return
	 */

	public boolean setAddUser(UserVO user) {
		String name = user.getUser_name();

		AS as = new AS();
		if (this.getUserIdByName(name) == 0) {
			// This user_name is available
			String sql = "insert into userinfo"
					+ "(user_id,dept_id,user_name,user_true_name,user_pswd,user_sex,user_birthday,user_picture,"
					+ "user_directory,user_office,user_phone,user_mobile,user_group,user_fax,user_mail,user_wage,user_bonus,user_card,user_entrytime"
					+ ",user_workage,user_intro,user_other,user_number)values(null,";
			sql += user.getDept_id() + ",";
			sql += "'" + user.getUser_name() + "',";
			sql += "'" + user.getUser_true_name() + "',";
			sql += "'" + as.md5(user.getUser_pswd()) + "',";
			if (user.isUser_sex() != -1)
				sql += "1,";
			else
				sql += "0,";
			// as.getsqlDateTime();
			
			sql += as.getsqlDateTime(user.getUser_birthday()) + ",";
			sql += "'" + user.getUser_picture() +"',";
			sql += "'" + user.getUser_directory() + "',";
			sql += "'" + user.getUser_office() + "',";
			sql += "'" + user.getUser_phone() + "',";
			sql += "'" + user.getUser_mobile() + "',";
			sql += user.getUser_group() + ",";// int
			sql += "'" + user.getUser_fax() + "',";
			sql += "'" + user.getUser_mail() + "',";
			sql += user.getUser_wage() + ",";
			sql += user.getUser_bonus() + ",";
			sql += "'" + user.getUser_card() + "',";
			sql += as.getsqlDateTime(user.getUser_entrytime()) + ",";
			sql += user.getUser_workage() + ",";
			sql += "'" + user.getUser_intro() + "',";
			sql += "'" + user.getUser_other() + "',";
			sql += "'" + user.getUser_number() + "'";
			sql += ")";
			System.out.println(sql);
			try {
				dbc.rs = dbc.st.executeQuery(sql);
				if (dbc.rs.next()) {
					return true;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			// return false;
		}
		return false;
	}

	/**
	 * 功能：修改密码 参数：用户名，原始密码，新密码 返回值：Boolean
	 */
	public boolean setUpdataPswd(String name, String pswd, String newpswd) {
		boolean flag = false;
		AS as = new AS();
		flag = this.login(name, pswd);

		if (flag) {
			/**
			 * 
			 * update user_info set user_pswd = md5(newpass) where user_name =
			 * name
			 */
			String sql = "update userinfo set user_pswd = '"
					+ as.md5(newpswd) + "' where user_name ='" + name + "'";
//			System.out.println(sql);
			try {
				if (dbc.st.executeUpdate(sql) != 0) {
					return true;
				} else
					return false;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 功能：修改用户信息 参数：UserVO 返回值：Boolean 备注：通过UserVO的user_id获得用户的id，作为用户的识别标志
	 */
	public boolean setUpdateUserInfor(UserVO user) {
		AS as = new AS();
		String sql = "update userinfo set user_id = "+user.getUser_id();

		if (user.getDept_id() != 0)
			sql += ",dept_id = " + user.getDept_id() + ",";
		if (!user.getUser_name().equals("NULL"))
			sql += ",user_name = '" + user.getUser_name() + "'";
		if (!user.getUser_true_name().equals("NULL"))
			sql += ",user_true_name = '" + user.getUser_true_name() + "'";
		if (!user.getUser_pswd().equals("NULL"))
			sql += ",user_pswd = '" + user.getUser_pswd() + "'";
		if (user.isUser_sex() != -1)
			sql += ",user_sex = 1";
		else
			sql += ",user_sex = 0";

		if (!user.getUser_birthday().equals("NULL"))
			sql += ",user_birthday = "+as.getsqlDateTime(user.getUser_birthday());
		if (!user.getUser_picture().equals("NULL"))
			sql += ",user_picture = '" + user.getUser_picture() + "'";
		if (!user.getUser_directory().equals("NULL"))
			sql += ",user_directory = '" + user.getUser_directory() + "'";
		if (!user.getUser_office().equals("NULL"))
			sql += ",user_office = '" + user.getUser_office() + "'";
		if (!user.getUser_phone().equals("NULL"))
			sql += ",user_phone = '" + user.getUser_phone() + "'";
		if (!user.getUser_mobile().equals("NULL"))
			sql += ",user_mobile = '" + user.getUser_mobile() + "'";
		if (user.getUser_group() != 0)
			sql += ",user_group = " + user.getUser_group() ;// int
		if (!user.getUser_fax().equals("NULL"))
			sql += ",user_fax = '" + user.getUser_fax() + "'";
		if (!user.getUser_mail().equals("NULL"))
			sql += ",user_mail = '" + user.getUser_mail() + "'";
		if (user.getUser_wage() != 0.0)
			sql += ",user_wage = " + user.getUser_wage() ;
		if (user.getUser_bonus() != 0.0)
			sql += ",user_bonus = " + user.getUser_bonus();
		if (!user.getUser_card().equals("NULL"))
			sql += ",user_card = '" + user.getUser_card() + "'";
		if (!user.getUser_entrytime().equals("NULL"))
			sql += ",user_entrytime = "+as.getsqlDateTime(user.getUser_entrytime());			
		if (user.getUser_workage() != 0)
			sql += ",user_workage = " + user.getUser_workage();
		if (!user.getUser_intro().equals("NULL"))
			sql += ",user_intro = '" + user.getUser_intro() + "'";
		if (!user.getUser_other().equals("NULL"))
			sql += ",user_other = '" + user.getUser_other() + "'";
		
		if(!user.getUser_number().equals("NULL"))
			sql += ",user_number = '" + user.getUser_number() + "'";

		sql += " where user_id = " + user.getUser_id();
		System.out.println(sql);
		try {
			if (dbc.st.executeUpdate(sql) != 0)
				return true;
			else
				return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 功能：修改用户信息 参数：UserVO 返回值：Boolean
	 */
	
	
	/**
	 * 功能：返回所有用户列表
	 * 参数：null
	 * 返回值：list
	 */
	public List getAllUserList(){
		List list = new ArrayList();
		UserVO user = null;
		SimpleDateFormat simpledate = new SimpleDateFormat();
		java.util.Date date = new java.util.Date();
		String strTime = "";
		AS as = new AS();
		try {
			String sql = "select * from userinfo where 1=1";
//			System.out.println(sql);
			dbc.rs = dbc.st.executeQuery(sql);
			while(dbc.rs.next()){
				user = new UserVO();
				
				if(dbc.rs.getTimestamp("user_birthday")!=null)
					date =dbc.rs.getTimestamp("user_birthday"); 
				
				strTime = simpledate.format(date);
				
//				System.out.println(strTime);
			    user.setUser_birthday(strTime);

				user.setUser_bonus((double) dbc.rs.getDouble("user_bonus"));
				user.setUser_card((String) dbc.rs.getString("user_card"));
				user.setUser_directory((String) dbc.rs
						.getString("user_directory"));
				user.setUser_mail((String) dbc.rs.getString("user_mail"));

				if(dbc.rs.getTimestamp("user_entrytime")!=null)
					date =dbc.rs.getTimestamp("user_entrytime");
				
				strTime = simpledate.format(date);
				user.setUser_entrytime(strTime);
				// user.setUser_entrytime((Date)
				// dbc.rs.getDate("user_entrytime"));

				user.setUser_group((int) dbc.rs.getInt("user_group"));
				user.setUser_name((String) dbc.rs.getString("user_name"));
				user.setUser_mobile((String) dbc.rs.getString("user_mobile"));
				user.setUser_true_name((String) dbc.rs
						.getString("user_true_name"));
				user.setUser_phone((String) dbc.rs.getString("user_phone"));
				user.setUser_other((String) dbc.rs.getString("user_other"));
				user.setUser_picture((String) dbc.rs.getString("user_picture"));
				user.setUser_pswd((String) dbc.rs.getString("user_pswd"));
				user.setUser_wage((double) dbc.rs.getDouble("user_wage"));
				user.setUser_intro(as.Clob_To_String((Clob) dbc.rs.getClob("user_intro")));
				user.setUser_sex((int) dbc.rs.getInt("user_sex"));
				user.setUser_id((int) dbc.rs.getInt("user_id"));
				user.setDept_id((int) dbc.rs.getInt("dept_id"));
				user.setUser_fax((String) dbc.rs.getString("user_fax"));
				user.setUser_office((String) dbc.rs.getString("user_office"));
				user.setUser_workage((int) dbc.rs.getInt("user_workage"));
				user.setUser_number((String) dbc.rs.getString("user_number"));
				
				list.add(user);		
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return list;
		}
		return list;
	}
	
	/**
	 * 功能：由用户id得到用户名真名
	 * 参数：int id
	 * 返回值：String
	 */
	public String getUserTrueNameById(int id){
		String user_true_name = "";
		UserVO uservo = getUserById(id);
		if(uservo!=null){
			return uservo.getUser_true_name();
		}
		else
			return user_true_name;
	}
	/**
	 * 获取用户的group
	 * @param id
	 * @return
	 */
	public int getUserGroupById(int id){
		int user_group = 1;
		UserVO uservo = getUserById(id);
		if(uservo!=null){
			return uservo.getUser_group();
		}
		else
			return user_group;
	}
	
	public int getUserNum(){
		String sql = "select count(*) num from userinfo";
		
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			dbc.rs.next();
			return dbc.rs.getInt("num");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		
	}
	
	
	/**
	 * 功能：获取某个部门的员工列表
	 * 参数：dept id
	 * 返回值：List
	 * 备注：通过测试
	 * @param id
	 * @return
	 */
	public List getUserListByDeptId(int id){
		List list = new ArrayList();
		String sql = "select * from userinfo where dept_id = "+id;
		java.util.Date date = new java.util.Date();
		String strTime = "";
		SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			while(dbc.rs.next()){
				UserVO user = new UserVO();
				AS as = new AS();
				// user.setUser_birthday(dbc.rs.getDate("user_birthday"));	

				if(dbc.rs.getTimestamp("user_birthday")!=null)
					date =dbc.rs.getTimestamp("user_birthday"); 
				
				strTime = simpledate.format(date);
				
//				System.out.println(strTime);
			    user.setUser_birthday(strTime);

				user.setUser_bonus((double) dbc.rs.getDouble("user_bonus"));
				user.setUser_card((String) dbc.rs.getString("user_card"));
				user.setUser_directory((String) dbc.rs
						.getString("user_directory"));
				user.setUser_mail((String) dbc.rs.getString("user_mail"));

				if(dbc.rs.getTimestamp("user_entrytime")!=null)
					date =dbc.rs.getTimestamp("user_entrytime");
				
				strTime = simpledate.format(date);
				user.setUser_entrytime(strTime);
				// user.setUser_entrytime((Date)
				// dbc.rs.getDate("user_entrytime"));

				user.setUser_group((int) dbc.rs.getInt("user_group"));
				user.setUser_name((String) dbc.rs.getString("user_name"));
				user.setUser_mobile((String) dbc.rs.getString("user_mobile"));
				user.setUser_true_name((String) dbc.rs
						.getString("user_true_name"));
				user.setUser_phone((String) dbc.rs.getString("user_phone"));
				user.setUser_other((String) dbc.rs.getString("user_other"));
				user.setUser_picture((String) dbc.rs.getString("user_picture"));
				user.setUser_pswd((String) dbc.rs.getString("user_pswd"));
				user.setUser_wage((double) dbc.rs.getDouble("user_wage"));
				user.setUser_intro(as.Clob_To_String((Clob) dbc.rs.getClob("user_intro")));
				user.setUser_sex((int) dbc.rs.getInt("user_sex"));
				user.setUser_id((int) dbc.rs.getInt("user_id"));
				user.setDept_id((int) dbc.rs.getInt("dept_id"));
				user.setUser_fax((String) dbc.rs.getString("user_fax"));
				user.setUser_office((String) dbc.rs.getString("user_office"));
				user.setUser_workage((int) dbc.rs.getInt("user_workage"));
				user.setUser_number((String) dbc.rs.getString("user_number"));
				list.add(user);
			}
			return list;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public PageHelp queryUser(String condi,int currentPage) throws SQLException {
		dbc=new DbConnection();
		dbc.st =dbc.conn.createStatement();

		int recordcount=0;
		String sql="select count(*) recordcount from userinfo where 1=1 "+condi+"";
		dbc.rs=dbc.st.executeQuery(sql);
		if(dbc.rs.next()){
			recordcount=dbc.rs.getInt("recordcount");
		}
		int startNum=(currentPage - 1) * PageHelp.pagesize+1;//由于数据库中没有第0条记录所以要进行+1修正
		int endNum= currentPage* PageHelp.pagesize+1;//查询结束行号
		String pagesql="select * from (" +
										"select a.* ,rownum rc from(" +
																	"select * from userinfo where 1=1 "+condi+" order by user_id desc" +
																") a where rownum<"+endNum+"" +
										") b where rc >="+startNum+"";	
		dbc.rs=dbc.st.executeQuery(pagesql);
		List users=new ArrayList();
		UserVO uservo =null;
		while(dbc.rs.next()){
			uservo= new UserVO();
			uservo.setUser_id(dbc.rs.getInt("user_id"));
			uservo.setDept_id(dbc.rs.getInt("dept_id"));
			uservo.setUser_name(dbc.rs.getString("user_name"));
			uservo.setUser_true_name(dbc.rs.getString("user_true_name"));
			uservo.setUser_pswd(dbc.rs.getString("user_pswd"));
			uservo.setUser_sex(dbc.rs.getInt("user_sex"));
			uservo.setUser_birthday(dbc.rs.getString("user_birthday"));
			uservo.setUser_picture(dbc.rs.getString("user_picture"));
			uservo.setUser_directory(dbc.rs.getString("user_directory"));
			uservo.setUser_office(dbc.rs.getString("user_office"));
			uservo.setUser_phone(dbc.rs.getString("user_phone"));
			uservo.setUser_mobile(dbc.rs.getString("user_mobile"));
			uservo.setUser_group(dbc.rs.getInt("user_group"));
			uservo.setUser_fax(dbc.rs.getString("user_fax"));
			uservo.setUser_mail(dbc.rs.getString("user_mail"));
			uservo.setUser_wage(dbc.rs.getInt("user_wage"));
			uservo.setUser_bonus(dbc.rs.getInt("user_bonus"));
			uservo.setUser_card(dbc.rs.getString("user_card"));
			uservo.setUser_entrytime(dbc.rs.getString("user_entrytime"));
			uservo.setUser_workage(dbc.rs.getInt("user_workage"));
			uservo.setUser_intro(dbc.rs.getString("user_intro"));
			uservo.setUser_other(dbc.rs.getString("user_other"));
			uservo.setUser_number(dbc.rs.getString("user_number"));
			users.add(uservo);
		}
		PageHelp pagehelp= new PageHelp(currentPage,recordcount,condi,users);
		return pagehelp;
	}
/*
	public static void main(String[] args){
		UserDAO userdao = new UserDAO();
		List list = new ArrayList();
		list = userdao.getUserListByDeptId(44);
		UserVO user = new UserVO();
		Iterator it = list.iterator();
		System.out.println("The list:");
		while(it.hasNext()){
			user = (UserVO)it.next();
			System.out.println(user.getUser_name());
		}
		int i;
		i = userdao.getUserNum();
		System.out.println(i);
	}

*/


}
