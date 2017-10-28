<!--
version 1.2
基本框架搞定
Guo Kai 2009-8-31
-->
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page language="java" import="com.as.function.UserSession" %>
<%@ page language="java" import="com.as.dao.FriendDAO" %>
<%@ page language="java" import="com.as.vo.FriendClassVO" %>
<%@ page language="java" import="com.as.vo.FriendVO" %>
<%@ page language="java" import="com.as.vo.UserVO" %>
<%@ page language="java" import="com.as.dao.UserDAO" %>
<%@ page language="java" import="com.as.function.AS" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<%
UserSession usersession = new UserSession();
usersession = (UserSession)session.getAttribute("usersession");
int user_id = usersession.getUser_id();
String user_name = usersession.getUser_name();

UserDAO userdao = new UserDAO();

FriendDAO frienddao = new FriendDAO();
FriendClassVO friendclassvo = new FriendClassVO();
List f_class_list = new ArrayList();
f_class_list = frienddao.getFriendClassByUserId(user_id);
AS as = new AS();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>

  	<link type="text/css" href="../js/jqueryui/css/ui.all.css" rel="stylesheet" />
	<link type="text/css" href="../css/base.css" rel="stylesheet"/>

	<script type="text/javascript" src="../js/jqueryui/ui/jquery-1.3.2.js"></script>
	<script type="text/javascript" src="../js/jqueryui/ui/ui.core.js"></script>
	<script type="text/javascript" src="../js/jqueryui/ui/ui.tabs.js"></script>
    <script type="text/javascript" src="../js/jqueryui/ui/ui.datepicker.js"></script>
    <script type="text/javascript">
	$(function() {
		$('#datepicker').datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat:"yy-mm-dd",
			minDate: '-100Y', 
			maxDate: '+10Y',
			scroll: 'true'
		});
	});
	</script>
    
    <base href="<%=basePath%>">
    
    <title>My JSP 'addfriend.jsp' starting page</title>
    
 

  </head>
  <script language="javascript">
  function add_friend(){
		var friend_name = document.getElementById("friend_name");
		if(friend_name.value == ""){
			alert("姓名不能为空");
		}else document.newfirendform.submit();
  }
  </script>
  <body>
 <div class="thickbox_content">
<div id="tabs-3">
  <h1>新增联系人</h1><br>
  
  <%
	String source = request.getParameter("source");
	if(source!=null && source.equals("list")){
		String idid = request.getParameter("user_id");
		int friend_id = Integer.parseInt(idid);
		UserVO uservo = new UserVO();
		uservo = userdao.getUserById(friend_id);
	%>
  
<form action="./friendoperation?operation=addfriend" method="post" name="newfirendform">
  <table border="1" width="98%">
		  <tr>
			<td scope="col">姓名</td>
			<td scope="col"><label>
			  <input type="text" name="friend_name" id="friend_name" value="<%=as.StringNotEmpty(uservo.getUser_name()) %>">
			</label></td>
			<td>好友分组</td>
			<td><label>
			  <select name="friend_class" id="friend_class">
			  <%
			  Iterator it;
			  it = f_class_list.iterator();
			  while(it.hasNext()){
			  friendclassvo = (FriendClassVO)it.next();
			   %>
            <option value = '<%=friendclassvo.getF_class_id() %>'><%=as.StringNotEmpty(friendclassvo.getF_class_name()) %></option>
            <%} %>
          </select>
			</label><a href="card/classmanage.jsp">新建分组</a></td>

		  </tr>
		  <tr>
			<td scope="row">生日</td>
			<td><label>
			  <input type="text" name="friend_birth" id="datepicker" value="<%=as.StringNotEmpty(uservo.getUser_birthday()) %>">
			</label></td>
						<td scope="col">性别</td>
			<td scope="col"><label>
			<%
				int user__sex = uservo.getUser_sex();
				if(user__sex==1){
			 %>
				<input type="radio" name="friend_sex" value="1" checked/>男
				<input type="radio" name="friend_sex" value="0"/>女
				<%
				}
				else
				{
				%>
				<input type="radio" name="friend_sex" value="1" />男
				<input type="radio" name="friend_sex" value="0" checked/>女
				<%
				}
				 %>
			</label></td>			
			
		  </tr>
		  <tr>
			<td scope="row">手机</td>
			<td><label>
			  <input type="text" name="friend_mobile" value="<%=as.StringNotEmpty(uservo.getUser_mobile()) %>">
			</label></td>
			
		 
			<td>电子邮件</td>
			<td><label>
			  <input type="text" name="friend_mail" id="friend_mail" value="<%=as.StringNotEmpty(uservo.getUser_mail()) %>">
			</label></td>
		  </tr>
		  <tr>
		    <td scope="row">通讯地址</td>
		    <td colspan="3"><label>
		      <input name="friend_addr" type="text" size="60">
		    </label></td>
	    </tr>
	      <tr>
		    <td scope="row">备注</td>
		    <td colspan="3"><label>
		      <textarea name="friend_intro" style="width:250px;height:150px; margin-left:10px;"></textarea>
		    </label></td>
	    </tr>
		  <tr>
		    <td colspan="4" scope="row"><label>
		      <input type="button" name="Submit" value="" class="sure_submit" onclick="add_friend()">
		    </label></td>
	    </tr>
		</table>
	   </form>
	
	
	<%
	}else
	{
   %>
   
   
   
<form action="./friendoperation?operation=addfriend" method="post" name="newfirendform">
  <table border="1" width="98%">
		  <tr>
			<td scope="col">姓名</td>
			<td scope="col"><label>
			  <input type="text" name="friend_name" id="friend_name">
			</label></td>
			<td>好友分组</td>
			<td><label>
			  <select name="friend_class" id="friend_class">
			  <%
			  Iterator it;
			  it = f_class_list.iterator();
			  while(it.hasNext()){
			  friendclassvo = (FriendClassVO)it.next();
			   %>
            <option value = '<%=friendclassvo.getF_class_id() %>'><%=friendclassvo.getF_class_name() %></option>
            <%} %>
          </select>
			</label><a href="card/classmanage.jsp">新建分组</a></td>

		  </tr>
		  <tr>
			<td scope="row">生日</td>
			<td><label>
			  <input type="text" id="datepicker" name="friend_birth">
			</label></td>
						<td scope="col">性别</td>
			<td scope="col"><label>
				<input type="radio" name="friend_sex" value="1" checked/>男
				<input type="radio" name="friend_sex" value="0"/>女
			</label></td>			
			
		  </tr>
		  <tr>
			<td scope="row">手机</td>
			<td><label>
			  <input type="text" name="friend_mobile">
			</label></td>
			
		 
			
			<td>电子邮件</td>
			<td><label>
			  <input type="text" name="friend_mail" id="friend_mail">
			</label></td>
		  </tr>
		  <tr>
		    <td scope="row">通讯地址</td>
		    <td colspan="3"><label>
		      <input name="friend_addr" type="text" size="60">
		    </label></td>
	    </tr>
	      <tr>
		    <td scope="row">备注</td>
		    <td colspan="3"><label>
		      <textarea name="friend_intro" style="width:250px;height:150px; margin-left:10px;"></textarea>
		    </label></td>
	    </tr>
		  <tr>
		    <td colspan="4" scope="row"><label>
		      <input type="button" name="Submit" value="" class="sure_submit" onclick="add_friend()">
		    </label></td>
	    </tr>
		</table>
	   </form>
	   <%} %>
     </div>
     </div>
  </body>
</html>
