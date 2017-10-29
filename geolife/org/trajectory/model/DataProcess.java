package org.trajectory.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.trajectory.util.ClusteredRegion;
import org.trajectory.util.GPSPoint;
import org.trajectory.util.Pair;
import org.trajectory.util.StayPoint;


public class DataProcess {
	private String root_path;
	private List<Integer> user_list;
	private List<StayPoint> sp_list;
	private List<ClusteredRegion> region_list;
	private Map<Integer, List<Pair<Integer, Integer>>> user_connection_map;
	
	public DataProcess(String path) {
		root_path = path;
		user_list = new ArrayList<Integer>();
		sp_list = new ArrayList<StayPoint>();
		region_list = new ArrayList<ClusteredRegion>();
		user_connection_map = new HashMap<Integer, List<Pair<Integer, Integer>>>();
	}
	
	public String getRootPath() {
		return root_path;
	}
	
	public void setRootPath(String root_path) {
		this.root_path = root_path;
	}
	
	public List<Integer> getUserList() {
		return user_list;
	}
	
	public void setUserList(List<Integer> user_list) {
		this.user_list = user_list;
	}
	
	public List<StayPoint> getStayPointList() {
		return this.sp_list;
	}
	
	public void setStayPointList(List<StayPoint> sp_list) {
		this.sp_list = sp_list;
	}
	
	public List<ClusteredRegion> getRegionList() {
		return this.region_list;
	}
	
	public void setRegionList(List<ClusteredRegion> region_list) {
		this.region_list = region_list;
	}
	
	public Map<Integer, List<Pair<Integer, Integer>>> getUserConnectionMap() {
		return user_connection_map;
	}
	
	public void setUserConnectionMap(Map<Integer, List<Pair<Integer, Integer>>> user_connection_map) {
		this.user_connection_map = user_connection_map;
	}
	
	public void process(List<String> years) {
		File root_dir = new File(root_path);
		int user_cnt = 0;
		int user_id;
		
		for (File user_dir : root_dir.listFiles()) {
			// For each user.
			++user_cnt;
			user_id = Integer.valueOf(user_dir.getName().trim());
			this.user_list.add(user_id);
			System.out.print(Integer.toString(user_cnt) + ". Start to add trajectories of User " + Integer.toString(user_id) + "...");
		
			processPerUser(user_id, user_dir, years);
			
			System.out.println("OK!");
		}
	}
	
	public void processPerUser(int user_id, File user_dir, List<String> years) {
		for (File child_dir : user_dir.listFiles()) {
			if (child_dir.getName().equals("Trajectory")) {
				for (File log_file : child_dir.listFiles()) {
					// For each GPS trajectory.
					String log_file_name = log_file.getName().trim();
					String log_year = log_file_name.substring(0, 4);
					
					if (years.contains(log_year)) {
						processPerLog(user_id, log_file);
					}
					
				}
			}
		}
	}
	
	public void processPerLog(int user_id, File log_file) {
		List<GPSPoint> point_list = getGPSPointsFromFile(user_id, log_file);
		
		double dist_thresh = 0.2;   // Unit: km.
		double time_thresh = 0.020833333;  // 30 minutes. Unit: day.
		
		Pair<List<StayPoint>, List<Pair<Integer, Integer>>> res_pair = genStayPointPerLog(this.sp_list.size() - 1, user_id, point_list, dist_thresh, time_thresh);
		
		// Add these stay points.
		this.sp_list.addAll(res_pair.getLeft());
		
		// Add user_connection.
		if (user_connection_map.containsKey(user_id)) {
			List<Pair<Integer, Integer>> conn_map_overall = this.user_connection_map.get(user_id);
			conn_map_overall.addAll(res_pair.getRight());
			user_connection_map.put(user_id, conn_map_overall);
		} else {
			this.user_connection_map.put(user_id, res_pair.getRight());
		}
		
	}
	
	
	public List<GPSPoint> getGPSPointsFromFile(int user_id, File filename) {
		List<GPSPoint> res_list = new ArrayList<GPSPoint>();
		
		Path path = Paths.get(filename.getPath());
		Charset cs = Charset.forName("US-ASCII");
		
		try {
			BufferedReader reader = Files.newBufferedReader(path, cs);
			String line = null;
			int line_no = 0;
			
			while ((line = reader.readLine()) != null) {
				++line_no;
				
				if (line_no > 6) {
					// Ignore the first 6 lines.
					line = line.trim();
					
					double latitude = Double.valueOf(line.substring(0, line.indexOf(',')));
					line = line.substring(line.indexOf(',')+1);
					
					double longitude = Double.valueOf(line.substring(0, line.indexOf(',')));
					line = line.substring(line.indexOf(',')+3);
					
					double altitude = Double.valueOf(line.substring(0, line.indexOf(',')));
					line = line.substring(line.indexOf(',')+1);
					
					double days_passed = Double.valueOf(line.substring(0, line.indexOf(',')));
					line = line.substring(line.indexOf(',')+1);
			
					
					String date = line.substring(0, line.indexOf(','));
					line = line.substring(line.indexOf(',')+1);
					
					String time = line;
					
					GPSPoint point = new GPSPoint(user_id, latitude, longitude, altitude, days_passed, date, time);
					res_list.add(point);
				}
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return res_list;
		
	}
	
	public Pair<Double, Double> computeMeanCoordinate(List<GPSPoint> pl, int start_idx, int end_idx) {
		double meanLat = 0;
		double meanLong = 0;
		
		int i;
		for (i = start_idx; i <= end_idx; ++i) {
			meanLat = meanLat + pl.get(i).getLatitude();
			meanLong = meanLong + pl.get(i).getLongitude();
		}
		
		int num = end_idx - start_idx + 1;
		meanLat = meanLat / num;
		meanLong = meanLong / num;
		
		return Pair.createPair(meanLat, meanLong);
	}
	
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
						++sp_cnt;
						StayPoint s = new StayPoint();
						Pair<Double, Double> tup = computeMeanCoordinate(pl, i, j);
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
	
	
	public List<Integer> calcUserForYear(int year) {
		File root_dir = new File(root_path);
		List<Integer> res_list = new ArrayList<Integer>();
		
		for (File user_dir: root_dir.listFiles()) {
			for (File child_dir: user_dir.listFiles()) {
				// Only consider trajectory directories.
				if (child_dir.getName().trim().equals("Trajectory")) {
					for (File trajectory_file: child_dir.listFiles()) {
						if (trajectory_file.getName().substring(0, 4).equals(Integer.toString(year))) {
							Integer user_id = Integer.valueOf(user_dir.getName().trim());
							res_list.add(user_id);
							break;
						}
					}
				}
			}
		}
		
		
		return res_list;
	}
	
	
	
	private int clusterStayPoint(StayPoint sp, List<ClusteredRegion> regions) {
		List<Double> distances = new ArrayList<Double>();
		
		for (int i = 0; i < regions.size(); ++i) {
			double d = regions.get(i).getCenter().geoDistance(sp);
			distances.add(d);
		}
		
		double min_dist = distances.get(0);
		int min_idx = 0;
		
		if (distances.size() > 1) {
			for (int i = 1; i < distances.size(); ++i) {
				if (distances.get(i) < min_dist) {
					min_dist = distances.get(i);
					min_idx = i;
				}
			}
		}
		
		return min_idx;
	}
	
	private double diffRegionCenters(List<ClusteredRegion> l1, List<ClusteredRegion> l2) {
		double max_distance = l1.get(0).getCenter().geoDistance(l2.get(0).getCenter());
		
		if (l1.size() > 1) {
			for (int i = 0; i < l1.size(); ++i) {
				double d = l1.get(i).getCenter().geoDistance(l2.get(i).getCenter());
				if (d > max_distance) {
					max_distance = d;
				}
			}
		}
		
		return max_distance;
	}
	
	// Currently use K-Means.
	public void buildRegions(int numRegion) {
		
		// Randomly choose the initial regions.
		Random rn = new Random();
		for (int i = 0; i < numRegion; ++i) {
			
			int idx = rn.nextInt(numRegion);
			StayPoint c = this.sp_list.get(idx);
			ClusteredRegion region = new ClusteredRegion(c);
			region.setId(i);
			this.region_list.add(region);
		}
		
		List<ClusteredRegion> new_region_list = this.region_list;
		
		while (true) {
			for (int i = 0; i < numRegion; ++i) {
				new_region_list.get(i).clearMemberSites();
			}
			
			Iterator<StayPoint> iter = this.sp_list.iterator();
			while (iter.hasNext()) {
				StayPoint cur_site = iter.next();
				int region_idx = clusterStayPoint(cur_site, new_region_list);
				new_region_list.get(region_idx).addMember(cur_site);
			}
			
			for (int i = 0; i < numRegion; ++i) {
				new_region_list.get(i).updateCenter();
			}
			
			double criterion = diffRegionCenters(new_region_list, this.region_list);
			if (criterion <= 1) {
				break;
			}
			
			this.region_list = new_region_list;
		}
		
		this.region_list = new_region_list;
	}
	
	public int findRegionByStayPoint(int sp_idx) {
		int region_idx = -1;
		
		if (this.region_list.size() > 0) {
			for (int i = 0; i < this.region_list.size(); ++i) {
				ClusteredRegion cur_region = this.region_list.get(i);
				boolean flag = false;
				for (StayPoint site : cur_region.getMemberSites()) {
					if (site.getId() == sp_idx) {
						region_idx = cur_region.getId();
						flag = true;
						break;
					}
				}
				if (flag) break;
			}
		}
		
		return region_idx;
	}
	
	
}
