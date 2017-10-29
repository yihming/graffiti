package org.trajectory.inference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.trajectory.model.DataProcess;
import org.trajectory.util.ClusteredRegion;
import org.trajectory.util.Matrix;
import org.trajectory.util.Pair;
import org.trajectory.util.StayPoint;

public class HITSModel {
	private DataProcess dp;
	private Matrix adj_mat;
	private List<Integer> user_list;
	private List<ClusteredRegion> region_list;
	private Map<Integer, Double> hub_score_map;
	private Map<Integer, Double> authority_score_map;
	private Map<Integer, List<List<Integer>>> user_log_map;
	
	
	public HITSModel(DataProcess dp) throws Exception {
		this.dp = dp;
		user_list = dp.getUserList();
		region_list = dp.getRegionList();
		
		hub_score_map = new HashMap<Integer, Double>();
		authority_score_map = new HashMap<Integer, Double>();
		user_log_map = new HashMap<Integer, List<List<Integer>>>();
	
		buildUserLogMap();
	}
	
	public void buildUserLogMap() throws Exception {
		Map<Integer, List<Pair<Integer, Integer>>> user_conn_map = dp.getUserConnectionMap();
		
		for (int i = 0; i < user_list.size(); ++i) {
			int user_id = user_list.get(i);
			List<List<Integer>> trip_list = new ArrayList<List<Integer>>();
			
			List<Pair<Integer, Integer>> site_conn_graph = user_conn_map.get(user_id);
			if (site_conn_graph != null && !site_conn_graph.isEmpty()) {
				int iter = 0;
				Pair<Integer, Integer> edge = site_conn_graph.get(iter);
				
				while (iter < site_conn_graph.size()) {
					int prev_site_id = edge.getLeft();
					List<Integer> trip = new ArrayList<Integer>();
					while (prev_site_id == edge.getLeft()) {
						trip.add(edge.getLeft());
						prev_site_id = edge.getRight();
						++iter;
						if (iter >= site_conn_graph.size()) break;
						edge = site_conn_graph.get(iter);
					}
					trip.add(prev_site_id);
					
					// Translate site_id to rigion_id.
					for (int j = 0; j < trip.size(); ++j) {
						int cur_site_id = trip.get(j);
						int region_id = -1;
						for (int k = 0; k < region_list.size(); ++k) {
							if (region_list.get(k).contains(cur_site_id)) {
								region_id = region_list.get(k).getId();
								break;
							}
						}
						if (region_id == -1) throw new Exception();
						else trip.set(j, region_id);
					}
					trip_list.add(trip);
				}
				this.user_log_map.put(user_id, trip_list);
			} // End if.
		} // End for.
	}
	
	public Matrix getAdjacentMatrix() {
		return adj_mat;
	}
	
	public Map<Integer, Double> getHubScoreMap() {
		return hub_score_map;
	}
	
	public List<Integer> getUserList() {
		return user_list;
	}
	
	public List<ClusteredRegion> getRegionList() {
		return region_list;
	}
	
	public void setHubScoreMap(Map<Integer, Double> hub_score_map) {
		this.hub_score_map = hub_score_map;
	}
	
	public Map<Integer, Double> getAuthScoreMap() {
		return authority_score_map;
	}
	
	public void setAuthScoreMap(Map<Integer, Double> authority_score_map) {
		this.authority_score_map = authority_score_map;
	}
	
	public boolean userVisitBothRegionsInOneTrip(int user_id, int region_a_id, int region_b_id) {
		boolean res = false;
		
		List<List<Integer>> log_list = this.user_log_map.get(user_id);
		
		if (log_list == null) return res;
		
		Iterator<List<Integer>> iter_log = log_list.iterator();
		while (iter_log.hasNext()) {
			List<Integer> log = iter_log.next();
			if (log.contains(region_a_id) && log.contains(region_b_id)) {
				int start_idx = log.indexOf(region_a_id);
				
				if (start_idx != log.size() - 1) {
					for (int i = start_idx + 1; i < log.size(); ++i) {
						if (log.get(i) == region_b_id) {
							res = true;
							break;
						}
					}
					if (res) break;
				}
			}
		}
		
		return res;
	}
	
	public void buildAdjacentMatrix() throws Exception {
		adj_mat = new Matrix(user_list.size(), region_list.size(), 0);
		
		for (int i = 0; i < user_list.size(); ++i) {
			for (int j = 0; j < region_list.size(); ++j) {
				adj_mat.setElement(i, j, region_list.get(j).getUserFrequency(user_list.get(i)));
			}
		}
		
	}
	
	public void inference() throws Exception {	
		Matrix old_user_hub = new Matrix(user_list.size(), 1, 1);
		Matrix old_region_auth = new Matrix(region_list.size(), 1, 1);
		Matrix adj_mat_trans = adj_mat.transpose();
		
		
		int cnt = 0;
		
		// TODO: fix the divergence problem.
		
		while (true) {
			++cnt;
			
			Matrix new_region_auth = adj_mat_trans.multiply(old_user_hub);
			
			Matrix new_user_hub = adj_mat.multiply(old_region_auth);
			
			//double diff = Math.max(old_user_hub.subtract(new_user_hub).maxAbs(), old_region_auth.subtract(new_region_auth).maxAbs()) / (user_list.size() * region_list.size());
			
			if (cnt == 2) {
				break;
			}
			
			
			old_user_hub = new_user_hub;
			old_region_auth = new_region_auth;
		}
		
		
		for (int i = 0; i < old_user_hub.size(); ++i) {
			int user_id = user_list.get(i);
			double hub_value = old_user_hub.getElement(i, 0);
			this.hub_score_map.put(user_id, hub_value);
		}
		
		for (int i = 0; i < old_region_auth.size(); ++i) {
			int region_id = region_list.get(i).getId();
			double auth_value = old_region_auth.getElement(i, 0);
			this.authority_score_map.put(region_id, auth_value);
		}
	}
	
	
	public boolean isVisited(int user_id, int region_id) throws Exception {
		int idx = this.user_list.indexOf(user_id);
		
		if (idx == -1) {
			// This user is not in the model.
			return false;
		} else return (this.adj_mat.getElement(idx, region_id) > 0);
	}
	
}
