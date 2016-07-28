import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DataController {

	public static void main(String[] args) throws IOException {
		BlockingQueue queue = new ArrayBlockingQueue(100);
		BlockingQueue client_queue = new ArrayBlockingQueue(100);
		BlockingQueue parking_status_queue = new ArrayBlockingQueue(100);

		Ingate_server s1 = new Ingate_server(1, queue, client_queue, parking_status_queue);
		CentralServer s2 = new CentralServer(2, queue);
		CentralClient s3 = new CentralClient(4, client_queue);
		HeartbeatClient h1 = new HeartbeatClient(5);

		ParkingAttendantApp app = new ParkingAttendantApp(3, parking_status_queue);
		ParkingEvent e1 = new ParkingEvent(app, queue, s1, s2);
		
		s1.start();
		
		s2.start();
		
		 s3.start();
		app.run();
		e1.start();
		h1.start();

		// s1.start();

		// Server s1 = new Server();

		// ParkingAttendantApp localApp = new ParkingAttendantApp(1);
		// InGate inGate = new InGate(2);
		// OutGate outGate = new OutGate(3);

		// localApp.run();
		// inGate.run();
		// outGate.run();
		// s1.run();

		// localApp.changeParkinglotColor(0, 2);
		// localApp.changeParkinglotColor(3, 3);

	}
}