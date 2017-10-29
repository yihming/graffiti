package org.trajectory.predict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.trajectory.inference.HITSModel;
import org.trajectory.util.Matrix;
import org.trajectory.util.Pair;

public class SlopeOne {
	protected HITSModel model;
	private Matrix correlation;
	private Matrix deviation;
	
	public SlopeOne(HITSModel model) throws Exception {
		this.model = model;
		
		int numRegion = model.getAdjacentMatrix().getColumnLength();
		
		correlation = new Matrix(numRegion, numRegion, 0);
		deviation = new Matrix(numRegion, numRegion, 0);
		
		buildCorrelation();
		buildDeviation();
	}
	
	public HITSModel getModel() {
		return model;
	}
	
	public Matrix getCorrelation() {
		return correlation;
	}
	
	public double getCorrelation(int region_a_idx, int region_b_idx) throws Exception {
		return correlation.getElement(region_a_idx, region_b_idx);
	} 
	
	public void setCorrelation(Matrix correlation) {
		this.correlation = correlation;
	}
	
	public void updateCorrelation(int x, int y, double v) throws Exception {
		this.correlation.setElement(x, y, v);
	}
	
	public Matrix getDeviation() {
		return deviation;
	}
	
	public double getDeviation(int region_a_idx, int region_b_idx) throws Exception {
		return deviation.getElement(region_a_idx, region_b_idx);
	}
	
	public void setDeviation(Matrix deviation) {
		this.deviation = deviation;
	}
	
	public void updateDeviation(int x, int y, double v) throws Exception {
		this.deviation.setElement(x, y, v);
	}
	
	private void buildCorrelation() throws Exception {
		List<Integer> user_list = model.getUserList();
		
		for (int i = 0; i < correlation.getRowLength(); ++i) {
			for (int j = 0; j < correlation.getColumnLength(); ++j) {
				//double factor = Math.pow(2, -(Math.abs(i-j)-1));
				double sum_corr = 0;
				for (int k = 0; k < user_list.size(); ++k) {
					int cur_user_id = user_list.get(k);
					if (model.userVisitBothRegionsInOneTrip(cur_user_id, i, j)) {
						sum_corr += model.getHubScoreMap().get(cur_user_id);
					}
				}
				//sum_corr *= factor;
				this.correlation.setElement(i, j, sum_corr);
			}
		}
	}
	
	private void buildDeviation() throws Exception {
		for (int i = 0; i < deviation.getRowLength(); ++i) {
			List<Double> rating_i = this.model.getAdjacentMatrix().getColumn(i);
			for (int j = i; j < deviation.getColumnLength(); ++j) {
				List<Double> rating_j = this.model.getAdjacentMatrix().getColumn(j);
				
				int nom_factor = 0;
				double dev_rating = 0;
				for (int k = 0; k < rating_j.size(); ++k) {
					if (rating_i.get(k) > 0 && rating_j.get(k) > 0) {
						++nom_factor;
						dev_rating += (rating_i.get(k) - rating_j.get(k));
					}
				}
				
				this.deviation.setElement(i, j, dev_rating / nom_factor);
				if (i != j) this.deviation.setElement(j, i, -dev_rating / nom_factor);
			}
		}
	}
	
	public double predictByUserAndRegion(int user_id, int region_id) throws Exception {
		double score = -1;
		
		if (!model.isVisited(user_id, region_id)) {
			int idx = model.getUserList().indexOf(user_id);
			List<Double> ratings = model.getAdjacentMatrix().getRow(idx);
			
			double sum1 = 0;
			double sum2 = 0;
			for (int i = 0; i < ratings.size(); ++i) {
				double rate_cur_region = ratings.get(i);
				
				if (rate_cur_region > 0) {
					sum1 = sum1 + (rate_cur_region + this.deviation.getElement(region_id, i)) * this.correlation.getElement(region_id, i);
					sum2 = sum2 + this.correlation.getElement(region_id, i);
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
	
	public List<Pair<Integer, Double>> predictByUser(int user_id) throws Exception {
		List<Pair<Integer, Double>> res_list = new ArrayList<Pair<Integer, Double>>();
		
		for (int i = 0; i < model.getRegionList().size(); ++i) {
			int cur_region_id = model.getRegionList().get(i).getId();
			double cur_score = predictByUserAndRegion(user_id, cur_region_id);
			if (cur_score > 0) {
				res_list.add(Pair.createPair(cur_region_id, cur_score));
			}
		}
		
		Collections.sort(res_list, new Comparator<Pair<Integer, Double>>() {
			@Override
			public int compare(Pair<Integer, Double> arg0, Pair<Integer, Double> arg1) {
				if (arg0.getRight() - arg1.getRight() > 0) return -1;
				else if (arg0.getRight() - arg1.getRight() < 0) return 1;
				else return 0;
			}
		});
		
		return res_list;
	}
}
