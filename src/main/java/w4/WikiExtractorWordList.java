package w4;

import info.bliki.api.Page;
import info.bliki.api.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
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

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.JSONSaver;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stemmers.Stemmer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.util.Map;

import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

public class WikiExtractorWordList {

	public String getJSONStringFromAPI(String keyword) throws URISyntaxException, IllegalStateException, IOException {
		java.net.URI uri = new URIBuilder().setScheme("http").setHost("en.wikipedia.org").setPath("/w/api.php")
				.setParameter("format", "json").setParameter("action", "query").setParameter("titles", keyword)
				.setParameter("prop", "revisions")
				.setParameter("rvprop", "content|user|timestamp|size|ids|userid|parsedcomment|tags")
				.setParameter("rvlimit", "60").build();

		HttpGet httpget = new HttpGet(uri);
		/*
		 * + "http://en.wikipedia.org/w/api.php?format=json&action=query&" +
		 * "titles=Crimea&prop=revisions&rvprop=" +
		 * "content|user|timestamp|size|ids|userid|parsedcomment|tags&rvlimit=5"
		 * );
		 */

		CloseableHttpClient httpclient = HttpClients.createDefault();

		CloseableHttpResponse resp = httpclient.execute(httpget);

		BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
		String theLine = "";
		String result = "";
		while ((theLine = br.readLine()) != null) {
			result = result + theLine;
		}

		// System.out.println(result);

		return result;
	}

	public RevisionList parseJSON(String keyword) throws IllegalStateException, URISyntaxException, IOException {
		RevisionList revisionsList = new RevisionList();
		String json = this.getJSONStringFromAPI(keyword);
		JSONObject obj = new JSONObject(json);

		JSONObject page = obj.getJSONObject("query").getJSONObject("pages").getJSONObject("163045");

		JSONArray revisions = page.getJSONArray("revisions");

		for (int i = 0; i < revisions.length(); i++) {
			JSONObject jobj = (JSONObject) revisions.get(i);

			Revision rev = new Revision(jobj.getString("user"), jobj.getInt("userid") + "",
					jobj.getString("timestamp"), jobj.getInt("size"), jobj.getString("*"),
					jobj.getString("parsedcomment"));
			revisionsList.add(rev);

			// System.out.println(rev.getComment().replaceAll("\\<.*?>",""));
			// System.out.println(rev.getTime_stamp());
			// System.out.println(rev.getText().replaceAll("\\<.*?>",""));

			// System.out.println("########");

		}

		return revisionsList;
		// System.out.println(page);

	}

	@SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
	public static void main(String args[]) throws Exception {

		WikiExtractorWordList e = new WikiExtractorWordList();
		HashMap<String, String> dayAggregate = e.parseJSON("Crimea").aggregateComments();
		Instances dataSet;

		FastVector attributes;
		attributes = new FastVector();

		// Create the dataset
		attributes.addElement(new Attribute("date", (FastVector) null));
		attributes.addElement(new Attribute("text", (FastVector) null));
		dataSet = new Instances("FOMC", attributes, 0);
		
		// Print
		for (Map.Entry<String, String> entry : dayAggregate.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			System.out.println(key);
			System.out.println(value);
			System.out.println("########");

			

			Instance instance = new DenseInstance(2);
			instance.setValue((Attribute) attributes.elementAt(0), key);
			instance.setValue((Attribute) attributes.elementAt(1), value); // meeting

//			speech = speech.replace("“", "").replace("”", "");
//			speech = speech.replaceAll("[0-9]", "GELD");

			dataSet.add(instance);

		}
		
		// //Apply StringToWord filter
		// StringToWordVector filter = new
		// StringToWordVector(Integer.MAX_VALUE);
		StringToWordVector filter = new StringToWordVector(2000);
		filter.setIDFTransform(true);
		filter.setTFTransform(true);
		filter.setOutputWordCounts(true);

		filter.setMinTermFreq(1);
		filter.setLowerCaseTokens(true);

//		Stemmer stemmer = new SnowballStemmer();
		// if (useStemmer) filter.setStemmer(stemmer);// TODO: stemmer!!!
		// if (stopwordsFile != null) filter.setStopwords(new
		// File(stopwordsFile));
		// filter.setUseStoplist(useStoplist); // default stopword list

		filter.setAttributeIndices("2");
		filter.setInputFormat(dataSet);
		Instances dataFiltered = Filter.useFilter(dataSet, filter);
		
		JSONSaver saver = new JSONSaver();
//		saver.setDestination(new File("output/idf.json"));
		saver.setFile(new File("output/idf.json"));
		saver.setInstances(dataFiltered);
		saver.writeBatch();

	}

}
