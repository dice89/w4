/**
 * 
 */
package w4;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author apfelbaum24
 *
 */
public class RevisionList extends ArrayList<Revision> {

	public HashMap<String, String> aggregateComments() {
		HashMap<String, String> result = new HashMap<String, String>();
		for(Revision rev: this) {
			String key = rev.getTime_stamp().substring(0, 10);//Date
			System.out.println(key);
			String current;
			if (result.get(key) != null) {
				current = result.get(key);
			} else
				current = "";
			result.put(key, current + " " + rev.getComment().replaceAll("\\<.*?>",""));
		}
		return result;
	}

}
