<%@ page language="java" import="java.util.*,com.as.dao.*,com.as.function.*,com.as.vo.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <link href="../../css/base.css" rel="stylesheet" type="text/css" />
    <base href="<%=basePath%>">
    
    <title>设备管理</title>
    
    <script>
    	function update(id){
			window.location.href="/AS/admin/conference/update_device.jsp?id="+id;
		}
		
		function del(id){		
			var b =confirm("确认要删除吗？");
			if(b){
				window.location.href="/AS/conference/DeviceServlet?opflag=del&id="+id;
			}else{
				alert("删除操作取消！");
			}
		}
		
		<!-- 翻页操作 -->
		  function changepage(currentpage){
				document.formpage.currentPage.value=currentpage;
				document.formpage.submit();
				return false;
		}
		
    </script>
        
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link href="../../css/base.css" rel="stylesheet" type="text/css">
 	</head>
  
 	<body> 		
		<div class="thickbox_content">
		<h1>设备管理</h1>

		<%
			String str=(String)request.getAttribute("success");
			if("del_success".equals(str))
			{
		%>
		<script>
		 	alert("删除成功!");		
		</script>
		<%
		 	}else if("del_failue".equals(str)){
		 %>
		<script>
		 	alert("删除失败，应该是还有会议在使用该类设备!");		
		</script>		 
		 <%
		 	}else if("add_success".equals(str))
		{
		 %>
	 <script>
	 alert("添加成功！");
	 </script>
	 <%
	 	}
	 %>
		<form>
			<table border="1" width="98%">
				<tr class="smallhead">
					<td align="center">设备名称</td>
					<td align="center">设备数目</td>
					<td align="center">损坏数目</td>
					<td align="center">当前可用数</td>
					<td align="center">操作</td>
				</tr>
				<%
					PageHelp pagehelp=null;
					List list =null;
						pagehelp=(PageHelp)request.getAttribute("pagehelp");
						
						if(pagehelp==null){
							int currentPage=1;
							DeviceDAO devicedao = new DeviceDAO();
							pagehelp =devicedao.queryDevice("",currentPage);
						}
						list=pagehelp.getObjectlist();
					Iterator it =list.iterator();
					while(it.hasNext()){
						DeviceVO devicevo=(DeviceVO)it.next();				
				 %>
				<tr>
					<td align="center"><%=devicevo.getDevice_name() %></td>
					<td align="center"><%=devicevo.getDevice_count() %></td>
					<td align="center"><%=devicevo.getDevice_damage() %></td>
					<td align="center"><%=devicevo.getDevice_valid() %></td>
					<td align="center">
						<input type="button" value="编辑" onClick="update(<%=devicevo.getDevice_id()%>)" />
						<input type="button" value="删除" onClick="del(<%=devicevo.getDevice_id() %>)" />
					</td>
				</tr>
				<%}%> 
			</table>
			
		</form>
		</div>

		<form name="formpage" action="/AS/conference/DeviceServlet" method="post">
			<table>			  
					<input type="hidden" name="opflag" value="changePage"/>
					<input type="hidden" name="currentPage" value="<%=pagehelp.getCurrentpage() %>"/> 
					<input type="hidden" name="condi" value="<%=pagehelp.getCondition()%>"/>
				<tr>
					<td height=25 cols="3"><%=pagehelp.getPagebar()%></td>
				</tr>
		</table>
		</form>

	</body>
	</html>
