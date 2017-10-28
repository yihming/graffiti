<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String result = (String) request.getParameter("flag");
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html" charset="utf-8" />
<title>用户登录</title>
<link href="thickbox/thickbox.css" rel="stylesheet" type="text/css" />

<script language="javascript">
   function login_check(user_name,user_pswd){
   if(user_name.value =='') 	alert("用户名不能为空！"); 
   else if(user_pswd.value =='')	alert("密码不能为空!");
 	else {
 	document.forms[0].submit();  
 	}
    }
</script>

</head>

<%

if(result != null)
{
	String outmessage = "欢迎使用AS办公系统！";
	if(result.equals("pass_wrong")){
		outmessage = "用户名密码错误！";
	}else if(result.equals("rand_wrong")){
		outmessage = "验证码错误！";
	}else if(result.equals("close")){
		outmessage = "注销成功！";
	}
 %> 
 <script language = "javascript">
	alert("<%=outmessage%>");
 </script>
 <%
}
%>
<body style="background:url(images/background.jpg) no-repeat; overflow:hidden;">


<div style="margin:100px auto 200px auto; background:url(images/login_bg.gif) no-repeat; width:478px; height:465px; padding-top:210px; padding-left:100px;">
  <div>
  <form action="./userlog?action=login" method="post">
      <table width="77%" border="0">
  <tr>
    <td height="30" scope="col"><div align="right">用户名：</div></td>
    <td height="30" scope="col"><label>
      <input type="text" name="user_name" />
    </label></td>
  </tr>
  <tr>
    <td height="30" scope="row"><div align="right">密码：</div></td>
    <td height="30"><label>
      <input type="password" name="user_pswd" />
      </label></td>
  </tr>
  <tr>
    <td height="30" scope="row"><div align="right">验证码：</div></td>
    <td height="30"><label>
      <input type=text name=rand maxlength=4 value=""/>
      </label></td>
       </tr>
       <tr><td></td><td><img border=0 src="./yzm.jsp"/></td></tr>
  <tr>
    <th height="30" scope="row">&nbsp;</th>
    <td height="30"><label>
      <input type="button" name="Submit" value="" style="background:url(images/login_buttom.gif) no-repeat; width:35px; height:35px; cursor:pointer" onclick="login_check(user_name,user_pswd)" />
    </label></td>
  </tr>
</table>
  </form>
  </div>


</div>

</body>
</html>
