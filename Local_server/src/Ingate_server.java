
/******************************************************************************************************************
* File: Server.java
* Course: 2013 LG Executive Education Program
* Project: Iot (2013), Autowarehouse (2014)
* Copyright: Copyright (c) 2013 Carnegie Mellon University
* Versions:
*	1.0 Apr 2013.
*
* Description:
*
* This class serves as an example for how to write a server application that a client Arudino application can
* connect to. There is nothing uniquely specific to the Arduino in this code, other than the application level
* protocol that is used between this application and the Arduino. The assumption is that the Arduino is running
* the ClientDemo application. When this application is started it listens until a client connects. Once the client
* connects, this app reads data from the client until the client sends the string "Bye." Each string
* read from the client will be writen to the terminal. Once the "Bye." string is read, the server will send
* a single message back to the client. After this the session ends and server will listen for another client to
* connect. Note, this example server application is single threaded.
*
* Compilation and Execution Instructions:
*
*	Compiled in a command window as follows: javac Server.java
*	Execute the program from a command window as follows: java Server
*
* Parameters: 		None
*
* Internal Methods: None
*
******************************************************************************************************************/

import java.net.*;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.io.*;

class Ingate_server extends Thread {
	String local_server = "192.168.1.136";
	String central_server = "192.168.1.3";
	private int id = -1;
	String parking_state = "0000";
	String parking_reserve_state = "0000";
	BlockingQueue queue;
	BlockingQueue client_queue;
	BlockingQueue parking_status_queue;
	int parking_lot_buff;
	String user_id;
	String reservation_code = "";
	String[] grace_time = new String[4];
	Database db;

	public Ingate_server(int id, BlockingQueue _queue, BlockingQueue _client_queue,
			BlockingQueue _parking_status_queue) {
		this.id = id;
		this.queue = _queue;
		this.client_queue = _client_queue;
		this.parking_status_queue = _parking_status_queue;
		db = new Database("localhost", "root", "1234");
		for (int i = 0; i < 4; i++)
			grace_time[i] = "";

	}

	public void judge_cancle_reservation(String[] data, String current_time) {
		for (int i = 0; i < 4; i++) {
			if (data[i].compareTo(" ") != 0) {
				String[] start_data = data[i].split(" ");
				String[] start_date = start_data[0].split("-");
				String[] start_time = start_data[1].split(":");
				String[] end_data = current_time.split(" ");
				String[] end_date = end_data[0].split("-");
				String[] end_time = end_data[1].split(":");
				String user_id = "";
				int charge_time = (((Integer.parseInt(end_date[2]) - Integer.parseInt(start_date[2])) * 24 * 60))
						+ (((Integer.parseInt(end_time[0])) - (Integer.parseInt(start_time[0]))) * 60)
						+ (Integer.parseInt(end_time[1]) - Integer.parseInt(start_time[1]));
				if (charge_time > 360) {
					queue.add("7 " + user_id + " " + data[i]);
					queue.add("5 " + i);
					String query = "DELETE FROM sure_park.reservation where RESERVATION_START_TIME =" + "'" + data[i]
							+ "'";
					try {
						db.set_statement(db.get_connection().prepareStatement(query));
						db.get_statement().executeUpdate();
					} catch (SQLException e) {
						System.out.println("Delete_reservation_error");
						System.out.println(query);
						e.printStackTrace();
					}
				}
			}

		}

	}

	public int auth_query(String _reservation_code) {
		String query = "select * from sure_park.reservation where" + "`" + "RESERVATION_ID" + "`" + "=" + "'"
				+ _reservation_code + "'";
		System.out.println("auth_query : " + query);
		try {
			db.set_statement(db.get_connection().prepareStatement(query));
			System.out.println("auth_query_1");
			db.set_resultset(db.get_statement().executeQuery());
			System.out.println("auth_query_2");
			if (db.get_resultset().next()) {
				System.out.println("auth_query_3");
				if (db.get_resultset().getInt("RESERVE_STATE") == 0)
					return 1;
				else
					return 0;
			} else
				return 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	public void update_reserve_state(String _reservation_code, int reserve_state, int parking_spot) {

		String user_id = get_user_id(_reservation_code);
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		String today = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
		this.grace_time[parking_spot - 1] = today;
		String query = "";
		String central_query = "";
		if (reserve_state == 1) {
			query = "Update sure_park.reservation set " + "`" + "RESERVE_STATE" + "`" + "=" + "'" + reserve_state + "'"
					+ "," + "`" + "PARKING_START_TIME" + "`" + "=" + "'" + today + "'" + " where " + "`"
					+ "RESERVATION_ID" + "`" + "=" + "'" + _reservation_code + "'";
			System.out.println("Auth query : " + query);
		} else if (reserve_state == 2) {
			query = "Update sure_park.reservation set " + "`" + "RESERVE_STATE" + "`" + "=" + "'" + reserve_state + "'"
					+ "," + "`" + "ASSIGNED_PARKING_SPOT" + "`" + "= " + "'" + parking_spot + "'" + "where " + "`"
					+ "RESERVATION_ID" + "`" + "=" + "'" + _reservation_code + "'";
			System.out.println("ocupy_query : " + query);
			client_queue.add("5" + " " + user_id + " " + today + " " + parking_spot);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("occupied query : " + query);
			System.out.println("occupied reservation time trans complete");

		} else if (reserve_state == 3) {
			query = "Update sure_park.reservation set " + "`" + "RESERVE_STATE" + "`" + "=" + "'" + reserve_state + "'"
					+ " where " + "`" + "RESERVATION_ID" + "`" + "=" + "'" + _reservation_code + "'";
			System.out.println("release query : " + query);

		} else if (reserve_state == 5) {
			query = "Update sure_park.reservation set " + "`" + "PARKING_END_TIME" + "`" + "=" + "'" + today + "'"
					+ " where " + "`" + "RESERVATION_ID" + "`" + "=" + "'" + _reservation_code + "'";
			System.out.println(query);

		} else {

		}
		try {
			db.set_statement(db.get_connection().prepareStatement(query));
			db.get_statement().executeUpdate();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}

	}

	public int update_parking_state(String _parking_state, String _trans_Msg) {

		char[] result = _parking_state.toCharArray();
		if (_trans_Msg.compareTo("1AUTH") == 0) {
			for (int i = 0; i < 3; i++) {
				if (result[i] == '0') {
					return i;
				}

			}

		} else if (_trans_Msg.compareTo("Central") == 0) {
			result = _parking_state.toCharArray();
			for (int i = 0; i < 3; i++)
				if (result[i] == '0') {
					result[i] = '2';
					_parking_state = new String(result, 0, result.length);
				}
			return 0;
		} else
			return 0;

		return -1;
	}

	public int get_charge_time(String _end_time, int parking_spot) {

		String query = "Select" + "`" + "PARKING_START_TIME" + "`" + "from" + "`" + "sure_park" + "`" + "." + "`"
				+ "reservation" + "`" + "where" + "`" + "ASSIGNED_PARKING_SPOT" + "`" + "=" + parking_spot;
		try {

			System.out.println("query :" + query);

			db.set_statement(db.get_connection().prepareStatement(query));
			db.set_resultset(db.get_statement().executeQuery());
			if (db.get_resultset().next()) {
				String starttime = db.get_resultset().getString("PARKING_START_TIME");
				System.out.println("starttime :" + starttime);
				String[] start_data = starttime.split(" ");
				System.out.println(start_data[0]);
				System.out.println(start_data[1]);
				String[] start_date = start_data[0].split("-");
				System.out.println(start_date[0] + " " + start_date[1] + " " + start_date[2]);
				String[] start_time = start_data[1].split(":");
				System.out.println(start_time[0] + " " + start_time[1] + " " + start_time[2]);
				String[] end_data = _end_time.split(" ");
				System.out.println("end_data :" + end_data[0] + " " + end_data[1]);
				String[] end_date = end_data[0].split("-");
				System.out.println(end_date[0] + " " + end_date[1] + " " + end_date[2]);
				String[] end_time = end_data[1].split(":");
				System.out.println(end_time[0] + " " + end_time[1] + " " + end_time[2]);
				int charge_time = (((Integer.parseInt(end_date[2]) - Integer.parseInt(start_date[2])) * 24 * 60))
						+ (((Integer.parseInt(end_time[0])) - (Integer.parseInt(start_time[0]))) * 60)
						+ (Integer.parseInt(end_time[1]) - Integer.parseInt(start_time[1]));
				return charge_time;
			}
			return 0;
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return 0;
		}
	}

	public float calculate_charge(String _end_time, int parking_spot) {
		System.out.println("_end_time :" + _end_time);
		System.out.println("calculate_charge_DB_Connection complete");
		String query = "Select" + "`" + "PARKING_START_TIME" + "`" + "from" + "`" + "sure_park" + "`" + "." + "`"
				+ "reservation" + "`" + "where" + "`" + "ASSIGNED_PARKING_SPOT" + "`" + "=" + parking_spot;
		try {

			System.out.println("query :" + query);

			db.set_statement(db.get_connection().prepareStatement(query));
			db.set_resultset(db.get_statement().executeQuery());
			if (db.get_resultset().next()) {
				String starttime = db.get_resultset().getString("PARKING_START_TIME");
				System.out.println("starttime :" + starttime);
				String[] start_data = starttime.split(" ");
				String[] start_date = start_data[0].split("-");
				System.out.println(start_date[0] + " " + start_date[1] + " " + start_date[2]);
				String[] start_time = start_data[1].split(":");
				System.out.println(start_time[0] + " " + start_time[1] + " " + start_time[2]);
				String[] end_data = _end_time.split(" ");
				System.out.println("end_data :" + end_data[0] + " " + end_data[1]);
				String[] end_date = end_data[0].split("-");
				System.out.println(end_date[0] + " " + end_date[1] + " " + end_date[2]);
				String[] end_time = end_data[1].split(":");
				System.out.println(end_time[0] + " " + end_time[1] + " " + end_time[2]);
				int charge_time = (((Integer.parseInt(end_date[2]) - Integer.parseInt(start_date[2])) * 24 * 60))
						+ (((Integer.parseInt(end_time[0])) - (Integer.parseInt(start_time[0]))) * 60)
						+ (Integer.parseInt(end_time[1]) - Integer.parseInt(start_time[1]));
				System.out.println("charge_time : " + charge_time);
				return (float) (charge_time * 0.125);
			}
			return 0;
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return 0;
		}

	}

	public int exit_process(int parking_slot, String end_time, float charge, BlockingQueue _queue) {
		String user_id = get_user_id(parking_slot);
		String query = "Update sure_park.reservation set" + "`" + "PARKING_END_TIME" + "`" + "=" + "'" + end_time + "'"
				+ "," + "`" + "CHARGED_FEE" + "`" + "=" + "'" + charge + "'" + " where " + "`" + "ASSIGNED_PARKING_SPOT"
				+ "`" + "=" + parking_slot;
		System.out.println(query);
		try {
			db.set_statement(db.get_connection().prepareStatement(query));
			db.get_statement().executeUpdate();
			_queue.add("6 " + user_id + " " + end_time + " " + charge);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 1;
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			return -1;
		}
	}

	public String get_user_id(String reservation_code) {
		String query = "SELECT" + " USER_ID" + " from sure_park.reservation" + " where" + "`" + "RESERVATION_ID" + "`"
				+ "=" + "'" + reservation_code + "'";
		try {
			db.set_statement(db.get_connection().prepareStatement(query));
			db.set_resultset(db.get_statement().executeQuery());
			if ((db.get_resultset().next())) {
				user_id = db.get_resultset().getString("USER_ID");
				return user_id;
			}

		} catch (SQLException e) {
			System.out.println("get user_id error");

			e.printStackTrace();

		}
		return "";
	}

	public String get_user_id(int parking_spot) {
		char ch = (char) (parking_spot + 48);
		String query = "SELECT * FROM sure_park.reservation";

		try {
			db.set_statement(db.get_connection().prepareStatement(query));
			db.set_resultset(db.get_statement().executeQuery());
			if ((db.get_resultset().next())) {
				user_id = db.get_resultset().getString("USER_ID");
				return user_id;
			}

		} catch (SQLException e) {
			System.out.println("get user_id error");

			e.printStackTrace();

		}
		return "";
	}

	public void Delete_reservation(String _user_id) {

		String query = "DELETE FROM sure_park.reservation where USER_ID =" + "'" + _user_id + "'";
		try {
			db.set_statement(db.get_connection().prepareStatement(query));
			db.get_statement().executeUpdate();
		} catch (SQLException e) {
			System.out.println("Delete_reservation_error");
			System.out.println(query);
			e.printStackTrace();
		}

	}

	public void Update_simulation(String _reservation_code) {
		String query = "Update sure_park.reservation set" + "`" + "RESERVE_STATE" + "`" + "=" + "'" + 0 + "'"
				+ " where " + "`" + "RESERVATION_ID" + "`" + "=" + "'" + _reservation_code + "'";
		System.out.println(query);
		try {
			db.set_statement(db.get_connection().prepareStatement(query));
			db.get_statement().executeUpdate();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());

		}
	}

	public void Update_parking_lot_state() {
		String query = "Update sure_park.parking_spot_info set " + "`" + "PARKING_LOT_STATE" + "`" + "=" + "'"
				+ this.parking_state + "'";
		try {
			db.set_statement(db.get_connection().prepareStatement(query));
			db.get_statement().executeUpdate();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
	}

	public void run() {
		int msgNum = 0; // Message to display from serverMsg[]
		String inputLine; // Data from client
		String resMsg = "";

		ServerSocket serverSocket = null; // Server socket object
		Socket clientSocket = null;
		int portNum = 1005;

		while (true) {
			/*****************************************************************************
			 * First we instantiate the server socket. The ServerSocket class
			 * also does the listen()on the specified port.
			 *****************************************************************************/
			try {
				serverSocket = new ServerSocket(portNum);
				System.out.println("\n\nWaiting for connection on port " + portNum + ".");
				System.out.println("Local Information : " + serverSocket.getLocalSocketAddress() + " "
						+ serverSocket.getLocalPort());
			} catch (IOException e) {

				System.err.println("\n\nCould not instantiate socket on port: " + portNum + " " + e);
				System.out.println(e.getMessage());
				System.exit(1);
			}

			/******************************
			 * 1*********************************************** If we get to
			 * this point, a client has connected. Now we need to instantiate a
			 * client socket. Once its instantiated, then we accept the
			 * connection.
			 *****************************************************************************/

			try {

				clientSocket = serverSocket.accept();
				System.out.println("clientsocket accept Complete");
			} catch (Exception e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}

			/*****************************************************************************
			 * At this point we are all connected and we need to create the
			 * streams so we can read and write.
			 *****************************************************************************/
			System.out.println("Socket : " + portNum + "Connection successful");

			try {
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				System.out.println("Initiate Buffer complete");

				Calendar cal = Calendar.getInstance();
				Date date = cal.getTime();
				String today = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));

				judge_cancle_reservation(grace_time, today);

				if ((resMsg = in.readLine()) != null) {
					System.out.println("resMsg : " + resMsg);
					switch (resMsg.charAt(0)) {
					case '1': // In front of enterance.
						System.out.println("case 1 :" + resMsg);
						reservation_code = resMsg.substring(2, 10);
						user_id = get_user_id(reservation_code);
						// System.out.println(reservation_code);
						this.parking_state = resMsg.substring(11, 15);
						// System.out.println("Parking state :"+parking_state);
						int result = auth_query(reservation_code);
						System.out.println("auth_query complete :" + result);

						if (result == 1) {
							update_reserve_state(reservation_code, 1, 0);
							System.out.println("update_reserve_state complete");
							int update_parking_msg = update_parking_state(this.parking_state, "1AUTH");

							queue.add("1" + " " + parking_state);
							Thread.sleep(200);

							queue.add("2" + " " + "entering" + " " + user_id);
							Thread.sleep(200);
							System.out.println("sending msg : " + "1Auth" + update_parking_msg);
							out.write("1Auth" + update_parking_msg + " " + user_id);
							out.flush();
						} else {
							System.out.println("wrong access");
							out.write("1Deny\n");
							out.flush();
						}
						break;
					case '2':// Occupy one parking slot
						char[] buff_arry;
						System.out.println("case2 :" + resMsg);

						int parking_spot = (int) resMsg.charAt(5) - 48;
						System.out.println("case2 : " + parking_spot);
						update_reserve_state(reservation_code, 2, parking_spot);
						out.write("2Occupied\n");
						out.flush();
						buff_arry = this.parking_state.toCharArray();
						buff_arry[parking_spot - 1] = '1';
						this.parking_state = new String(buff_arry, 0, buff_arry.length);
						buff_arry = this.parking_reserve_state.toCharArray();
						buff_arry[parking_spot - 1] = '1';
						this.parking_reserve_state = new String(buff_arry, 0, buff_arry.length);

						queue.add("2" + "occupied parking spot #" + parking_spot); // popup에
																					// 출력하기
																					// 위한
																					// 것
						Thread.sleep(200);

						queue.add("1" + " " + this.parking_state); // parking
																	// event 의
																	// parking_state
																	// 최신화
						Thread.sleep(200);

						break;

					case '3': // Release one parking slot
						System.out.println("case3 :" + resMsg);
						update_reserve_state(reservation_code, 3, 0);
						out.write("3Release\n");
						out.flush();

						buff_arry = this.parking_state.toCharArray();
						parking_spot = (int) resMsg.charAt(5) - 48;
						buff_arry[parking_spot - 1] = '0';
						this.parking_state = new String(buff_arry, 0, buff_arry.length);
						buff_arry = this.parking_reserve_state.toCharArray();
						buff_arry[parking_spot - 1] = '0';
						this.parking_reserve_state = new String(buff_arry, 0, buff_arry.length);
						queue.add("3" + " " + this.parking_reserve_state);
						Thread.sleep(200);

						queue.add("2" + " " + "Released parking spot #" + parking_spot);
						Thread.sleep(200);

						break;
					case '4': // close outgate door
						queue.add("2 close open gate");
						// Thread.sleep(200);
						out.write("4messge\n");
						out.flush();
						break;
					case '5': // depart in front of outgate
						System.out.println("case5 :" + resMsg);

						String[] msg_data = resMsg.split(" ");
						parking_spot = (int) msg_data[1].charAt(4) - 48;
						update_reserve_state(reservation_code, 5, parking_spot);// End
																				// 시간
																				// 업데이트
						// System.out.println("today:"+today);
						// System.out.println("parking_lot :
						// "+((int)resMsg.charAt(3)-48));
						float cal_charge = calculate_charge(today, parking_spot);
						int charge_time = get_charge_time(today, parking_spot);
						String release_user_id = get_user_id(parking_spot);
						out.write("5 " + release_user_id + " " + charge_time + " " + cal_charge + "\n");
						out.flush();
						System.out.println("Sending Message to Arduino : " + "5 " + release_user_id + " " + charge_time
								+ " " + cal_charge + "\n");
						queue.add("2 Open end_gate");
						exit_process(parking_spot, today, cal_charge, client_queue);// 새로운
						// 내용으로
						// 업데이트
						// Delete_reservation(release_user_id);

						break;
					case 6:
						System.out.println(resMsg);
						out.write("6complete\n");
						out.flush();
						break;
					case 7:
						System.out.println(resMsg);
						String[] res_buff = resMsg.split(" ");
						// parking_status_queue.add(res_buff[1]);
						out.write("7complete\n");
						out.flush();

					default:
						System.out.println(resMsg);
						break;
					}
				}

				Update_parking_lot_state();
				out.close();
				System.out.println("Ingate_server out close complete");
				in.close();
				System.out.println("Ingate_server in close complete");
				clientSocket.close();
				System.out.println("Ingate_server clientsocket close complete");
				serverSocket.close();
				System.out.println("Ingate_server serversocket close complete");

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

}
