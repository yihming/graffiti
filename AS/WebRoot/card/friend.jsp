<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page language="java" import="com.as.function.UserSession" %>
<%@ page language="java" import="com.as.dao.FriendDAO" %>
<%@ page language="java" import="com.as.vo.FriendClassVO" %>
<%@ page language="java" import="com.as.vo.FriendVO" %>
<%@ page language="java" import="com.as.vo.UserVO" %>
<%@ page language="java" import="com.as.dao.UserDAO" %>
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

FriendVO friendvo = new FriendVO(); 

FriendDAO frienddao = new FriendDAO();
FriendClassVO friendclassvo = new FriendClassVO();
List f_class_list = new ArrayList();
f_class_list = frienddao.getFriendClassByUserId(user_id);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<link type="text/css" href="../css/base.css" rel="stylesheet"/>
    <base href="<%=basePath%>">
    
    <title>My JSP 'friend.jsp' starting page</title>
    
 

  </head>
  <script language="javascript">
   function update_friend(){
  	document.getElementById("form1").submit();
   }
   function delete_friend(f_id){
   	if(confirm("确认删除？"))
   		window.location.href="../friendoperation?operation=deletefriend&friend_id="+f_id;
   }
  </script>
<body>
<div class="thickbox_content">
<%
	String idid = request.getParameter("friend_id");
	int friend_id = Integer.parseInt(idid);
	friendvo = frienddao.getFriendById(friend_id);
 %>
<form action="./friendoperation?operation=updatefriend&friend_id=<%=friend_id %>" method="post" name="newfirendform" id ="form1">
  <table border="1" width="98%">
		  <tr>
			<td scope="col">姓名</td>
			<td scope="col"><label>
			  <input type="text" name="friend_name" id="friend_name" value="<%=friendvo.getFriend_name() %>">
			</label></td>
			<td>好友分组</td>
			<td><label>
			  <select name="friend_class" id="friend_class">
			  <%
			  Iterator it;
			  it = f_class_list.iterator();
			  while(it.hasNext()){
			  friendclassvo = (FriendClassVO)it.next();
			  if(friendclassvo.getF_class_id() == friendvo.getF_class_id()){
			   %>
            <option value = '<%=friendclassvo.getF_class_id() %>' selected><%=friendclassvo.getF_class_name() %></option>
            <%}else{
            %>
            <option value = '<%=friendclassvo.getF_class_id() %>'><%=friendclassvo.getF_class_name() %></option>
            <%
            }
            } %>
          </select>
			</label><a href="./card/classmanage.jsp">新建分组</a></td>

		  </tr>
		  <tr>
			<td scope="row">生日</td>
			<td><label>
			  <input type="text" name="friend_birth" value="<%=friendvo.getFriend_birth() %>">
			</label></td>
						<td scope="col">性别</td>
			<td scope="col"><label>
			<%
				int friend__sex = friendvo.getFriend_sex();
				if(friend__sex==1){
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
			  <input type="text" name="friend_mobile" value="<%=friendvo.getFriend_phone() %>">
			</label></td>
		
		  </tr>
		  <tr>
		
			<td>电子邮件</td>
			<td><label>
			  <input type="text" name="friend_mail" id="friend_mail" value="<%=friendvo.getFriend_mail() %>">
			</label></td>
		  </tr>
		  <tr>
		    <td scope="row">通讯地址</td>
		    <td colspan="3"><label>
		      <input name="friend_addr" type="text" size="60" value="<%=friendvo.getFriend_addr() %>">
		    </label></td>
	    </tr>
	      <tr>
		    <td scope="row">备注</td>
		    <td colspan="3"><label>
		      <textarea name="friend_intro" style="width:250px;height:150px; margin-left:10px;"><%=friendvo.getFriend_intro() %></textarea>
		    </label></td>
	    </tr>
		  <tr>
		    <td colspan="4" scope="row"><label>
		      <input type="button" name="Submit" value="更新好友信息" onclick="update_friend()">
		      <input type="button" name="Submit" value="删除好友"  onclick="delete_friend(<%=friendvo.getFriend_id() %>)">
		      <input type="button" name="Submit" value="返回"  onclick="javascript:window.history.back()">
		    </label></td>
	    </tr>
		</table>
	   </form>
	   </div>
  </body>
</html>
