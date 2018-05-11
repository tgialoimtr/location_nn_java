package loitg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.PriorityQueue;

public class Column {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	public static final List<String> ANSI_COLORS = Arrays.asList(ANSI_RED, ANSI_GREEN, ANSI_YELLOW, ANSI_BLUE, ANSI_PURPLE, ANSI_CYAN);
	
	public static String s2c(String input) {
		int index = input.hashCode() % ANSI_COLORS.size();
		index =  (index +ANSI_COLORS.size()) % ANSI_COLORS.size();
		return ANSI_COLORS.get(index);
	}
	
	public class Description
	{
		public Description(Store firstStore) {
			this.rows = new HashSet<Store>();
			this.rows.add(firstStore);
		}
		
		public Set<Store> rows;
		
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
			if (newval.length() < 6) continue;
			Description desc = values.get(newval);
			if (desc != null) {
				desc.rows.add(store);
			} else {
				values.put(newval, new Description(store));
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
	
	public Set<Store> search(List<String> items, List<String> founds) {	
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
				if (temp > 0.5) {
					System.out.println(String.format("\tMATCH %d%% of " + s2c(value.trim()) + "\"%s\"" +ANSI_RESET+ " from ", Math.round(100f*temp), value.trim()));
					founds.add(value.trim());
				}
			}
			result0.add(new Result(prob, value));
		}
		for(int i = 0; i < 4; i++) {
			Result rs = result0.poll();
			if ((rs != null) && rs.prob > 0.5) {
				result1.addAll(this.values.get(rs.value).rows);
			} else {
				break;
			}
		}
		return result1;
	}
	
	public Set<Store> search(String alllines, List<String> founds) {
		PriorityQueue<Result> result0 = new PriorityQueue<Result>(this.values.size(), Collections.reverseOrder());
		String allines_std = Store.standardizeByName(this.name, alllines);
		for(Map.Entry<String, Description> value_desc : this.values.entrySet()) {
			String value = value_desc.getKey();
			int dist = match(allines_std, value);
			double punish = value.length();
			if ((dist < value.length()) && ((value.length() > 7) || (dist == 0))) {
				double x = (punish-dist)/punish - 0.75;
				punish = punish - 2;
				punish = punish*punish;
				punish = punish/(punish + this.shortWordPunish);
//				double prob = Math.exp(-dist/this.errorToProb)*punish;
				double prob = (0.5*Math.tanh(15*x)+0.5)*punish;
				if (prob > 0.5) {
					System.out.println(String.format("\tMATCH %d%% of " + s2c(value.trim()) + "\"%s\"" +ANSI_RESET+ " from ", Math.round(100f*prob), value.trim()));
					founds.add(value.trim());
				}
				result0.add(new Result(prob, value));
			} else {
				result0.add(new Result(0.0, value));
			}
		}
		Set<Store> result1 = new HashSet<Store>();
		for(int i = 0; i < 4; i++) {
			Result rs = result0.poll();
			if ((rs != null) && rs.prob > 0.5) {
				result1.addAll(this.values.get(rs.value).rows);
			} else {
				break;
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
	public int match(String text, String pattern) {
		int n = text.length();
		int m = pattern.length();
		int[][] g = new int[m + 1][n + 1];
		int distance = 999;
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
			}
		}
		return distance;
	}    
	
	
	public String name;
	public HashMap<String, Description> values;
	private double errorToProb;
	private double shortWordPunish;
	
	
}


