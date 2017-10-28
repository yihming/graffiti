package com.as.vo;


public class DeptVO {
	private int dept_id;
	private String dept_name;
	private String dept_intro;
	
   public DeptVO(){
	  dept_id=0;
	  dept_name="NULL";
	  dept_intro="NULL";
	  }

	public int getDept_id() {
		return dept_id;
	}

	public void setDept_id(int dept_id) {
		this.dept_id = dept_id;
	}

	public String getDept_intro() {
		return dept_intro;
	}

	public void setDept_intro(String dept_intro) {
		this.dept_intro = dept_intro;
	}

	public String getDept_name() {
		return dept_name;
	}

	public void setDept_name(String dept_name) {
		this.dept_name = dept_name;
	}
	

}
