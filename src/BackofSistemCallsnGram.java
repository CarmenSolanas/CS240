import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class BackofSistemCallsnGram {
	public static File folder = new File("C:/Users/lluna/Desktop/mio/Desktop/UCR/Fall16/Network Routing/Project/testMipsSep26/analysis/result/SystemCallTraces");
	public static List<String> fileList = new ArrayList<String>();
	
	
	
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
			}
		}
		return systemCallTraceFreq;
	}
	
	
	
	/*
	 * Main 
	 */
	public static void main(String[] args){
		walk(folder.getAbsolutePath());
		ArrayList<ArrayList<String>> allTraces=new ArrayList<ArrayList<String>>();
		ArrayList<Hashtable<String,Integer>> allFrequencies=new ArrayList<Hashtable<String,Integer>>();
		for(String item:fileList){
			ArrayList<String> systemCallTrace=importCSV(item);
			Hashtable<String,Integer> systemCallTraceFreq=nGram(systemCallTrace,2);
			System.out.println(systemCallTraceFreq);
			allTraces.add(systemCallTrace);
			allFrequencies.add(systemCallTraceFreq);
		}
		
		
	}
	
}
