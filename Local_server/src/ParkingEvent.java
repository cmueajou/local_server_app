import java.util.concurrent.BlockingQueue;

import javax.swing.JLabel;

public class ParkingEvent extends Thread {
	ParkingAttendantApp app;
	BlockingQueue queue;
	

	ParkingEvent(ParkingAttendantApp _app, BlockingQueue _queue) {
		this.app = _app;
		this.queue = _queue;
	}

	public void run() {
		while (true) {
			try {
				String Command_arbitor = (String) queue.take();
				
				if (Command_arbitor.charAt(0) == '1') { // 11000
					System.out.println("1 Command_arbitor : " + Command_arbitor);
					char[] rec_data = Command_arbitor.substring(2).toCharArray();// 1000
					char[] buff = app.parking_status.toCharArray(); // 2000
					for (int i = 0; i < app.parking_status.length(); i++) {
						if (buff[i] == '2' && rec_data[i] == '1') {
							buff[i] = rec_data[i];
							System.out.println("changeparkinglotcolor : red");
							app.changeParkinglotColor(i, 2);
						}
					}
					app.parking_status = new String(buff, 0, buff.length);
				} else if (Command_arbitor.charAt(0) == '2') {
					System.out.println("2 Command_arbitor : " + Command_arbitor);
					app.broadcast = Command_arbitor.substring(1);
					app.popUpMeassage(app.broadcast);
				} else if (Command_arbitor.charAt(0) == '3') {
					System.out.println("3 Command_arbitor: "+Command_arbitor);
					char[] rec_data = Command_arbitor.substring(2).toCharArray();
					System.out.println("3 rec_data : "+rec_data[0]+rec_data[1]+rec_data[2]+rec_data[3]);
					char[] buff = app.parking_status.toCharArray(); // ±âÁ¸ÀÇ
					System.out.println("3 buff_data : "+buff[0]+buff[1]+buff[2]+buff[3]);								
					for (int i = 0; i < app.parking_status.length(); i++) {
						if (buff[i] == '1' && rec_data[i] == '0') {
							buff[i] = rec_data[i];
							System.out.println("changeparkinglotcolor : green");
							app.changeParkinglotColor(i, 0);
						}
					}

				} else {

				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
