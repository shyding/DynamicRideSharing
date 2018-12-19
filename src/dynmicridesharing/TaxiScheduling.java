package dynmicridesharing;

import static dynmicridesharing.RideSharingApp.DETOUR_DELAY;
import static dynmicridesharing.RideSharingApp.PER_MILE;
import static dynmicridesharing.RideSharingApp.SPEED;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class TaxiScheduling implements Runnable {
	List<String> list = Collections.synchronizedList(new ArrayList<String>());
	private Request req;
	private Taxi taxi;

	public TaxiScheduling(Request req, Taxi taxi) {
		super();
		this.req = req;
		this.taxi = taxi;
	}
	public void checkDistConstraints(Taxi taxi, Request req) {
		double dist = 0.0, time = 0.0, cost = 0.0;
		Response res = null;
		if (taxi.getOccupiedSeats() == 0) { // Taxi is currently empty
			Event e2 = req.getEpick();
			dist = distance(taxi.getLat(), e2.getLat(), taxi.getLongi(), e2.getLongi());
			System.out.println("Dist for :" + taxi.getTaxiId() + " is = " + dist);
			if (dist < 0.124274) {// if the taxi is within the radius (200metres =0.124274 miles)
				System.out.println("Taxi is within radius");
				// calculate time to reach there by the taxi
				time = timeCal(dist);
				cost = costCalc(taxi,req,0); //cost calculation for the trip
				System.out.println("time in minutes:" + time + " for event" + e2.getEventId());
				res = new Response(req, cost, time, taxi);
				 req.getRespQue().add(res);
			} 
			/*else if (dist > 200 && dist <= 400) {
				System.out.println("Taxi is 1 hop away ");
				time = timeCal(dist);
				System.out.println("time in minutes:" + time + " for event" + e2.getEventId());
				cost = costCalc(taxi,req,0);
				res = new Response(req, cost, time, taxi);
				 req.getRespQue().add(res);
			} else if (dist > 400 && dist < 1000) {
				System.out.println("Taxi is n hop away ");
				// keep searching for +200 meters till 1km to find a match
				time = timeCal(dist);
				System.out.println("time in minutes:" + time + " for event" + e2.getEventId());
				cost = costCalc(taxi,req,0);
				res = new Response(req, cost, time, taxi);
				System.out.println("Res :"+ res);
				req.getRespQue().add(res);
			}*/ else {
				System.out.println("Cannot match  this taxi as it is outside the Search Zone " + taxi.getTaxiId());
			}
		} else if (taxi.getOccupiedSeats() > 0 && taxi.getOccupiedSeats() < 4) { // Taxi
																					// already
																					// has
																					// other
																					// passengers
																					// in
																					// motion
			//
			System.out.println("It should come here the second time");
			taxi.setTemporaryTaxiSchedule(taxi.getPermanentTaxiSchedule());
			System.out.println(" Size of permanent schedule"+ taxi.getPermanentTaxiSchedule().size());
			int pickIndex = schedulePick(taxi.getTemporaryTaxiSchedule(), taxi, req);
			if (pickIndex != -1) {
				// Schedule the taxi drop
				int dropIndex = scheduleDrop(taxi.getTemporaryTaxiSchedule(), taxi, req, pickIndex);
				if (dropIndex == -1) {
					// remove the corresponding pick event from the temp queue
					taxi.getTemporaryTaxiSchedule().remove(pickIndex);
					// add both events at the end of the queue
					int size=taxi.getTemporaryTaxiSchedule().size();
					taxi.getTemporaryTaxiSchedule().add(req.getEpick());
					taxi.getTemporaryTaxiSchedule().add(req.getEpick());

				} else {
					// set the response	
					cost =costCalc(taxi, req, pickIndex);
					res = new Response(req, cost, time, taxi);
					req.getRespQue().add(res);
				}
			}

		} else {
			//Cannot match this taxi as it is already full
			System.out.println("Cannot match this taxi as it is already full " + taxi.getTaxiId());
			
		}
	}

	private double nextHopDistance(Taxi taxi, BlockingQueue<Taxi> taxiQue, double dist) {
		if (taxiQue.size() == 0)
			return dist;
		if (dist > 1000)
			return dist;
		Taxi prev = taxi;
		Taxi nextTaxi = taxiQue.poll();
		return dist += distance(prev.getLat(), nextTaxi.getLat(), prev.getLongi(), nextTaxi.getLongi());
	}

	public static double distance(double lat1, double lat2, double lon1, double lon2) {	
		// Radius of earth in kilometers. Use 3956 for miles
		 double r =  3956;
		 Double latDistance = Math.toRadians(lat2-lat1);
		 Double lonDistance = Math.toRadians(lon2-lon1);
		 Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + 
		 Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * 
		 Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		 Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return (c * r);
	}

	public  double costCalc(Taxi taxi,Request req, int index) {
		//Base Fare + (Cost per minute * time in ride) + 
		//(Cost per mile * ride distance) + Booking Fee + Other Fees = Your Fare
		double cost = 0;
		//double initialCost=0;
		double dist=0;
		dist=distance(req.getEpick().getLat(),req.getEdrop().getLat(),req.getEpick().getLongi(),req.getEdrop().getLongi());		
		System.out.println("the distance between pick and drop is : "+ dist );
		if (taxi.getOccupiedSeats() == 0){
		cost = dist* PER_MILE;  // the cost from source to destination
		return cost;
		}
		else{ // cost calculation for more than one passenger
			int passenCount=taxi.getOccupiedSeats()+1;
			cost= dist* PER_MILE/passenCount;
			//initialCost=dist* PER_MILE;			
		}
		return cost;
	}

	static double timeCal(double dist) {
		return SPEED * dist;
	}

	public int schedulePick(LinkedList<Event> linkedList, Taxi taxi2, Request req2) {
		System.out.println("Inside Pick Schedule");
		int index = -1;
		LinkedList<Event> tempQ = taxi.getTemporaryTaxiSchedule();

		for (int i = 0; i < taxi.getTemporaryTaxiSchedule().size(); i++) {
			Event eve = tempQ.get(i);
			if (!eve.isPick) // just go through the pick up events here, skip
								// others
				continue;
			index = i;
			// distance between taxi and already existing event in the queue
			double dist1 = distance(taxi.getLat(), eve.getLat(), taxi.getLongi(), eve.getLongi());
			double time1 = timeCal(dist1);
			// distance between taxi and new request pick up location
			Event e2 = req.getEpick();
			double dist2 = distance(taxi.getLat(), e2.getLat(), taxi.getLongi(), e2.getLongi());
			double time2 = timeCal(dist2);
			// distance between new request and already existing event in the
			// queue
			double dist3 = distance(e2.getLat(), eve.getLat(), e2.getLongi(), eve.getLongi());
			double time3 = timeCal(dist3);
			if (time2 < time1) { // new request is nearer to the original pick
									// up
				System.out.println("new pick up is nearer to the original pick up");
				if ((time2 + time3) < (time1 + DETOUR_DELAY)) {
					tempQ.add(index, e2);
					taxi.setTemporaryTaxiSchedule(tempQ);
					return index;

				}

			}

		}
		return index;
	}

	public int scheduleDrop(LinkedList<Event> linkedList, Taxi taxi2, Request req2, int pickIndex) {
		System.out.println("Inside Drop Schedule");
		int index = -1;
		LinkedList<Event> tempQ = taxi.getTemporaryTaxiSchedule();
		for (int i = pickIndex + 1; i < taxi.getTemporaryTaxiSchedule().size(); i++) {
			Event eve = tempQ.get(i);
			if (eve.isPick) // just go through the drop off events here, skip
							// others
				continue;
			index = i;
			// distance between taxi and already existing event in the queue
			double dist1 = distance(taxi.getLat(), eve.getLat(), taxi.getLongi(), eve.getLongi());
			double time1 = timeCal(dist1);
			// distance between taxi and new request pick up location
			Event e2 = req.getEpick();
			double dist2 = distance(taxi.getLat(), e2.getLat(), taxi.getLongi(), e2.getLongi());
			double time2 = timeCal(dist2);
			// distance between new request and already existing event in the
			// queue
			double dist3 = distance(e2.getLat(), eve.getLat(), e2.getLongi(), eve.getLongi());
			double time3 = timeCal(dist3);
			if (time2 < time1) { // new request is nearer to the original pick
									// up
				System.out.println("new drop  is nearer to the original pick up");
				if ((time2 + time3) < (time1 + DETOUR_DELAY)) {
					tempQ.add(index, e2);
					taxi.setTemporaryTaxiSchedule(tempQ);
					return index;

				}

			}

		}
		return index;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		checkDistConstraints(taxi, req);
	}
}
