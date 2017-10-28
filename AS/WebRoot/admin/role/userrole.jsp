<%@ page language="java" import="java.util.*,com.as.*" pageEncoding="UTF-8"%>
<jsp:directive.page import="com.as.dao.AgencyDAO"/>
<jsp:directive.page import="com.as.vo.AgencyVO"/>
<jsp:directive.page import="com.as.vo.Agency_listVO"/>
<jsp:directive.page import="javax.management.relation.Role"/>
<jsp:directive.page import="com.as.dao.RoleDAO"/>
<jsp:directive.page import="com.as.vo.RoleVO"/>
<jsp:directive.page import="com.as.dao.UserDAO"/>
<jsp:directive.page import="com.as.vo.UserVO"/>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

RoleDAO roledao = new RoleDAO();
RoleVO rolevo = new RoleVO();

UserDAO userdao = new UserDAO();
UserVO uservo = new UserVO();

List list = new ArrayList();
list = roledao.getUserRoleList();
Iterator it = list.iterator();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  <link href="../../css/base.css" rel="stylesheet" type="text/css" />
    <base href="<%=basePath%>">

    <title>My JSP 'index.jsp' starting page</title>
 
<script language="javascript">
  function userrole_del(user_role_rs_id){
    if(confirm("确认删除这个种类")){
    window.location.href="./admin/role/RoleServlet?opflag=userrole_del&user_role_rs_id="+user_role_rs_id;
    }
  }
  function role_add(){
    if(document.role_add_form.role_name.value==""){
      alert ("您的权限的名称没有填写");
      doucment.role_add_form.role_name.focus();
      }
      else{
       document.role_add_form.submit();
      }
  }
</script>
  </head>
  
  <body> 
  <div style="height:300px; overflow:scroll">
  <h1>用户权限组的赋予</h1>
 
 <%
   String flag = "";
   if(request.getAttribute("flag")!=null){
      flag = (String)request.getAttribute("flag");
       if(flag.equals("1")) out.println(" <div class='successFail'>操作成功 </div>");
       else if(flag.equals("0"))out.println(" <div class='successFail'>操作失败 </div>");
   }
   else{
    flag = "";
   }
 %>

  <table width="98%" border="1">
  <tr class="smallhead">
  <td>序号</td>
  <td>用户名</td>
    <td >角色名称</td>
      <td >批准会议</td>
      <td>设备管理</td>
      <td> 安排工作</td>
      <td>账号删除</td>
      <td>账号更新</td>
      <td >备注1</td>
       <td >备注2</td>
      <td >操作</td></tr>
      
   <% int i = 1;
   while(it.hasNext()){
   rolevo = (RoleVO)it.next();
  %>
      <tr>
      <td><%=i%></td>
      <td><%=rolevo.getUser_true_name()%></td>
      <td> <%=rolevo.getRole_name() %></td>
      <td> <%if(rolevo.getMeeting_agree()==1)out.println("有"); %></td>
      <td><%if(rolevo.getDevice_mag()==1)out.println("有"); %></td>
      <td><%if(rolevo.getAssign_work()==1)out.println("有"); %> </td>
      <td> <%if(rolevo.getAccount_add_del()==1)out.println("有"); %></td>
      <td> <%if(rolevo.getAccount_update()==1)out.println("有"); %></td>
      <td> <%if(rolevo.getNew_power1()==1)out.println("有"); %></td>
      <td><%if(rolevo.getNew_power2()==1)out.println("有"); %></td>
      <td><input type="button" value="删除" onclick="userrole_del(<%=rolevo.getUser_role_rs_id() %>)" /></td>
      </tr>
      <%
       i ++;
      } %>
  </table>
  
  <h2>新增用户权限</h2>
  <form action="./admin/role/RoleServlet" method="post" />
 用户名<select name="user_id"/>
 <% list = userdao.getAllUserList();
   it = list.iterator();
 while(it.hasNext()){
 uservo = (UserVO)it.next();
 %>
    <option value="<%=uservo.getUser_id() %>"><%=uservo.getUser_true_name() %></option>
    <%} %>
 </select>
权限
<select name="role_id">
<%
   list = roledao.getRoleList();
   it = list.iterator();
   while(it.hasNext()){
   rolevo = (RoleVO)it.next();
 %>
 <option value="<%=rolevo.getRole_id()%>"><%=rolevo.getRole_name() %></option>
 <%} %>
</select>
<input type="hidden" name="opflag" value="userrole_add"/>
<input type="submit" value="新增角色" />
  </form>
  </div>
  </body>
</html>
