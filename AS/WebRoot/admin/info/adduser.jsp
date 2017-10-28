<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%
String result = request.getParameter("result");
String outmessage = "欢迎使用AS办公系统";
if(result!=null)
{
	if(result.equals("newuser_ok")){
		outmessage = "新用户添加成功！";
	}else if(result.equals("newuser_failed")){
		outmessage = "新用户添加失败！";
	}
	
	%>
	<script language="javascript">
	alert("<%=outmessage%>");
	</script>
	<%
}
 %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  <link type="text/css" href="../../css/base.css" rel="stylesheet"/>

    <base href="<%=basePath%>">
    
    <title>My JSP 'adduser.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <script language="javascript">
	function newuser(){
		var test = document.getElementById("user_true_name").value;
		if(test=="")alert("用户名不能为空!");
		else{
			test = document.getElementById("user_name").value;
			if(test == "")alert("用户真实姓名不能为空！");
			else{
			 	test = document.getElementById("user_pswd").value;
			 	if(test == "") alert("密码不能为空！");
			 	else{
			 		test = document.getElementById("user_number").value;
			 		if(test=="") alert("员工编号不能为空！");
			 		else{
			 			test = document.getElementById("user_card").value;
			 			if(test == "") alert("银行卡号不能为空！");
			 			else{
			 				if(confirm("确认提交？"))
			 					document.newuserform.submit();
			 			}
			 		}
			 	}
			}
		}
	}
</script>
  
  <body>
	<table width="500" height="126" border="1">
  <caption> 
 	<h1>添加新用户 </h1>
  </caption>
  <form name="newuserform" action="./userinfo?action=newuser" method="post">
  <tr>
    <td width="100" scope="col">用户姓名</td>
    <td width="150" scope="col">*<input id="user_true_name" name="user_true_name" type="text" value=""/></td>
    <td colspan='2' rowspan='3'>上传照片<input id="user_picture" name="user_picture" type="file" value=""/>
    </td>

  </tr>
  <tr>
     <td width="100" scope="col">性别</td>
    <td width="150" scope="col">
    <input type="radio" name="user_sex" value="1" checked/>男
	<input type="radio" name="user_sex" value="0"  />女
    </td>

  </tr>
  <tr>
    <td>登陆账号</td>
    <td>*<input id="user_name" name="user_name" type="text" value=""/></td>
    
  </tr>
  <tr>
  	<td>密码</td>
	<td>*<input id="user_pswd" name="user_pswd" type="password" value=""/></td>
    <td width="100">出生年月</td>
    <td><input name="user_birth" type="text" value=""/></td>
  </tr>
  <tr>
     <td>所在部门</td>
	<td>*<input name="user_dept" type="text" value=""/></td>
	<td>员工编号</td>
	<td>*<input id="user_number" name="user_number" type="text" value=""/></td>
  </tr>
  <tr>
    <td>办公室</td>
    <td><input name="user_office" type="text" value=""/></td>
    <td>座机</td>
    <td><input name="user_phone" type="text" value=""/></td>
  </tr>
  <tr>
    <td>手机</td>
    <td><input name="user_mobile" type="text" value=""/></td>
    <td>Email</td>
    <td><input name="user_mail" type="text" value=""/></td>
  </tr>
  <tr>
    <td>传真</td>
    <td><input name="user_fax" type="text" value=""/></td>
	    <td>银行账号</td>
    <td>*<input id="user_card" name="user_card" type="text" value=""/></td>
  </tr>
  <tr>
    <td>工资</td>
    <td><input name="user_wage" type="text" value=""/></td>
    <td>奖金</td>
    <td><input name="user_bonus" type="text" value=""/></td>
  </tr>
  <tr>
    <td>用户简介</td>
    <td colspan='3'>
    <textarea name="user_intro" style="width:300px; height:100px;"></textarea>

  </tr>
  </form>
</table>

<input type="button" name="Submit" class="sure_submit" onclick="newuser()" value="提交"/>
<input type="reset" name="Submit" class="sure_submit"  />
  </body>
</html>
