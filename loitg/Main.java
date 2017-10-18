package loitg;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {

	public static List<String> readFileToList(String txtFile) {
	    BufferedReader br = null;
	    String line = "";
	    List<String> result = new ArrayList<String>();
	    try {
	        br = new BufferedReader(new FileReader(txtFile));
//	        line = br.readLine();
	        while ((line = br.readLine()) != null) {
	        	result.add(line);
	        }
	
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        if (br != null) {
	            try {
	                br.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }		
	    return result;
	}
	public static void main(String[] args) {
//		Searcher searcher = new Searcher("/home/loitg/trung_kw_3.csv");
		Searcher searcher = new Searcher("/home/loitg/workspace/receipttest/rescources/db/top200.csv");
		
		List<String> allines = readFileToList("/home/loitg/workspace/receipttest/rescources/db/images.txt");
		String filename = null;
		HashMap<String, List<String>> prediction = new HashMap<String, List<String>>();
		HashMap<String, List<String>> images = new HashMap<String, List<String>>();
		for (String line : allines) {
			if (line.length() > 20 && line.substring(0,11).equals("filename---")) {
				filename = line.substring(11);
                prediction.put(filename, new ArrayList<String>());
			} else {
				prediction.get(filename).add(line);
			}
		}
		
		for (HashMap.Entry<String, List<String>> entry : prediction.entrySet()) {
		    filename = entry.getKey();
		    List<String> lines = entry.getValue();
		    Store rs = searcher.search(lines);
		    if (rs != null) {
				System.out.println(filename);
				System.out.println("--------------------");
//				System.out.println(rs);
				List<String> ls = images.get(rs.locationCode);
				if (ls != null) {
					ls.add(filename);
				} else {
					ArrayList<String> temp = new ArrayList<String>();
					temp.add(filename);
					images.put(rs.locationCode, temp);
				}
		    }
		}
		
		
		for (HashMap.Entry<String, List<String>> entry : images.entrySet()) {
			System.out.print(entry.getKey() + ',');
			for (String fn : entry.getValue()) {
				System.out.print(fn + ' ');
			}
			System.out.println();
		}
		
		
		
//		File dir = new File("/home/loitg/Downloads/textResult/");
//		File[] directoryListing = dir.listFiles();
//		if (directoryListing != null) {
//			for (File child : directoryListing) {
//				List<String> lines = readFileToList(child.getAbsolutePath());
//				Store rs = searcher.search(lines);
//				if (rs != null) {
//					System.out.println(child.getName());
//					System.out.println("--------------------");
//				}
//				
////				Scanner scan = new Scanner(System.in);
////				String s = scan.next();
//			}
//		}
		
		

	}

}
