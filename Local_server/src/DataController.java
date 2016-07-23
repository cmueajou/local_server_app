import java.io.*;

public class DataController {

		   public static void main(String[] args) throws IOException {
			   PipedInputStream ingate_input = new PipedInputStream();
			   PipedOutputStream ingate_output = new PipedOutputStream(ingate_input);
			   
			   
			   Server s1 = new Server();
			   s1.start();
			  
			 //  Server s1 = new Server();
		      
	      //ParkingAttendantApp localApp = new ParkingAttendantApp(1);
		    //  InGate inGate = new InGate(2);
		     // OutGate outGate = new OutGate(3);
		     
		      //localApp.run();
		      //inGate.run();
		      //outGate.run();
		    //s1.run();
		      
		      //localApp.changeParkinglotColor(0, 2);
		      //localApp.changeParkinglotColor(3, 3);
		    
		      

		   }
}