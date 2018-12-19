package dynmicridesharing;

import java.io.File;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.LinkedList;

public class Taxi implements Comparator<Taxi>  {
	private static int idCounter = 501;
	private int taxiId;
	double lat,longi;
	int occupiedSeats;
	LinkedList<Event> temporaryTaxiSchedule;
	LinkedList<Event> permanentTaxiSchedule;
	public Taxi(double lat, double longi, LinkedList<Event> temporaryTaxiSchedule, LinkedList<Event> permanentTaxiSchedule) {
		super();
		this.taxiId=createID();
		this.lat = lat;
		this.longi = longi;
		this.temporaryTaxiSchedule = temporaryTaxiSchedule;
		this.permanentTaxiSchedule = permanentTaxiSchedule;
	}
	public int getTaxiId() {
		return taxiId;
	}
	public void setTaxiId(int taxiId) {
		this.taxiId = taxiId;
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
	public int getOccupiedSeats() {
		return occupiedSeats;
	}
	public void setOccupiedSeats(int occupiedSeats) {
		this.occupiedSeats = occupiedSeats;
	}
	public LinkedList<Event> getTemporaryTaxiSchedule() {
		return temporaryTaxiSchedule;
	}
	public void setTemporaryTaxiSchedule(LinkedList<Event> temporaryTaxiSchedule) {
		this.temporaryTaxiSchedule = temporaryTaxiSchedule;
	}
	public LinkedList<Event> getPermanentTaxiSchedule() {
		return permanentTaxiSchedule;
	}
	public void setPermanentTaxiSchedule(LinkedList<Event> permanentTaxiSchedule) {
		this.permanentTaxiSchedule = permanentTaxiSchedule;
	}
	public static synchronized int createID()
	{
	    return idCounter++;
	}
	
	
	// Receive the confirmation message from the taxi and add it in the permanent taxi schedule
	 public void processResponse(Request req) {
		 Taxi taxi=this;
		 //needs to add the events in the correct order in the permanent schedule
		 taxi.getPermanentTaxiSchedule().add(req.getEpick());
		 taxi.getPermanentTaxiSchedule().add(req.getEdrop());
		 taxi.setOccupiedSeats(taxi.getOccupiedSeats()+1);
		 System.out.println("**Permanent Schedule** for taxi " +taxi.getTaxiId());
		 System.out.println("It has " + taxi.getOccupiedSeats() +" occupied seats");
		 
		String responseFile = "src//dynmicridesharing//outputForTaxi//Taxi"+taxi.getTaxiId();
		System.out.println(responseFile);
 		try {
 		File file = new File(responseFile);
 		//Write Content
 		FileWriter writer = new FileWriter(file);
 		//Create the file
 		if (file.createNewFile())
 		{
 		    System.out.println("Taxi File is created!");
 		} else {
 		    System.out.println("Taxi File already exists.");
 		   // file.delete();
 		   // file.createNewFile();
 		    writer.write(" ");
 		}
 		
 		String data=" ";
		 for(Event e:taxi.getPermanentTaxiSchedule()){
			  data += e.getEventId() + "," + e.isPick() +"\n";
			  
 		}
		writer.write(data);
 		writer.close(); 		
 		
 		}catch(Exception e) {
 			System.out.println("Exception while creating taxi schedule file :" + e.getMessage());
 		}
 		
	 }
	 @Override
		public int compare(Taxi arg0, Taxi arg1) {
			if (arg0.getTaxiId()==arg1.getTaxiId()) return -1; 
			return 0;
		}
}
