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
import loitg.Column.MatchResult;
import loitg.Column;


public class Searcher {

	private static final Pattern RE_GST1 = Pattern.compile("\\W+([MHNl21I][\\dOo$DBQRSIl\\']-?[\\dOoDBQ$SIl\\']{7,8}[ ]{0,3}-?[ ]{0,3}\\w)\\W+");
	private static final Pattern RE_GST2 = Pattern.compile("([rR][eE][gG]|[gG][$sS5][tT]).*?(\\w{1,2}-?\\w{6,8}-?\\w{1,2})\\W");
	private static final Pattern RE_ZIPCODE1 = Pattern.compile("([sS5][li1I][nN]|[pP][oO0][rR][eE]).*?([\\dOoDBQSIl$]{5,7})\\W+");
	private static final Pattern RE_ZIPCODE2 = Pattern.compile("\\W(\\([S5]\\)|[S5][GE]?)[ ]{0,3}\\(?([\\dOoDBQSIl$]{5,7})\\W+");
	
	public class StoreCompareInfo implements Comparable<StoreCompareInfo>{
		int mallCharCount;
		int storeCharCount;
		Store store;
		int winCount;
		int totalCount;
		
		public int compareInt(int a, int b) {
			if (a<b-2) {
				return -1;
			} else if (a>b+2) {
				return 1;
			} else {
				return 0;
			}
		}
		
		public boolean same(String rawas,String rawbs) {
			String[] as = rawas.split("\\|");
			String[] bs = rawbs.split("\\|");
			for (int i = 0; i < as.length; i++) {
				for (int j = 0; j < bs.length; j++) {
					if (as[i].equals(bs[j])) {
						return true;
					}
				}
			}
			return false;
		}
		@Override
		public int compareTo(final StoreCompareInfo other) {
			// MALL
			boolean samemall = same(this.store.mallKeyword, other.store.mallKeyword);
			if (samemall) {
				System.out.print("\tSame Mall ");
				return compareInt(this.storeCharCount, other.storeCharCount);
			}
			//STORE
			boolean samestore = same(this.store.storeKeyword, other.store.storeKeyword);
			if (samestore) {
				System.out.print("\tSame Store ");
				return compareInt(this.mallCharCount, other.mallCharCount);
			}
			//GST
			boolean samegst = Store.standardizeByName(Store.GST_NO, this.store.gstNoPattern).equals(Store.standardizeByName(Store.GST_NO, other.store.gstNoPattern));
			if (samegst) {
				System.out.print("\tSame GST ");
				return compareInt(this.storeCharCount, other.storeCharCount);
			}
			
			return 0;
		}
	}
	public static String addColor(String raw, String name, List<String> founds) {
		String[] row = raw.split("\\|", -1);
		String rs = "";
		for(String s: row) {
			s = Store.standardizeByName(name, s);
			String exact = "";
			if (s.contains("::")) {
				String[] kwandexact = s.split("::");
				s = kwandexact[0];
				exact = kwandexact[1];
			}			
			s = s.trim();
			if ((!founds.isEmpty()) && founds.contains(s)) {
				rs += Column.s2c(s) + s + Column.ANSI_RESET;
			} else {
				rs += s;
			}
			if (!exact.equals("")) {
				rs += "::" + exact;
			}
			rs += "|";
		}
		return rs;
	}
	
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
			Matcher matcher_gst1 = RE_GST1.matcher(line + " ");
			while (matcher_gst1.find()) {
				String temp = matcher_gst1.group(1);
//				System.out.println("gst1 - " + line);
//				System.out.println(temp + "-->" + Store.standardizeByName(Store.GST_NO, temp)); 
				gst_list.add(Store.standardizeByName(Store.GST_NO, temp));
			}
			Matcher matcher_gst2 = RE_GST2.matcher(line + " ");
			while (matcher_gst2.find()) {
				String temp = matcher_gst2.group(2);
//				System.out.println("gst2 - " + line);
//				System.out.println(temp + "-->" + Store.standardizeByName(Store.GST_NO, temp));
				gst_list.add(Store.standardizeByName(Store.GST_NO, temp));
			}
			Matcher matcher_zipcode1 = RE_ZIPCODE1.matcher(line + " ");
			while (matcher_zipcode1.find()) {
				String temp = matcher_zipcode1.group(2);
//				System.out.println("zc1 - " + line);
//				System.out.println(temp + "-->" + Store.standardizeByName(Store.ZIPCODE, temp));
				zipcode_list.add(Store.standardizeByName(Store.ZIPCODE, temp));
			}
			Matcher matcher_zipcode2 = RE_ZIPCODE2.matcher(line + " ");
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
//		int dist = storeCol.match(alllines, "CEGAR @ ");
//		int temp = storeCol.match(alllines, "CENTRAL @STARVISTA");
//		if (temp < dist) dist = temp;
//		if (dist < 3) return null;
		ArrayList<String> founds = new ArrayList<String>();
		System.out.println("GST:");
		Set<Store> rs1 = gstCol.search(gst_list, founds);
		System.out.println("Merchant:");
		Set<Store> rs2 = storeCol.search(alllines, founds);
		rs1.addAll(rs2);
		for (Store s: rs1) {
			System.out.print(s.locationCode + ", ");
		}
		if (rs1.size() >= 1) {
			System.out.println("Mall:");
			Set<Store> rs3 = mallCol.search(alllines, founds);
			System.out.print("rs3: ");
			for (Store s: rs3) {
				System.out.print(s.locationCode + ", ");
			}
			System.out.println("Zipcode:");
			Set<Store> rs4 = zipcodeCol.search(zipcode_list, founds);
			System.out.print("rs4: ");
			for (Store s: rs4) {
				System.out.print(s.locationCode + ", ");
			}
			Set<Store> rs_mall = new HashSet<Store>(rs1);
			rs_mall.retainAll(rs3);
			System.out.print("\nrs_mall: ");
			for (Store s: rs_mall) {
				System.out.print(s.locationCode + ", ");
			}
			Set<Store> rs_zc = new HashSet<Store>(rs1);
			rs_zc.retainAll(rs4);
			System.out.print("\nrs_zc: ");
			for (Store s: rs_zc) {
				System.out.print(s.locationCode + ", ");
			}
			if (rs_mall.isEmpty() && rs_zc.isEmpty()) {
				System.out.println("case1 ");
				return null;
			} else if (rs_mall.size() > 1 && (!rs_zc.isEmpty())) {
				System.out.println("case2 ");
				rs_mall.retainAll(rs_zc);
			} else if (rs_mall.isEmpty()) {
				System.out.println("case3 ");
				rs_mall = rs_zc;
			}
			System.out.print("rs_mall: ");
			for (Store s: rs_mall) {
				System.out.print(s.locationCode + ", ");
			}
			rs1 = rs_mall;
		}
		
		
		List<StoreCompareInfo> compareInfos = new ArrayList<StoreCompareInfo>();
		for(Store s: rs1) {
			StoreCompareInfo ci = new StoreCompareInfo();
			System.out.print(s.locationCode);
			ci.mallCharCount = ss(alllines, s.mallKeyword, mallCol);
			System.out.print(": Mall charcount " + ci.mallCharCount);
			ci.store = s;
			ci.storeCharCount = ss(alllines, s.storeKeyword, storeCol);
			System.out.println(", Store charcount " + ci.storeCharCount);
			ci.totalCount = ci.mallCharCount + ci.storeCharCount;
			compareInfos.add(ci);
		}
		StoreCompareInfo winner = null;
		for(int i = 0; i < compareInfos.size()-1; i ++) {
			for(int j=i+1; j < compareInfos.size(); j++) {
				int winnerindex = -1;
				int compareresult = compareInfos.get(i).compareTo(compareInfos.get(j));
				if (compareresult < 0) {
					System.out.println(String.format("%d < %d, ",i,j));
					winnerindex = j;
				} else if (compareresult > 0) {
					System.out.println(String.format("%d > %d, ",i,j));
					winnerindex = i;
				} else {
					System.out.println(String.format("%d = %d, ",i,j));
				}
				if (winnerindex >= 0) {
					compareInfos.get(winnerindex).winCount += 1;
					if (compareInfos.get(winnerindex).winCount == compareInfos.size()-1) {
						winner = compareInfos.get(winnerindex);
					}
				}
			}
		}
		if (winner == null) {
			if (rs1.size() == 1) {
				winner = compareInfos.get(0);
			}
		}

		
		System.out.println("Found " + rs1.size() + " location code(s):");
		int i = 0;
		for(Store s: rs1) {
			String foundcode = s.locationCode;
			if (winner!= null && winner.store!= null && s == winner.store) {
				foundcode = Column.ANSI_RED + foundcode + Column.ANSI_RESET;
			}
			System.out.println(String.format("\t%d/%-13s:%30s---%s", i, foundcode, addColor(s.mallKeyword, Store.MALL_NAME, founds), 
					addColor(s.storeKeyword, Store.STORE_NAME, founds)));
			i++;
		}
		if (winner!= null && winner.store!= null) {
			return winner.store;
		}
		else {
			return null;
		}
	}

	public int ss(String allines, String keywords, Column col) {
		String allines_std = Store.standardizeByName(col.name, allines);
		String[] newvals = keywords.split("\\|");
		int maxVal = 0;
		int totalVal = 0;
		for (int i = 0; i < newvals.length; i++) {
			String newval = Store.standardizeByName(col.name, newvals[i]);
			String exact = "";
			if (newval.contains("::")) {
				String[] kwandexact = newval.split("::");
				newval = kwandexact[0];
				exact = kwandexact[1];
			}
			if (newval.length() < 6) {
				exact = newval;
			}			
			String value = newval;
			MatchResult mrs = new MatchResult();
			int dist = col.match(allines_std, value, mrs);

			if ((!exact.equals(""))) {
				if ((!mrs.matchedStr.contains(exact))) {
					continue;
				}
			}
			
			if (mrs.numMatchedChar > Math.floor(0.6*value.length()) ) {
				if (mrs.numMatchedChar > maxVal) {
					maxVal = mrs.numMatchedChar;
				}
				totalVal += mrs.numMatchedChar;
			}
			
		}
		return totalVal;
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
                String[] row = line.split(cvsSplitBy, -1);
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
