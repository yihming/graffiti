<%@ page language="java" import="java.util.*,com.as.dao.*,com.as.vo.*" pageEncoding="UTF-8"%>
<jsp:directive.page import="com.as.function.*;"/>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'search_member_result.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<script>
	  function changepage(currentpage){
			document.formpage.currentPage.value=currentpage;
			document.formpage.submit();
			return false;
	}
</script>

  </head>
  
  <body>
	<form >
		<table width="80%" cellSpacing=1 cellPadding=0 border="0">
		<caption>查询结果</caption>
		<tr>
		<td align="center">姓名</td>
		<td align="center">部门</td>
		<td align="center">办公室电话</td>
		<td align="center">电子信箱</td>
		<td align="center">操作</td>
		</tr>
					<%
						PageHelp pagehelp=null;
						List list =null;
						String dept_name="";
							pagehelp=(PageHelp)request.getAttribute("pagehelp");
							
							if(pagehelp==null){
								int currentPage=1;
								UserDAO userdao = new UserDAO();
								pagehelp =userdao.queryUser("",currentPage);
							}
							list=pagehelp.getObjectlist();
						Iterator it =list.iterator();
						DeptDAO deptdao=new DeptDAO();
						while(it.hasNext()){
							UserVO uservo=(UserVO)it.next();
							dept_name=deptdao.getDeptById(uservo.getDept_id()).getDept_name();				
					 %>
		<tr>
		<td align="center"><%=uservo.getUser_name() %></td>
		<td align="center"><%=dept_name %></td>
		<td align="center"><%=uservo.getUser_phone() %></td>
		<td align="center"><%=uservo.getUser_mail() %></td>
		<td align="center"><input type="submit" name="edit_user" value="编辑" /><input type="button" value="删除" onClick="doDelete(this)" /></td>
		</tr>
		<%} %>
		</table>
	</form>
	
	<form name="formpage" action="/AS/userinfo" method="post">
		<table>			  
				<input type="hidden" name="action" value="changePage"/>
				<input type="hidden" name="currentPage" value="<%=pagehelp.getCurrentpage() %>"/> 
				<input type="hidden" name="condi" value="<%=pagehelp.getCondition()%>"/>
			<tr>
				<td height=25 cols="3"><%=pagehelp.getPagebar()%></td>
			</tr>
	</table>
	</form>
  </body>
</html>
