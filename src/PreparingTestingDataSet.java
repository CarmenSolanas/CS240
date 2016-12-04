
import java.io.*;

import java.util.ArrayList;
import java.util.Hashtable;

import java.util.List;
import java.util.Set;
import Jama.*;


public class PreparingTestingDataSet {
	public PreparingTestingDataSet(Hashtable<String,Integer> table){
		usefulTable = table;
	}
	public static Hashtable<String,Integer> usefulTable = new Hashtable<String,Integer>();
	
	//public static File folder = new File("/Users/MrTiriti/Downloads/testMipsSep26/analysis/result/SystemCallTraces");
 	public static File folder = new File("/Users/hyang027/Desktop/240/more_more_data");
 	
 	
//	public static File folder = new File("C:/Users/lluna/Desktop/mio/Desktop/UCR/Fall16/Network Routing/Project/testMipsSep26/analysis/result/SystemCallTraces");
	public static List<String> fileList = new ArrayList<String>();
	public static Hashtable<String,Integer> generalFrequencies = new Hashtable<String,Integer>(); //(Hashtable)usefulTable.clone();

	
	/*
	 * Save in a list all the paths of the System Calls traces 
	 */
	public static void walk( String path ) {
        File root = new File( path );
        File[] list = root.listFiles();
        if (list == null) return;
        for ( File f : list ) {
            if ( f.isDirectory() ) {
                walk( f.getAbsolutePath() );               
            }
            else {
            	fileList.add(f.getAbsoluteFile().toString());                
            }
        }
    }
	
	
	
	/*
	 * For each System Call trace, we are going to save the column with all the system calls 
	 */
	public static ArrayList<String> importCSV(String csvFile){
		BufferedReader br=null;
		String line="";
		String csvSplitBy=",";
		ArrayList<String> listOfString=new ArrayList<String>();
		
		try{
			br=new BufferedReader(new FileReader(csvFile));
			boolean addToList=false;
			while((line=br.readLine())!=null){
				
				String sysCall=line.split(csvSplitBy)[2].replace("\"","");
				if(addToList){
					listOfString.add(sysCall);
				}
				addToList=true;
			}
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(br!=null){
				try{
					br.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		return listOfString;
	}
	
	
	
	/*
	 * For each System Call trace, we are going to compute the bag-of-words-n-gram 
	 */
	public static Hashtable<String,Integer> nGram(ArrayList<String> systemCallTrace,int n){
		Hashtable<String,Integer> systemCallTraceFreq=new Hashtable<String,Integer>();
		for(int i=0;i<systemCallTrace.size()-n+1;i++){
			String newString=systemCallTrace.get(i);
			for(int j=i+1;j<i+n;j++){
				newString=newString+systemCallTrace.get(j);
			}
			

			// added by Hui
			// ignore features not included in training data
			if( usefulTable.keySet().contains(newString) ){
				if(systemCallTraceFreq.containsKey(newString)){
					systemCallTraceFreq.put(newString, systemCallTraceFreq.get(newString)+1);
				}else{
					systemCallTraceFreq.put(newString, 1);	
					
					
									
						if(generalFrequencies.containsKey(newString) ){
							generalFrequencies.put(newString, generalFrequencies.get(newString)+1);
						}else{
							generalFrequencies.put(newString, 1);	
						}
					
				}
			}
			
		}
		return systemCallTraceFreq;
	}
	
	
	
	/*
	 * TF, part of scaling. 
	 */
	public static Hashtable<String,Double> TF(Hashtable<String,Integer> hashtable){
		Hashtable<String,Double> tf=new Hashtable<String,Double>();
		Set<String> keyHashTable=hashtable.keySet();
		for(String item:keyHashTable){
			int freq=hashtable.get(item);
			double newFreq=Math.log10((freq+1.0) ); 
			tf.put(item, newFreq);
		}
		return tf;
	}
	
	/*
	 * IDF, part of scaling. 
	 */
	public static Hashtable<String,Double> IDF(){
		Hashtable<String,Double> idf=new Hashtable<String,Double>();
		int N=fileList.size();
		Set<String> keyHashTable=generalFrequencies.keySet();
		for(String item:keyHashTable){
			int freq=generalFrequencies.get(item);
			double newFreq=Math.log10( (1.0+N)/(freq+1.0) ) +1.0;
			idf.put(item, newFreq);
		}
		return idf;
	}
	
	
	/*
	 * TFIDF, final of scaling. 
	 */
	public static ArrayList<Hashtable<String,Double>> TFIDF(ArrayList<Hashtable<String,Double>> listhashtablesTF, Hashtable<String,Double> hashtableIDF){
		ArrayList<Hashtable<String,Double>> newAllScaling= new ArrayList<Hashtable<String,Double>>();
		
		
		for(Hashtable<String,Double> eachTF:listhashtablesTF){
			Hashtable<String,Double> newTable = new Hashtable<String,Double>();
			
			Set<String> keyHashTable=eachTF.keySet();
			double norm2=0.0;
			for(String ngram:keyHashTable){
				double TFIDF_value = eachTF.get(ngram)*hashtableIDF.get(ngram);
				newTable.put(ngram, TFIDF_value);
				norm2=norm2+Math.pow(TFIDF_value, 2.0);
			}
			
			
			norm2=Math.sqrt(norm2);
			for(String ngram:keyHashTable){
				newTable.put(ngram, newTable.get(ngram)/norm2);
			}
			
			newAllScaling.add(newTable);
		}
		
		return newAllScaling;
	}
	
	
	/*
	 * Creating Matrix with zeros to prepare the data set for the classifiers
	 */
	public static ArrayList<ArrayList<Double>> prepareDataSet(ArrayList<Hashtable<String,Double>> allScaling){
		Set<String> keySetGF=generalFrequencies.keySet();
		String[] stringGF=new String[generalFrequencies.size()];
		int order=0;
		for(String keys:keySetGF){
			stringGF[order]=keys;
			order++;
		}
		
		ArrayList<ArrayList<Double>> dataset=new ArrayList<ArrayList<Double>>();
		
		for(int i=0;i<fileList.size();i++){
			Hashtable<String,Double> row=allScaling.get(i);
			ArrayList<Double> newRow=new ArrayList<Double>();
			for(int j=0;j<stringGF.length;j++){
				if(row.containsKey(stringGF[j])){	
					newRow.add(row.get(stringGF[j]));
				}
				else{
					newRow.add(0.0);
				}
			}
			dataset.add(newRow);
		}
		return dataset;
	}
	
	
	
	/*
	 * SVD, if we want to use it we have to review the results
	 */
	public static Matrix SVD(ArrayList<Hashtable<String,Double>> allScaling){
		Set<String> keySetGF=generalFrequencies.keySet();
		String[] stringGF=new String[generalFrequencies.size()];
		int order=0;
		for(String keys:keySetGF){
			stringGF[order]=keys;
			order++;
		}
		
		double[][] beforeSVD=new double[stringGF.length][fileList.size()];
		
		for(int j=0;j<fileList.size();j++){
			Hashtable<String,Double> column=allScaling.get(j);
			for(int i=0;i<stringGF.length;i++){
				if(column.containsKey(stringGF[i])){
					beforeSVD[i][j]=column.get(stringGF[i]);
				}
				else{
					beforeSVD[i][j]=0;
				}
			}
		}
		
		
		
		Matrix newMatrix = new Matrix(beforeSVD);
		SingularValueDecomposition svd = new SingularValueDecomposition(newMatrix);
		
		double[] sv=svd.getSingularValues();
		double sum = 0.0;
		for (int i=0; i<sv.length; i++) {
			sum = sum + sv[i];
		}
		double[] dvNorm = sv;
		for (int i=0; i<sv.length; i++) {
			dvNorm[i] = sv[i] / sum;
		}
		double sumDV = 0.0;
		int iter = 0;
		while (sumDV <0.75) {
			sumDV = sumDV + dvNorm[iter];
			iter ++;
		}
		
		Matrix uk = svd.getU().getMatrix(0, svd.getU().getRowDimension()-1, 0, iter-1);
		Matrix sk = svd.getU().getMatrix(0, iter-1, 0, iter-1);
		Matrix partialProduct = sk.times(uk.transpose());
		Matrix afterSVD = partialProduct.times(newMatrix);
		
		return afterSVD;
	}
	
	
	

	/*
	 * Run
	 */
	public static ArrayList<ArrayList<Double>> run(){
		walk(folder.getAbsolutePath());
		ArrayList<ArrayList<String>> allTraces=new ArrayList<ArrayList<String>>();
		ArrayList<Hashtable<String,Integer>> allFrequencies=new ArrayList<Hashtable<String,Integer>>();
		ArrayList<Hashtable<String,Double>> allScaling=new ArrayList<Hashtable<String,Double>>();

		
		// preserver the keySets form the generalFrequencies from training data
		generalFrequencies = (Hashtable)usefulTable.clone();
		for(String key: generalFrequencies.keySet()){
			generalFrequencies.put(key, 0);
		}

		for(String item:fileList){
			ArrayList<String> systemCallTrace=importCSV(item);
			Hashtable<String,Integer> systemCallTraceFreq=nGram(systemCallTrace,2);
			Hashtable<String,Double> systemCallTraceTF=TF(systemCallTraceFreq);
			allTraces.add(systemCallTrace);
			allFrequencies.add(systemCallTraceFreq);
			allScaling.add(systemCallTraceTF);
		}

		Hashtable<String,Double> systemCallTraceIDF= IDF();
		allScaling=TFIDF(allScaling,systemCallTraceIDF);
		
		//Matrix reducedAllScaling = SVD(allScaling);
		
		ArrayList<ArrayList<Double>> dataset=prepareDataSet(allScaling);
		
		return dataset;
	}
	
}
