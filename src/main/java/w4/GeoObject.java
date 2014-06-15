package w4;

import org.apache.http.client.methods.HttpGet;

public class GeoObject {
	private String lati;
	private String longi;
	
	private String countryCode;

	public GeoObject(String lati, String longi, String countryCode) {
		super();
		this.lati = lati;
		this.longi = longi;
		this.countryCode = countryCode;
	}

	public String getLati() {
		return lati;
	}

	public String getLongi() {
		return longi;
	}

	public String getCountryCode() {
		return countryCode;
	}

	
	
	


}
