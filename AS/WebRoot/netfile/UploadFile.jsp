<%@ page 	contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*,com.as.netfile.*,com.as.vo.*,java.sql.*,com.as.db.*,com.as.dao.*"%>
<html>
<head>
<title>get method</title>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
</head>
<body>
<%
	String userno =(String)session.getAttribute("uno");
	UploadFilemethod fu = new UploadFilemethod();
	//String sucMsg = "添加成功！";
	
	String path = "D:\\upload\\";
	fu.setRequest(request);
	fu.setUploadPath(path);
	fu.process();
	
	DbConnection db = new DbConnection();
	ResultSet rs=db.st.executeQuery("select seq_folder.nextval as netfile_id from dual");
	String pid = fu.getParameter("netfile_id");
	
	String fileName ="";
	String[] FileNames = fu.getFileNames();
	String[] uploadFileNames = fu.getUpdFileNames();
	String uploadFileName = "";
	if(uploadFileNames.length > 0)
	{
		uploadFileName = uploadFileNames[0];
	}
	if(FileNames.length > 0)
	{
		fileName = FileNames[0];
	}
	
	int UID=-1;
	if(rs.next())
		UID=rs.getInt("netfile_id");			
	rs.close();
	//java.util.Date date = new java.util.Date();
	//java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//String  datestr = format.format(date); 
	rs = db.st.executeQuery("select netfile_share from netfile where netfile_id = "+pid);
	
	String fileshare = "0";
	if(rs.next())
	{
		fileshare = rs.getString(1);
	}
	rs.close();
	String sql = "insert into netfile(netfile_id,folder_id,netfile_name,netfile_type,netfile_path,netfile_share) values("+UID+"," + pid
					+ uploadFileName+ "', 0,"+path+","+fileshare+")";
	
	db.st.executeUpdate(sql);
	db.closeConnection();
%>
<p>添加成功!
<p>
<a href="upload.jsp?flag=<%=pid %>">返回</a>
</body>
</html>
