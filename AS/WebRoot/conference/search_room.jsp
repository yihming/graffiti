<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
  <link type="text/css" href="../css/base.css" rel="stylesheet"/>
    <base href="<%=basePath%>">
    
    <title>会议室查询及修改</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link href="../css/base.css" rel="stylesheet" type="text/css" />

  </head>
  
  <body>
<form>
<div align="center">
	<table border="1" bordercolor="#B1CEEE" width="70%" cellpadding="10">
		<tr bgcolor="#EBF4FD"><td colspan="9"><span class="style5">会议室信息如下：</span></td>
		</tr>
		<tr bgcolor="#FFFDF0" align="center">
			<td rowspan="3"><span class="style5">会议室</span></td>
			<td rowspan="3"><span class="style5">大小</span></td>
			<td colspan="4"><span class="style5">拥有设备</span></td>
			<td rowspan="3"><span class="style5">备注</span></td>
			<td rowspan="3"><span class="style5">操作</span></td>
		</tr>
		<tr bgcolor="#FFFDF0" align="center">
			<td colspan="2">投影仪</td>
	  		<td colspan="2">电视</td>
	  	</tr>
		<tr bgcolor="#FFFDF0" align="center">
		  <td>总数</td>
	  <td>损坏</td>
	      <td>总数</td>
	  <td>损坏</td>
	  </tr>
		<tr bgcolor="#ECF3FD" align="center">
			<td><span class="style5">电子楼104</span></td>
			<td><span class="style5">40</span></td>
			<td>1</td>
	    	<td>0</td>
	    	<td>0</td>
	    	<td>0</td>
	    	<td><span class="style5">&nbsp;</span></td>
	    	<td><input type="button" value="编辑" onclick="doEdit(this)" /><input type="button" value="删除" onclick="doDelete(this)" /></td></tr>
		<tr bgcolor="#ECF3FD" align="center">
			<td><span class="style5">英东楼217</span></td>
			<td><span class="style5">20</span></td>
			<td>1</td>
			<td>0</td>
			<td>2</td>
			<td>1</td>
			<td><span class="style5">有沙发</span></td>
			<td><input type="button" value="编辑" onclick="doEdit(this)" /><input type="button" value="删除" onclick="doDelete(this)" /></td>
		</tr>
		<tr bgcolor="#ECF3FD" align="center">
			<td><span class="style5">敬文讲堂</span></td>
			<td><span class="style5">300</span></td>
			<td>1</td>
			<td>0</td>
			<td>3</td>
			<td>0</td>
			<td><span class="style5">有空调和舞台</span></td>
			<td><input type="button" value="编辑" onclick="doEdit(this)" /><input type="button" value="删除" onclick="doDelete(this)" /></td>
		</tr>
		<tr bgcolor="#ECF3FD" align="center">
			<td><span class="style5">电子楼400</span></td>
			<td><span class="style5">30</span></td>
			<td>1</td>
			<td>1</td>
			<td>0</td>
			<td>0</td>
			<td><span class="style5">&nbsp;</span></td>
			<td><input type="button" value="编辑" onclick="doEdit(this)" /><input type="button" value="删除" onclick="doDelete(this)" /></td>
		</tr>
  </table>
</form>
<script>
	//修改   
  function   doEdit(src)   {   
  if(src.value   ==   "编辑")   {   
        for(i   =   0;   i   <   7;   i++)   {   
                  obj   =   src.parentElement.parentElement.children[i];   
                  obj.innerHTML   =   "<input size='8' type='text' value='"   +     obj.innerText   +   "'>";   
        }   
          src.value="保存";   
  }else   if(src.value   ==   "保存"){   
          for(i   =   0;   i   <  7;   i++)   {   
  			obj   =   src.parentElement.parentElement.children[i];   
  			obj.innerHTML   =   obj.children[0].value;   
  		}   
        src.value="编辑";   
  	}   
  }   
    
  //删除   
  function   doDelete(src)   {   
                                i   =   src.parentElement.parentElement.rowIndex;   
    
            if(window.confirm("确定要删除吗?"))   {   
  src.parentElement.parentElement.parentElement.deleteRow(i);   
            }   
  }   
</script>
</body>
</html>
