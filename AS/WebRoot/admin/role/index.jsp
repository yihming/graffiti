<%@ page language="java" import="java.util.*,com.as.*" pageEncoding="UTF-8"%>
<jsp:directive.page import="com.as.dao.AgencyDAO"/>
<jsp:directive.page import="com.as.vo.AgencyVO"/>
<jsp:directive.page import="com.as.vo.Agency_listVO"/>
<jsp:directive.page import="javax.management.relation.Role"/>
<jsp:directive.page import="com.as.dao.RoleDAO"/>
<jsp:directive.page import="com.as.vo.RoleVO"/>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

RoleDAO roledao = new RoleDAO();
RoleVO rolevo = new RoleVO();
 

List list = new ArrayList();
list = roledao.getRoleList();
Iterator it = list.iterator();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
<link href="../../css/base.css" rel="stylesheet" type="text/css" />

    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
 
<script language="javascript">
  function role_del(role_id){
    if(confirm("确认删除这个种类")){
    window.location.href="./admin/role/RoleServlet?opflag=del&role_id="+role_id;
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
<h1>角色权限修改</h1>
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
      <td >编号</td>
      <td >角色名称</td>
      <td >批准会议</td>
      <td>设备管理</td>
      <td> 安排工作</td>
      <td>账号删除</td>
      <td>账号更新</td>
      <td >备注1</td>
       <td >备注2</td>
 
      <td >操作</td>
    </tr>
 <%while(it.hasNext()){
   rolevo = (RoleVO)it.next();
  %>
   <form action="./admin/role/RoleServlet" method="post">
    <tr>
      <td><%=rolevo.getRole_id() %></td>
      <td><input type="text" value="<%=rolevo.getRole_name() %>" name="role_name"/></td>
      <td><input type="checkbox" value="1" name="meeting_agree" <%if(rolevo.getMeeting_agree()==1)out.println("checked"); %>/></td>
      <td><input type="checkbox" value="1" name="device_mag" <%if(rolevo.getDevice_mag()==1)out.println("checked"); %>/></td>
      <td><input type="checkbox" value="1" name="assign_work" <%if(rolevo.getAssign_work()==1)out.println("checked"); %>/></td>
      <td><input type="checkbox" value="1" name="account_add_del" <%if(rolevo.getAccount_add_del()==1)out.println("checked"); %>/></td>
      <td><input type="checkbox" value="1" name="account_update" <%if(rolevo.getAccount_update()==1)out.println("checked"); %>/></td>
      <td><input type="checkbox" value="1" name="new_power1" <%if(rolevo.getNew_power1()==1)out.println("checked"); %>/></td>
      <td><input type="checkbox" value="1" name="new_power2" <%if(rolevo.getNew_power2()==1)out.println("checked"); %>/></td>
      
      <td> 
      <input type="hidden" name="role_id" value="<%=rolevo.getRole_id() %>"/>
      <input type="hidden" name="opflag" value="update"/>
        <input type="submit" name="Submit" value="单个更新">
        <input type="button" name="del" value="删除" onClick="role_del(<%=rolevo.getRole_id()%>)">      </td>
    </tr>
</form>
	 <%} %>
  </table>
    <h1>新增角色</h1>
	<table width="98%" border="1">
    
    <tr>
      <td >角色名称</td>
      <td >批准会议</td>
      <td>设备管理</td>
      <td> 安排工作</td>
      <td>账号删除</td>
      <td>账号更新</td>
      <td >备注1</td>
       <td >备注2</td>
      <td >操作</td>
    </tr>
   <form action="./admin/role/RoleServlet" method="post" name="role_add_form">
    <tr>
      <td><input type="text" value="" name="role_name" disable/></td>
      <td><input type="checkbox" value="1" name="meeting_agree" /></td>
      <td><input type="checkbox" value="1" name="device_mag" /></td>
      <td><input type="checkbox" value="1" name="assign_work" /></td>
      <td><input type="checkbox" value="1" name="account_add_del"/></td>
      <td><input type="checkbox" value="1" name="account_update"/></td>
      <td><input type="checkbox" value="1" name="new_power1"/></td>
      <td><input type="checkbox" value="1" name="new_power2"/></td>
      <td> 
      <input type="hidden" name="opflag" value="add"/>
        <input type="button" name="Submit" value="确认添加新角色" onclick="role_add()"/>
      </td>
    </tr>
   </form>
  </table>
  </body>
</html>
