package org.trajectory.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.trajectory.util.ClusteredRegion;
import org.trajectory.util.GPSPoint;
import org.trajectory.util.Pair;
import org.trajectory.util.StayPoint;


public class BeijingDataProcess extends DataProcess {
	private List<Integer> traveller_list;
	private List<Integer> native_list;
	
	
	public BeijingDataProcess(String path) {
		super(path);
		traveller_list = new ArrayList<Integer>();
		native_list = new ArrayList<Integer>();
	}
	
	public List<Integer> getTravellerList() {
		return traveller_list;
	}
	
	public List<Integer> getNativeList() {
		return native_list;
	}
	
	@Override
	public void process(List<String> years) {
		super.process(years);
		
	}
	
	public boolean isWithinBeijing(StayPoint site) {
		double latitude = site.getLatitude();
		double longitude = site.getLongitude();
		
		return isWithinBeijing(Pair.createPair(latitude, longitude));
		
	}
	
	public boolean isWithinBeijing(Pair<Double, Double> coord) {
		double latitude = coord.getLeft();
		double longitude = coord.getRight();
		
		double latitude_min = 39.45;
		double latitude_max = 41;
		double longitude_min = 115.43;
		double longitude_max = 117.40;
		
		if (latitude >= latitude_min && latitude <= latitude_max
				&& longitude >= longitude_min && longitude <= longitude_max) {
			return true;
		} else return false;
	}
	
	public boolean isWithinBeijing(int region_id) {
		for (int i = 0; i < super.getRegionList().size(); ++i) {
			if (super.getRegionList().get(i).getId() == region_id) {
				ClusteredRegion region = super.getRegionList().get(i);
				StayPoint center = region.getCenter();
				if (isWithinBeijing(center)) return true;
				else return false;
			}
		}
		
		return false;
		
	}
	
	@Override
	public Pair<List<StayPoint>, List<Pair<Integer, Integer>>> genStayPointPerLog(int sp_id_base, int user_id, List<GPSPoint> pl, double dist_thresh, double time_thresh) {
		List<StayPoint> site_list = new ArrayList<StayPoint>();
		List<Pair<Integer, Integer>> connection_list = new ArrayList<Pair<Integer, Integer>>();
		
		int i = 0;
		int point_num = pl.size();
		int sp_cnt = 0;
		int j;
		while (i < point_num) {
			j = i + 1;
			GPSPoint p1 = pl.get(i);
			while (j < point_num) {
				GPSPoint p2 = pl.get(j);
				double dd = p1.geoDistance(p2);
				if (dd > dist_thresh) {
					double dt = p2.getDays_passed() - p1.getDays_passed();
					if (dt > time_thresh) {
						// Discover a new stay point.
						
						Pair<Double, Double> tup = computeMeanCoordinate(pl, i, j);
						
						// Check whether the region is centered within Beijing.
						if (isWithinBeijing(tup)) {
							++sp_cnt;
							StayPoint s = new StayPoint();
							s.setId(sp_id_base + sp_cnt);
							s.setUserId(user_id);
							s.setLatitude(tup.getLeft());
							s.setLongitude(tup.getRight());
							s.setArriveTime(p1.getDays_passed());
							s.setLeaveTime(p2.getDays_passed());
							
							// Update connection_list.
							if (!site_list.isEmpty()) {
								StayPoint prev = site_list.get(site_list.size() - 1);
								connection_list.add(Pair.createPair(prev.getId(), s.getId()));
							}
							
							// Update site_list.
							site_list.add(s);
						}
						
					}
					i = j;
					break;
				}
				++j;
			} // End of while.
			
			// Coming to the last point.
			if (j == point_num) {
				break;
			}
		} // End of while.
		
		return Pair.createPair(site_list, connection_list);
		
	}

	public void genTravellerList(DataProcess global_dp) {
		for (int i = 0; i < super.getUserList().size(); ++i) {
			int user_id = super.getUserList().get(i);
			if (this.travellerJudge(user_id, global_dp)) {
				this.traveller_list.add(user_id);
			}
		}
	}
	
	public void genNativeList() {
		for (int i = 0; i < super.getUserList().size(); ++i) {
			int user_id = super.getUserList().get(i);
			if (this.isNative(user_id))
				this.native_list.add(user_id);
		}
	}
	
	public boolean travellerJudge(int user_id, DataProcess global_dp) {
		if (super.getUserList().contains(user_id)) {
			for (StayPoint site : global_dp.getStayPointList()) {
				if (site.getUserId() == user_id && !this.isWithinBeijing(site))
					return true;
			}
			return false;
			
		} else return false;
	}
	
	public boolean isTraveller(int user_id) {
		return (this.traveller_list.contains(user_id));
	}
	
	public boolean isNative(int user_id) {
		if (!this.isTraveller(user_id)) {
			for (StayPoint site : this.getStayPointList()) {
				if (site.getUserId() == user_id) 
					return true;
			}
			return false;
			
		} else {
			return false;
		}
	}
	
}
