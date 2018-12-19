package dynmicridesharing;

import java.util.Comparator;

public class Event implements Comparator<Event>{
	int eventId;
	double lat,longi;
	String time_s; //request pick up time
	private static final int SLACKTIME=10;//declare it as constant later
	boolean isPick; //decides if it is a pickup or dropoff event
	public Event(int eventId, double lat, double longi, String time_s,boolean isPick) {
		super();
		this.eventId = eventId;
		this.lat = lat;
		this.longi = longi;
		this.time_s = time_s;
		this.isPick = isPick;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLongi() {
		return longi;
	}
	public void setLongi(double longi) {
		this.longi = longi;
	}
	public String getTime_s() {
		return time_s;
	}
	public void setTime_s(String time_s) {
		//
		this.time_s = time_s;
	}
	public int getSlackTime() {
		return SLACKTIME;
	}

	public boolean isPick() {
		return isPick;
	}
	public void setPick(boolean isPick) {
		this.isPick = isPick;
	}
	@Override
	public int compare(Event arg0, Event arg1) {
		if (arg0.getLat()!=arg1.getLat() && arg0.getLongi()!=arg1.getLongi()) return -1; 
		return 0;
	}
	
}
