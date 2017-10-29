package org.trajectory.util;

public class GPSPoint {
	private int point_id;
	private int user_id;
	private double latitude;
	private double longitude;
	private double altitude;
	private double days_passed;
	private String date;
	private String time;
	
	public GPSPoint() {
		
	}
	
	public GPSPoint(int user_id, double latitude, double longitude, double altitude, double days_passed, String date, String time) {
		this.user_id = user_id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.days_passed = days_passed;
		this.date = date;
		this.time = time;
	}
	
	public int getPoint_id() {
		return point_id;
	}
	
	public void setPoint_id(int point_id) {
		this.point_id = point_id;
	}
	
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
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
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	public double getDays_passed() {
		return days_passed;
	}
	public void setDays_passed(double days_passed) {
		this.days_passed = days_passed;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public String toString() {
		String res = "";
		
		res += Integer.toString(point_id) + ": ";
		res += Integer.toString(user_id) + ", ";
		res += Double.toString(latitude) + ", ";
		res += Double.toString(longitude) + ", ";
		res += Double.toString(altitude) + ", ";
		res += Double.toString(days_passed) + ", ";
		res += date + ", ";
		res += time;
		
		return res;
	}
	
	public double geoDistance(GPSPoint that) {
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
	
}
