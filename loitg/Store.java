package loitg;

public class Store {
	public static final String LOCATION_CODE = "locationCode";
	public static final String STORE_NAME = "storeKeyword";
	public static final String MALL_NAME = "mallKeyword";
	public static final String ZIPCODE = "zipcode";
	public static final String GST_NO = "gstNoPattern";
	
	@Override
	public String toString() {
		return this.locationCode + "=,=" + this.mallKeyword + "=,=" + this.storeKeyword + "=,=" + this.zipcode + "=,=" + this.gstNoPattern;
	}
	
	public static String standardizeByName(String colname, String rawValue) {
		String temp = null;
		switch (colname) {
		case Store.LOCATION_CODE:
			return "";
		case Store.STORE_NAME:
		case Store.MALL_NAME:
			temp = rawValue.replaceAll("5", "S").replaceAll("::", " :: ").replaceAll("1", "I").replaceAll("0", "O").replaceAll("8", "B");
			temp = temp.toUpperCase();
			temp = temp.replaceAll(" +", " ");
			return " "+temp+" ";
		case Store.ZIPCODE:
			temp = rawValue.replaceAll("[OoDQ]", "0").replaceAll("[$S]", "5").replaceAll("[lI]", "1");
			return temp;
		case Store.GST_NO:
			if (rawValue.length() > 3) {
				temp = rawValue.replaceAll("[ -]+", "");
				String first = temp.substring(0,1);
				String middle = temp.substring(1, temp.length()-1);
				String last = temp.substring(temp.length()-1);
				middle = middle.replaceAll("[OoDQ]", "0").replaceAll("[$S]", "5").replaceAll("[lI]", "1");
				temp = first + middle + last;
				return temp.toUpperCase();
			} else {
				return "";
			}
		default:
			return "null";
		}		
	}
	
	public Store(String locationCode, String storeKeyword, String mallKeyword, String zipcode, String gstNoPattern) {
		super();
		this.locationCode = locationCode;
		this.storeKeyword = storeKeyword;
		this.mallKeyword = mallKeyword;
		this.zipcode = zipcode;
		this.gstNoPattern = gstNoPattern;
	}
	
	String getByColName(String colname) {
		switch (colname) {
		case Store.LOCATION_CODE:
			return this.locationCode;
		case Store.STORE_NAME:
			return this.storeKeyword;
		case Store.MALL_NAME:
			return this.mallKeyword;
		case Store.ZIPCODE:
			return this.zipcode;
		case Store.GST_NO:
			return this.gstNoPattern;
		default:
			return "null";
		}
	}
	
	
	String locationCode;
	String storeKeyword;
	String mallKeyword;
	String zipcode;
	String gstNoPattern;
	
	
}
