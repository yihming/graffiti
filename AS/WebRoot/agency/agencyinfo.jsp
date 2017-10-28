<%@ page language="java" import="java.util.*,com.as.function.*,com.as.*,com.as.dao.*" pageEncoding="UTF-8"%>
<jsp:directive.page import="com.as.vo.AgencyVO"/>
<jsp:directive.page import="com.as.vo.AgencyAllVO"/>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
int agency_id = Integer.parseInt((String)request.getParameter("agency_id"));
 
 AgencyVO agencyvo = new AgencyVO();
 AgencyAllVO agencyallvo = new AgencyAllVO();
AgencyDAO agencydao = new AgencyDAO();

List list = agencydao.getAgencyPriListByAgencyId(agency_id);
		Iterator it = list.iterator();
 
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
      	<link type="text/css" href="../js/jqueryui/css/ui.all.css" rel="stylesheet" />
	<link type="text/css" href="../css/base.css" rel="stylesheet"/>

	<script type="text/javascript" src="../js/jqueryui/ui/jquery-1.3.2.js"></script>
	<script type="text/javascript" src="../js/jqueryui/ui/ui.core.js"></script>
	<script type="text/javascript" src="../js/jqueryui/ui/ui.tabs.js"></script>
    <script type="text/javascript" src="../js/jqueryui/ui/ui.datepicker.js"></script>
    <title>My JSP 'agencyinfo.jsp' starting page</title>
 
  </head>
  <h1>代办具体权限</h1>
  <h2>由于代办事件已经发送给对方，所以不提供修改，请直接删除代办事件</h2>
  <body>
   <table>
      <tr><td>代办种类</td><td>添加</td><td>删除</td><td>更新</td><td>查询</td><td>点击这里操作</td></tr>
      <%	while(it.hasNext()){
			agencyallvo = (AgencyAllVO)it.next(); %>
      <tr><td><%=agencyallvo.getAg_list_name() %></td>
      <td><%=agencyallvo.getAgency_add() %></td>
      <td><%=agencyallvo.getAgency_delete() %></td>
      <td><%=agencyallvo.getAgency_update() %></td>
      <td><%=agencyallvo.getAgency_select() %></td>
      <td><%=agencyallvo.getAg_list_url() %></td>
       </tr>
      <%} %>
   </table>
    <input type="button" value="返回" onclick="window.history.back()"/>
  </body>
</html>
