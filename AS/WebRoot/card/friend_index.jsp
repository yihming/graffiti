<%@ page language="java" import="java.util.*,com.as.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

 <frameset rows="12%,88%"> 
  <frame src="banner.html" name="head" scrolling="no"   noresize="noresize" marginwidth="0"   marginheight="0" frameborder="0"/>
   <frame src="colleaguelist.jsp" name="body" scrolling="yes"   noresize="noresize" marginwidth="0"   marginheight="0"  frameborder="0" />
<noframes>
<body>您的浏览器无法处理框架！</body>
</noframes>

</frameset>
 
</html>
