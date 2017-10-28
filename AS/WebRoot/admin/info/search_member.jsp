<%@ page language="java" import="java.util.*,com.as.dao.*,com.as.vo.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'search_member.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
		<style type="text/css"> 
		 #container1{visibility:visible;} 
		 #container2{visibility:hidden;}
	 </style> 
	 
<script>
	function submitsearch()
	{
			if(document.seach_member.search_type.value=="3"){
				document.seach_member.search_key.value=document.seach_member.dept_name.value;
			}
		document.seach_member.submit();
	}
	
	function change_select(){
		if(document.seach_member.search_type.value=="3"){
			container2.style.visibility='visible';
			container1.style.visibility='hidden';
		}else{
			document.seach_member.search_key.value="";
			container1.style.visibility='visible';
			container2.style.visibility='hidden';
		}
	}
</script>
  </head>
  
  <body>
	<form name="seach_member" action="/AS/userinfo" method="post">&nbsp; 
	<input type="hidden" name="action" value="search" />
	查询类型 &nbsp;&nbsp;&nbsp;<select name="search_type" onclick="change_select()">
					<option value="1">按员工号查询</option>
					<option value="2">按姓名查询</option>	
					<option value="3">按部门查询</option>
				</select>
	<br />
	 <div id="container1"> 
		 	查询关键字&nbsp;&nbsp;<input type="text" name="search_key"/>
 	</div> 
	 <div id="container2">
		 	查询关键字&nbsp;&nbsp;<select name="dept_name">
				 	 	<%
					 		DeptDAO deptdao=new DeptDAO();
					 		List li=new ArrayList();
					 		li=(List)deptdao.getAllDeptList();
					 		Iterator it=li.iterator();
					 		while(it.hasNext()){
					 			DeptVO deptvo=new DeptVO();
					 			deptvo=(DeptVO)it.next();
					 	 %>
		 							<option value="<%=deptvo.getDept_name() %>"><%=deptvo.getDept_name() %></option>
		 				<%} %>
 							</select>
	 </div> 

	<br />
	<input type="button" value="查询" onclick="submitsearch()"/>
	<input type="reset" name="reset" value="重置"/>
	</form>
  </body>
</html>
