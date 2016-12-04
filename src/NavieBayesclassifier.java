
import java.io.BufferedReader;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Hashtable;


import jsat.ARFFLoader;
import jsat.DataSet;
import jsat.classifiers.CategoricalResults;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.Classifier;
import jsat.classifiers.DataPoint;
import jsat.classifiers.bayesian.NaiveBayes;
import jsat.classifiers.knn.NearestNeighbour;

 


public class NavieBayesClassifier {
	
	//change to your path
	public static String folder = "/Users/hyang027/Desktop/240/";
	
	
	// run classifier
	public static void runClassifier(String trainfileName, String testfileName)
	{
				// change to your path  
				File train_file = new File( folder+trainfileName+"_arff.txt");

		        File test_file = new File( folder+testfileName + "_arff.txt");
		        
		        DataSet train_dataSet = ARFFLoader.loadArffFile(train_file);

		        DataSet test_dataSet = ARFFLoader.loadArffFile(test_file);

		        //We specify '0' as the class we would like to make the target class. 
		        ClassificationDataSet cTrain_dataSet = new ClassificationDataSet(train_dataSet, 0);

		        ClassificationDataSet cTest_dataSet = new ClassificationDataSet(test_dataSet, 0);
		        
		        int errors = 0;
		        Classifier classifier =  new NearestNeighbour(1);
		     
		        classifier.trainC(cTrain_dataSet);

		        
		        for(int i = 0; i < cTest_dataSet.getSampleSize(); i++)
		        {
		            DataPoint dataPoint = cTest_dataSet.getDataPoint(i);//It is important not to mix these up, the class has been removed from data points in 'cDataSet' 
		            int truth = cTest_dataSet.getDataPointCategory(i);//We can grab the true category from the data set

		            //Categorical Results contains the probability estimates for each possible target class value. 
		            //Classifiers that do not support probability estimates will mark its prediction with total confidence. 
		            CategoricalResults predictionResults = classifier.classify(dataPoint);
		            int predicted = predictionResults.mostLikely();
		            if(predicted != truth)
		                errors++;
		            System.out.println( i + "| True Class: " + truth + ", Predicted: " + predicted + ", Confidence: " + predictionResults.getProb(predicted) );
		        }

		        System.out.println(errors + " errors were made, " + 100.0*errors/cTest_dataSet.getSampleSize() + "% error rate" );
	}
	
	
	
	
	// prepare data in csv format
	public static void prepareCSV(ArrayList<ArrayList<Double>>dataSet, Hashtable<String,Integer>userfulTable, int numOfbenign, String fileName){
		try{
		    PrintWriter writer = new PrintWriter(folder+fileName+"_csv.txt", "UTF-8");
		    for (String item:userfulTable.keySet()) {
		    	writer.print(item+",");
		    }
		    writer.println("class");
		    
		      int i=0;
		      for(ArrayList<Double> innerList : dataSet) {
		          for(Double number : innerList) {
		        	  writer.print(number+",");
		             
		          }
		          if(i< numOfbenign)
		        	  writer.print("benign");
		          else 
		        	  writer.print("malware");
		          
		          writer.println();
		          i++;
		      }
		      
		    writer.close();
		} catch (Exception e) {
		   System.out.println("Exception");
		}
			
		  
	}
	
	// csv to arff format
	public static void csvToArff(String fileName) throws Exception
	{
		  FileReader fr = new FileReader(folder+fileName+"_csv.txt");
		  BufferedReader br = new BufferedReader(fr);
		  FileWriter fw =new FileWriter(folder+fileName+"_arff.txt");
		  String n1="@ATTRIBUTE";
		  String n2="REAL";
		  fw.write("@RELATION ngramSysCalls\n");
		  fw.write("\n");
		  String temp=br.readLine();
		  String ttt[]=temp.split(",");
		  for (int i=0;i<ttt.length-1;i++)
		  {
			  fw.write(n1);
			  fw.write(" "+ttt[i].toLowerCase());
			  fw.write(" "+n2);
			  fw.write("\n");
		  }
		  fw.write(n1+" "+ttt[ttt.length-1]+" {benign,malware}\n");
		  
		  fw.write("\n@DATA\n");
		  
		  while (br.ready())
		  {
			  temp=br.readLine();
			 // fw.write("{");
			  fw.write(temp);
			 // fw.write("}");
			  fw.write("\n");
		  }
		  
		  
		  fw.close();
		
	}
	
	public static void main(String[] args) {
		PreparingTrainingDataSet a = new PreparingTrainingDataSet();
		ArrayList<ArrayList<Double>> trainingSet = a.run();
		Hashtable<String,Integer> usefulTable= a.generalFrequencies;
		
		// testing dataset is sperated from training dataset
		PreparingTestingDataSet b = new PreparingTestingDataSet(usefulTable);
		ArrayList<ArrayList<Double>> testingSet = b.run();
		
		prepareCSV(trainingSet, usefulTable, 1, "trainSet");
		prepareCSV(testingSet, usefulTable, 4,  "testSet");
		
		
		
		try{
		csvToArff("trainSet");
		csvToArff("testSet");
		}
		catch (Exception e){
			e.printStackTrace();
		}

		runClassifier("trainSet","testSet");
		
		
	}

}
