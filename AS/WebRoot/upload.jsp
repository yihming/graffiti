<%@ page contentType="text/html; charset=gb2312" language="java" %>  
<html>  
<head>  
<title>文件上传</title>  
</head>  
  
<body>  
<form name="form1" method="post" action="./netfile/UploadFile.jsp" enctype="multipart/form-data">  
文件上传:   
<label>  
<input type="file" name="upfile" size="50" />  
</label>  
<p>  
<label>  
<input type="submit" name="Submit" value="提交" />  
</label>  
</p>  
</form>  
<script type='text/javascript'>  
//<![CDATA[  
document.getElementById('processtime').innerHTML="<span style='font-size: 8pt; font-family: Georgia;'>Run in 158 ms, 10 Queries, Gzip enabled.</span>";  
//]]>  
</script>

</body>  

</html>  
