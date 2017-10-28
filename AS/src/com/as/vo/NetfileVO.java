package com.as.vo;

public class NetfileVO {
	private int netfile_id;

	private int folder_id;

	private String netfile_name;

	private String netfile_type;

	private String netfile_path;

	private int netfile_share;

	public NetfileVO() {
		netfile_id = 0;
		folder_id = 0;
		netfile_name = "NULL";
		netfile_type = "NULL";
		netfile_path = "NULL";
		netfile_share = 0;
	}

	public int getFolder_id() {
		return folder_id;
	}

	public void setFolder_id(int folder_id) {
		this.folder_id = folder_id;
	}

	public int getNetfile_id() {
		return netfile_id;
	}

	public void setNetfile_id(int netfile_id) {
		this.netfile_id = netfile_id;
	}

	public String getNetfile_name() {
		return netfile_name;
	}

	public void setNetfile_name(String netfile_name) {
		this.netfile_name = netfile_name;
	}

	public String getNetfile_path() {
		return netfile_path;
	}

	public void setNetfile_path(String netfile_path) {
		this.netfile_path = netfile_path;
	}

	public int getNetfile_share() {
		return netfile_share;
	}

	public void setNetfile_share(int netfile_share) {
		this.netfile_share = netfile_share;
	}

	public String getNetfile_type() {
		return netfile_type;
	}

	public void setNetfile_type(String netfile_type) {
		this.netfile_type = netfile_type;
	}

}
