package org.trajectory.util;


public class StayPoint {
	private int id;
	private int user_id;
	private double latitude;
	private double longitude;
	private double arrive_time;
	private double leave_time;
	
	public StayPoint() {
		id = -1;
		user_id = -1;
		latitude = 0;
		longitude = 0;
		arrive_time = 0;
		leave_time = 0;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getUserId() {
		return user_id;
	}
	
	public void setUserId(int user_id) {
		this.user_id = user_id;
	}
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getArriveTime() {
		return arrive_time;
	}
	public void setArriveTime(double arrive_time) {
		this.arrive_time = arrive_time;
	}
	public double getLeaveTime() {
		return leave_time;
	}
	public void setLeaveTime(double leave_time) {
		this.leave_time = leave_time;
	}
	
	public double geoDistance(StayPoint that) {
		double radius_earth = 6378.137;   // Radius of the Earth in km.
		
		double rad_lat_1 = Math.toRadians(this.getLatitude());
		double rad_long_1 = Math.toRadians(this.getLongitude());
		double rad_lat_2 = Math.toRadians(that.getLatitude());
		double rad_long_2 = Math.toRadians(that.getLongitude());
		
		double diff_lat = rad_lat_1 - rad_lat_2;
		double diff_long = rad_long_1 - rad_long_2;
		
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(diff_lat / 2), 2) + Math.cos(rad_lat_1) * Math.cos(rad_lat_2) * Math.pow(Math.sin(diff_long / 2), 2)));

		s = s * radius_earth;
		//s = Math.round(s * 10000) / 10000;
		
		return s;
	}
	
	public boolean equals(StayPoint sp) {
		double thresh = 0.2;  // unit: km.
		if (this.geoDistance(sp) <= thresh) {
			return true;
		} else return false;
	}
	
	public String toString() {
		String res = new String();
		
		res = "[ ID: " + this.getId() + ", Latitude: " + this.getLatitude() + ", Longitude: " + this.getLongitude() + ", Arrive Time: " + this.getArriveTime() + ", Leave Time: " + this.getLeaveTime() + " ]"; 
	
		return res;
	}
	
}
