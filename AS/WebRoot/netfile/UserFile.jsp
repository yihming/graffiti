<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page language="java" import="com.as.dao.NetfileDAO,com.as.vo.NetfileVO,com.as.vo.FolderVO"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'UserFile.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
      <script language = "javascript" type="text/javascript">
     function delfile_check(netfile_id){
    	 if(confirm("确实要删除吗?")){
       		document.user_file.action="./servlet/UploadFile?submitFrom=user_file&netfile_id="+netfile_id;
       		document.user_file.submit();
     	}
     }
    </script>
 
  </head>
  
  <body>
      <form name="user_file" method="post">
        <table border="1" width="90%"> 
        <tr>
            <th scope="col">所在目录</th>
            <th scope="col">文件名</th> 
		    <th scope="col">文件类型</th> 
		    <th scope="col">文件路径</th> 
		    <th scope="col">删除</th>
	      </tr> 
	      <%
	         int user_id=42;
	        
	         NetfileDAO netfile=new NetfileDAO();
	         NetfileVO netfilevo = new NetfileVO();
	         FolderVO  foldervo=new FolderVO();
	         List folderList =new ArrayList();
	         folderList=netfile.getFolderByuser_id(user_id);
	         Iterator it1 =folderList.iterator();
	         while(it1.hasNext()){
	            foldervo=(FolderVO)it1.next();
	         int folder_id=foldervo.getFolder_id();
	         List fileList =new ArrayList();
	         fileList=netfile.getNetFileByfolder_id(folder_id) ;
	         Iterator it2 =  fileList.iterator();
	          
	          while(it2.hasNext())
	          {
	             netfilevo=(NetfileVO) it2.next();
	          %>
	             <tr>
	             <td align="center"><%=foldervo.getFolder_name() %></td>
	             <td align="center"><%=netfilevo.getNetfile_name() %></td>
	             <td align="center"><%=netfilevo.getNetfile_type() %></td>
	             <td align="center"><%=netfilevo.getNetfile_path() %></td>
	             <td align="center">
				
			    <input type="button" value="删除" onclick="delfile_check(<%=netfilevo.getNetfile_id()%>)"/>
			    <%System.out.println("id:"+netfilevo.getNetfile_id()) ;%>
			    </td>
	             </tr>
	          <%          
	          }
	          }
	       %>
	      </table>
      </form>
  </body>
</html>
