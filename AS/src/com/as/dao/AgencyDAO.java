package com.as.dao;

import com.as.*;
import com.as.db.DbConnection;
import com.as.function.AS;
import com.as.vo.AgencyAllVO;
import com.as.vo.AgencyVO;
import com.as.vo.Agency_listVO;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
 

public class AgencyDAO {

	private DbConnection dbc;
	private int res;
	private Agency_listVO agency_listvo;
	private AgencyAllVO agencyallvo;
	private AgencyVO agencyvo;
	private AS as;
	
	public AgencyDAO(){
		dbc = new DbConnection();
	}
	/**
	 * 函数：获得agency_listVO的列表
	 * 就是列出 某个事情对应的授予权限和url的设置
	 * 返回值：agency_listVO的列表
	 * @return
	 */
	public List getAgencyList(){
		List list = new ArrayList();
		
		String sql = "select * from agency_list";
	 
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			while(dbc.rs.next()){
			   agency_listvo = new Agency_listVO();
			   agency_listvo.setAgency_list_id(dbc.rs.getInt("agency_list_id"));
			   agency_listvo.setAg_list_add(dbc.rs.getInt("ag_list_add"));
			   agency_listvo.setAg_list_delete(dbc.rs.getInt("ag_list_delete"));
	           agency_listvo.setAg_list_name(dbc.rs.getString("ag_list_name"));
	           agency_listvo.setAg_list_select(dbc.rs.getInt("ag_list_select"));
	           agency_listvo.setAg_list_update(dbc.rs.getInt("ag_list_update"));
	           agency_listvo.setAg_list_url(dbc.rs.getString("ag_list_url"));
	           
	           list.add(agency_listvo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	/**
	 * 删除某个代办的事情的种类，比如删除站内短信代办，所有用户将不能进行代办
	 * 参数：agency_listvo_id
	 * 返回值：0 或者 1
	 * @param args
	 */
	public int  setDelAgencyList(int agency_list_id){
		 res = 0;
		 String sql = "delete from agency_list where agency_list_id = "+agency_list_id;
		 
		 try {
			res = dbc.st.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 return res;
	}
	
	/**
	 * 插入新的代办种类，比如新加了某种代办在这里设置所有用户的权限和url
	 * 参数：agency_listvoVO
	 * 返回值：0 或者 1
	 * @param args
	 */
	public int setInsertAgencyList(Agency_listVO agency_listvo){
		int res = 0;
	    String sql = "insert into agency_list values(agency_list_seq.nextval,'"+agency_listvo.getAg_list_name()+"','"
	            +agency_listvo.getAg_list_url()+"',"+agency_listvo.getAg_list_add()+","
	    		+agency_listvo.getAg_list_delete()+","+agency_listvo.getAg_list_update()+","
	    		+agency_listvo.getAg_list_select()+")";
//	    String sql = "insert into agency_listvo values(agency_listvo_seq.nextval,'网络硬盘','disk/index.jsp',1,1,1,0)";
	    System.out.println(sql);
		try {
			res = dbc.st.executeUpdate(sql);
			System.out.println(sql);
		} catch (SQLException e) {
		 
			e.printStackTrace();
		}
		return  res;
	}
	/**
	 * 更新
	 * 参数 
	 * @param args
	 */
	public int setUpdateAgencyList(Agency_listVO agency_listvo){
		res = 0; 
		String sql="update agency_list set ag_list_name='"
			+agency_listvo.getAg_list_name()+"',ag_list_url='"
			+agency_listvo.getAg_list_url()+"',ag_list_add="
			+agency_listvo.getAg_list_add()+",ag_list_delete="
			+agency_listvo.getAg_list_delete()+",ag_list_update="
			+agency_listvo.getAg_list_update()+",ag_list_select="
			+agency_listvo.getAg_list_select()+" where agency_list_id="
			+agency_listvo.getAgency_list_id();
		System.out.println(sql);
		try {
			res = dbc.st.executeUpdate(sql);
		} catch (SQLException e) {
		 
			e.printStackTrace();
		}
		return res;
	}
	/**
	 * 函数：用户添加新的代办，加入agency表
	 * 参数：agencyvo
	 * 返回值：0或者1
	 */
	public int setInsertAgency(AgencyVO agencyvo){
		String sql =" insert into agency values (null,"+agencyvo.getDoer_id()+","
		+agencyvo.getUser_id()+",'"
		+agencyvo.getAgency_intro()
		+"',to_date('"+agencyvo.getAgency_begin()
		+"','yyyy-mm-dd hh24-mi-ss'),to_date('"
		+agencyvo.getAgency_end()+"','yyyy-mm-dd hh24-mi-ss'),"
		+agencyvo.getAgency_state()+")";
		
		try {
			res = dbc.st.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	/**
	 * 删除agency
	 * 参数：删除一个代办
	 * 返回值：true 或者false
	 * @param args
	 */
	public int setDelAgency(int agency_id){
		
		String sql = "delete from agency where agency_id="+agency_id;
		try {
			res = dbc.st.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	/**
	 * 获得我希望别人代办的列表
	 * 参数：user_id
	 * @param args
	 */
	public List getMyAgencyList(int user_id){
		List list = new ArrayList();
		AS as = new AS();
		
		//String sql = "select userinfo.user_name as doer_name,userinfo.user_id,agency.* from userinfo,agency where agency.doer_id=userinfo.user_id and userinfo.user_id="+user_id;
//		String sql = "select * from agency where user_id="+user_id;
		String sql ="select userinfo.*,agency.* from userinfo,agency where agency.doer_id = userinfo.user_id and agency.user_id="+user_id;
		
		try {
			dbc.rs = dbc.st.executeQuery(sql);
		    while(dbc.rs.next()){
		    	agencyallvo = new AgencyAllVO();
		    	agencyallvo.setAgency_id(dbc.rs.getInt("agency_id"));
		    	agencyallvo.setUser_id(dbc.rs.getInt("user_id"));
		    	agencyallvo.setDoer_id(dbc.rs.getInt("doer_id"));
		    	agencyallvo.setDoer_true_name(dbc.rs.getString("user_true_name"));
		    	agencyallvo.setAgency_state(dbc.rs.getInt("agency_state"));

		    	
		    	Date date = (Date)dbc.rs.getTimestamp("agency_begin");
				String strTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(date);
				agencyallvo.setAgency_begin(strTime);
				
				date = (Date)dbc.rs.getTimestamp("agency_end");
				strTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(date);
				agencyallvo.setAgency_end(strTime);
				
				agencyallvo.setAgency_intro(as.Clob_To_String(dbc.rs.getClob("agency_intro")));
				
				list.add(agencyallvo);
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 函数：获取别人给我的代办任务列表
	 * 参数：user_id我的user_id
	 * 返回值：list
	 */
	public List getOtherAgencyList(int user_id){
		List list = new ArrayList();
		AS as = new AS();
		
		//String sql = "select userinfo.user_name as doer_name,userinfo.user_id,agency.* from userinfo,agency where agency.doer_id=userinfo.user_id and userinfo.user_id="+user_id;
//		String sql = "select * from agency where user_id="+user_id;
		String sql ="select userinfo.*,agency.* from userinfo,agency where agency.doer_id = userinfo.user_id and agency.doer_id="+user_id;
		
		try {
			dbc.rs = dbc.st.executeQuery(sql);
		    while(dbc.rs.next()){
		    	agencyallvo = new AgencyAllVO();
		    	agencyallvo.setAgency_id(dbc.rs.getInt("agency_id"));
		    	agencyallvo.setUser_id(dbc.rs.getInt("user_id"));
		    	agencyallvo.setDoer_id(dbc.rs.getInt("doer_id"));
		    	agencyallvo.setUser_true_name(dbc.rs.getString("user_true_name"));
		    	agencyallvo.setAgency_state(dbc.rs.getInt("agency_state"));

		    	
		    	Date date = (Date)dbc.rs.getTimestamp("agency_begin");
				String strTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(date);
				agencyallvo.setAgency_begin(strTime);
				
				date = (Date)dbc.rs.getTimestamp("agency_end");
				strTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(date);
				agencyallvo.setAgency_end(strTime);
				
				agencyallvo.setAgency_intro(as.Clob_To_String(dbc.rs.getClob("agency_intro")));
				
				list.add(agencyallvo);
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 获取某个agency_id对应的所有的权限的描述
	 * 参数：agency_id
	 * 返回值list
	 * @param args
	 */
	public List getAgencyPriListByAgencyId(int agency_id){
		List list = new ArrayList();
		String sql = "select agency_list.*,agency_pri.* From agency_list,agency_pri Where agency_pri.agency_list_id=agency_list.agency_list_id and agency_id="+agency_id;
		
		try {
			dbc.rs = dbc.st.executeQuery(sql);
			while(dbc.rs.next()){
				AgencyAllVO agencyallvo = new AgencyAllVO();
				
				agencyallvo.setAg_list_name(dbc.rs.getString("ag_list_name"));
				agencyallvo.setAg_list_url(dbc.rs.getString("ag_list_url"));
				agencyallvo.setAgency_pri_id(dbc.rs.getInt("agency_pri_id"));
				agencyallvo.setAgency_add(dbc.rs.getInt("agency_add"));
				agencyallvo.setAgency_delete(dbc.rs.getInt("agency_delete"));
				agencyallvo.setAgency_update(dbc.rs.getInt("agency_update"));
				agencyallvo.setAgency_select(dbc.rs.getInt("agency_select"));
				list.add(agencyallvo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * agency的状态修改，改成0，1，2，3，
	 * 0表示还未接受
	 * 1表示放弃
	 * 2表接受，正在做
	 * 3表示完成
	 * 其他表示错误
	 * @param args
	 */
   public int setUpdateAgencyState(int agency_id,int agency_state){
		
		String sql = "update agency set agency_state = "+agency_state+" where agency_id="+agency_id;
		try {
			res = dbc.st.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	public static void main(String[] args) {
		AgencyDAO agencydao = new AgencyDAO();
		AgencyAllVO agencyallvo = new AgencyAllVO();
		Agency_listVO agency_listvo  = new Agency_listVO();
//		agency_listvo.setAgency_list_id(5);
//		agency_listvo.setAg_list_name("网络硬盘111");
//		agency_listvo.setAg_list_url("disk/index.jsp");
//		agency_listvo.setAg_list_add(1);
//		agency_listvo.setAg_list_delete(1);
//		agency_listvo.setAg_list_update(1);
//		agency_listvo.setAg_list_select(0);
//		//获得列表测试
////	//	agencydao.testInsertDate();
////		List list = agencydao.getAgencyList();
////		
////		Agency_listVO agency_listvo = new Agency_listVO();
////		Iterator it = list.iterator();
////		while(it.hasNext()){
////			agency_listvo = (Agency_listVO)it.next();
////			System.out.println(agency_listvo.getagency_listvo_id());
////		 
////		}
////		System.out.println("nothing!");
//		//del测试
//		int res= agencydao.setDelAgencyList(22);
//		int res = agencydao.setUpdateAgencyList(agency_listvo);
//		int res= 0;
//		res = agencydao.setInsertAgencyList(agency_listvo);
//		System.out.println(res);
//		agencydao.getMyAgencyList(41);
//		List list = agencydao.getMyAgencyList(41);
		List list = agencydao.getAgencyPriListByAgencyId(101);
		Iterator it = list.iterator();
		while(it.hasNext()){
			agencyallvo = (AgencyAllVO)it.next();
			System.out.println(agencyallvo.getAg_list_name());
			
		}

	}

}
