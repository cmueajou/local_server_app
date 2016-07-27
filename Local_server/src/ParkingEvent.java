import java.util.concurrent.BlockingQueue;

import javax.swing.JLabel;

public class ParkingEvent extends Thread {
	ParkingAttendantApp app;
	BlockingQueue queue;
	Ingate_server ingate;
	CentralServer central;
	

	ParkingEvent(ParkingAttendantApp _app, BlockingQueue _queue, Ingate_server _ingate, CentralServer _central) {
		this.app = _app;
		this.queue = _queue;
		this.ingate = _ingate;
		this.central = _central;
	}

	public void run() {
		while (true) {
			try {
				String Command_arbitor = (String) queue.take();
				
				if (Command_arbitor.charAt(0) == '1') { // 11000
					System.out.println("1 Command_arbitor : " + Command_arbitor);
					char[] rec_data = Command_arbitor.substring(2).toCharArray();// 1000
					char[] buff = app.parking_reserve_status.toCharArray(); // 2000
					for (int i = 0; i < app.parking_reserve_status.length(); i++) { // 예약한 곳으로 점우했을 때
						if (buff[i] == '2' && rec_data[i] == '1') {
							buff[i] = rec_data[i];
							System.out.println("changeparkinglotcolor : red");
							app.changeParkinglotColor(i, 2);
						}
						else if(buff[i] == '0'&& rec_data[i]=='1'){ // 예약한 곳으로 점유하지 않았을 때
							for( int j =0; j<app.parking_reserve_status.length();j++){
								if(buff[j]=='2') // 예약 된 한 곳 예약 해제
									buff[j]='0';
								break;
								
							}
							buff[i] = '1'; // 점유한 곳 을 예약 불가 상태로 업데이트
							app.changeParkinglotColor(i, 2); // parking lot red로 변화
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
					char[] buff = app.parking_status.toCharArray(); // 기존의
					System.out.println("3 buff_data : "+buff[0]+buff[1]+buff[2]+buff[3]);								
					for (int i = 0; i < app.parking_status.length(); i++) {
						if (buff[i] == '1' && rec_data[i] == '0') {
							buff[i] = rec_data[i];
							System.out.println("changeparkinglotcolor : green");
							app.changeParkinglotColor(i, 0);
						}
					}

				} else if(Command_arbitor.charAt(0)=='4') {
					     System.out.println("4 Command_arbitor ="+Command_arbitor);
				         char[] rec_data = Command_arbitor.substring(2).toCharArray();
				         //System.out.println("4 Command_arbitor :"+rec_data);
				         char[] buff = app.parking_reserve_status.toCharArray();
				         for(int i=0; i< app.parking_reserve_status.length();i++){
				        	 if(buff[i] == '0'){
				        		 buff[i] = '2';
				        		 app.changeParkinglotColor(i, 1);
				        		 break;
				        	 }
				         }

				}
				else if(Command_arbitor.charAt(0)=='5'){//occupy starttime transfer
					central.resMsg = "2 "+Command_arbitor.substring(1);
					central.transfer_to_central(central.resMsg, central.out);
					
				}
				else if(Command_arbitor.charAt(0)=='6'){//Exit endtime transfer
					central.resMsg = "3 "+Command_arbitor.substring(1);
					central.transfer_to_central(central.resMsg,central.out);
					
				}
				
				else{
					
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
