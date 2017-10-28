<%@ page language="java" import="java.util.*,com.as.function.*,com.as.*" pageEncoding="UTF-8"%>
<jsp:directive.page import="com.as.dao.AgencyDAO"/>
<jsp:directive.page import="com.as.vo.AgencyVO"/>
<jsp:directive.page import="com.as.vo.Agency_listVO"/>
<jsp:directive.page import="com.as.vo.AgencyAllVO"/>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

 //UserSession usersession = (UserSession)session.getAttribute("usersession");
// int user_id =usersession.getUser_id();
int  user_id = 41;
 
 AgencyAllVO agencyallvo = new AgencyAllVO();
 AgencyVO agencyvo = new AgencyVO();
 
AgencyDAO agencydao = new AgencyDAO();

List list = new ArrayList();
list = agencydao.getMyAgencyList(user_id);
Iterator it = list.iterator();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
 
    <base href="<%=basePath%>">
   	<link type="text/css" href="../js/jqueryui/css/ui.all.css" rel="stylesheet" />
	<link type="text/css" href="../css/base.css" rel="stylesheet"/>

	<script type="text/javascript" src="../js/jqueryui/ui/jquery-1.3.2.js"></script>
	<script type="text/javascript" src="../js/jqueryui/ui/ui.core.js"></script>
	<script type="text/javascript" src="../js/jqueryui/ui/ui.tabs.js"></script>
    <script type="text/javascript" src="../js/jqueryui/ui/ui.datepicker.js"></script>
       <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>My JSP 'index.jsp' starting page</title>
 
<script language="javascript">
  function agency_del(agency_id){
    if(confirm("确认删除这个种类")){
    window.location.href="./agency/AgencyServlet?opflag=del&agency_id="+agency_id;
    }
  }
</script>
  </head>
  
  <body> 
    <div class="thickbox_content">
<h1>我申请的代办事件</h1>
<%
   String flag = "";
   if(request.getAttribute("flag")!=null){
      flag = (String)request.getAttribute("flag");
       if(flag.equals("1")) out.println("操作成功");
       else if(flag.equals("0"))out.println("操作失败");
   }
   else{
    flag = "";
   }
  
 %>
  <form action="#" method="post">
  <table width="98%" border="0" style="border:0px;">
    <tr class="smallhead">
      <td>编号</td>
      <td  >代办人</td>
      <td  >开始时间</td>
      <td >结束时间</td>
       <td >代办简介</td>
       <td >状态</td>
      <td  >操作</td>
      <td  >具体权限</td>
    </tr>
    
 <% int i = 1;
 while(it.hasNext()){
   agencyallvo = (AgencyAllVO)it.next();
   
  %>
   <form action="/agency/AgencyServletAdmin" method="post">
    <tr>
      <td><%=i %></td>
      <td><%=agencyallvo.getDoer_true_name()%> </td>
      <td><%=agencyallvo.getAgency_begin() %></td>
      <td> <%=agencyallvo.getAgency_end() %></td>
      <td><%=agencyallvo.getAgency_intro() %></td>
      <td><%
      int state = agencyallvo.getAgency_state();
      if(state == 0) out.println("对方还未接受");
      else if(state == 1) out.println("已经接收,正执行");
      else if(state == 2) out.println("对方放弃");
      else if(state == 3) out.println("已完成");
      else out.println("数据出错");
       %></td>
      <input type="hidden" value="<%=agencyallvo.getDoer_id() %> name="doer_id" />
      <input type="hidden" value="<%=agencyallvo.getAgency_id()%>" name="agency_id" />
      <td><input type="button" onclick="agency_del(<%=agencyallvo.getAgency_id() %>)" value="删除"/>
      <td><a href="agency/agencyinfo.jsp?agency_id=<%=agencyallvo.getAgency_id() %>">具体权限</a></td>
      </tr>
	 </form>
	 <%
	 ++i;} %>
  </table>
   </div>
  </body>
</html>
