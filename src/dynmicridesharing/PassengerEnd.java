package dynmicridesharing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.ComparisonChain;

public class PassengerEnd {
	private static List<Response> resList = new ArrayList<Response>();
	public PassengerEnd(List<Response> tempRespQue) {
		// TODO Auto-generated constructor stub
		resList=tempRespQue;
	}


	public static List<Response> getResList() {
		return resList;
	}


	public static void setResList(List<Response> resList) {
		PassengerEnd.resList = resList;
	}


	public  synchronized  void processResponse() {
		System.out.println("Now Processing the Response:"+resList.size());
		
		 if(resList.size()==0){
			 System.out.println("No Taxi found for this request");
			 return ;
			 }
		 /* try{
		 for (Response res : resList) {
			    if(res==null){
			    	System.out.println("Res is null");
			    }
				System.out.println(res.getCost() );
			}
		 }catch(NullPointerException ex){
			 System.out.println("Inside Null exception");
			 System.out.println(ex.getMessage());
		 } */
		// sort the response based on cost,vacancy and time !!note check vacancy again
		Collections.sort(resList, new Comparator<Response>() {

			public int compare(Response r1, Response r2) {
				return ComparisonChain.start().compare(r1.getCost(), r2.getCost())
						.compare(r1.getTaxi().getOccupiedSeats(), r2.getTaxi().getOccupiedSeats())
						.compare(r1.getTime(), r2.getTime()).result();

			}
		});
		for (Response res : resList) {
			System.out.println(res.getReq().getPassengerId() + "," + res.getTaxi().getTaxiId() + "," + res.cost + ","
					+ res.getTaxi().getOccupiedSeats() + "," + res.getTime());
		}
		//Response is sorted : the passenger picks the top response in the list 
		//this doesn't check how much time does it take to pick the taxi, we can define it later
		Response selRes=resList.get(0);
		System.out.println("The taxi is going to take : " +selRes.getTime()+ " to pick you up");
		//if(selRes.getTime()+)
		selRes.getTaxi().processResponse(selRes.getReq()); //this is "YES" to the ride [CONFIRMATION]
		
	}
	// Output file for every requestID
	// Now select the top element and check the contraints and if it matches send
	// the confirmation request to taxi
    // unblock other taxis using a timer and set a time like 5 minutes (don't blokc th eother taxi)
	// which will add the request in it's permanent schedule
}
