package w4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

public class IPLocExtractor {
	
	
	CloseableHttpClient  client;

	public IPLocExtractor() {
		super();
		client = HttpClients.createDefault();
	}
	
	public GeoObject getGeoLocationForIP(String ip) throws URISyntaxException, ClientProtocolException, IOException{
		
		//freegeoip.net/{format}/{ip_or_hostname}
		java.net.URI uri = new URIBuilder()
        .setScheme("http")
        .setHost("freegeoip.net")
        .setPath("/json/"+ip)
        .build();
		
		HttpGet httpget = new HttpGet(uri);
		
		CloseableHttpResponse resp = client.execute(httpget);

		BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
		String theLine= "";
		String result = "";
		while( (theLine = br.readLine())!= null){
			result = result + theLine;
		}
		//System.out.println(result);
		JSONObject jsonobj= new JSONObject(result);
		
		String country_code = jsonobj.getString("country_code");
		

		String latitude = jsonobj.getDouble("latitude")+"";
		
		String longitude = jsonobj.getDouble("longitude")+"";
		
		GeoObject geobj = new GeoObject(latitude, longitude,country_code);
		
		return geobj;
	}
	
	public static void main (String args[]) throws ClientProtocolException, URISyntaxException, IOException{
		IPLocExtractor ip = new IPLocExtractor();
		ip.getGeoLocationForIP("46.237.207.122");
	}
	
}
