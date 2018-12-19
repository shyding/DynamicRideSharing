package dynmicridesharing;

public class Response {
	//int requestID;
	Request req;
	double cost;
	double time;
	Taxi taxi;
	
	public Response(Request req, double cost, double time, Taxi taxi) {
		super();
		this.req = req;
		this.cost = cost;
		this.time = time;
		this.taxi = taxi;
	}
	
	public Request getReq() {
		return req;
	}

	public void setReq(Request req) {
		this.req = req;
	}

	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
	public Taxi getTaxi() {
		return taxi;
	}
	public void setTaxi(Taxi taxi) {
		this.taxi = taxi;
	}

}
