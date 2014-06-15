package w4;

import java.util.ArrayList;
import java.util.Date;

public class SummaryObjectGroup implements Comparable<SummaryObjectGroup>{
	
	private Date timeStamp;
	
	private ArrayList<RevisionSummaryObject> data;

	public SummaryObjectGroup(Date timeStamp) {
		super();
		this.timeStamp = timeStamp;
		this.data = new ArrayList<RevisionSummaryObject>();
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public ArrayList<RevisionSummaryObject> getSummary() {
		return data;
	}
	
	public void addSummary(RevisionSummaryObject obj){
		this.data.add(obj);
	}

	public int compareTo(SummaryObjectGroup o) {
	    return getTimeStamp().compareTo(o.getTimeStamp());
	}
	
}
