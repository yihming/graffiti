package org.bnuminer.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bnuminer.Functions;


public class FileTest {
	public static void main(String[] args) {
		FileTest ft = new FileTest();
		//File here = new File("D:/test.class");
		//ft.checkFileCreate(here);
		//ft.checkFileModify(here);
		//ft.checkFileSize(here);
		//ft.readFileContent("/conf/sys.config");
		
		File originFile = new File("D:/weka");
		ft.testDirectoryName(originFile);
		
	}
	
	void readFileContent(String path) {
		try {
			InputStream is = getClass().getResourceAsStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			List<String> listLines = new ArrayList<String>();
			String line;
			do {
				line = reader.readLine();
				listLines.add(line);
			} while (line != null);
			
			for (int i = 0; i < listLines.size(); ++i) {
				
				// 忽略空格行和注释行
				if (listLines.get(i) == null)
					break;
				
				String regex = "^#|^\\s*$";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(listLines.get(i));
				if (m.find())
					continue;
				else { // 不是空格和注释行
					System.out.println("Line No. " + i);
					// 取得参数
					String[] options = new String[2];
					options = listLines.get(i).split("\\s+");
						
					System.out.println(options[0]);
					System.out.println(options[1]);
						
						
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	void checkFileSize(File chosen_file) {
		Long size = chosen_file.length();
		System.out.println(size.intValue() + " Bytes");
	}
	
	void checkFileCreate(File chosen_file) {
		 System.out.println(Functions.getCurrentDatetime());
	}
	
	void checkFileModify(File chosen_file) {
		Date date = new Date(chosen_file.lastModified());
		Time time = new Time(chosen_file.lastModified());
		
		System.out.println(date.toString());
		System.out.println(time.toString());
	}
	
	void moveFile(File oldFile, String newPath) {
		File newFile = new File(newPath);
		System.out.println(newFile.getName());
		if (!newFile.exists())
			newFile.mkdirs();
		
		newFile = new File(newPath + "/" + oldFile.getName());
		oldFile.renameTo(newFile);
	}
	
	void testDirectoryName(File file) {
		System.out.println(file.getPath());
	}
}
