package w4;

import info.bliki.api.Page;
import info.bliki.api.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

public class WikiExtractor {
	
	
	public String getJSONStringFromAPI(String keyword) throws URISyntaxException, IllegalStateException, IOException{
		java.net.URI uri = new URIBuilder()
        .setScheme("http")
        .setHost("en.wikipedia.org")
        .setPath("/w/api.php")
        .setParameter("format", "json")
        .setParameter("action", "query")
        .setParameter("titles", keyword)
         .setParameter("prop", "revisions")
         .setParameter("rvprop", "content|user|timestamp|size|ids|userid|parsedcomment|tags")
        .setParameter("rvlimit", "10")
        .build();
		
		HttpGet httpget = new HttpGet(uri);
				/*
				+ "http://en.wikipedia.org/w/api.php?format=json&action=query&"
				+ "titles=Crimea&prop=revisions&rvprop="
				+ "content|user|timestamp|size|ids|userid|parsedcomment|tags&rvlimit=5");*/
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		
		CloseableHttpResponse resp = httpclient.execute(httpget);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
		String theLine= "";
		String result = "";
		while( (theLine = br.readLine())!= null){
			result = result + theLine;
		}
		
		//System.out.println(result);
		
		return result;
	}
	
	public ArrayList<Revision> parseJSON(String keyword) throws IllegalStateException, URISyntaxException, IOException{
		ArrayList<Revision> revisionsList = new ArrayList<Revision>();
		String json = this.getJSONStringFromAPI(keyword);
		JSONObject obj = new JSONObject(json);
		
		JSONObject page = obj.getJSONObject("query").getJSONObject("pages").getJSONObject("163045");

		JSONArray revisions = page.getJSONArray("revisions");
		
		for(int i= 0; i <revisions.length(); i++) {
			JSONObject jobj = (JSONObject) revisions.get(i);
			System.out.println(jobj.getInt("revid"));
			
			Revision rev = new Revision(jobj.getString("user"), jobj.getInt("userid")+"", jobj.getString("timestamp"),jobj.getInt("size"), jobj.getString("*"), jobj.getString("comment"));
			revisionsList.add(rev);
			
			
		}
		
		return revisionsList;
		//System.out.println(page);
		
		
		
	}
	
	public static void main (String args[]) throws URISyntaxException, ClientProtocolException, IOException{
		
		WikiExtractor e = new WikiExtractor();
		e.parseJSON("Crimea");
	
	}
	
}
