package org.bnuminer.vo;

public class User_file_infoVO {
	private int user_file_info_id;
	private int user_id;
	private int file_id;
	private int is_owner;
	//private FileInfoVO fileinfovo;
	
	/*public User_file_infoVO() {
		fileinfovo = new FileInfoVO();
	}*/
	
	public int getUser_file_info_id() {
		return user_file_info_id;
	}
	public void setUser_file_info_id(int userFileInfoId) {
		user_file_info_id = userFileInfoId;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int userId) {
		user_id = userId;
	}
	public int getFile_id() {
		return file_id;
	}
	public void setFile_id(int fileId) {
		file_id = fileId;
	}
	public int getIs_owner() {
		return is_owner;
	}
	public void setIs_owner(int isOwner) {
		is_owner = isOwner;
	}
	
	/*public FileInfoVO getFileinfovo() {
		return fileinfovo;
	}
	
	public void setFileinfovo(FileInfoVO fileinfovo) {
		
		this.fileinfovo.setFile_id(fileinfovo.getFile_id());
		this.fileinfovo.setFile_name(fileinfovo.getFile_name());
		this.fileinfovo.setFile_create_time(fileinfovo.getFile_create_time());
		this.fileinfovo.setFile_modify_time(fileinfovo.getFile_modify_time());
		this.fileinfovo.setFile_size(fileinfovo.getFile_size());
		this.fileinfovo.setFile_type(fileinfovo.getFile_type());
	}*/
	
	
	
}
