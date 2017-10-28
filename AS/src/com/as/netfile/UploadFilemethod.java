package com.as.netfile;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.*;


public class UploadFilemethod {
    /** request对象 */
    private HttpServletRequest request = null;
    /** 上传文件的路径 */
    private String uploadPath = null;
    /** 每次读取得字节的大小 */
    private static int BUFSIZE = 1024 * 8;
    /** 存储参数的Hashtable */
    private Hashtable paramHt = new Hashtable();
    /** 存储上传的文件的文件名的ArrayList */
    private ArrayList updFileArr = new ArrayList();
    private ArrayList FileArr = new ArrayList();

    /**
     * 设定request对象。
     * 
     * @param request
     *            HttpServletRequest request对象
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
    /**
     * 设定文件上传路径。
     * 
     * @param path
     *            用户指定的文件的上传路径。
     */
    public void setUploadPath(String path) {
        this.uploadPath = path;
    }
    /**
     * 文件上传处理主程序。
     * 
     * @return int 操作结果 0 文件操作成功；1 request对象不存在。 2 没有设定文件保存路径或者文件保存路径不正确；3
     *         没有设定正确的enctype；4 文件操作异常。
     */
    public int process() {
        int status = 0;
        // 文件上传前，对request对象，上传路径以及enctype进行check。
        status = preCheck();
        // 出错的时候返回错误代码。
        if (status != 0)
            return status;
        try {
            // &#65533;&#65533;参数或者文件名&#65533;u&#65533;&#65533;
            String name = null;
            // 参数的value
            String value = null;
            // 读取的流是否为文件的标志位
            boolean fileFlag = false;
            // 要存储的文件。
            File tmpFile = null;
            // 上传的文件的名字
            String fName = null;
            FileOutputStream baos = null;
            BufferedOutputStream bos = null;
            // &#65533;&#65533;存储参数的Hashtable
            paramHt = new Hashtable();
            updFileArr = new ArrayList();
            FileArr = new ArrayList();
            
            int rtnPos = 0;
            byte[] buffs = new byte[BUFSIZE * 8];
            // &#65533;取得ContentType
            String contentType = request.getContentType();
            String boundary = "--";
            String endBoundary = boundary + "--";
            if(contentType!=null)
            {            
            	int index = contentType.indexOf("boundary=");
            	boundary = "--" + contentType.substring(index + 9);
            	endBoundary = boundary + "--";
            }
            // &#65533;从request对象中取得流。
            ServletInputStream sis = request.getInputStream();
            // 读取1行
            while ((rtnPos = sis.readLine(buffs, 0, buffs.length)) != -1) {
                String strBuff = new String(buffs, 0, rtnPos);
                // 读取1行数据&#65533;n&#65533;&#65533;
                if (strBuff.startsWith(boundary)) {
                    if (name != null && name.trim().length() > 0) {
                        if (fileFlag) {
                            bos.flush();
                            baos.close();
                            bos.close();
                            baos = null;
                            bos = null;
                            updFileArr.add(fName);
                                                  
                        } else {
                            Object obj = paramHt.get(name);
                           
                            ArrayList al = new ArrayList();
                            if (obj != null) {
                                al = (ArrayList) obj;
                            }
                            al.add(value);
                           System.out.println(value);
                            paramHt.put(name, al);
                        }
                    }
                    name = new String();
                    value = new String();
                    fileFlag = false;
                    fName = new String();
                    rtnPos = sis.readLine(buffs, 0, buffs.length);
                    if (rtnPos != -1) {
                        strBuff = new String(buffs, 0, rtnPos);
                        if (strBuff.toLowerCase().startsWith(
                                "content-disposition: form-data; ")) {
                            int nIndex = strBuff.toLowerCase().indexOf(
                                    "name=\"");
                            int nLastIndex = strBuff.toLowerCase().indexOf(
                                    "\"", nIndex + 6);
                            name = strBuff.substring(nIndex + 6, nLastIndex);
                        }
                        int fIndex = strBuff.toLowerCase().indexOf(
                                "filename=\"");
                        if (fIndex != -1) {
                            fileFlag = true;
                            int fLastIndex = strBuff.toLowerCase().indexOf(
                                    "\"", fIndex + 10);
                            fName = strBuff.substring(fIndex + 10, fLastIndex);
                            fName = getFileName(fName);
                            if (fName == null || fName.trim().length() == 0) {
                                fileFlag = false;
                                sis.readLine(buffs, 0, buffs.length);
                                sis.readLine(buffs, 0, buffs.length);
                                sis.readLine(buffs, 0, buffs.length);
                                continue;
                            }else{
                            	FileArr.add(fName);
                              
                                sis.readLine(buffs, 0, buffs.length);
                                sis.readLine(buffs, 0, buffs.length);
                            }
                        }
                    }
                } else if (strBuff.startsWith(endBoundary)) {
                    if (name != null && name.trim().length() > 0) {
                        if (fileFlag) {
                            bos.flush();
                            baos.close();
                            bos.close();
                            baos = null;
                            bos = null;
                            updFileArr.add(fName);
                      
                        } else {
                            Object obj = paramHt.get(name);
                            ArrayList al = new ArrayList();
                            if (obj != null) {
                                al = (ArrayList) obj;
                            }
                            al.add(value);
                            paramHt.put(name, al);
                           
                        }
                    }
                } else {
                    if (fileFlag) {
                        if (baos == null && bos == null) {
                            tmpFile = new File(uploadPath + fName);
                            baos = new FileOutputStream(tmpFile);
                            bos = new BufferedOutputStream(baos);
                        }
                        bos.write(buffs, 0, rtnPos);

                        baos.flush();
                    } else {
                        System.out.println("test :" + value + "--" + strBuff);
                        value = value + strBuff;
                    }
                }
            }
        } catch (IOException e) {
            status = 4;
        }
        return status;
    }
    
    private int preCheck() {
        int errCode = 0;
        if ( request == null )
            return 1;
        if ( uploadPath == null || uploadPath.trim().length() == 0 )
            return 2;
        else{
            File tmpF = new File(uploadPath);
            if (!tmpF.exists())
                return 2;
        }
        String contentType = request.getContentType();
        if ( contentType != null && contentType.indexOf("multipart/form-data") == -1 )
            return 3;
        return errCode;
    }
    
    public String getParameter(String name){
        String value = "";
        if ( name == null || name.trim().length() == 0 )
            return value;
        value = (paramHt.get(name) == null)?"":(String)((ArrayList)paramHt.get(name)).get(0);
        return value;
    }
    public String[] getParameters(String name){
        if ( name == null || name.trim().length() == 0 )
            return null;
        if ( paramHt.get(name) == null )
            return null;
        ArrayList al = (ArrayList)paramHt.get(name);
        String[] strArr = new String[al.size()];
        for ( int i=0;i<al.size();i++ )
            strArr[i] = (String)al.get(i);
        return strArr;
    }
    
    public String[] getUpdFileNames(){
        String[] strArr = new String[updFileArr.size()];
        for ( int i=0;i<updFileArr.size();i++ )
            strArr[i] = (String)updFileArr.get(i);
        return strArr;
    }
    public String[] getFileNames(){
        String[] strArr = new String[FileArr.size()];
        for ( int i=0;i<FileArr.size();i++ )
            strArr[i] = (String)FileArr.get(i);
        return strArr;
    }
   /* public int[] getFileSizes(){
        ArrayList strArr = new ArrayList();
        for ( int i=0;i<FileSize.size();i++ )
            strArr[i] = FileSize.get(i);
        return strArr;
    }*/
    private String getFileName(String input){
        int fIndex = input.lastIndexOf("\\");
        if (fIndex == -1) {
            fIndex = input.lastIndexOf("/");
            if (fIndex == -1) {
                return input;
            }
        } 
        input = input.substring(fIndex + 1);
        return input;
    }
   
   public static void  main( String args[])
   {
	   UploadFilemethod uf1= new UploadFilemethod();
	   System.out.println(uf1.process());
   }
   
}
