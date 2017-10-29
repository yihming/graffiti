package org.trajectory.predict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.trajectory.inference.HITSModel;
import org.trajectory.model.BeijingDataProcess;
import org.trajectory.util.Pair;

public class DichotomizedSlopeOne extends SlopeOne {
	private List<Integer> travellerList;
	private List<Integer> nativeList;

	public DichotomizedSlopeOne(HITSModel model, BeijingDataProcess bdp) throws Exception {
		super(model);
		
		travellerList = bdp.getTravellerList();
	}
	
	@Override
	public double predictByUserAndRegion(int user_id, int region_id) throws Exception {
		double score = -1;
		
		if (!super.getModel().isVisited(user_id, region_id)) {
			int idx = super.getModel().getUserList().indexOf(user_id);
			List<Double> ratings = super.getModel().getAdjacentMatrix().getRow(idx);
			
			double sum1 = 0;
			double sum2 = 0;
			for (int i = 0; i < ratings.size(); ++i) {
				double rate_cur_region = ratings.get(i);
				
				if (rate_cur_region > 0) {
					double cur_corr = genCorrelation(user_id, region_id, i);
					sum1 = sum1 + (rate_cur_region + super.getDeviation().getElement(region_id, i)) * cur_corr;
					sum2 = sum2 + cur_corr;
				}
			}
			
			if (sum2 != 0) {
				score = sum1 / sum2;
			} else {
				score = 0;
			}
		}
		
		return score;
	}
	
	@Override
	public List<Pair<Integer, Double>> predictByUser(int user_id) throws Exception {
		List<Pair<Integer, Double>> res_list = new ArrayList<Pair<Integer, Double>>();
		
		for (int i = 0; i < super.getModel().getRegionList().size(); ++i) {
			int cur_region_id = super.getModel().getRegionList().get(i).getId();
			double cur_score = predictByUserAndRegion(user_id, cur_region_id);
			
			if (cur_score > 0) {
				res_list.add(Pair.createPair(cur_region_id, cur_score));
			}
		}
		
		Collections.sort(res_list, new Comparator<Pair<Integer, Double>>() {

			@Override
			public int compare(Pair<Integer, Double> o1,
					Pair<Integer, Double> o2) {
				if (o1.getRight() - o2.getRight() > 0) return -1;
				else if (o1.getRight() - o2.getRight() < 0) return 1;
				else return 0;
			}
			
		});
		
		return res_list;
	}
	
	public double genCorrelation(int user_id, int region_a_id, int region_b_id) {
		double sum_corr = 0;
		
		if (isTraveller(user_id)) {
			//double factor = Math.pow(2, -(Math.abs(region_a_id-region_b_id)-1));
			for (int i = 0; i < super.getModel().getUserList().size(); ++i) {
				int cur_user_id = super.getModel().getUserList().get(i);
				if (isTraveller(cur_user_id)) {
					if (super.getModel().userVisitBothRegionsInOneTrip(cur_user_id, region_a_id, region_b_id)) {
						sum_corr += super.getModel().getHubScoreMap().get(cur_user_id);
					}
				}
			}
			//sum_corr *= factor;
			
		} else {
			//double factor = Math.pow(2, -(Math.abs(region_a_id-region_b_id)-1));
			for (int i = 0; i < super.getModel().getUserList().size(); ++i) {
				int cur_user_id = super.getModel().getUserList().get(i);
				if (!isTraveller(cur_user_id)) {
					if (super.getModel().userVisitBothRegionsInOneTrip(cur_user_id, region_a_id, region_b_id)) {
						sum_corr += super.getModel().getHubScoreMap().get(cur_user_id);
					}
				}
			}
			//sum_corr *= factor;
		}
		
		return sum_corr;
	}
	
	public boolean isTraveller(int user_id) {
		if (this.travellerList.contains(user_id) || !super.getModel().getUserList().contains(user_id)) return true;
		else return false;
	}

}
