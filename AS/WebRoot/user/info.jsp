<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.as.user.*" %>
<%@ page language="java" import="com.as.dao.UserDAO" %>
<%@ page language="java" import="com.as.function.UserSession" %>
<%@ page language="java" import="com.as.vo.UserVO" %>
<%@ page language="java" import="com.as.vo.DeptVO" %>
<%@ page language="java" import="com.as.dao.DeptDAO" %>
<%@ page language="java" import="com.as.function.AS" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%
UserSession usersession = new UserSession();
usersession = (UserSession)session.getAttribute("usersession");

int id = usersession.getUser_id();
UserDAO userdao = new UserDAO();
UserVO user = new UserVO();
user = userdao.getUserById(id); 

DeptDAO deptdao = new DeptDAO();
DeptVO deptvo = new DeptVO();
deptvo = deptdao.getDeptById(user.getDept_id());

AS as = new AS();


%>

<%
String result = request.getParameter("result");
String outmessage = "";
if(result!=null){
	if(result.equals("info_ok"))
	{
		outmessage = "个人信息修改成功！";
	}else if(result.equals("info_failed"))
	{
		outmessage = "个人信息修改失败！";
	}else if(result.equals("pswd_ok"))
	{
		outmessage = "密码修改成功！";
	}else if(result.equals("pswd_failed"))
	{
		outmessage = "密码修改失败！";
	}

%>
<script language="javascript">
alert("<%=outmessage%>");
</script>
<% 
}
%>

 
<script language="javascript">
function info_sub(){	
	var be = confirm("确认提交？");
	if(be) document.forms[0].submit();	
}
</script>


<script language="javascript">
function pass_sub(){	

	if(document.getElementById("oldpswd").value=='' ||document.getElementById("newpswd1").value=='' ||document.getElementById("newpswd2").value=='')
	alert("密码不能为空！");
	else if(document.getElementById("newpswd1").value != document.getElementById("newpswd2").value)
			alert("新密码不一致！");
			else if(confirm("确认提交？")){document.pswdform.submit();}
}
</script>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>jQuery UI Tabs - Content via Ajax</title>
	<script type="text/javascript" src="../js/jqueryui/ui/jquery-1.3.2.js"></script>
	<script type="text/javascript" src="../js/jqueryui/ui/ui.core.js"></script>
	<script type="text/javascript" src="../js/jqueryui/ui/ui.tabs.js"></script>
	<link type="text/css" href="../js/jqueryui/css/ui.all.css" rel="stylesheet" />
	<link type="text/css" href="../css/base.css" rel="stylesheet"/>
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
 
	<script type="text/javascript">
	$(function() {
		$("#tabs").tabs();
	});
	</script>
</head>
<body>



<div id="tabs">
	<ul>
		<li><br></li><li><a href="#tabs-1">修改基本信息</a></li>
		<li><a href="#tabs-2">修改密码</a></li>
		<li><a href="#tabs-3">上传头像</a></li>
	</ul>
  <div id="tabs-1">
  <form action="../userinfo?action=update" method="post" name="form1">
    <table border="1" cellspacing="0"  height="32" style="border-collapse: collapse;border:1px solid #ddd;"  cellpadding="0" >
      <tr>
        <td width="65">员工号</td>
        <td width="168"><%=user.getUser_number() %></td>
        <td width="168" colspan="2" rowspan="3"><img src="<%=user.getUser_picture() %>" width="122" height="155"></td>
      </tr>
      <tr>
        <td>姓名</td>
        <td><%=user.getUser_true_name() %></td>
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
        <td> 男
            <input type="radio" name="user_sex" checked value="1"/>
          女
          <input type="radio" name="user_sex" value="0" />        </td>
          <%}
          else{%>
          <td> 男
            <input type="radio" name="user_sex" value="1" />
          女
          <input type="radio" name="user_sex"  value="0" checked/>        </td>
          <%
          }
           %>
        <td>出生日期</td>
        <td><input type="text" id="datepicker" name="user_birthday" value="<%=as.StringNotEmpty(user.getUser_birthday()) %>" /></td>
      </tr>
      
      <tr>        
        <td>职位</td>
        <td><select name="select">
            <option>主管</option>
            <option>主管助理</option>
            <option>职员</option>
          </select>        </td>
           <td>办公室</td>
        <td><input type="text" name="user_office" value="<%=as.StringNotEmpty(user.getUser_office()) %>"/></td>
		<td colspan="2"></td>
      </tr>
      
      
      <tr>
        <td>座机</td>
        <td><input type="text" name="user_phone" value="<%=as.StringNotEmpty(user.getUser_phone()) %>" /></td>
        <td>传真</td>
        <td><input type="text" name="user_fax" value="<%=as.StringNotEmpty(user.getUser_fax()) %>" /></td>
      </tr>
      
      <tr>
      <td>E-mail地址</td>
        <td><input type="text" name="user_mail" value="<%=as.StringNotEmpty(user.getUser_mail()) %>" style="width:180px;"/></td>
        <td>手机</td>
        <td><input type="text" name="user_mobile" value="<%=as.StringNotEmpty(user.getUser_mobile()) %>" /></td>
        
      </tr>
      <tr>
      <td>个人简介</td>
      <td><textarea name="user_intro" style="height:100px; width:200px;"><%=as.StringNotEmpty(user.getUser_intro())%></textarea></td>
      </tr>
      
    </table>
    <input type="button" name="submit1" value="" class="sure_submit" onclick="info_sub()"/>
  </form>
  </div>
	<div id="tabs-2">
	<form action="../userinfo?action=change_pass" method="post" name="pswdform">
	<table border="1">
	<tr><td>原密码</td><td><input type="password" name="oldpswd" id="oldpswd"/></td></tr>
	<tr><td>新密码</td><td> <input type="password" name="newpswd1" id="newpswd1" /></td></tr>
    <tr><td>确认新密码 </td><td><input type="password" name="newpswd2" id="newpswd2" /></td></tr>
	</table>
	<input type="hidden" name="user_name" value="<%=user.getUser_name() %>"/>
	<input type="button" name="submit1" value="" class="sure_submit" onclick="pass_sub()"/>

    
</form>
	</div>
	<div id="tabs-3">
		 
	    <form name="form1" method="post" action="">
	      <label>
	      <input type="file" name="textfield">
	      </label>
                <p>
                  <label>
                  <input type="submit" name="Submit" value="" class="sure_submit">
                  </label>
          </p>
	    </form>
  </div>
</div>

 
</body>
</html>
