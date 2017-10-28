<%@ page language="java" import="java.util.*,com.as.*" pageEncoding="UTF-8"%>
<jsp:directive.page import="com.as.dao.AgencyDAO"/>
<jsp:directive.page import="com.as.vo.AgencyVO"/>
<jsp:directive.page import="com.as.vo.Agency_listVO"/>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

AgencyDAO agencydao = new AgencyDAO();
AgencyVO agencyvo = new AgencyVO();
Agency_listVO agency_listvo = new Agency_listVO();

List list = new ArrayList();
list = agencydao.getAgencyList();
Iterator it = list.iterator();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
  <link href="../../css/base.css" rel="stylesheet" type="text/css" />
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
    
 
<script language="javascript">
  function agency_del(agency_list_id){
    if(confirm("确认删除这个种类")){
    window.location.href="./admin/agency/AgencyServletAdmin?opflag=del&agency_list_id="+agency_list_id;
    }
  }
</script>
  </head>
  
  <body> 
  <div class="thickbox_content">
<h1>更改代办权限列表，暂时只能单个更新</h1>
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
      <td width="4%">编号</td>
      <td width="18%">代办事件名称</td>
      <td width="27%">url</td>
      <td width="8%">添加</td>
       <td width="6%">删除</td>
       <td width="7%">更新</td>
      <td width="8%">查询</td>
      <td width="22%">操作</td>
    </tr>
 <%while(it.hasNext()){
   agency_listvo = (Agency_listVO)it.next();
  %>
   <form action="./admin/agency/AgencyServletAdmin" method="post">
    <tr>
      <td><%=agency_listvo.getAgency_list_id() %></td>
      <td><input type="text" value="<%=agency_listvo.getAg_list_name() %>" name="ag_list_name"/></td>
      <td><input type="text" value="<%=agency_listvo.getAg_list_url() %>" name="ag_list_url"/></td>
      <td><input type="checkbox" value="1" name="ag_list_add" <%if(agency_listvo.getAg_list_add()==1)out.println("checked"); %>/></td>
      <td><input type="checkbox" value="1" name="ag_list_delete" <%if(agency_listvo.getAg_list_delete()==1)out.println("checked"); %>/></td>
      <td><input type="checkbox" value="1" name="ag_list_update" <%if(agency_listvo.getAg_list_update()==1)out.println("checked"); %>/></td>
      <td><input type="checkbox" value="1" name="ag_list_select" <%if(agency_listvo.getAg_list_select()==1)out.println("checked"); %>/></td>
      <td> 
      <input type="hidden" name="agency_list_id" value="<%=agency_listvo.getAgency_list_id() %>"/>
      <input type="hidden" name="opflag" value="update"/>
        <input type="submit" name="Submit" value="单个更新">
        <input type="button" name="del" value="删除" onClick="agency_del(<%=agency_listvo.getAgency_list_id()%>)">
       </td>
    </tr>
</form>
	 <%} %>
  </table>
    <h1>新建代办权限</h1>
	<table width="98%" border="1">
    
    <tr>
      
      <td width="18%">代办事件名称</td>
      <td width="27%">url</td>
      <td width="8%">添加</td>
       <td width="6%">删除</td>
       <td width="7%">更新</td>
      <td width="8%">查询</td>
      <td width="22%">操作</td>
    </tr>
   <form action="./admin/agency/AgencyServletAdmin" method="post">
    <tr>
      <td><input type="text" value="" name="ag_list_name"/></td>
      <td><input type="text" value="" name="ag_list_url"/></td>
      <td><input type="checkbox" value="1" name="ag_list_add" /></td>
      <td><input type="checkbox" value="1" name="ag_list_delete" /></td>
      <td><input type="checkbox" value="1" name="ag_list_update" /></td>
      <td><input type="checkbox" value="1" name="ag_list_select"/></td>
      <td> 
      <input type="hidden" name="opflag" value="add"/>
        <input type="submit" name="Submit" value="新建代办种类">
       </td>
    </tr>
   </form>
  </table>
  </div>
  </body>
</html>
