package loitg;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.trungp.ocrreport.FileFolderUtils;

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
		temp.add("lllllV"); 
		temp.add("UNIT"); 
		temp.add("by FanPnce"); 
		temp.add("by FairPrice"); 
		temp.add("L0]"); 
		temp.add("LQ"); 
		temp.add("I ﬂlPPBE' HALL"); 
		temp.add("1 SHOPPERS‘ MALL"); 
		temp.add("2! Ohm Cm Kane Ave 4"); 
		temp.add("21 Choa Chu Kang Ave 4"); 
		temp.add("dBI-M/OS"); 
		temp.add("#01 —04/05"); 
		temp.add("Singapore 589m?"); 
		temp.add("Singapore 689812"); 
		temp.add("[EN H0: SSGCSOIBIL"); 
		temp.add("LEM No: SB3CSO19iL"); 
		temp.add("GS! NO: “4-0004578-0"); 
		temp.add("GST NO: M4—0004578—0"); 
		temp.add("[e]: 5783 7678"); 
		temp.add("[el: 6763 7678"); 
		temp.add("Slip: MITIOIMMIISQI"); 
		temp.add("Slip: — 00000£T(01000411821"); 
		temp.add("Stan: Isaac mthmy Tram: 212088753"); 
		temp.add("Staft: Isaac Anthony — Trans: 2120907693"); 
		temp.add("Date: 10-10-17 1:45"); 
		temp.add("Date: 10—10—17 7:45"); 
		temp.add("Description"); 
		temp.add("Description"); 
		temp.add("mm"); 
		temp.add("faount"); 
		temp.add("2500318"); 
		temp.add("2600318"); 
		temp.add("ENSUM.’"); 
		temp.add("ENSURE"); 
		temp.add("PLUS VANILLA 2mm"); 
		temp.add("PLUS VANILLA 2004"); 
		temp.add("lam-53m"); 
		temp.add("10 pes X 2.80"); 
		temp.add("211.99 9"); 
		temp.add("28.00 C"); 
		temp.add(""); 
		return temp;
	}
	
    public static List<List<String>> readCsvFile(String csvFilePath) {

        File file = new File(csvFilePath);
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        List<List<String>> result = new ArrayList<>();
        try {

            br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] record = line.split(cvsSplitBy);
                List<String> list = new ArrayList<>();
                Collections.addAll(list, record);
                result.add(list);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static String findReceiptFilePath(Map<String, String> receiptFileMap, String fileName) {

        // Remove the extension first.
        if (fileName.toLowerCase().endsWith("jpg")) {
            fileName = fileName.substring(0, fileName.length() - 4);
        }
        for (String name : receiptFileMap.keySet()) {

            // int ratio = FuzzySearch.ratio(fileName, name);
            if (name.contains(fileName)) {
                return receiptFileMap.get(name);
            }
        }
        return null;
    }
    
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("groupby, texts, topx00, range/list");
			System.exit(0);
			args = new String[4];
			args[0] = "/media/sf_uatfull/groupByLocation";
			args[1] = "/media/sf_uatfull/week3/texts_uat1.7k/"; //text results
			args[2] = "/media/sf_uatfull/TOP466_V3.1.csv"; //top200.csv			
		}
		File imageFolder = new File(args[0]);
		
		List<String> codeList = null;
		boolean reversedlist = false;
		if (args.length >3) {
			if (args[3].substring(0,1).equals("~")) {
				args[3] = args[3].substring(1);
				reversedlist = true;
			}
			if (args[3].contains("-")) {
				List<String> intList = Arrays.asList(args[3].split("-"));
				codeList = new ArrayList<String>();
				int lower = Integer.parseInt(intList.get(0))-1;
				int upper = Integer.parseInt(intList.get(1))-1;
				if (lower < 1) {lower = 1;}
				List<List<String> > topx00data = readCsvFile(args[2]);
				if (upper > topx00data.size()) { upper = topx00data.size()-1;}
				for(int i = lower; i <= upper; i++) {
					codeList.add(topx00data.get(i).get(1));
				}
			} else {
				codeList = Arrays.asList(args[3].split(","));
			}
		}
		if (codeList != null) {
			System.out.println("Codes filterred: " + Arrays.toString(codeList.toArray()));
		}
		
//		Searcher searcher = new Searcher("/home/loitg/trung_kw_3.csv");
		Searcher searcher = new Searcher(args[2]);
        
        Map<String, String> fileNames = new HashMap<>();
        FileFolderUtils.getFileNames(fileNames, imageFolder.toPath(), "jpg");
        String crmLocationCode="";
        
		HashMap<String, List<String>> prediction = new HashMap<String, List<String>>();
		  File dir = new File(args[1]);
		  File[] directoryListing = dir.listFiles();
		  int undetected=0, sucess=0, fail =0;
		  if (directoryListing != null) {
		    for (File child : directoryListing) {
		    	String filename = child.getName();
		    	filename = filename.substring(0, filename.length() - 4);
		    	
		    	String receiptFilePath = findReceiptFilePath(fileNames, filename);
		    	
		    	
		    	if (receiptFilePath == null) continue;
              File csvFile = new File(receiptFilePath.substring(0, receiptFilePath.length() - 4) + ".csv");
                if (csvFile.exists()) {

                    // Read csv file
                    List<List<String>> csvFileContent = readCsvFile(csvFile.getAbsolutePath());
                    if (csvFileContent.size() > 1) {
                        // Ignore header
                        List<String> crmRecord = null;
                        ;
                        for (int i = 1; i < csvFileContent.size(); i++) {
                            List<String> rc = csvFileContent.get(i);
                            if (rc.size() > 4) {
                                crmRecord = rc;
                                break;
                            }
                        }

                        if (crmRecord == null || crmRecord.size() < 4) {
                            System.out.println("csvFile: " + csvFile.getAbsolutePath());
                            System.out.println(Arrays.toString(csvFileContent.toArray()));
                            continue;
                        }

                       crmLocationCode = crmRecord.get(0);
//                        String crmReceiptTransactionDate = crmRecord.get(1);
//                        String crmReceiptNo = crmRecord.get(2);
//                        String crmAmount = crmRecord.get(3);
                       
                       boolean containCond;
                       if (codeList == null) {
                    	   containCond = true;
                       } else if (reversedlist) {
                    	   containCond = !codeList.contains(crmLocationCode);
                       } else {
                    	   containCond = codeList.contains(crmLocationCode);
                       }
                       
                       if (containCond) {
                    	   System.out.println("-------" + filename);
                    	   System.out.println("full---" + receiptFilePath);
	           		    	List<String> lines = readFileToList(child.getAbsolutePath());
	        		    	Store rs = searcher.search(lines);                   	   
                    	   
                    	   System.out.println("Actual Code:");
                    	   System.out.println("\t" + crmLocationCode);
                    	   
                    	   if (rs == null) {
                    		   undetected++;
                    		   System.out.println(Column.ANSI_GREEN + "======================  UNDETECTED ===================="+Column.ANSI_RESET);
                    	   } else if (!rs.locationCode.equals(crmLocationCode)) {
                    		   System.out.println(Column.ANSI_RED + "================================  WRONG ==============================" + Column.ANSI_RESET);
                    		   System.out.println(Column.ANSI_RED + "================================  WRONG ==============================" + Column.ANSI_RESET);
                    		   System.out.println(Column.ANSI_RED + "================================  WRONG ==============================" + Column.ANSI_RESET);
                    		   fail++;
                    	   } else {
                    		   sucess++;
                    	   }
                       }
                    }
                }
                

		    	
		    	
		    }
		    System.out.println("UNDETECTED: "+undetected +", SUCCESS: " + sucess +", FAIL: "+ fail);
		    if (codeList != null) {
		    	System.out.println("Codes filterred: " + Arrays.toString(codeList.toArray()));
		    }
		  } else {
		    // Handle the case where dir is not really a directory.
		  // Checking dir.isDirectory() above would not be sufficient
		  // to avoid race conditions with another process that deletes
		  // directories.
		  }

//		if (args.length < 4) {
//			args = new String[4];
//			args[0] = "/home/loitg/Downloads/part2/";
//			args[1] = "/home/loitg/workspace/location_nn/resources/";
//			args[2] = "/media/sf_uatfull/week3/texts_uat1.7k/"; //text results
//			args[3] = "/home/loitg/Downloads/top200_v2.csv"; //top200.csv			
//		}
//
//		
////			Searcher searcher = new Searcher("/home/loitg/trung_kw_3.csv");
//		Searcher searcher = new Searcher(args[3]);
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
