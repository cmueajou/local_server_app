import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DataController {

		   public static void main(String[] args) throws IOException {
			   BlockingQueue queue = new ArrayBlockingQueue(50);
			   
			   
			   Ingate_server s1 = new Ingate_server(1, queue);
			   CentralServer s2 = new CentralServer(2);
			   ParkingAttendantApp app = new ParkingAttendantApp(3,queue);
			   s1.start();
			   s2.start();
			   app.run();
			  // s1.start();
			  
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