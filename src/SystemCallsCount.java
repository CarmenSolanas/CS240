import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


public class SystemCallsCount {
	public static File folder = new File("/Users/MrTiriti/Downloads/testMipsSep26/analysis/result/SystemCallTraces");
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
	 * Main 
	 */
	public static void main(String[] args){
		HashSet<String> hs = new HashSet<String>();
		walk(folder.getAbsolutePath());
		for(String item:fileList){
			ArrayList<String> systemCallTrace=importCSV(item);
			for (String item2: systemCallTrace)
				hs.add(item2);
		}
		System.out.println(hs);
		System.out.println(hs.size());	
		
		try{
		    PrintWriter writer = new PrintWriter("list_of_syscalls.txt", "UTF-8");
		    for (String item:hs) {
		    	writer.println(item);
		    }
		    writer.close();
		} catch (Exception e) {
		   System.out.println("La cagamos");
		}
	}
	
}
