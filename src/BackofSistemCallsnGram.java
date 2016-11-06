package helloworld;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class BackofSistemCallsnGram {
	//public static File folder = new File("/Users/MrTiriti/Downloads/testMipsSep26/analysis/result/SystemCallTraces");
	public static File folder = new File("/Users/hyang027/Desktop/240/data");
	public static List<String> fileList = new ArrayList<String>();
	public static Hashtable<String,Integer> generalFrequencies = new Hashtable<String,Integer>();
	
	
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
			if(systemCallTraceFreq.containsKey(newString)){
				systemCallTraceFreq.put(newString, systemCallTraceFreq.get(newString)+1);
			}else{
				systemCallTraceFreq.put(newString, 1);	
				
				if(generalFrequencies.containsKey(newString)){
					generalFrequencies.put(newString, generalFrequencies.get(newString)+1);
				}else{
					generalFrequencies.put(newString, 1);	
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
	 * Main 
	 */
	public static void main(String[] args){
		walk(folder.getAbsolutePath());
		ArrayList<ArrayList<String>> allTraces=new ArrayList<ArrayList<String>>();
		ArrayList<Hashtable<String,Integer>> allFrequencies=new ArrayList<Hashtable<String,Integer>>();
		ArrayList<Hashtable<String,Double>> allScaling=new ArrayList<Hashtable<String,Double>>();
		
	
		
		for(String item:fileList){
			ArrayList<String> systemCallTrace=importCSV(item);
			Hashtable<String,Integer> systemCallTraceFreq=nGram(systemCallTrace,2);
			Hashtable<String,Double> systemCallTraceTF=TF(systemCallTraceFreq);
		   
		 
			allTraces.add(systemCallTrace);
			allFrequencies.add(systemCallTraceFreq);
			allScaling.add(systemCallTraceTF);
		}
		

		Hashtable<String,Double> systemCallTraceIDF= IDF();
		
		System.out.print(systemCallTraceIDF);
		for(Hashtable<String,Double> eachTF:allScaling)
		{
			Set<String> keyHashTable=eachTF.keySet();
			for(String ngram:keyHashTable)
			{
				
				double TFIDF_value = eachTF.get(ngram)*systemCallTraceIDF.get(ngram);
				eachTF.put(ngram, TFIDF_value);
			}
		}
		System.out.println(allScaling);
		
		System.out.println(systemCallTraceIDF);
	
	}
	
}
