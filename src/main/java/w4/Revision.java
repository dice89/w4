package w4;

public class Revision {
	private String user_name;
	private String user_id;
	private String time_stamp;
	private int size;
	private String text;
	private String comment;
	public Revision(String user_name, String user_id, String time_stamp,
			int size, String text, String comment) {
		super();
		this.user_name = user_name;
		this.user_id = user_id;
		this.time_stamp = time_stamp;
		this.size = size;
		this.text = text;
		this.comment = comment;
	}
	public String getUser_name() {
		return user_name;
	}
	public String getUser_id() {
		return user_id;
	}
	public String getTime_stamp() {
		return time_stamp;
	}
	public int getSize() {
		return size;
	}
	public String getText() {
		return text;
	}
	public String getComment() {
		return comment;
	}
	
	 
}
