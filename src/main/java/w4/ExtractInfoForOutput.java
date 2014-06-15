package w4;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

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
	
	
	public ArrayList<SummaryObjectGroup> getInfoForMap(RevisionList revisions) throws ParseException{
		
		
		HashMap<String,HashMap<String,ArrayList<Revision>>> aggregates = revisions.aggregateCommentsOverTimeAndOrigin();
	
		ArrayList<RevisionSummaryObject> summaryObject = new ArrayList<RevisionSummaryObject>();
		
		ArrayList<SummaryObjectGroup> summaryObjectList = new ArrayList<SummaryObjectGroup>();
		HashMap<Date,ArrayList<RevisionSummaryObject>> datemap  = new HashMap<Date, ArrayList<RevisionSummaryObject>>();
		for (String date :aggregates.keySet()) {
			
			
			HashMap<String,ArrayList<Revision>> map= aggregates.get(date);
	
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
		System.out.println(gson.toJson(toserialize));
	}
}
