<%@ page language="java" import="java.util.*,com.as.dao.*,com.as.vo.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
 <link type="text/css" href="../css/base.css" rel="stylesheet"/>
    <base href="<%=basePath%>">
    
    <title>My JSP 'inner_list.jsp' starting page</title>
    
 
	<script>
		function backtofinish_meeting(){
			window.history.back();
		}
	</script>
  </head>
  
  <body>
 <form name="inner_list" action="/AS/conference/MeetingServlet" method="post">
 <input type="hidden" name="opflag" value="inner_list" />
	<table width="90%" border>
		<tr>
			<td>选择</td>
			<td>职工号</td>				
			<td>姓名</td>
			<td>性别</td>
			<td>部门</td>
			<td>详细</td>
		</tr>
		<%
			List li=new ArrayList();
			li=(List)request.getAttribute("ulist");
			String title=(String)request.getAttribute("title");
			String intro=(String)request.getAttribute("intro");
				UserDAO userdao=new UserDAO();
				List list=userdao.getAllUserList();
				Iterator it=list.iterator();
				while(it.hasNext())
				{
					UserVO uservo=(UserVO)it.next();
					String user_no=uservo.getUser_number();
					String sex;
					if(uservo.getUser_sex()==1){
						sex="男";
					}else{
						sex="女";
					}
					DeptDAO deptdao=new DeptDAO();
					String dept_name=deptdao.getDeptById(uservo.getDept_id()).getDept_name();					
		 %>
		 
		 <tr>
		 <%
		 	if(li!=null){
		 		boolean c=li.contains(uservo);
				if(c){
		%>
			 <td><input type="checkbox" name="<%=uservo.getUser_id() %>" checked /></td>		
		<%
				}else{
		%>
			 <td><input type="checkbox" name="<%=uservo.getUser_id() %>" unchecked /></td>		
		<%	
				}
			}else{
		 %>
			 <td><input type="checkbox" name="<%=uservo.getUser_id() %>" unchecked /></td>		 
		 <%} %>
			 <td><%=user_no %></td>
			 <td><%=uservo.getUser_true_name() %></td>
			 <td><%=sex %></td>
			 <td><%=dept_name %></td>
			 <td><a href="./userinfo.jsp"><font color="#0000ff">详细</a></td>
		 </tr>
		 <%}%>
	</table>
	<input type="hidden" name="title" value="<%=title %>"/>
	<input type="hidden" name="intro" value="<%=intro %>"/>
	<div align="center"><input type="submit" name="add_into" value="确定" />
		<input type="button" name="cancel_into" value="返回" onclick="backtofinish_meeting()"/>
	</div>
</form>
  </body>
</html>
