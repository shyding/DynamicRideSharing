package dynmicridesharing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
/* Author : Shrawani Silwal, 2018*/
public class RideSharingApp {
	public final static double PER_MILE = 1.68;// $1.68 per mile
	public final static double SPEED = 24.8548; // 40miles/HR
	public final static double SLACK_TIME=0.25;//15 minutes in HRS
	public final static double DETOUR_DELAY=0.25;//15 minutes in HRS
	// Request Queue is thread safe 
	private static BlockingQueue<Request> reqQue = new ArrayBlockingQueue<Request>(100);
	private static BlockingQueue<Taxi> taxiQue = new ArrayBlockingQueue<Taxi>(100);//Not in thread
	private static int eventId = 301;
	

	public static void main(String[] args) throws InterruptedException {
		createTaxis(); // create all the taxis

		Thread t1 = new Thread(new Runnable() {
			public void run() {
				try {
					createRequests();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		t1.start();
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				try {
					processRequests();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		t2.start();
		t1.join();
		t1.join();

	}

	private static void createRequests() throws FileNotFoundException, IOException, InterruptedException {
		// Read the file and add the requests into the Request Queue
		String RequestFile = "src/dynmicridesharing/input/18Hour_1.txt";
		// while(true) { //loop infinitely randomly pick the number of lines of request
		// from the file ad execute
		
		try (BufferedReader br = new BufferedReader(new FileReader(RequestFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] token = line.trim().split(",");
				eventId++;// adding the same event id for both pick and drop
				if(token.length==1)
					break;
				Event epick = new Event(eventId, Double.parseDouble(token[0].trim()),
						Double.parseDouble(token[1].trim()), token[2].trim(), true);
				Event edrop = new Event(eventId, Double.parseDouble(token[3].trim()),
						Double.parseDouble(token[4].trim()), " ", false);
				Request req = new Request(epick, edrop, epick.getTime_s());
				System.out.println("Passenger Id" + req.getPassengerId());
				reqQue.put(req);

			}
		}catch (Exception e) {
		        System.out.println("INPUT DATA WAS NOT FOUND, PLEASE PLACE FILE ");
		        throw new RuntimeException(e);
		    } 

		}

	

	private static void createTaxis() {
		String RequestFile = "18DecTaxi.txt";
		try (BufferedReader br = new BufferedReader(new FileReader("src//dynmicridesharing//input//" + RequestFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] token = line.split(",");
				Taxi taxi = new Taxi(Double.parseDouble(token[0].trim()), Double.parseDouble(token[1].trim()),
						new LinkedList<Event>(), new LinkedList<Event>());
				//if(taxi.getTaxiId()==501)
				//	taxi.setOccupiedSeats(4); // test for over capacity
				taxiQue.put(taxi);
			}
		} catch (Exception e) {
			System.out.println("Exception while creating taxis :" + e.getMessage());
		}

	}

	private static void processRequests() throws InterruptedException {
		System.out.println("taxi size is:" + taxiQue.size());
		while (true) {
			Thread.sleep(5000); // waits for 100 miliseconds
			Request req = reqQue.take(); // take will wait until something is added to the queue
			System.out.println("***Que size is ****:" + reqQue.size());
			req.processRequest(req, taxiQue);
		}
	}

}
