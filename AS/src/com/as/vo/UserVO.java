/**
 * All Rights Reserved to AlphaSphere R&D Group
 * Version: 1.1:
 * 2009-8-26 Guo Kai
 * 
 * Version 1.2: 2009-8-26
 * Guo Kai : change import java.util.Date to java.sql.Date
 * 
 * Version 1.3: 2009-8-26
 * Guo Kai : change Date to String
 * 
 * Version 1.4: 2009-8-26
 * Guo Kai : change sex to int
 *  
 *  version 1.5 2009-8-29
 *  Guo Kai : debug
 */
package com.as.vo;

public class UserVO {
	private int user_id;
	private int dept_id;
	private String user_office;
	private String user_fax;
	private int user_workage;
	private String user_true_name;// True name
	private String user_name;// Login name(Account)
	private String user_pswd;
	private int user_sex;
	private String user_birthday;
	private String user_picture;
	private String user_directory;
	private String user_phone;
	private String user_mobile;
	private int user_group;
	private String user_mail;
	private double user_wage;
	private double user_bonus;
	private String user_card;
	private String user_entrytime;
	private String user_intro;
	private String user_other;
	private String user_number;

	public String getUser_number() {
		return user_number;
	}

	public void setUser_number(String user_number) {
		this.user_number = user_number;
	}

	public int getUser_sex() {
		return user_sex;
	}

	public UserVO() {
		user_id = 0;
		dept_id = 0;
		user_office = "NULL";
		user_fax = "NULL";
		user_workage = 0;
		user_true_name = "NULL";
		user_name = "NULL";
		user_pswd = "NULL";
		user_sex = -1;
		user_birthday = "2009-01-01";
		user_picture = "NULL";
		user_directory = "NULL";
		user_phone = "NULL";
		user_mobile = "NULL";
		user_group = 0;
		user_mail = "NULL";
		user_wage = 0.0;
		user_bonus = 0.0;
		user_card = "NULL";
		user_entrytime = "2009-01-01";
		user_intro = "NULL";
		user_other = "NULL";
		user_number = "NULL";

	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getDept_id() {
		return dept_id;
	}

	public void setDept_id(int dept_id) {
		this.dept_id = dept_id;
	}

	public String getUser_office() {

		return user_office;
	}

	public void setUser_office(String user_office) {
		this.user_office = user_office;
	}

	public String getUser_fax() {
		return user_fax;
	}

	public void setUser_fax(String user_fax) {
		this.user_fax = user_fax;
	}

	public int getUser_workage() {
		return user_workage;
	}

	public void setUser_workage(int user_workage) {
		this.user_workage = user_workage;
	}

	public String getUser_true_name() {
		return user_true_name;
	}

	public void setUser_true_name(String user_true_name) {
		this.user_true_name = user_true_name;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_pswd() {
		return user_pswd;
	}

	public void setUser_pswd(String user_pswd) {
		this.user_pswd = user_pswd;
	}

	public int isUser_sex() {
		return user_sex;
	}

	public void setUser_sex(int user_sex) {
		this.user_sex = user_sex;
	}

	public String getUser_birthday() {
		return user_birthday;
	}

	public void setUser_birthday(String user_birthday) {
		this.user_birthday = user_birthday;
	}

	public String getUser_picture() {
		return user_picture;
	}

	public void setUser_picture(String user_picture) {
		this.user_picture = user_picture;
	}

	public String getUser_directory() {
		return user_directory;
	}

	public void setUser_directory(String user_directory) {
		this.user_directory = user_directory;
	}

	public String getUser_phone() {
		return user_phone;
	}

	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}

	public String getUser_mobile() {
		return user_mobile;
	}

	public void setUser_mobile(String user_mobile) {
		this.user_mobile = user_mobile;
	}

	public int getUser_group() {
		return user_group;
	}

	public void setUser_group(int user_group) {
		this.user_group = user_group;
	}

	public String getUser_mail() {
		return user_mail;
	}

	public void setUser_mail(String user_email) {
		this.user_mail = user_email;
	}

	public double getUser_wage() {
		return user_wage;
	}

	public void setUser_wage(double user_wage) {
		this.user_wage = user_wage;
	}

	public double getUser_bonus() {
		return user_bonus;
	}

	public void setUser_bonus(double user_bonus) {
		this.user_bonus = user_bonus;
	}

	public String getUser_card() {
		return user_card;
	}

	public void setUser_card(String user_card) {
		this.user_card = user_card;
	}

	public String getUser_entrytime() {
		return user_entrytime;
	}

	public void setUser_entrytime(String user_entrytime) {
		this.user_entrytime = user_entrytime;
	}

	public String getUser_intro() {
		return user_intro;
	}

	public void setUser_intro(String user_intro) {
		this.user_intro = user_intro;
	}

	public String getUser_other() {
		return user_other;
	}

	public void setUser_other(String user_other) {
		this.user_other = user_other;
	}
	
    public boolean equals(Object obj) {

        if (this == obj)

            return true;

        if (obj == null)

            return false;

        if (getClass() != obj.getClass())

            return false;

        final UserVO other = (UserVO) obj;

         if(this.getUser_id()!=other.getUser_id())
        	 
        	 return false;
         
        return true;
   }

}
