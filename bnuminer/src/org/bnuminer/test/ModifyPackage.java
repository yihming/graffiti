package org.bnuminer.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModifyPackage {
	public static void main(String[] args) {
		ModifyPackage mp = new ModifyPackage();
		
		mp.modify("D:/Eclipse_workspace/BnuMiner/src/org/bnuminer/weka");
		System.out.println("OK!");
		//mp.testModify("D:/Eclipse_workspace/weka/associations/Tertius.java");
		//mp.testIndexOf("import weka.core.Instances");
	}
	
	public void testIndexOf(String base) {
		System.out.println(base.indexOf("import weka."));
		String second = base.replaceAll("import weka", "import org.bnuminer.weka");
		//base.replaceAll("import\\ weka\\.", "import org.bnuminer.weka.");
		System.out.println(second);
	}
	
	public void testModify(String path) {
		
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			List<String> listLines = new ArrayList<String>();
			String line;
			do {
				line = reader.readLine();
				listLines.add(line);
			} while (line != null);
			
			reader.close();
			
			File changed = new File("D:/Eclipse_workspace/weka/temp.java");
			
			
			for (int i = 0; i < listLines.size() - 1; ++i) {
				line = listLines.get(i);
				
				if (line.contains("package weka")) {
					line = line.replace("package weka", "package org.bnuminer.weka");
				} else if (line.contains("import weka")) {
					line = line.replace("import weka", "import org.bnuminer.weka");
				}
				
				listLines.set(i, line);
			}
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(changed));
			for (int i = 0; i < listLines.size() - 1; ++i) {
				line = listLines.get(i);
				writer.write(line + "\n");
			}
			
			writer.flush();
			writer.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testParent(String path) {
		File current_file = new File(path);
		File[] children = new File[50];
		children = current_file.listFiles();
		
		if (children.length != 0) {
		
			for (int i = 0; i < children.length; ++i) {
				System.out.println(children[i].getPath());
			}
		} else {
			System.out.println("Not child!");
		}
	}
	
	// 正式程序函数
	public void modify(String path) {
		File current_file = new File(path);
		
		if (current_file.isDirectory()) { // 文件夹
			
			if (! current_file.getName().equals(".svn")) { // 不是.svn文件夹
			
				File[] children = new File[100];
				children = current_file.listFiles();
			
				for (int i = 0; i < children.length; ++i) {
					modify(children[i].getPath());
				}
				
			} else { // 是.svn文件夹
				removeDirectory(current_file);
				
			}
			
			
		} else { // 文件
			String fileName = current_file.getName();
			
			if (fileName.contains(".") && fileName.substring(fileName.lastIndexOf(".") + 1).equals("java")) {
				// 为.java文件
				
				modifyContent(current_file);
				
			} // End of if.
			
		} // End of if.
	
	} // End of modify().
	
	
	public void modifyContent(File file) {
		try {
			String filePath = file.getPath();
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			List<String> listLines = new ArrayList<String>();
			String line;
			
			do {
				// 逐行读取文件内容
				line = reader.readLine();
				listLines.add(line);
			} while (line != null);
				
			reader.close();
			
			file.delete();
			
			// 进行修改
			for (int i = 0; i < listLines.size() - 1; ++i) {
				line = listLines.get(i);
				
				
				if (line.contains("package weka.")) {
					line = line.replaceAll("package weka", "package org.bnuminer.weka");
					
				} else if (line.contains("import weka.")) {
					line = line.replaceAll("import weka", "import org.bnuminer.weka");
			
				}
				
				listLines.set(i, line);
			} // End of for.
			
			
			
			// 修改完毕，写入到原文件中
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
			for (int j = 0; j < listLines.size() - 1; ++j) {
				line = listLines.get(j);
				writer.write(line + "\n");
			}
			
			writer.flush();
			writer.close();
			
			System.out.println("完成修改  " + filePath);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void removeDirectory(File file) {
		
		if (file.isDirectory()) { // 是文件夹
		
			File[] children = new File[100];
			children = file.listFiles();
		
			if (children.length != 0) { // 有子文件
				
				for (int i = 0; i < children.length; ++i) {
					removeDirectory(children[i]);
				}
			
			}
			
			// 文件夹已为空，删除之
			file.delete();
			
		} else { // 是文件
			
			file.delete();
		}
		
	}
}
