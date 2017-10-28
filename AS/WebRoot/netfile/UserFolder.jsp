<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page language="java" import="com.as.dao.NetfileDAO,com.as.vo.FolderVO"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">

    <title>My JSP 'UserFolder.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  <script language = "javascript" type="text/javascript">
     function delfile_check(folder_id){
    	 if(confirm("确实要删除吗?")){
       		document.user_folder.action="./servlet/UploadFile?submitFrom=user_folder&folder_id="+folder_id;
       		document.user_folder.submit();
     	}
     }
    </script>
 
  </head>
  
  <body>
  <form name="user_file" method="post">
        <table border="1" width="90%"> 
        <tr>
            <th scope="col">文件名</th> 
		    <th scope="col">文件类型</th> 
		    <th scope="col">文件路径</th> 
		    <th scope="col">删除</th>
	      </tr> 
	      <%
	         int user_id =2;
	         NetfileDAO netfile=new NetfileDAO();
	         FolderVO foldervo = new FolderVO();
	         List fileList =new ArrayList();
	         fileList=netfile.getNetFileByfolder_id(folder_id) ;
	         Iterator it =  fileList.iterator();
	          
	          while(it.hasNext())
	          {
	             foldervo=(FolderVO) it.next();
	          %>
	             <tr>
	             <td><%=foldervo. %></td>
	             <td><%=foldervo.getNetfile_type() %></td>
	             <td><%=foldervo.getNetfile_path() %></td>
	             <td align="center">
				
			    <input type="button" value="删除" onclick="delfile_check(<%=netfilevo.getNetfile_id()%>)"/>
			    <%System.out.println("id:"+netfilevo.getNetfile_id()) ;%>
			    </td>
	             </tr>
	          <%          
	          }
	       %>
	      </table>
      </form>
  </body>
</html>
