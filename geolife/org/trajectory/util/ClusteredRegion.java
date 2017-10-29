package org.trajectory.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClusteredRegion {
	private int id;
	private StayPoint center;
	private List<StayPoint> member_sites;
	
	public ClusteredRegion() {
		id = -1;
		center = new StayPoint();
		member_sites = new ArrayList<StayPoint>();
	}
	
	public ClusteredRegion(StayPoint sp) {
		id = -1;
		center = new StayPoint();
		center.setLatitude(sp.getLatitude());
		center.setLongitude(sp.getLongitude());
		member_sites = new ArrayList<StayPoint>();
		member_sites.add(sp);
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public StayPoint getCenter() {
		return center;
	}
	
	public void setCenter(StayPoint center) {
		this.center = center;
	}
	
	public void setCenter(double latitude, double longitude) {
		center.setLatitude(latitude);
		center.setLongitude(longitude);
	}
	
	public List<StayPoint> getMemberSites() {
		return member_sites;
	}
	
	public void setMeberSites(List<StayPoint> member_sites) {
		this.member_sites = member_sites;
	}
	
	public void addMember(StayPoint sp) {
		if (!member_sites.contains(sp)) {
			member_sites.add(sp);
		}
	}
	
	public void removeMember(StayPoint sp) {
		for (StayPoint site : member_sites) {
			if (site.getId() == sp.getId()) {
				member_sites.remove(member_sites.indexOf(site));
			}
		}
	}
	
	public void clearMemberSites() {
		member_sites = new ArrayList<StayPoint>();
	}
	
	public void updateCenter() {
		if (this.member_sites.size() == 0) return;
		
		double sum_latitude = 0;
		double sum_longitude = 0;
		
		Iterator<StayPoint> iter = this.member_sites.iterator();
		
		while (iter.hasNext()) {
			StayPoint cur_site = iter.next();
			sum_latitude += cur_site.getLatitude();
			sum_longitude += cur_site.getLongitude();
		}
		
		sum_latitude /= this.member_sites.size();
		sum_longitude /= this.member_sites.size();
		
		this.center.setLatitude(sum_latitude);
		this.center.setLongitude(sum_longitude);
	}
	
	public String toString() {
		String res = "";
		
		res += "{ ID: " + getId() + ", Center (" + center.getLatitude() + ", " + center.getLongitude() + ")\n";
		res += member_sites.toString() + " }\n";
		
		return res;
	}
	
	public int getUserFrequency(int user_id) {
		int freq = 0;
		
		for (StayPoint site : this.member_sites) {
			if (site.getUserId() == user_id) {
				++freq;
			}
		}
		
		return freq;
	}
	
	public boolean contains(int site_id) {
		boolean res = false;
		
		for (StayPoint member : this.member_sites) {
			if (member.getId() == site_id) {
				res = true;
				break;
			}
		}
		
		return res;
	}
}
