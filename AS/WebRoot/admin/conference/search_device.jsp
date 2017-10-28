<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <link href="../../css/base.css" rel="stylesheet" type="text/css" />
    <base href="<%=basePath%>">
    
    <title>查找设备</title>
 
  </head>
  
  <body>

<div class="thickbox_content">
<h1>查找设备</h1>
<form name="form1" action="./conference/DeviceServlet" method="post">&nbsp; 
	<input type="hidden" name="opflag" value="query" /><br />
	<input type="text" name="equip_name" value="设备名称"/>
	<input type="submit" value="查找" />
</form>
</div>
</body>
</html>
