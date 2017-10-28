package org.bnuminer.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Cobweb;
import weka.clusterers.EM;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.experiment.InstanceQuery;



public class WekaTest {
	public static void main(String[] args) {
		//String options = "-C 0.25 -M 2 -N 3 -S";
		
		WekaTest wt = new WekaTest();
		try {
			//wt.openDb();
			//wt.clustertest();
			//wt.testEM();
			wt.testDb();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void testDb() {
		try {
			InstanceQuery query = new InstanceQuery();
			query.setDatabaseURL("jdbc:mysql://localhost:3309/student");
			query.setUsername("root");
			query.setPassword("871121");
			query.connectToDatabase();
			
			if (query.execute("SELECT * FROM student_stat")) {
				ResultSet rs = query.getResultSet();
				ResultSetMetaData columns = rs.getMetaData();
				for (int i = 1; i <= columns.getColumnCount(); ++i) {
					System.out.println(columns.getColumnName(i));
				}
				
				while (rs.next()) {
					System.out.print(rs.getDouble("aver_score") + ", ");
					System.out.print(rs.getString("student_id") + ", ");
					System.out.print(rs.getString("name") + ", ");
					System.out.print(rs.getString("gender") + ", ");
					System.out.print(rs.getString("province") + ", ");
					System.out.print(rs.getString("future") + ", ");
					System.out.println(rs.getString("position"));
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void testAttribute() throws Exception {
		List<Attribute> attrs = new ArrayList<Attribute>(2);
		attrs.add(new Attribute("Message", (ArrayList<String>) null));
		List<String> values = new ArrayList<String>(2);
		values.add("hit");
		values.add("miss");
		attrs.add(new Attribute("Class", values));
		System.out.println(attrs.get(1));
	}
	
	public void testOptions() throws Exception {
		String[] options = new String[2];
		options[0] = "-classifier";
		options[1] = "EM";
		
		String option = Utils.getOption("classifier", options);
		System.out.println(option);
	}
	
	public void clustertest() throws Exception {
		Instances data = DataSource.read("D:/iris.arff");
		
		// Generate data for clusterer
		/*Remove filter = new Remove();
		filter.setAttributeIndices("" + (data.classIndex() + 1));
		filter.setInputFormat(data);
		Instances dataClusterer = Filter.useFilter(data, filter);
		*/
		// train clusterer
		Cobweb clusterer = new Cobweb();
		// set further options for EM
		
		
		clusterer.buildClusterer(data);
		
		// evaluate clusterer
		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(clusterer);
		eval.evaluateClusterer(data);
		
		// print results
		System.out.println(eval.clusterResultsToString());
	}
	
	public void openDb() {
		try {
			
			
			InstanceQuery query = new InstanceQuery();
			query.setDatabaseURL("jdbc:mysql://localhost:3309/bnuminer");
			query.setUsername("root");
			query.setPassword("871121");
			query.setQuery("SELECT * FROM user_info");
			
			Instances data = query.retrieveInstances();
			File file = new File("D:/db_test.arff");
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(data.toString());
			
			writer.flush();
			writer.close();
			System.out.println("OK!");
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadFile() throws Exception {
		DataSource source = new DataSource("D:/upload/1/test.arff");
		Instances data = source.getDataSet();
		
		if (data.classIndex() == -1) {
			data.setClassIndex(data.numAttributes() - 1);
			System.out.println("Success");
		}
	}
	
	public void testJ48() {
		
		try {
			Instances training_data = DataSource.read("D:/upload/1/iris.arff");
			Instances testing_data = DataSource.read("D:/upload/1/iris.arff");
			
			
			J48 classifier = new J48();
			classifier.setBinarySplits(false);
			classifier.setConfidenceFactor((float) 0.25);
			classifier.setMinNumObj(2);
			classifier.setNumFolds(3);
			classifier.setReducedErrorPruning(false);
			classifier.setSubtreeRaising(true);
			classifier.setUnpruned(false);
			classifier.setUseLaplace(false);
			classifier.buildClassifier(training_data);
			
			Evaluation eval = new Evaluation(testing_data);
			eval.crossValidateModel(classifier, testing_data, 10, new Random(1));
			System.out.println(eval.toSummaryString());
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void testEM() {
		try {
			Instances training_data = DataSource.read("D:/upload/1/iris.arff");
			Instances testing_data = DataSource.read("D:/upload/1/iris.arff");
			
			EM clusterer = new EM();
			clusterer.buildClusterer(training_data);
			
			
			
			ClusterEvaluation eval = new ClusterEvaluation();
			eval.setClusterer(clusterer);
			eval.evaluateClusterer(testing_data);
			String result = ClusterEvaluation.crossValidateModel("weka.clusterers.EM", testing_data, 10, null, new Random(1));
			System.out.println(eval.clusterResultsToString());
			System.out.println(result);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
