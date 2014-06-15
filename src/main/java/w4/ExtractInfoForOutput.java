package w4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.jsoup.Jsoup;

import weka.core.pmml.NormDiscrete;

import com.google.gson.Gson;

public class ExtractInfoForOutput {
	private WikiExtractor wikiExtractor;
	
	public ExtractInfoForOutput(){
		this.wikiExtractor = new WikiExtractor();
		
	}
	
	public ArrayList<SummaryObjectGroup> extract(String keyword) throws  URISyntaxException, IOException, ParseException{
		RevisionList revisions = wikiExtractor.parseJSON(keyword);
		return getInfoForMap(revisions);
	}
	
	public void buildTFIDFVector(String date,HashMap<String,ArrayList<Revision>> lists) throws FileNotFoundException{
		ArrayList<String> comments = new ArrayList<String>();
		for(String key: lists.keySet() ){
			for(Revision revision : lists.get(key)){
				comments.add(revision.getText());
			}
		}
		
		ArrayList<HashMap<String,Double>> wordVectors = new ArrayList<HashMap<String,Double>>();
		
		ArrayList<TFIDFWord> words = new ArrayList<TFIDFWord>();
		HashMap<String,Integer> docfrequeny = new HashMap<String, Integer>();
		for(String revision: comments){

			String revision2 = html2text(revision);		
			revision2 = revision2.replaceAll("[0-9]", "");
			revision2 = revision2.replace(";", "");
			revision2 = revision2.replace(",", "");
			revision2 = revision2.replace("]", "");
			revision2 = revision2.replace("[", "");
			revision2 = revision2.replace("'", "");
			revision2 = revision2.replace("&", "");
			revision2 = revision2.replace("=", "");
			revision2 = revision2.replace("?", "");
			revision2 = revision2.replace("!", "");
			revision2 = revision2.replace("(", "");
			revision2 = revision2.replace(")", "");
			revision2 = revision2.replaceAll("\\?", "");
			revision2 = revision2.replace("\"", "");
			revision2 = revision2.replace(":", "");
			revision2 = revision2.replace("|", " ");
			revision2 = revision2.replace("}", " ");
			String[] splitting = revision2.split(" ");
			for(String word: splitting) {
			
				String normword = word.toLowerCase().trim();
				int doc_frequency = 1;

				if(docfrequeny.containsKey(normword)){
					doc_frequency = docfrequeny.get(normword);
					doc_frequency++;
				}
		
				docfrequeny.put(normword,doc_frequency);
		
			}
			
		}
		
		for(String revision: comments){
			String revision2 = html2text(revision);		
			revision2 = revision2.replaceAll("[0-9]", "");
			revision2 = revision2.replace(";", "");
			revision2 = revision2.replace(",", "");
			revision2 = revision2.replace("]", "");
			revision2 = revision2.replace("[", "");
			revision2 = revision2.replace("'", "");
			revision2 = revision2.replace("&", "");
			revision2 = revision2.replace("=", "");
			revision2 = revision2.replace("?", "");
			revision2 = revision2.replace("!", "");
			revision2 = revision2.replace("(", "");
			revision2 = revision2.replace(")", "");
			revision2 = revision2.replaceAll("\\?", "");
			revision2 = revision2.replace("\"", "");
			revision2 = revision2.replace(":", "");
			revision2 = revision2.replace("|", " ");
			revision2 = revision2.replace("}", " ");
			String[] splitting = revision2.split(" ");
			HashMap<String,Integer> termfrequency = new HashMap<String, Integer>();
			for(String word: splitting) {

				String normword = word.toLowerCase().trim();
				int term_frequency = 1;
				
				if(termfrequency.containsKey(normword)){
					term_frequency = termfrequency.get(normword);
				}
				termfrequency.put(normword, term_frequency);
			}
			HashMap<String,Double> tfidfs = new HashMap<String, Double>();
			for(String normword: termfrequency.keySet()){
				double tfidf =(double) ((double) termfrequency.get(normword)) * ( Math.log( comments.size() /( (double)	docfrequeny.get(normword))));
				
				tfidfs.put(normword, tfidf);
				
				TFIDFWord ww = new TFIDFWord(tfidf, normword);
				
				words.add(ww);
			}
			
			wordVectors.add(tfidfs);	
		}
		
		Collections.sort(words);
		
		String html="";
		
		for(int i = 0; i<  words.size() && i < 30; i++){
			String tag = "<a target=\"_blank\" href=\"https://www.google.de/?q="+words.get(i).getWord()+"\" rel=\""+ Math.ceil(words.get(i).getTfidf()*100)+"\">"+words.get(i).getWord()+"</a> \n";
			html = html +tag;
		}
		
		
		File f = new File("data/datetagcloud"+date+".html");
		
		PrintWriter pw = new PrintWriter(f);
		pw.write(html);
		pw.flush();
		pw.close();
		
	}
	
	
	public ArrayList<SummaryObjectGroup> getInfoForMap(RevisionList revisions) throws ParseException, FileNotFoundException{
		
		
		HashMap<String,HashMap<String,ArrayList<Revision>>> aggregates = revisions.aggregateCommentsOverTimeAndOrigin();
	
		ArrayList<RevisionSummaryObject> summaryObject = new ArrayList<RevisionSummaryObject>();
		
		ArrayList<SummaryObjectGroup> summaryObjectList = new ArrayList<SummaryObjectGroup>();
		HashMap<Date,ArrayList<RevisionSummaryObject>> datemap  = new HashMap<Date, ArrayList<RevisionSummaryObject>>();
		for (String date :aggregates.keySet()) {
			
			
			HashMap<String,ArrayList<Revision>> map= aggregates.get(date);
			
			
			this.buildTFIDFVector(date,map);
	
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date result =  df.parse(date);
			
			SummaryObjectGroup sog = new SummaryObjectGroup(result);
			System.out.println(result.toGMTString());
			for (String code : map.keySet()) {
				ArrayList<Revision> reflist = map.get(code);
				String country = code;
				int frequency = reflist.size();
				int editSize = 0;
				for (Revision revision : reflist) {
					editSize  = editSize + revision.getSize(); 
				}
				
				double avg_editSize = editSize/frequency;
				RevisionSummaryObject rso = new RevisionSummaryObject(frequency, avg_editSize, country,date);
				sog.addSummary(rso);
			}
			
			summaryObjectList.add(sog);
		}
		return summaryObjectList;
	}
	
	public static void main(String args[]) throws URISyntaxException, IOException, ParseException{
		ExtractInfoForOutput eiFO = new ExtractInfoForOutput();
		ArrayList<SummaryObjectGroup> toserialize= eiFO.extract("Crimea");
		
		Gson gson = new Gson();
		
		Collections.sort(toserialize);
		
		
	String json = gson.toJson(toserialize);
		
		File jsonFile = new File("dataCrimea.json");
		
		PrintWriter pw = new PrintWriter(jsonFile);
		pw.println(json);
		pw.flush();
		pw.close();
	}
	
	public static String html2text(String html) {
	    return Jsoup.parse(html).text();
	}
}
