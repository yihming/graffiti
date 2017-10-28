package org.bnuminer.client;

import org.bnuminer.client.model.Folder;
import org.bnuminer.client.model.MinerItem;

public class MinerReader {
	
	public static Folder getTreeModel(String period) {
		Folder[] miner_folders = null;
		
		
		if (period.equals("filter")) { // 显示filter目录
			
		} else if (period.equals("clusterer")) { // 显示聚类器目录
			
			
			miner_folders = new Folder[] {
					new Folder("聚类器", 
							new MinerItem[] {
							new MinerItem("CLOPE", "-R 2.6"),
							new MinerItem("Cobweb", "-A 1.0 -C 0.0028209479177387815 -S 42"),
							new MinerItem("DBScan", "-E 0.9 -M 6 -I weka.clusterers.forOPTICSAndDBScan.Databases.SequentialDatabase -D weka.clusterers.forOPTICSAndDBScan.DataObjects.EuclidianDataObject"),
							new MinerItem("EM", "-I 100 -N -1 -M 1.0E-6 -S 100"),
							new MinerItem("FarthestFirst", "-N 2 -S 1"),
							new MinerItem("FilteredClusterer", "-F weka.filters.AllFilter -W weka.clusterers.SimpleKMeans -- -N 2 -A 'weka.core.EuclideanDistance -R first-last'"),
							new MinerItem("MakeDensityBasedClusterer", "-M 1.0E-6 -W weka.clusterers.SimpleKMeans -- -N 2 -A 'weka.core.EucliedianDistance -R first-last'"),
							new MinerItem("OPTICS", "-E 0.9 -M 6 -I weka.clusterers.forOPTICSAndDBScan.Databases.SequentialDatabase -D weka.clusterers.forOPTICSAndDBScan.DataObjects.EuclidianDataObject"),
							new MinerItem("SimpleKMeans", "-N 2 -A 'weka.core.EuclideanDistance -R first-last' -I 500 -S 10")
						}
					)
			};
			
			
			
		}
		
		Folder root = new Folder("BNU Miner");
		for (int i = 0; i < miner_folders.length; ++i) {
			root.add((Folder) miner_folders[i]);
		}
		
		return root;
		
	} // End of getTreeModel().
	
}
