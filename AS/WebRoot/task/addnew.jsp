<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.as.dao.UserDAO" %>
<%@ page language="java" import="com.as.vo.UserVO" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
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

	</script>
    <base href="<%=basePath%>">
    <link type="text/css" href="../css/base.css" rel="stylesheet"/>
    <title>My JSP 'addnewtask.jsp' starting page</title>
    <script language="javascript">
       function sm_convert(){
         var list = document.forms[0].receiver;
         var user_name = list.options[list.selectedIndex].text;
        
         document.forms[0].user_name.value+=user_name;
         
        list.options[list.selectedIndex].disabled =true;
        document.forms[0].msg_user_id_list.value += list.options[list.selectedIndex].value+"+";
       }
       
       function task_check(){
       		//alert(document.forms[0].msg_rece.value);	
       		if(!document.task_form.user_name.value){
       			alert("需要的人不能为空！");
       		}else if(!document.task_form.task_title.value){
       			alert("任务主题不能为空！");
       		}else if(!document.task_form.task_end.value){
       			alert("结束时间不能为空！");
       		}else if(!document.task_form.task_intro.value){
       			alert("任务内容不能为空，请认真填写！");
       		}else{
       			document.task_form.action="./servlet/Taskservlet?submitFrom=addNew";
       			document.task_form.submit();
       		}
       		
       }
    </script>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
   
  <body>
   <form name="task_form" action="./servlet/Taskservlet?submitFrom=addNew" method="post"> 
  	<h1>添加新任务</h1> 
    <table border="1px"> 
	 <tr><td>需要的人</td><td><input type="text" name="user_name" type="text" size="50"></td>
	 <td>
	  <select name="receiver" onchange="sm_convert(); ">
	  <%
	
	  int msg_reply_to_id = 10;//attention
	
	  UserDAO userdao = new UserDAO();
	  List list = new ArrayList();
	  UserVO uservo = new UserVO();
	  list = userdao.getAllUserList();
	  Iterator it = list.iterator();	
	  %>
	  
	  <option value="">请选择需要的人
	  
	  <%
	  	while(it.hasNext())
	  {
	  	uservo = (UserVO)it.next();
//System.out.println("user_id:"+uservo.getUser_id()+"user_name:"+uservo.getUser_name());
	   %>
	   <option value="<%=uservo.getUser_id() %>"><%=uservo.getUser_name() %>
	  <%
	   }
	  %>
	  </select>
	 </td> 
	 </tr> 
	  <tr>
				<td>开始时间</td>
				<td colspan="4"> 
				  <input  id="datepicker1" type="text" name="task_start"/>
				  <select name="task_start_hour">
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
				  <select name="task_start_min">
				    <option value="0">00</option>
				     <option value="15">15</option>
				     <option value="30">30</option>
				     <option value="45">45</option>
				  </select>
				  </td></tr>
				  <tr>
				  <td>结束时间</td>
				  <td><input name="task_end" id="datepicker2" type="text"/>	
					<select name="task_end_hour">
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
				  <select name="task_end_min">
				    <option value="0">00</option>
				     <option value="15">15</option>
				     <option value="30">30</option>
				     <option value="45">45</option>
				  </select>			</td>
			  </tr>
	
	 
	 <tr><td>任务标题</td><td><input name="task_title" type="text" size="50"></td> </tr> 
	 <tr><td>任务内容</td><td><textarea name="task_intro" cols="50" rows="6"></textarea></td> </tr>
	 <input type="hidden" name="msg_reply_to_id" value="<%=msg_reply_to_id %>"/>
	 <input type="hidden" vlaue="" name="msg_user_id_list"/>
	 <tr><td>&quot;</td><td align="center"><input type="button" onclick="task_check()" value="提交"/></td></tr> 
	 
	</table> 
  </form> 
  </body>
</html>
