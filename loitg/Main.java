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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;


public class Main {

	public static void copyAndRename(String source, String dst) {
		Path src = Paths.get(source);
	  Path dest =  Paths.get(dst);
	  try {
	      Files.copy(src,dest);
	  } catch (IOException e) {
	      e.printStackTrace();
	  }
	}
	
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
	
	
	public static List<String> abcc() {
		ArrayList<String> temp = new ArrayList<String>();
		temp.add("r");
		return temp;
	}
	
	
	public static void main(String[] args) {
        if (args[0].equals("-v")) {
            System.out.println("20180825T103100_master");
            System.exit(0);
        }
		Searcher searcher = new Searcher(args[0]);
		List<String> allines = readFileToList(args[1]);
		Store rs1 = searcher.search(allines);
		if (rs1 != null) {
			System.out.println(rs1);
		} else {
			System.out.println("None");
		}
//		
//		
//		args = new String[4];
//		args[0] = "/home/loitg/Downloads/part2/";
//		args[1] = "/home/loitg/workspace/location_nn/resources/";
//		args[2] = "/home/loitg/workspace/receipttest/rescources/db/images.txt"; //text results
//		args[3] = "/home/loitg/workspace/receipttest/rescources/db/top200 (copy).csv"; //top200.csv
//		
////		Searcher searcher = new Searcher("/home/loitg/trung_kw_3.csv");
//		Searcher searcher = new Searcher(args[3]);
//		
//		
////		List<String> hihi = abcc();
////		Store rs1 = searcher.search(hihi);
////		if (rs1 != null) {
////			System.out.println(rs1);
////		}
////		System.exit(0);
//		
//		
//		List<String> allines = readFileToList(args[2]);
//		String filename = null;
//		HashMap<String, List<String>> prediction = new HashMap<String, List<String>>();
//		HashMap<String, List<String>> images = new HashMap<String, List<String>>();
//		for (String line : allines) {
//			if (line.length() > 20 && line.substring(0,11).equals("filename---")) {
//				filename = line.substring(11);
//                prediction.put(filename, new ArrayList<String>());
//			} else {
//				prediction.get(filename).add(line);
//			}
//		}
//		
//		for (HashMap.Entry<String, List<String>> entry : prediction.entrySet()) {
//		    filename = entry.getKey();
//		    System.out.println(filename);
//		    List<String> lines = entry.getValue();
//		    Store rs = searcher.search(lines);
//		    if (rs != null) {
//				System.out.println(filename + "==>" + rs.locationCode);
////				System.out.println(rs);
//				List<String> ls = images.get(rs.locationCode);
//				if (ls != null) {
//					ls.add(filename);
//				} else {
//					ArrayList<String> temp = new ArrayList<String>();
//					temp.add(filename);
//					images.put(rs.locationCode, temp);
//				}
//		    }
//		}
//		
//		
//		for (HashMap.Entry<String, List<String>> entry : images.entrySet()) {
//			System.out.print(entry.getKey() + ',');
//			int i = 0;
//			for (String fn : entry.getValue()) {
//				System.out.print(fn + ' ');
//				i++;
//				
//				copyAndRename(args[0] + fn, args[1] + String.format("%-12s", entry.getKey() ).replace(' ', '0') + '_' + i + ".jpg");
//			}
//			System.out.println();
//		}
//		
//		
//		
////		File dir = new File("/home/loitg/Downloads/textResult/");
////		File[] directoryListing = dir.listFiles();
////		if (directoryListing != null) {
////			for (File child : directoryListing) {
////				List<String> lines = readFileToList(child.getAbsolutePath());
////				Store rs = searcher.search(lines);
////				if (rs != null) {
////					System.out.println(child.getName());
////					System.out.println("--------------------");
////				}
////				
//////				Scanner scan = new Scanner(System.in);
//////				String s = scan.next();
////			}
////		}
		
		

	}

}
