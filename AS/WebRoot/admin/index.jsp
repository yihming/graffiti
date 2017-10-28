<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Frameset//EN">
<HTML>
<HEAD>
	<TITLE>会议管理系统</TITLE>
	<META http-equiv=Content-Type content="text/html; charset=utf-8">
	<META http-equiv=Pragma content=no-cache>
	<META http-equiv=Cache-Control content=no-cache>
	<META http-equiv=Expires content=-1000>
	<LINK href="images/admin.css" type=text/css rel=stylesheet>
</HEAD>
	 
	 
		      <frameset cols="170, *">
					<frame name=menu src="menu.htm" frameBorder=0 noResize>
                             <frameset border=0 frameSpacing=0 rows="60, *,80" frameBorder=0>
								 <frame name=logo src="logo.htm" frameBorder=0 noResize scrolling=no>
                                 <frame name=main src="main.htm" scrolling="yes" frameBorder=0 noResize scrolling=no>
                                 <frame name=bottom src="bottom.htm" frameBorder=0 noResize scrolling=no>
                             </frameset>		
              </frameset>
               
 
	<noframes>对不起，您的浏览器不支持该页面，请换个浏览器再试。</noframes>
</HTML>
