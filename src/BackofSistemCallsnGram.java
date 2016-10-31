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
	//"C:/Users/lluna/Desktop/mio/Desktop/UCR/Fall16/Network Routing/Project/testMipsSep26/analysis/result/MIPS-AES-49mips.AES.DDoS.MIPS/syscalls/parsed_MIPS-AES-49mips.AES.DDoS.MIPS.csv"
	
	
	/*
	 * Import the System Call trace 
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
				String sysCall=line.split(csvSplitBy)[2];
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
	
	public static void main(String[] args){
		ArrayList<String> systemCallTrace=importCSV("C:/Users/lluna/Desktop/mio/Desktop/UCR/Fall16/Network Routing/Project/testMipsSep26/analysis/result/MIPS-AES-49mips.AES.DDoS.MIPS/syscalls/parsed_MIPS-AES-49mips.AES.DDoS.MIPS.csv");
		Hashtable<String,Integer> systemCallTraceFreq=new Hashtable<String,Integer>();
		for(String item:systemCallTrace){
			if(systemCallTraceFreq.containsKey(item)){
				systemCallTraceFreq.put(item, systemCallTraceFreq.get(item)+1);
			}else{
				systemCallTraceFreq.put(item, 1);	
			}
		}
		System.out.println(systemCallTraceFreq);
	}
	
}
