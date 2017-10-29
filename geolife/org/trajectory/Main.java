package org.trajectory;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.trajectory.inference.HITSModel;
import org.trajectory.model.BeijingDataProcess;
import org.trajectory.model.DataProcess;
import org.trajectory.predict.DichotomizedSlopeOne;
import org.trajectory.predict.SlopeOne;
import org.trajectory.util.ClusteredRegion;
import org.trajectory.util.Pair;

public class Main {
	public static void main(String[] args) throws Exception {
		Config cfg = new Config();
		
		DataProcess dp = new DataProcess(cfg.getDataRootPath());
		
		List<String> years = new ArrayList<String>();
		years.add("2007");
		years.add("2008");
		years.add("2009");
		years.add("2010");
		years.add("2011");
		years.add("2012");
		
		dp.process(years);
		dp.buildRegions(80);
		//System.out.println(dp.getRegionList());
		
		PrintWriter writer = new PrintWriter("simulation.output", "UTF-8");
		
		// Beijing.
		BeijingDataProcess bdp = new BeijingDataProcess(cfg.getDataRootPath());
		bdp.process(years);
		bdp.buildRegions(80);
		bdp.genTravellerList(dp);
		bdp.genNativeList();
		
		writer.println("There are " + dp.getUserList().size() +  " users in total.");
		writer.println("Among them, " + bdp.getNativeList().size() + " users are native in Beijing; while " 
				           + bdp.getTravellerList().size() + " travels inside and outside Beijing.");
		
		
		HITSModel model = new HITSModel(bdp);
		model.buildAdjacentMatrix();
		
		System.out.println("Inference start.");
		
		model.inference();
		
		System.out.println("Inference is finished!");
		
		
		SlopeOne recommend1 = new SlopeOne(model);
		DichotomizedSlopeOne recommend2 = new DichotomizedSlopeOne(model, bdp);
		
		for (int i = 0; i < model.getUserList().size(); ++i) {
			// For each user.
			int cur_user_id = model.getUserList().get(i);
			
			System.out.print((i + 1) + ". Predict for user " + cur_user_id + "...");
			
			writer.println("\n" + "For user " + cur_user_id + ":");
			
			if (bdp.isTraveller(cur_user_id)) {
				writer.println("    User " + cur_user_id + " is a traveller.");
			} else if (bdp.isNative(cur_user_id)) {
				writer.println("    User " + cur_user_id + " is a native.");
			} else {
				writer.println("    User " + cur_user_id + " has never been to Beijing.");
			}
			
			// For SlopeOne Method.
			List<Pair<Integer, Double>> sites_recommend1 = recommend1.predictByUser(cur_user_id);
			
			writer.println("------- SlopeOne -------");
			Iterator<Pair<Integer, Double>> iter_site = sites_recommend1.iterator();
			int cnt = 0;
			while (iter_site.hasNext()) {
				++cnt;
				Pair<Integer, Double> cur_tup = iter_site.next();
				ClusteredRegion cur_region = model.getRegionList().get(cur_tup.getLeft());
				writer.println(cnt + ". Region " + cur_region.getId() + ": ( " 
							     + cur_region.getCenter().getLatitude() + ", " + cur_region.getCenter().getLongitude() + " ), Predicted rate = " + cur_tup.getRight() + ";");
			}
			
			
			// For DichotomizedSlopeOne Method.
			List<Pair<Integer, Double>> sites_recommend2 = recommend2.predictByUser(cur_user_id);
			
			writer.println("\n-------------- DichotomizedSlopeOne --------------");
			iter_site = sites_recommend2.iterator();
			cnt = 0;
			while (iter_site.hasNext()) {
				++cnt;
				Pair<Integer, Double> cur_tup = iter_site.next();
				ClusteredRegion cur_region = model.getRegionList().get(cur_tup.getLeft());
				writer.println(cnt + ". Region " + cur_region.getId() + ": ( "
				                 + cur_region.getCenter().getLatitude() + ", " + cur_region.getCenter().getLongitude() + " ), Predicted rate = " + cur_tup.getRight() + ";");
			}
			
			System.out.println("OK!");
		}
		
		
		writer.close();
		
		System.out.println("Finished!");
		
	}
}
