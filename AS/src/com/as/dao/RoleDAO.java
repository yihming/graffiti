package com.as.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.as.*;
import com.as.db.*;
import com.as.function.AS;
import com.as.vo.RoleVO;

public class RoleDAO {

	private UserDAO userdao;
	private DbConnection dbc;
	private ResultSet rs;
	private int res;//插入删除的时候的返回值
	private AS as;
	
    public RoleDAO(){
		dbc = new DbConnection();
		as = new AS();
	}
	/**
	 * 函数：获得role的列表
	 * 参数：no
	 * 返回值：list<RoleVo>
	 * @param args
	 */
	
	public List getRoleList(){
	    List list = new ArrayList();
	   
	    String sql = "select * from role";
	    try {
			dbc.rs = dbc.st.executeQuery(sql);
			while(dbc.rs.next()){
				 RoleVO rolevo = new RoleVO();
		       rolevo.setRole_id(dbc.rs.getInt("role_id"));
		       rolevo.setRole_name(dbc.rs.getString("role_name"));
		       rolevo.setAccount_add_del(dbc.rs.getInt("account_add_del"));
		       rolevo.setAccount_update(dbc.rs.getInt("account_update"));
		       rolevo.setAssign_work(dbc.rs.getInt("assign_work"));
		       rolevo.setDevice_mag(dbc.rs.getInt("device_mag"));
		       rolevo.setMeeting_agree(dbc.rs.getInt("meeting_agree"));
		       rolevo.setNew_power1(dbc.rs.getInt("new_power1"));
		       rolevo.setNew_power2(dbc.rs.getInt("new_power2"));
		       
		       list.add(rolevo);
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return list;
	}
	/**
	 * 更新role表
	 * 参数rolevo
	 * 返回值：int  0 或者 1 
	 * @param args
	 */
	public int setUpdateRole(RoleVO rolevo){
		String sql = "update role set role_name='"+
		rolevo.getRole_name()+"',meeting_agree="+
		rolevo.getMeeting_agree()+",device_mag="+
		rolevo.getDevice_mag()+",assign_work="+
		rolevo.getAssign_work()+",account_add_del="+
		rolevo.getAccount_add_del()+",account_update="+
		rolevo.getAccount_update()+",new_power1="+
		rolevo.getNew_power1()+",new_power2="+
		rolevo.getNew_power2()+"  where role_id="+rolevo.getRole_id();
		System.out.println(sql);
		try {
			res = dbc.st.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	/**
	 * 新建角色
	 * 参数rolevo
	 */
	public int setInsertRole(RoleVO rolevo){
		String sql="insert into role(role_name,meeting_agree,device_mag,assign_work,account_add_del,account_update,new_power1,new_power2) values ('"+
		         rolevo.getRole_name()+"',"+
		         rolevo.getMeeting_agree()+","+
		         rolevo.getDevice_mag()+","+
		         rolevo.getAssign_work()+","+
		         rolevo.getAccount_add_del()+","+
		         rolevo.getAccount_update()+","+
		         rolevo.getNew_power1()+","+
		         rolevo.getNew_power2()+")";
		System.out.println(sql);
		try {
			res = dbc.st.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	/**
	 * 删除角色
	 * @param args
	 */
	public int setDelRole(int role_id){
		String sql = "delete from role where role_id="+role_id;
		try {
			res = dbc.st.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	/**
	 * 获取用户列表和用户的所有的角色组
	 * @param args
	 */
	public List getUserRoleList(){
		List list = new ArrayList();
		String sql = "select role.*,user_role_rs.*,userinfo.* from userinfo,role,user_role_rs where userinfo.user_id=user_role_rs.user_id and user_role_rs.role_id=role.role_id order by userinfo.user_id";
//		String sql = "select userinfo.*,user_role_rs.* from userinfo,user_role_rs where userinfo.user_id=user_role_rs.user_id";
		try {
			dbc.rs  = dbc.st.executeQuery(sql);
			System.out.println(sql);
			while(dbc.rs.next()){
				RoleVO rolevo = new RoleVO();
				rolevo.setUser_true_name(dbc.rs.getString("user_true_name"));
				rolevo.setUser_name(dbc.rs.getString("role_name"));
				rolevo.setUser_id(dbc.rs.getInt("user_id"));
				rolevo.setUser_role_rs_id(dbc.rs.getInt("user_role_rs_id"));
				rolevo.setRole_id(dbc.rs.getInt("role_id"));
				
			    rolevo.setRole_name(dbc.rs.getString("role_name"));
			    rolevo.setAccount_add_del(dbc.rs.getInt("account_add_del"));
			    rolevo.setAccount_update(dbc.rs.getInt("account_update"));
			    rolevo.setAssign_work(dbc.rs.getInt("assign_work"));
			    rolevo.setDevice_mag(dbc.rs.getInt("device_mag"));
			    rolevo.setMeeting_agree(dbc.rs.getInt("meeting_agree"));
			    rolevo.setNew_power1(dbc.rs.getInt("new_power1"));
			    rolevo.setNew_power2(dbc.rs.getInt("new_power2"));
			    
			    System.out.println(dbc.rs.getString("role_name"));
			    list.add(rolevo);
			   
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	/**
	 * 删除某人的某个角色
	 * @param args
	 */
	public int setDelUserRole(int user_role_rs_id){
		String sql = "delete from user_role_rs where user_role_rs_id="+user_role_rs_id;
		try {
			res = dbc.st.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	/**
	 * 插入用户角色，就是插到关系表中
	 * @param args
	 */
	public int setInsertUserRole(int user_id,int role_id){
		String sql = "Insert Into user_role_rs Values(Null,"+user_id+","+role_id+")";
		System.out.println(sql);
		try {
			res = dbc.st.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	/**
	 * 获得用户是否对某事情有权限
	 * 参数：user_id
	 * 
	 * 这个特别重要！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
	 * @param args
	 */
	public RoleVO getUserRole(int user_id){
		String sql = "select role.*,user_role_rs.*,userinfo.* from userinfo,role,user_role_rs where userinfo.user_id=user_role_rs.user_id and user_role_rs.role_id=role.role_id and userinfo.user_id="+user_id;
		System.out.println(sql);
		List list = new ArrayList();
		try {
			dbc.rs  = dbc.st.executeQuery(sql);
			System.out.println(sql);
			while(dbc.rs.next()){
				RoleVO rolevo = new RoleVO();
				rolevo.setUser_true_name(dbc.rs.getString("user_true_name"));
				rolevo.setUser_name(dbc.rs.getString("role_name"));
				rolevo.setUser_id(dbc.rs.getInt("user_id"));
				rolevo.setUser_role_rs_id(dbc.rs.getInt("user_role_rs_id"));
				rolevo.setRole_id(dbc.rs.getInt("role_id"));
				
			    rolevo.setRole_name(dbc.rs.getString("role_name"));
			    rolevo.setAccount_add_del(dbc.rs.getInt("account_add_del"));
			    rolevo.setAccount_update(dbc.rs.getInt("account_update"));
			    rolevo.setAssign_work(dbc.rs.getInt("assign_work"));
			    rolevo.setDevice_mag(dbc.rs.getInt("device_mag"));
			    rolevo.setMeeting_agree(dbc.rs.getInt("meeting_agree"));
			    rolevo.setNew_power1(dbc.rs.getInt("new_power1"));
			    rolevo.setNew_power2(dbc.rs.getInt("new_power2"));
			    
			    System.out.println(dbc.rs.getString("role_name"));
			    list.add(rolevo);
			   
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RoleVO rolevo  = new RoleVO();
		RoleVO rolevo1 = new RoleVO();
		Iterator it = list.iterator();
		while(it.hasNext()){
			rolevo = (RoleVO)it.next();
		    if(rolevo.getAccount_add_del()!=0){
		    	rolevo1.setAccount_add_del(1);
		    }
		    if(rolevo.getAssign_work()!=0){
		    	rolevo1.setAssign_work(1);
		    }
		    if(rolevo.getDevice_mag()!=0){
		    	rolevo1.setDevice_mag(1);
		    }
		    if(rolevo.getMeeting_agree()!=0){
		    	rolevo1.setMeeting_agree(1);
		    }
		    if(rolevo.getAccount_add_del()!=0){
		    	rolevo1.setAccount_add_del(1);
		    }
		    if(rolevo.getNew_power1()!=0){
		    	rolevo1.setNew_power1(1);
		    }
		    if(rolevo.getNew_power2()!=0){
		    	rolevo1.setNew_power1(1);
		    }
		  
		}
		return rolevo1;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
         RoleDAO roledao = new RoleDAO();
         RoleVO rolevo = new RoleVO();
//         List list = roledao.getUserRoleList();
//         Iterator it = list.iterator();
//         while(it.hasNext()){
//        	 rolevo = (RoleVO)it.next();
//        	 System.out.println(rolevo.getAssign_work());
//         }
	     rolevo = roledao.getUserRole(48);
	     System.out.print(rolevo.getAccount_add_del());
	     System.out.print(rolevo.getAssign_work());
	}

}

