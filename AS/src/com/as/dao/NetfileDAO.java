
package com.as.dao;


import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.as.db.DbConnection;
import com.as.function.AS;

import com.as.vo.DeptVO;
import com.as.vo.FolderVO;
import com.as.vo.NetfileVO;

public class NetfileDAO {
	
		private DbConnection dbc;

		private String  netfile= "netfile";

		/**
		 * 功能：NetFileDAO构造函数，连接数据库 
		 * 参数：null 
		 * 返回值：void
		 * @return 
		 */
		public  void NetFileDAO() {
			dbc = new DbConnection();
		}
		/**
		 * 功能:根据user_id查看文件夹
		 * 参数：user_id
		 * 返回值：List
		 */
		public List getFolderByuser_id(int user_id){
			List folderList =new ArrayList();
			dbc=new DbConnection();
			String sql=new String();
			sql="select * from folder where user_id="+user_id;
			try {
				dbc.rs=dbc.st.executeQuery(sql);
				while(dbc.rs.next()){
					FolderVO foldervo=new FolderVO();
					foldervo.setFolder_id(dbc.rs.getInt("folder_id"));
					foldervo.setFolder_name(dbc.rs.getString("folder_name"));
					foldervo.setUser_id(dbc.rs.getInt("user_id"));
					folderList.add(foldervo);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dbc.closeConnection();
			return folderList;
		}
		/**
		 * 功能：根据网络文件ID查找网络文件表 
		 * 参数：folder_id
		 * 返回值：NetFileVO类
		 */
		public List getNetFileByfolder_id(int folder_id) {
			dbc=new DbConnection();
			String sql=new String();
			List fileList =new ArrayList();
			 sql = "select * from " + netfile + " where folder_id = " + folder_id;

			try {
				dbc.rs = dbc.st.executeQuery(sql);
				while (dbc.rs.next()) {
					NetfileVO netfilevo = new NetfileVO();
					netfilevo.setNetfile_id(dbc.rs.getInt("netfile_id"));
					netfilevo.setNetfile_name((String) dbc.rs.getString("netfile_name"));
					netfilevo.setFolder_id((int)dbc.rs.getInt("folder_id"));
					netfilevo.setNetfile_type((String) dbc.rs.getString("netfile_type"));
					netfilevo.setNetfile_path((String) dbc.rs.getString("netfile_path"));
					netfilevo.setNetfile_share((int) dbc.rs.getInt("netfile_share"));
					
				     fileList.add(netfilevo);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dbc.closeConnection();
			return fileList;
		}
		
		/**
		 * 功能：根据网络文件名称查找网络文件ID
		 * 参数：字符串name
		 * 返回值：int型 序号
		 */
		public int getNetfileIdByname(String name) {
			String sql = "select netfile_id from " + netfile 
			             + " where netfile_name = "+ name;
			try {
				dbc.rs = dbc.st.executeQuery(sql);
				if (dbc.rs.next()) {
					return dbc.rs.getInt("netfile_id");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
			return 0;
		}
	    
		/**
		 * 功能：增加一个网络文件
		 * 参数：netfileVO类型对象
		 * 返回值：布尔型
		 */
		public boolean setAddNetfile(int folder_id,String netfile_name,String netfile_path) {
		         
			   String sql = "insert into  netfile (netfile_id,folder_id,netfile_name,netfile_path)"
			             +"values(NULL,"+folder_id+","+netfile_name+","+netfile_path+")";
			try {
				System.out.println(sql);
					dbc.st.executeUpdate(sql);
					return true;
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			return false ;
		}
       

		/**
		 * 功能：删除一个网络文件
		 * 参数：网络文件ID
		 * 返回值：布尔型
		 */
		public boolean setDelNetfile(int netfile_id) {
			int i = 0;
			boolean setDelFlag=false;
			String sql=new String();
			dbc =new DbConnection();
			 sql = "delete from " + netfile+" where netfile_id=" + netfile_id;
			 System.out.println("sql:"+sql);
			try {
				i = dbc.st.executeUpdate(sql);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
			
			if(i>0)
				setDelFlag = true;
System.out.println(setDelFlag);
           dbc.closeConnection();
			return setDelFlag;

		}
		/**
		 * 功能：删除一个文件夹
		 * 参数：folder_id
		 * 返回值：布尔型
		 */
		public boolean setDelFolder(int folder_id) {
			int i = 0;
			boolean setDelFlag=false;
			String sql=new String();
			dbc =new DbConnection();
			 sql = "delete from folder where folder_id=" + folder_id;
			 System.out.println("sql:"+sql);
			try {
				i = dbc.st.executeUpdate(sql);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
			
			if(i>0)
				setDelFlag = true;
System.out.println(setDelFlag);
           dbc.closeConnection();
			return setDelFlag;

		}
		/**
		 * 功能：修改一个网络文件
		 * 参数：NetfileVO型对象
		 * 返回值：int型
		 */
		public int setUdpNetfile(NetfileVO netfilevo) {
			int i = 0;
            
			String sql = "update " + netfile
			             + " set forlder_id='" + netfilevo.getFolder_id()
			             + "',netfile_name='"+ netfilevo.getNetfile_name()
			             + "',netfile_type='"+ netfilevo.getNetfile_type()
			             + "',netfile_path='"+ netfilevo.getNetfile_path()
			             + "',netfile_share='"+ netfilevo.getNetfile_share();
			try {

				i = dbc.st.executeUpdate(sql);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
			return i;
		}
		
		
	
		
}
