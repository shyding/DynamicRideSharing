package dynmicridesharing;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
/* Author : Shrawani Silwal, 2018*/
public class Request {	
	private static int idCounter = 101;
	int passengerId;
	Event epick, edrop;
	String time;
	private  List<Response>  respQue;

	public Request() {
	}

	public Request(Event epick, Event edrop, String time) {
		super();
		this.passengerId = createID();
		this.epick = epick;
		this.edrop = edrop;
		this.time = time;
		this.respQue=new ArrayList<Response>();
	}

	public int getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(int passengerId) {
		this.passengerId =passengerId ;
	}

	public Event getEpick() {
		return epick;
	}

	public void setEpick(Event epick) {
		this.epick = epick;
	}

	public Event getEdrop() {
		return edrop;
	}

	public void setEdrop(Event edrop) {
		this.edrop = edrop;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	public List<Response> getRespQue() {
		return respQue;
	}

	public void setRespQue(List<Response> respQue) {
		this.respQue = respQue;
	}
	
	public static synchronized int createID()
	{
	    return idCounter++;
	} 
    public void processRequest(Request req, BlockingQueue<Taxi> taxiQue) {
    	int threadCount=0;
		List<Thread> taxiProcess= Collections.synchronizedList(new ArrayList<Thread>());
		TaxiScheduling ts=null;
		for(Taxi taxi:taxiQue) {
		ts = new TaxiScheduling(req,taxi); // check the constraints
		Thread thread = new Thread(ts, String.valueOf(threadCount++));
		taxiProcess.add(thread);
		}
		
		for(int i=0;i<taxiProcess.size();i++)
        {
			
             taxiProcess.get(i).start();
        }
        for(int i=0;i<taxiProcess.size();i++)
        {
            try {
            	
                taxiProcess.get(i).join();
             
            } catch (InterruptedException ex) {
                    System.out.println(ex);  
                    System.exit(0);
              }
        
        }
        System.out.println("The threads have finished their work");
        System.out.println("Response size in Request class : "+req.getRespQue().size());
       /* for(Response re:req.getRespQue()){
        	if(re==null)
        		System.out.println(re);
        	else
        		System.out.println(re.getTaxi().getTaxiId());
        }*/
        
        synchronized (this) {
        	String responseFile = "src/dynmicridesharing/outputForRequestID/"+req.getPassengerId();
    		try {
    		File file = new File(responseFile);
    		//Create the file
    		if (file.createNewFile())
    		{
    		    System.out.println("File is created!");
    		} else {
    		    System.out.println("File already exists.");
    		    file.delete();
    		    file.createNewFile();
    		}
    		//Write Content
    		FileWriter writer = new FileWriter(file);
    		
    	
    		for(Response res:req.getRespQue()) {
    			String data = res.getReq().getPassengerId() + "," + res.getTaxi().getTaxiId() + "," + res.cost + ","
    					+ res.getTaxi().getOccupiedSeats() + "," + res.getTime();
    			writer.write(data +"\n");
    		}
    		writer.close();
    		
    		
    		}catch(Exception e) {
    			System.out.println("Exception while creating request files :" + e.getMessage());
    		}
    		
        	
    		PassengerEnd passen=new PassengerEnd(req.getRespQue());
    		passen.processResponse();
    		}
    }
	
	
}
