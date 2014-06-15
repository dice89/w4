package w4;

import info.bliki.api.Page;
import info.bliki.api.Revision;
import info.bliki.api.User;

import java.io.IOException;
import java.net.URISyntaxException;
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

import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

public class WikiExtractor {
	
	public static void main (String args[]) throws URISyntaxException, ClientProtocolException, IOException{
		java.net.URI uri = new URIBuilder()
        .setScheme("http")
        .setHost("en.wikipedia.org")
        .setPath("/w/api.php")
        .setParameter("format", "json")
        .setParameter("action", "query")
        .setParameter("titles", "Crimea")
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
		
		resp.getEntity().getContent();
		
		
		
	}
	
}
