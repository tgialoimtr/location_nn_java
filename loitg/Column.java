package loitg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import loitg.Column.Result;

import java.util.PriorityQueue;

public class Column {

	public static class MatchResult {
		MatchResult() {
			matchedStr = "";
			matchedPos = 0;
			numMatchedChar = 0;
		}
		String matchedStr;
		int matchedPos;
		int numMatchedChar;
	}

	public class Description
	{
		public Description(Store firstStore, String exact) {
			this.rows = new HashSet<Store>();
			this.rows.add(firstStore);
			this.exact = exact;
		}
		public Description(Store firstStore) {
			this(firstStore, ""); 
		}
		public Set<Store> rows;
		public String exact;
	};
	
	public Column(String name, double e2p, double swp) {
		super();
		this.name = name;
		this.values = new HashMap<String, Description>();
		this.errorToProb = e2p;
		this.shortWordPunish = swp;
	}
	
	public void initAddRow(Store store) {
		String rawval = store.getByColName(this.name);
		if (rawval.isEmpty()) {
//			System.out.println("null value");
			return;
		}
		
		String[] newvals = rawval.split("\\|");
		for (int i = 0; i < newvals.length; i++) {
			String newval = Store.standardizeByName(this.name, newvals[i]);
			String exact = "";
			if (newval.contains("::")) {
				String[] kwandexact = newval.split("::");
				newval = kwandexact[0];
				exact = kwandexact[1];
			}
			if (newval.length() < 2) {
				continue;
			}
			if (newval.length() < 6) {
				exact = newval;
			}
			Description desc = values.get(newval);
			if (desc != null) {
				desc.rows.add(store);
				desc.exact = exact;
			} else {
				values.put(newval, new Description(store, exact));
			}
		}
	}
	
	class Result implements Comparable<Result> {
		double prob;
		String value;
		public Result(double prob, String value) {
			super();
			this.prob = prob;
			this.value = value;
		}
		@Override
		public int compareTo(Result other) {
			if (this.prob < other.prob) return -1;
			else if (this.prob > other.prob) return 1;
			else return 0;
		}
	}	
	
	public Set<Store> search(List<String> items) {	
		Set<Store> result1 = new HashSet<Store>();
		if (items.isEmpty()) return result1;
		PriorityQueue<Result> result0 = new PriorityQueue<Result>(this.values.size(), Collections.reverseOrder());
		for(Map.Entry<String, Description> value_desc : this.values.entrySet()) {
			String value = value_desc.getKey();
			double prob = 0.0;
			for(String item : items){
				int dist = match(item, value);
				double temp = Math.exp(-dist/this.errorToProb);
				if (temp > prob) {
					prob = temp;
				}
			}
			result0.add(new Result(prob, value));
		}
		int selectedCount = 0;
		while(true) {
			Result rs = result0.poll();
			if (rs == null) {
				break;
			} else {
				if (rs.prob < 0.50 || (rs.prob < 0.90 && selectedCount >= 4)) break;
				result1.addAll(this.values.get(rs.value).rows);
				selectedCount++;
			}
		}
		return result1;
	}
	
	public Set<Store> search(String alllines) {
		PriorityQueue<Result> result0 = new PriorityQueue<Result>(this.values.size(), Collections.reverseOrder());
		String allines_std = Store.standardizeByName(this.name, alllines);
		for(Map.Entry<String, Description> value_desc : this.values.entrySet()) {
			String value = value_desc.getKey();
			MatchResult mrs = new MatchResult();
			int dist = match(allines_std, value, mrs);
			
			String exact = value_desc.getValue().exact;
			if ((!exact.equals(""))) {
				if ((!mrs.matchedStr.contains(exact))) {
					continue;
				}
			}
			double punish = value.length();
			if ((dist < value.length()) && ((value.length() > 7) || (dist == 0))) {
				double x = (punish-dist)/punish - 0.75;
				punish = punish - 2;
				punish = punish*punish;
				punish = punish/(punish + this.shortWordPunish);
//				double prob = Math.exp(-dist/this.errorToProb)*punish;
				double prob = (0.5*Math.tanh(15*x)+0.5)*punish;
				result0.add(new Result(prob, value));
			} else {
				result0.add(new Result(0.0, value));
			}
		}
		Set<Store> result1 = new HashSet<Store>();
		int selectedCount = 0;
		while(true) {
			Result rs = result0.poll();
			if (rs == null) {
				break;
			} else {
				if (rs.prob < 0.50 || (rs.prob < 0.90 && selectedCount >= 4)) break;
				result1.addAll(this.values.get(rs.value).rows);
				selectedCount++;
			}
		}
		return result1;
	}

	private int min(int a, int b, int c) {
		int result = a;
		if (result > b) result = b;
		if (result > c) result = c;
		return result;
	}
	public int match(String text, String pattern, MatchResult rs) {
		int n = text.length();
		int m = pattern.length();
		int[][] g = new int[m + 1][n + 1];
		int distance = 999;
		int pos = 0;
		for (int i = 0; i < m + 1; ++i) {
			g[i][0] = i;
		}
		
		for (int j = 1; j < n + 1; ++j) {
			for (int i = 1; i < m + 1; ++i) {
				int delta = 1;
				if (text.charAt(j - 1) == pattern.charAt(i - 1)) {
					delta = 0;
				}
				g[i][j] = min(g[i - 1][j - 1] + delta,
				              g[i - 1][j] + 1,
				              g[i][j - 1] + 1);
			}
			if (g[m][j] <= distance) {
				distance = g[m][j];
				pos = j;
			}
		}
		if (rs != null) {
			int start = pos - pattern.length();
			if (start < 0) { start = 0; }
			rs.matchedStr = text.substring(start, pos);
			rs.matchedPos = start;
			rs.numMatchedChar = pattern.length() - distance;
		}
		return distance;
	}    

	public int match(String text, String pattern) {
		return match(text, pattern, null);
	}
	
	public String name;
	public HashMap<String, Description> values;
	private double errorToProb;
	private double shortWordPunish;
	
	
}


