


	public class DataController {

		   public static void main(String[] args) {
			   
			   Server s1 = new Server();
		      
	      ParkingAttendantApp localApp = new ParkingAttendantApp(1);
		      InGate inGate = new InGate(2);
		      OutGate outGate = new OutGate(3);
		      s1.run();
		      localApp.run();
		      inGate.run();
		      outGate.run();
		      
		      
		      localApp.changeParkinglotColor(0, 2);
		      localApp.changeParkinglotColor(3, 3);
		    
		      

		   }
}