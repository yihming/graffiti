<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page language="java" import="com.as.function.UserSession" %>
<%@ page language="java" import="com.as.dao.FriendDAO" %>
<%@ page language="java" import="com.as.dao.UserDAO" %>
<%@ page language="java" import="com.as.dao.DeptDAO" %>
<%@ page language="java" import="com.as.vo.FriendClassVO" %>
<%@ page language="java" import="com.as.vo.FriendVO" %>
<%@ page language="java" import="com.as.vo.UserVO" %>
<%@ page language="java" import="com.as.vo.DeptVO" %>
<%@ page language="java" import="com.as.function.AS" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<link type="text/css" href="../css/base.css" rel="stylesheet"/>
    <base href="<%=basePath%>">
    
    <title>My JSP 'detail_info.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
  <div class="thickbox_content">
  		<%
  		AS as = new AS();
  		UserVO user = new UserVO();
  		UserDAO userdao = new UserDAO();
  		DeptDAO deptdao = new DeptDAO();
  		DeptVO deptvo = new DeptVO();
  		String id = request.getParameter("user_id");  		
  		int user_id = Integer.parseInt(id);
  		user = userdao.getUserById(user_id);
  		deptvo = deptdao.getDeptByUserId(user_id);
  		 %>
	 <table border="1" cellspacing="0" width="98%" height="32" style="border-collapse: collapse;border:1px solid #ddd;"  cellpadding="0" >
      <tr>
        <td width="65">员工号</td>
        <td width="168"><%=user.getUser_number() %></td>
        <td width="168" colspan="2" rowspan="3"><img src="<%=user.getUser_picture() %>" width="122" height="155"></td>
      </tr>
      <tr>
        <td>姓名</td>
        <td><%=as.StringNotEmpty(user.getUser_true_name()) %></td>
      </tr>
      <tr>
      <td>部门</td>
        <td><%=deptvo.getDept_name() %></td>        
      </tr>
      <tr>
        <td>性别</td>
        <%
        if(user.getUser_sex() == 1){
         %>
        <td> 男   </td>
          <%}
          else{%>
          <td>
          女
           </td>
          <%
          }
           %>
        <td>出生日期</td>
        <td><input disabled="true" type="text" name="user_birthday" value="<%=as.StringNotEmpty(user.getUser_birthday()) %>" /></td>
      </tr>
      
      <tr>        
        <td>职位</td>
        <td><select name="select" disabled="true">
            <option>主管</option>
            <option>主管助理</option>
            <option>职员</option>
          </select>        </td>
           <td>办公室</td>
        <td><input disabled="true" type="text" name="user_office" value="<%=as.StringNotEmpty(user.getUser_office()) %>"/></td>
		<td colspan="2"></td>
      </tr>
      
      
      <tr>
        <td>座机</td>
        <td><input disabled="true" type="text" name="user_phone" value="<%=as.StringNotEmpty(user.getUser_phone()) %>" /></td>
        <td>传真</td>
        <td><input disabled="true" type="text" name="user_fax" value="<%=as.StringNotEmpty(user.getUser_fax()) %>" /></td>
      </tr>
      
      <tr>
      <td>E-mail地址</td>
        <td><input disabled="true" type="text" name="user_mail" value="<%=as.StringNotEmpty(user.getUser_mail()) %>" style="width:180px;"/></td>
        <td>手机</td>
        <td><input  disabled="true" type="text" name="user_mobile" value="<%=as.StringNotEmpty(user.getUser_mobile()) %>" /></td>
        
      </tr>
      <tr>
      <td>个人简介</td>
      <td><textarea disabled="true" name="user_intro" ><%=as.StringNotEmpty(user.getUser_intro())%></textarea></td>
      <td colspan="2"><input type="button" onclick="javascript:window.history.back()" value="返回"/>
      <input type="button" onclick="window.location.href='addfriend.jsp?source=list&user_id=<%=user_id %>'" value="加为好友"/></td>
      
      </tr>      
    </table>
    </div>
  </body>
</html>
