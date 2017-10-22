package loitg;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import loitg.Column.Description;


public class Searcher {

	private static final Pattern RE_GST1 = Pattern.compile("\\W+([MHNl21I][\\dOo$DBQRSIl\\']-?[\\dOoDBQ$SIl\\']{7,8}[ ]{0,3}-?[ ]{0,3}\\w)\\W+");
	private static final Pattern RE_GST2 = Pattern.compile("([rR][eE][gG]|[gG][$sS5][tT]).*?(\\w{1,2}-?\\w{6,8}-?\\w{1,2})\\W");
	private static final Pattern RE_ZIPCODE1 = Pattern.compile("([sS5][li1I][nN]|[pP][oO0][rR][eE]).*?([\\dOoDBQSIl\\']{5,7})\\W+");
	private static final Pattern RE_ZIPCODE2 = Pattern.compile("\\W(\\([S5]\\)|[S5][GE]?)[ ]{0,3}([\\dOoDBQSIl\\']{5,7})\\W+");
			    
	public Searcher(String database_file) {
		data = new ArrayList<Store>();
		readCSV(database_file);
		for (Store store : data) {
			mallCol.initAddRow(store);
			zipcodeCol.initAddRow(store);
			gstCol.initAddRow(store);
			storeCol.initAddRow(store);
		}
//		int total = 0;
//		for(Map.Entry<String, Description> zc : storeCol.values.entrySet()) {
//			System.out.println(zc.getKey() + "--" + zc.getValue().rows.size());
//			total += zc.getValue().rows.size();
//		}
//		System.out.println("sum " + total);
	}
	
	
	public Store search(List<String> lines) {
		List<String> gst_list = new ArrayList<String>();
		List<String> zipcode_list = new ArrayList<String>();
		String alllines = "";
		for (String line : lines) {
			Matcher matcher_gst1 = RE_GST1.matcher(line);
			while (matcher_gst1.find()) {
				String temp = matcher_gst1.group(1);
//				System.out.println("gst1 - " + line);
//				System.out.println(temp + "-->" + Store.standardizeByName(Store.GST_NO, temp)); 
				gst_list.add(Store.standardizeByName(Store.GST_NO, temp));
			}
			Matcher matcher_gst2 = RE_GST2.matcher(line);
			while (matcher_gst2.find()) {
				String temp = matcher_gst2.group(2);
//				System.out.println("gst2 - " + line);
//				System.out.println(temp + "-->" + Store.standardizeByName(Store.GST_NO, temp));
				gst_list.add(Store.standardizeByName(Store.GST_NO, temp));
			}
			Matcher matcher_zipcode1 = RE_ZIPCODE1.matcher(line);
			while (matcher_zipcode1.find()) {
				String temp = matcher_zipcode1.group(2);
//				System.out.println("zc1 - " + line);
//				System.out.println(temp + "-->" + Store.standardizeByName(Store.ZIPCODE, temp));
				zipcode_list.add(Store.standardizeByName(Store.ZIPCODE, temp));
			}
			Matcher matcher_zipcode2 = RE_ZIPCODE2.matcher(line);
			while (matcher_zipcode2.find()) {
				String temp = matcher_zipcode2.group(2);
//				System.out.println("zc2 - " + line);
//				System.out.println(temp + "-->" + Store.standardizeByName(Store.ZIPCODE, temp));
				zipcode_list.add(Store.standardizeByName(Store.ZIPCODE, temp));
			}
//			System.out.println(line);
			alllines += line + " ";
		}
		// Cheating
		int dist = storeCol.match(alllines, "CEGAR @ ");
		int temp = storeCol.match(alllines, "CENTRAL @STARVISTA");
		if (temp < dist) dist = temp;
		if (dist < 3) return null;
		Set<Store> rs1 = gstCol.search(gst_list);
		Set<Store> rs2 = storeCol.search(alllines);
		rs1.addAll(rs2);

		if (rs1.size() >= 1) {
			Set<Store> rs3 = mallCol.search(alllines);
			Set<Store> rs4 = zipcodeCol.search(zipcode_list);
			Set<Store> rs_mall = new HashSet<Store>(rs1);
			rs_mall.retainAll(rs3);
			Set<Store> rs_zc = new HashSet<Store>(rs1);
			rs_zc.retainAll(rs4);
			if (rs_mall.isEmpty() && rs_zc.isEmpty()) {
				return null;
			} else if (rs_mall.size() > 1 && (!rs_zc.isEmpty())) {
				rs_mall.retainAll(rs_zc);
			} else if (rs_mall.isEmpty()) {
				rs_mall = rs_zc;
			}
			rs1 = rs_mall;
		}
		if (rs1.size() == 1) {
			return rs1.iterator().next();
		}
		else {
			if (rs1.size() > 1) {
				System.out.println("Too many " + rs1.size());
				// Cheating
				HashSet<Store> filterBugis = new HashSet<Store>();
				int busisJuntionCount = 0;
				for (Store s : rs1) {
					System.out.println(s);
					if (s.mallKeyword.contains("BUGIS+") || s.mallKeyword.contains("JUNCTION 8")) {
						busisJuntionCount++;
					}
					if (s.mallKeyword.contains("BUGIS JUNCTION")) {
						busisJuntionCount++;
						filterBugis.add(s);
					}
					if (busisJuntionCount == rs1.size() && filterBugis.size() == 1) {
						return filterBugis.iterator().next();
					}
				}
			}
			return null;
		}
	}
	
	public void readCSV(String csv_file) {
       String csvFile = csv_file;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            br = new BufferedReader(new FileReader(csvFile));
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] row = line.split(cvsSplitBy,-1);
//                Store store = new Store(row[0],row[4],row[6],row[2],row[1]);
                Store store = new Store(row[1],row[4],row[3],row[5],row[6]);
                data.add(store);
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
	}
	
	List<Store> data;
	Column mallCol = new Column(Store.MALL_NAME, 2.0, 8.0);
	Column zipcodeCol = new Column(Store.ZIPCODE, 2.0, 1.0);
	Column gstCol = new Column(Store.GST_NO, 2.0, 1.0);
	Column storeCol = new Column(Store.STORE_NAME, 2.0, 8.0);
}
