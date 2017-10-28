package com.as.dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.as.db.DbConnection;
import com.as.function.AS;
import com.as.vo.FriendClassVO;
import com.as.vo.FriendVO;

public class FriendDAO {
	private DbConnection dbc;

	private String friend = "friend";

	private String friendclass = "friendclass";

	public FriendDAO() {
		dbc = new DbConnection();
	}

	/**
	 * 功能：通过id号找到好友分类 参数：id 返回值：friendclassvo 备注：通过测试
	 * 
	 * @param id
	 * @return
	 */
	public FriendClassVO getFriendclassById(int id) {
		String sql = "select * from " + friendclass + " where f_class_id = "
				+ id;

		FriendClassVO friendclassvo = new FriendClassVO();
		// System.out.println(sql);
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			if (dbc.rs.next()) {
				friendclassvo.setF_class_id(id);
				friendclassvo.setF_class_name(dbc.rs.getString("f_class_name"));
				friendclassvo.setUser_id(dbc.rs.getInt("user_id"));
			} else {
				return friendclassvo;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return friendclassvo;
		}
		return friendclassvo;

	}

	/**
	 * 功能：添加好友分类 参数：friendclassvo 返回值：Boolean 备注：通过测试
	 * 
	 * @param friendclassvo
	 * @return
	 */
	public boolean setaddFriendClas(FriendClassVO friendclassvo) {
		String f_class_name = "";
		int user_id = 0;

		f_class_name = friendclassvo.getF_class_name();
		user_id = friendclassvo.getUser_id();
		String sql = "insert into " + friendclass
				+ "(f_class_id,user_id,f_class_name) " + "values(null,"
				+ user_id + ",'" + f_class_name + "')";
		try {
			int re = dbc.st.executeUpdate(sql);
			if (re != 0) {
				return true;
			} else
				return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 功能：删除某个好友分类 参数：id 返回值：Boolean 备注：通过测试
	 */
	public boolean setDelFriendClass(int id) {
		String sql = "delete from " + friendclass + " where f_class_id = " + id;
		System.out.println(sql);
		int re;
		try {
			re = dbc.st.executeUpdate(sql);
			if (re == 0)
				return false;
			else
				return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 功能：更新FriendClass 参数：FriendClassVO 返回值：Boolean 备注：通过测试
	 * 
	 * @param args
	 */
	public boolean setUpdateFriendClass(FriendClassVO friendclassvo) {
		String f_class_name = "";

		int user_id = 0;
		int f_class_id = 0;
		String sql = "update friendclass set ";

		if (friendclassvo.getF_class_name() != null) {
			f_class_name = friendclassvo.getF_class_name();
			sql += "f_class_name = '" + f_class_name + "'";
		}

		if (friendclassvo.getUser_id() != 0) {
			user_id = friendclassvo.getUser_id();
			sql += ",user_id = " + user_id;
		}

		sql += "where f_class_id = " + friendclassvo.getF_class_id();
		try {
			int res = dbc.st.executeUpdate(sql);
			if (res != 0)
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
	 * 功能：查找用户的所有好友分类 参数：用户id 返回值：List 备注：通过测试
	 * 
	 * @param id
	 * @return
	 */
	public List getFriendClassByUserId(int id) {
		String sql = "select * from friendclass where user_id = " + id;
		List list = new ArrayList();
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			while (dbc.rs.next()) {
				FriendClassVO friendclassvo = new FriendClassVO();
				friendclassvo.setF_class_id((int) dbc.rs.getInt("f_class_id"));
				friendclassvo.setF_class_name(dbc.rs.getString("f_class_name"));
				friendclassvo.setUser_id(dbc.rs.getInt("user_id"));
				list.add(friendclassvo);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			return list;
		}

	}

	/**
	 * 功能：获得某个好友类的所有好友 参数：好友类的id 返回值：List 备注：通过测试
	 * 
	 * @param id
	 * @return
	 */
	public List getFriendByClassId(int id) {
		String sql = "select * from friend where f_class_id = " + id;
		List list = new ArrayList();
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
		AS as = new AS();

		try {
			dbc.rs = dbc.st.executeQuery(sql);
			while (dbc.rs.next()) {
				FriendVO friendvo = new FriendVO();
				friendvo.setF_class_id(id);
				friendvo.setFriend_id(dbc.rs.getInt("friend_id"));
				friendvo.setFriend_name(dbc.rs.getString("friend_name"));
				friendvo.setFriend_sex(dbc.rs.getInt("friend_sex"));
				friendvo.setFriend_phone(dbc.rs.getString("friend_phone"));
				friendvo.setFriend_addr(dbc.rs.getString("friend_addr"));

				if (dbc.rs.getTimestamp("friend_birth") != null) {
					Date date = new Date();
					date = dbc.rs.getTimestamp("friend_birth");
					friendvo.setFriend_birth(simple.format(date));
				}

				friendvo.setFriend_mail(dbc.rs.getString("friend_mail"));
				friendvo.setFriend_intro(as.Clob_To_String(dbc.rs
						.getClob("friend_intro")));
				list.add(friendvo);
			}
			return list;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return list;
		}

	}

	/**
	 * 功能：读取好友 参数：好友id 返回值：FriendVO 备注：通过测试
	 * 
	 * @param id
	 * @return
	 */
	public FriendVO getFriendById(int id) {
		String sql = "select * from friend where friend_id = " + id;
		FriendVO friendvo = new FriendVO();
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
		AS as = new AS();
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			if (dbc.rs.next()) {
				friendvo.setF_class_id(dbc.rs.getInt("f_class_id"));
				friendvo.setFriend_id(dbc.rs.getInt("friend_id"));
				friendvo.setFriend_name(dbc.rs.getString("friend_name"));
				friendvo.setFriend_sex(dbc.rs.getInt("friend_sex"));
				friendvo.setFriend_phone(dbc.rs.getString("friend_phone"));
				friendvo.setFriend_addr(dbc.rs.getString("friend_addr"));

				if (dbc.rs.getTimestamp("friend_birth") != null) {
					Date date = new Date();
					date = dbc.rs.getTimestamp("friend_birth");
					friendvo.setFriend_birth(simple.format(date));
				}

				friendvo.setFriend_mail(dbc.rs.getString("friend_mail"));
				friendvo.setFriend_intro(as.Clob_To_String(dbc.rs
						.getClob("friend_intro")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			return friendvo;
		}

	}
	
	/**
	 * 功能：更新某个好友
	 * 参数：FriendVO
	 * 返回值：Boolean
	 * 备注：通过FriendVO.firend_id标志好友，通过测试
	 * @param friendvo
	 * @return
	 */

	public boolean setUpdateFriend(FriendVO friendvo) {
		AS as = new AS();
		int friend_id = 0;
		friend_id = friendvo.getFriend_id();
		if(friend_id == 0) return false;
		int f_class_id = 0;
		f_class_id = friendvo.getF_class_id();
		String friend_name = friendvo.getFriend_name();
		int friend_sex = -1;
		friend_sex = friendvo.getFriend_sex();
		String friend_phone = friendvo.getFriend_phone();
		String friend_addr = friendvo.getFriend_addr();
		String friend_birth = friendvo.getFriend_birth();
		String friend_mail = friendvo.getFriend_mail();
		String friend_intro = friendvo.getFriend_intro();
		
		String sql = "update friend set ";
		
		if(f_class_id != 0 )
			sql += "f_class_id = "+f_class_id;
		if(friend_name!=null)
			sql += ",friend_name = '"+friend_name+"'";
		if(friend_sex != -1)
			sql += ",friend_sex ="+friend_sex;
		if(friend_phone != null)
			sql += ",friend_phone = '"+friend_phone+"'";
		if(friend_addr != null)
			sql += ",friend_addr = '"+friend_addr+"'";
		if(friend_birth != null)
			sql += ",friend_birth = "+as.getsqlDateTime(friend_birth);
		if(friend_mail != null)
			sql += ",friend_mail = '"+friend_mail+"'";
		if(friend_intro != null)
			sql += ",friend_intro = '"+friend_intro+"'";
		
		sql += "where friend_id = "+friend_id;
		
		int res;
		try {
			res = dbc.st.executeUpdate(sql);
			if(res != 0)
				return true;
			else return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 功能：新增好友
	 * 参数：FriendVO
	 * 返回值：Boolean
	 * 备注：通过测试
	 * 
	 * @param args
	 */
	
	public boolean setaddFriend(FriendVO friendvo){
		AS as = new AS();
		String sql = "insert into friend (friend_id,f_class_id,friend_name,friend_sex,friend_phone" +
				",friend_addr,friend_birth,friend_mail,friend_intro) values (null,";
		sql += friendvo.getF_class_id()+",";
		sql += "'"+friendvo.getFriend_name()+"',";
		sql += friendvo.getFriend_sex()+",";
		sql += "'"+friendvo.getFriend_phone() + "',";
		sql += "'"+friendvo.getFriend_addr()+"',";
		sql += as.getsqlDateTime(friendvo.getFriend_birth())+",";
		sql += "'" + friendvo.getFriend_mail() + "',";
		sql += "'" + friendvo.getFriend_intro() + "'";
		
		sql += ")";
		
		int res;
		try {
			res = dbc.st.executeUpdate(sql);
			if(res != 0) return true;
			else return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 功能：通过好友id删除一个好友
	 * 参数：in id
	 * 返回值：Boolean
	 * 备注：通过测试 
	 * @param id
	 * @return
	 */
	public boolean setdelFriendById(int id){
		String sql = "delete from friend where friend_id = "+id;
		int res;
		try {
			res = dbc.st.executeUpdate(sql);
			if(res != 0 )return true;
			else return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
	}
	
	

	public static void main(String[] args) {
		FriendClassVO friendclassvo = new FriendClassVO();
		FriendVO friendvo = new FriendVO();
		FriendDAO frienddao = new FriendDAO();
		friendvo = frienddao.getFriendById(5);
		System.out.println(friendvo.getFriend_birth());
		friendvo.setFriend_intro("I love this girl....Kai");
		friendvo.setFriend_name("锅盖");
		friendvo.setFriend_birth("1987-11-30");
		
		if(frienddao.setdelFriendById(22))
			System.out.println("Ok");
		else System.out.println("NO");
	}

}
