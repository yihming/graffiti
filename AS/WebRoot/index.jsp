<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.as.function.*" %>
<%@ page language="java" import="com.as.dao.UserDAO" %>
<%@ page language="java" import="java.text.SimpleDateFormat;"%>
<%String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

int user_id = 0;
String user_true_name = "";
int user_group = 1;
try{
  UserSession usersession = new UserSession();
  session = request.getSession();
  usersession = (UserSession)session.getAttribute("usersession");
  user_id = usersession.getUser_id();
  user_true_name = usersession.getUser_true_name();
  user_group = usersession.getUser_group();
}catch(Exception e){
  // request.getRequestDispatcher("./login.jsp").forward(request,response);
}

java.util.Date date = new java.util.Date();
SimpleDateFormat simple = new SimpleDateFormat("yyyy");
String year = simple.format(date);
simple.applyPattern("MM");
String month = simple.format(date);
simple.applyPattern("dd");
String day = simple.format(date);
 %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
	<title>AS协同办公系统</title>
	<script src="js/jquery-1.2.6.min.js" type="text/javascript"></script>
	<script src="js/jquery.ui.interaction.min.js" type="text/javascript"></script>
	<script src="js/dashboard.js" type="text/javascript"></script>
	<script src="js/jquery.jqDock.min.js" type="text/javascript"></script>
	<script src="js/thickbox/thickbox.js" type="text/javascript"></script>
	<script src="js/facebox/facebox.js" type="text/javascript" language="javascript"></script>
	<style type="text/css">
		@import url(css/style.css);	
	</style>
	<link href="js/thickbox/thickbox.css" rel="stylesheet" type="text/css"/>
    <link href="js/facebox/facebox.css" rel="stylesheet" type="text/css"/>
	<link href="css/base.css" rel="stylesheet" type="text/css"/>
	
</head>

<script language="javascript">
function check_out(){
	if(confirm("谢谢您使用AS协同办公系统，下班平安回家^_^")){
		window.location.href='./userlog?action=logout';
	}
}
</script>
<body>
	<div id="wrapper">
	<ul id="desktopItems">
	<a href="disk.html?KeepThis=true&TB_iframe=true&height=400&width=600" title="我的网络硬盘" class="thickbox" >
		<li id="macintoschHD"> 
			<span>网络硬盘</span>		</li>
	  </a>
		<a href="needdo.html?KeepThis=true&TB_iframe=true&height=400&width=600" title="待办事件" class="thickbox" >
		<li id="icon1">
			<span>待办事件</span>		</li>
		</a>
		<a href="message/index.jsp?KeepThis=true&TB_iframe=true&height=400&width=600" title="个人信息修改" class="thickbox" ><li id="icon2">
			<span>站内短信</span>
		</li>
		</a>
		<a href="conference/index.jsp?KeepThis=true&TB_iframe=true&height=500&width=900" title="申请会议室" class="thickbox" >
		<li id="icon3">
			<span>会议管理</span>		</li>
		</a>		
		<%if(user_group==2){ %>
		<a href="admin/index.jsp?KeepThis=true&TB_iframe=true&height=450&width=900" title="系统管理" class="thickbox" >
		<li id="icon4">
			<span>系统管理</span>		</li>
		</a>
		<%} %>
	</ul>
	
	<div class="draggableWindow" style="">
		<h1><span></span>AS协同办公系统</h1>
		<div class="content">
			<h2>AS协同办公系统</h2>
			<p style="font-size:15px; color:#FF0000;">欢迎&nbsp;<font style="color:#0000FF; font-size:15px;"><%=user_true_name%></font>
			 &nbsp;登录，今天是<%=year %>年<%=month %>月<%=day %>日<br/>AS伴您一天轻松工作^_^</p>
			 <br/>
			 			 <p align="center" style="font-size:14px;"><img src="./images/home.jpg" width="25px" height=25px" />一直被追赶，从未被超越！</p>
			 <br />
			 <p align="center">
			 Copyright @ AlphaSphere R&D Group<br/>
			 All Rights Reserved
			 </p>

		</div>
	</div>
	<div class="draggableWindow" id="smaller">
		<h1><span></span>今日提醒</h1>
		<div class="content">
			<ul>
			   <li>11点到203开会</li><span class="small_close"></span>
			   <li>中午要见客户赵虎</li><span class="small_close"></span>
			   <li>下午要打印文档</li><span class="small_close"></span>
			   <li>晚上要去超市</li><span class="small_close"></span>
			   <li>20点给一个客户打电话</li><span class="small_close"></span>
			</ul>
		</div>
	</div>
	<!-- 右边栏 -->
		<div id="right_banner">
		   <div class="right_box">
			  <div class="right_title">当前时间</div>
			  <div class="right_content">
				<img src="images/widgets/clock.png"/>			  </div>
		   </div>
		   <div class="right_box">
			  <div class="right_title">日历</div>
			  <div class="right_content">
			  <a href="frame/frame.html?KeepThis=true&TB_iframe=true&height=400&width=600" title="个人信息修改" class="thickbox" >测试用
		</a>
				<img src="images/widgets/date.png" width="230"/>
				<!--<a href="info.html" rel="facebox">用Facebox观看加载远程的页面内容1</a><br/>
				<a href="user/info.jsp?KeepThis=true&TB_iframe=true&height=400&width=600" title="个人信息修改" class="thickbox" >查看个人信息</a>
				-->
			  </div>
		   </div>
		   <div class="right_box">
			  <div class="right_title">天气</div>
			  <div class="right_content">
			  <a href="message/sendMessage.jsp">发送短信</a>
			  <a href="message/unReadBox.jsp">未读短信</a>
               <img  src="images/widgets/dictionary.png" width="240"/>			  </div>
		   </div>
      </div>
	  	<!-- 右边栏 -->
		<!--<div id="dashboardWrapper">
			<ul id="widgets">
				<li class="widget"><img src="images/widgets/googlesearch.png" alt="" /></li>
				<li class="widget"><img src="images/widgets/date.png" alt="" /></li>
				<li class="widget"><img src="images/widgets/dictionary.png" alt="" /></li>
				<li class="widget stickyWidget"><textarea rows="10" cols="10">This is a text widget! You can make any type of widget you want, simply by adding the class 'widget' to an li element within the Dashboard unordered list. Don't stop there though, create custom looking widgets by adding another class (e.g. myClass) and styling that in style.css. Like this one!</textarea></li>
			</ul>
			<div id="addWidgets">
			<span id="openAddWidgets">Add/remove widgets</span>
			<div id="dashPanel">
				<ul>
					<li><img src="images/widgets/thumbs/sticky.png" alt="" id="sticky" class="widgetThumb" /><span>Sticky</span></li>
					<li><img src="images/widgets/thumbs/clock.png" alt="" id="clock" class="widgetThumb" /><span>Clock</span></li>
					<li><img src="images/widgets/thumbs/weather.png" alt="" id="weather" class="widgetThumb" /><span>Weather</span></li>
				</ul>
			</div>
		</div>
		</div>-->
	</div>
	<ul id="dock">
		 
	<!--	<li><img src="images/dashboard.png" alt="Dashboard" id="dashboardLaunch" title="Dashboard" class="dockItem"/ > -->
	<a href="setting.html?KeepThis=true&TB_iframe=true&height=400&width=600" title="偏好设置" class="thickbox"><li><img src="images/setting.png" alt="Dashboard"  title="Dashboard" class="dockItem"/></li> </a>
	    <a href="bianqian.html?KeepThis=true&TB_iframe=true&height=400&width=600" title="我的便签" class="thickbox"><li><img src="images/note.png" alt="发送站内短信" title="finder" class="dockItem" /></li></a>
	    <a href="task.html?KeepThis=true&TB_iframe=true&height=400&width=600" title="我的团队任务" class="thickbox"><li><img src="images/contact-list.png" alt="Coda" title="Coda" class="dockItem" /></li></a>
		
		<a href="agency/index.jsp?KeepThis=true&TB_iframe=true&height=500&width=900" title="申请代办事件" class="thickbox"><li><img src="images/link.png" alt="Coda" title="Coda" /></li></a>
		<a href="card/friend_index.jsp?KeepThis=true&TB_iframe=true&height=500&width=700" title="我的名片夹" class="thickbox"><li><img src="images/contact.png" alt="我的名片夹" title="Coda" class="dockItem" /></li></a>
		<a href="user/info.jsp?KeepThis=true&TB_iframe=true&height=500&width=600" title="修改我的名片" class="thickbox"><li><img src="images/info.png" alt="我的基本信息"   class="thickbox" /></li></a>
	</ul>
	<!--
	<div class="stack">
	<img src="images/stack.png" alt="stack"/>
		<ul id="stack1">
			<li><span>Acrobat</span><img src="images/adobeAcrobat.png" alt="" /></li>
			<li><span>Aperture</span><img src="images/aperture.png" alt="" /></li>
			<li><span>Photoshop</span><img src="images/photoshop.png" alt="" /></li>
			<li><span>Safari</span><img src="images/safari.png" alt="" /></li>
			<li><span>Finder</span><img src="images/finder.png" alt="" /></li>
		</ul>
	</div>
	-->
	<a href="#" onClick="check_out()"><div id="logout"><span>退 出</span></div>
	</a>
</body>
</html>