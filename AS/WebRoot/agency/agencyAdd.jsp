<%@ page language="java" import="java.util.*,com.as.*" pageEncoding="UTF-8"%>
<jsp:directive.page import="com.as.function.UserSession"/>
<jsp:directive.page import="com.as.dao.AgencyDAO"/>
<jsp:directive.page import="com.as.vo.Agency_listVO"/>
<jsp:directive.page import="com.as.vo.AgencyVO"/>
<jsp:directive.page import="com.as.vo.Agency_priVO"/>
<jsp:directive.page import="com.as.user.userinfo"/>
<jsp:directive.page import="com.as.dao.UserDAO"/>
<jsp:directive.page import="com.as.vo.UserVO"/>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

AgencyDAO agencydao = new AgencyDAO();
Agency_listVO agency_listvo = new Agency_listVO();
AgencyVO agencyvo = new AgencyVO();

UserSession usersession = (UserSession)session.getAttribute("usersession");
//int user_id = usersession.getUser_id();

int user_id = 41;
List list = new ArrayList();
//获取那个用户的列表
UserDAO userdao = new UserDAO();
UserVO uservo = new UserVO();
List userList = userdao.getAllUserList();
Iterator userIt = userList.iterator();

list = agencydao.getAgencyList();
Iterator it = list.iterator();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

	

<html>
  <head>
  
  	<link type="text/css" href="../js/jqueryui/css/ui.all.css" rel="stylesheet" />
	<link type="text/css" href="../css/base.css" rel="stylesheet"/>

	<script type="text/javascript" src="../js/jqueryui/ui/jquery-1.3.2.js"></script>
	<script type="text/javascript" src="../js/jqueryui/ui/ui.core.js"></script>
	<script type="text/javascript" src="../js/jqueryui/ui/ui.tabs.js"></script>
    <script type="text/javascript" src="../js/jqueryui/ui/ui.datepicker.js"></script>

	<script type="text/javascript">
	$(function() {
		$("#datepicker1").datepicker({dateFormat:"yy-mm-dd",minDate: +0, maxDate: '+1M +10D'});
	   $("#datepicker2").datepicker({dateFormat:"yy-mm-dd ",minDate: +0, maxDate: '+1M +10D'});
	});
	function agency_submit(){
	  alert("提交成功");
	  return false;
	  }
	  
	 function agency_cancel(){
	  if(confirm("确定取消代办？")){
           alert("您的代办已经取消");
	   }
	   return false;
	 }
	</script>
    <base href="<%=basePath%>">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>My JSP 'agency.jsp' starting page</title>
  </head>
  
  <body>
   <div class="thickbox_content">
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
  out.println(user_id);
 %>
     <form action="./agency/AgencyServlet" method="post">
		<table width="98%" border="0" style="border:0px;">
		  <tr>
			<td width="14%"><br>选择代办人</td>
			<td width="86%"><label>
			  <select name="doer_id">
			  <%while(userIt.hasNext()) {
			     uservo = (UserVO)userIt.next();
			   %>
			   <option value="<%=uservo.getUser_id() %>"><%=uservo.getUser_name() %></option>
			   <%
			   } %>
			  </select>
			</label></td>
		  </tr>
		  <tr>
			<td colspan="2">
			<table width="98%" border="0">
			  <tr class="smallhead">
				<td width="19%">事情</td>
				<td width="18%">添加</td>
			    <td width="17%">删除</td>
			    <td width="23%">更改</td>
			    <td width="23%">查看</td>
			  </tr>
			  <%while(it.hasNext()){ 
			   agency_listvo = (Agency_listVO)it.next();
			  %>
			  <tr>
				<td><%=agency_listvo.getAg_list_name() %></td>
				<td  ><label>
				<%if(agency_listvo.getAg_list_add() == 1){ %>
				  <input type="checkbox" name="ag_list_add[]" value="1">
				  <%} %>
				</label></td>
				<td ><label>
				  <%if(agency_listvo.getAg_list_delete() == 1){ %>
				  <input type="checkbox" name="ag_list_delete[]" value="1">
				  <%} %>
				</label></td>
				<td ><label>
				  <%if(agency_listvo.getAg_list_update() == 1){ %>
				  <input type="checkbox" name="ag_list_update[]" value="1">
				  <%} %>
				</label></td>
				<td ><label>
				  <%if(agency_listvo.getAg_list_select() == 1){ %>
				  <input type="checkbox" name="ag_list_select[]" value="1">
				  <%} %>
				</label></td>
			  </tr>
			  <%} %>
			  <tr>
				<td>授权时间</td>
				<td colspan="4"> 
				  <input  id="datepicker1" type="text" name="agency_begin_date"/>
				  <select name="agency_begin_hour">
				    <option value="0">0</option>
				    <option value="1">1</option>
				    <option value="2">2</option>
				    <option value="3">3</option>
				    <option value="4">4</option>
				    <option value="5">5</option>
				    <option value="6">6</option>
				    <option value="7">7</option>
				    <option value="8">8</option>
				    <option value="9">9</option>
				    <option value="10">10</option>
				    <option value="11">11</option>
				    <option value="12">12</option>
				    <option value="13">13</option>
				    <option value="14">14</option>
				    <option value="15">15</option>
				    <option value="16">16</option>
				    <option value="17">17</option>
				    <option value="18">18</option>
				    <option value="19">19</option>
				    <option value="20">20</option>
				    <option value="21">21</option>
				    <option value="22">22</option>
				    <option value="23">23</option>
				  </select>
				  <select name="agency_begin_min">
				    <option value="0">00</option>
				     <option value="15">15</option>
				     <option value="30">30</option>
				     <option value="45">45</option>
				  </select>
				到
				<input name="agency_end_date" id="datepicker2" type="text"/>	
				<select name="agency_end_hour">
				    <option value="0">0</option>
				    <option value="1">1</option>
				    <option value="2">2</option>
				    <option value="3">3</option>
				    <option value="4">4</option>
				    <option value="5">5</option>
				    <option value="6">6</option>
				    <option value="7">7</option>
				    <option value="8">8</option>
				    <option value="9">9</option>
				    <option value="10">10</option>
				    <option value="11">11</option>
				    <option value="12">12</option>
				    <option value="13">13</option>
				    <option value="14">14</option>
				    <option value="15">15</option>
				    <option value="16">16</option>
				    <option value="17">17</option>
				    <option value="18">18</option>
				    <option value="19">19</option>
				    <option value="20">20</option>
				    <option value="21">21</option>
				    <option value="22">22</option>
				    <option value="23">23</option>
				  </select>
				  <select name="agency_end_min">
				    <option value="0">00</option>
				     <option value="15">15</option>
				     <option value="30">30</option>
				     <option value="45">45</option>
				  </select>			</td>
			  </tr>
			  
			  <tr>
			    <td>代办简介</td>
			    <td colspan="4"><label>
			      <textarea name="agency_intro" cols="50" rows="3">
			        给我代办个事情，我在上面写清楚了
			      </textarea>
			    </label></td>
			  </tr>
			  <tr>
			    <td>&nbsp;</td>
			    <td colspan="4"><input type="submit" name="Submit" value=" " class="sure_submit"/></td>
			    
		      </tr>
			</table></td>
			</tr>
		</table>
		<input type="hidden" value="<%=list.size() %>" name="agency_pri_count"/>
		<input type="hidden" name="opflag" value="add_agency"/>
		<input type="hidden" name="user_id" value="<%=user_id %>"/>
		</form>
</div>
 
  </body>
</html>
