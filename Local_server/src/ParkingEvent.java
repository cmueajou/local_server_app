import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

import javax.swing.JLabel;

public class ParkingEvent extends Thread {
	ParkingAttendantApp app;
	BlockingQueue queue;
	Ingate_server ingate;
	CentralServer central_server;
	Database db;

	ParkingEvent(ParkingAttendantApp _app, BlockingQueue _queue, Ingate_server _ingate, CentralServer _central_server) {
		this.app = _app;
		this.queue = _queue;
		this.ingate = _ingate;
		this.central_server = _central_server;
		db = new Database("localhost", "root", "1234");

	}

	public void run() {
		while (true) {

			String query = "Update sure_park.parking_spot_info set " + "`" + "RESERVATION_STATE" + "`" + "=" + "'"
					+ app.parking_reserve_status + "'" + "," + "`" + "PARKING_LOT_STATE" + "`" + "=" + "'"
					+ app.parking_status + "'";
			try {

				String Command_arbitor = "";
				if (queue.isEmpty() == false) {
					Command_arbitor = (String) queue.take();

					if (Command_arbitor.charAt(0) == '1') { // 11000
						System.out.println("1 Command_arbitor : " + Command_arbitor);
						char[] rec_data = Command_arbitor.substring(2).toCharArray();// 최신
																						// parking_state
						char[] parking_status_buff = app.parking_status.toCharArray(); // 2000
						char[] reserve_status_buff = app.parking_reserve_status.toCharArray();
						for (int i = 0; i < app.parking_status.length(); i++) { // 예약한
																				// 곳으로
																				// 점우했을
																				// 때
							if (parking_status_buff[i] == '0' && rec_data[i] == '1' && reserve_status_buff[i] == '2') {// blue
																														// -->
																														// red
								parking_status_buff[i] = rec_data[i];
								reserve_status_buff[i] = rec_data[i];
								System.out.println("changeparkinglotcolor : red");
								app.parking_status = new String(parking_status_buff, 0, parking_status_buff.length);
								app.parking_reserve_status = new String(reserve_status_buff, 0,
										reserve_status_buff.length);
								app.changeParkinglotColor(i, 2);
								break;
							}

						}
					} else if (Command_arbitor.charAt(0) == '2') {
						System.out.println("2 Command_arbitor : " + Command_arbitor);
						app.broadcast = Command_arbitor.substring(1);
						app.popUpMeassage(app.broadcast);
					} else if (Command_arbitor.charAt(0) == '3') { // Release
						System.out.println("3 Command_arbitor: " + Command_arbitor);
						char[] rec_data = Command_arbitor.substring(2).toCharArray();// 최신
																						// parking
																						// status
						System.out.println("3 rec_data : " + rec_data[0] + rec_data[1] + rec_data[2] + rec_data[3]);
						char[] parking_state_buff = app.parking_status.toCharArray(); // 기존의
																						// parking
																						// status
						char[] parking_reserve_buff = app.parking_reserve_status.toCharArray();
						for (int i = 0; i < app.parking_reserve_status.length(); i++) {
							if (parking_state_buff[i] == '1' && rec_data[i] == '0') { // red
																						// -->
																						// green
								parking_state_buff[i] = rec_data[i];
								System.out.println("changeparkinglotcolor : green");
								app.changeParkinglotColor(i, 0);
								app.parking_status = new String(parking_state_buff, 0, parking_state_buff.length);
								break;
							}
						}

					} else if (Command_arbitor.charAt(0) == '4') {
						System.out.println("4 Command_arbitor =" + Command_arbitor);
						char[] rec_data = Command_arbitor.substring(2).toCharArray();
						// System.out.println("4 Command_arbitor :"+rec_data);
						char[] parking_reserve_state_buff = app.parking_reserve_status.toCharArray();
						for (int i = 0; i < app.parking_reserve_status.length(); i++) {
							if (parking_reserve_state_buff[i] == '0' && rec_data[i] == '2') {// green
																								// -->
																								// blue
								parking_reserve_state_buff[i] = rec_data[i];
								app.changeParkinglotColor(i, 1);
								app.parking_reserve_status = new String(parking_reserve_state_buff, 0,
										parking_reserve_state_buff.length);
								break;
							}
						}

					} else if (Command_arbitor.charAt(0) == '5') {
						System.out.println("5 Command_arbitor : " + Command_arbitor);
						String data[] = Command_arbitor.split(" ");
						int position = Integer.parseInt(data[1]);
						char[] parking_reserve_state_buff = app.parking_reserve_status.toCharArray();
						parking_reserve_state_buff[position] = '0';
						app.changeParkinglotColor(position, 0);
						app.parking_reserve_status = new String(parking_reserve_state_buff, 0,
								parking_reserve_state_buff.length);
						break;

					}

					else {

					}
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}
}
