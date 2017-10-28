package org.bnuminer.test;

public class Chinese {
	private static Chinese instance;
	private String content;
	
	/**
	 * -1 - 初始化
	 * 0 - 英文状态
	 * 1 - 汉文状态
	 */
	private int mode = -1;
	
	public static Chinese getInstance() {
		if (instance == null)
			instance = new Chinese();
		return instance;
	}
	
	public Chinese() {
		
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}
	
	public void show() {
		if (mode == 0) {
			System.out.println("英文");
			System.out.println(getContent());
		} else if (mode == 1) {
			System.out.println("汉文");
			System.out.println(getContent());
		}
	}
	
	public int getMode() {
		return mode;
	}
	
	public void setMode(int mode) {
		this.mode = mode;
	}

}
