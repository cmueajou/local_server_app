
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PAUI extends ParkingAttendantApp{
   protected int currentCarNum = 0;

   Database db;
   JLabel PAUI_p2_info_time;
   
   
   public PAUI(int id ,BlockingQueue queue, JLabel timer) {
	   
      super(id, queue);
      db= new Database("localhost","root","1234");
      
      JPanel p = new JPanel();
      p.setLayout(new BorderLayout());
      JPanel PAUI_p1 = new JPanel();
      PAUI_p1.setLocation(0, 0);
      PAUI_p1.setSize(800,50);
      PAUI_p2_info_time = timer;
      //PAUI_p1.setLayout(new FlowLayout());
    
      
      JLabel PAUI_p1_title = new JLabel("CMU Parking lot(Pittsburgh)             ");
      PAUI_p1_title.setLocation(20,0);
      JButton PAUI_p1_authenticationButton = new JButton("user Autentication");
      PAUI_p1_authenticationButton.setLocation(600,300);
      final JTextField PAUI_p1_Code = new JTextField(20);
      PAUI_p1_Code.setLocation(900,400);
      
      PAUI_p1.add(PAUI_p1_title);
      PAUI_p1.add(PAUI_p1_authenticationButton);
      PAUI_p1.add(PAUI_p1_Code);
      
      ActionListener a1 = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            
           String query = "Select * from sure_park.reservation";
           String reservation_code ="";
           String user_id="";
          try {
   			db.set_statement(db.get_connection().prepareStatement(query));
   			db.set_resultset(db.get_statement().executeQuery());
   			while(db.get_resultset().next()){
   				reservation_code = db.get_resultset().getString("RESERVATION_ID");
   				
   			}
   				
   				
   		} catch (SQLException ex) {
   			// TODO Auto-generated catch block
   			ex.printStackTrace();
   			
   		}
            if(PAUI_p1_Code.getText().equals("1")){
              int a =123;
               JOptionPane.showMessageDialog( null, String.format("Welcome \n parking lot NO: " + a) );
            }
            else
               JOptionPane.showMessageDialog( null, String.format("Wrong Code. please check again") );
            
         }
      };
      
      PAUI_p1_authenticationButton.addActionListener(a1);
      
      //p2
      
      JPanel PAUI_p2 = new JPanel();
      PAUI_p2.setLocation(0, 100);
      //PAUI_p2.setLayout(new FlowLayout());
      
      JPanel PAUI_p2_parkingLot = new JPanel();
      PAUI_p2_parkingLot.setLocation(0, 100);
      PAUI_p2_parkingLot.setSize(500,500);
      
      JPanel PAUI_p2_info = new JPanel();
      PAUI_p2_info.setLocation(500, 100);
      PAUI_p2_info.setSize(500,300);
      
      //PAUI_p2_parkingLot.setLayout(new FlowLayout());
      //PAUI_p2_info.setLayout(new GridLayout(3,1));
      
      JButton PAUI_p2_parkingLot_infoButton[] = new JButton[4];
      JLabel PAUI_p2_parkingLot_No[] = new JLabel[4];
      char[] reserve_buff = super.parking_reserve_status.toCharArray();
      char[] parking_state_buff = super.parking_status.toCharArray();
      for(int i = 0 ; i < 4 ; i++){
         PAUI_p2_parkingLot_space[i] = new JPanel();
         PAUI_p2_parkingLot_space[i].setSize(120,300);
         if(reserve_buff[i] =='0' && parking_state_buff[i]=='0')
         PAUI_p2_parkingLot_space[i].setBackground(Color.green);
         else if(reserve_buff[i] =='2'&&parking_state_buff[i]=='0')
        	 PAUI_p2_parkingLot_space[i].setBackground(Color.blue);
         else if(reserve_buff[i] =='0'&&parking_state_buff[i]=='1')
        	 PAUI_p2_parkingLot_space[i].setBackground(Color.red);
         PAUI_p2_parkingLot_infoButton[i] = new JButton("Info");
         PAUI_p2_parkingLot_No[i] = new JLabel(Integer.toString(i+1));
         
      }
      
      for(int i = 0 ; i < 4 ; i++){
         //PAUI_p2_parkingLot_space[i].setLayout(new BorderLayout());
         PAUI_p2_parkingLot_infoButton[i].setText("info "+(i+1));
         PAUI_p2_parkingLot_No[i].setText(Integer.toString(i+1));
         
      }
      
      Box b[] = new Box[4];
      
      for(int i = 0 ; i < 4 ; i++){
         b[i] = new Box(BoxLayout.Y_AXIS);
      }
      
      for(int i = 0 ; i < 4 ; i++){
         b[i].add(PAUI_p2_parkingLot_No[i]);
         b[i].add(PAUI_p2_parkingLot_space[i]);
         b[i].add(PAUI_p2_parkingLot_infoButton[i]);
      }
      
      for(int i = 0 ; i < 4 ; i ++){
         PAUI_p2_parkingLot.add(b[i]);
      }
      
      ActionListener b1 = new ActionListener() {
    	
         public void actionPerformed(ActionEvent e) {
        	
            String[] temp;
            temp = e.getActionCommand().split(" ");
            
            System.out.println(e.getActionCommand());
            
            if(temp[1].equals("1")){
            	 String query = "select * from sure_park.reservation where" +"`"+ "ASSIGNED_PARKING_SPOT"+"`"+"="+'1';
            	 String user_id="";
            	 String reservation_code="";
            	
            	 double charge;
            	 //charge 부분 따오자
                 /*1,2,3,4 각 자동차 현재 세부사항 가져와서 띄워야한다
                  * 
                  * */
             	 try{
             		System.out.println("asdajbkwqjbcjbkwjcbjsbkcbaskjc");
              		db.set_statement(db.get_connection().prepareStatement(query));
              		db.set_resultset(db.get_statement().executeQuery());
              		if(db.get_resultset().next()){
              			user_id = db.get_resultset().getString("USER_ID");
              			reservation_code = db.get_resultset().getString("RESERVATION_ID");
              			String starttime =  db.get_resultset().getString("PARKING_START_TIME");
              			Calendar cal = Calendar.getInstance();
						Date date = cal.getTime();
						String current_time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        				//System.out.println("starttime :" + starttime);
        				String[] start_data = starttime.split(" ");
        				//System.out.println(start_data[0]);
        				//System.out.println(start_data[1]);
        				String[] start_date = start_data[0].split("-");
        				//System.out.println(start_date[0] + " " + start_date[1] + " " + start_date[2]);
        				String[] start_time = start_data[1].split(":");
        				//System.out.println(start_time[0] + " " + start_time[1] + " " + start_time[2]);
        				String[] end_data = current_time.split(" ");
        				//System.out.println("end_data :" + end_data[0] + " " + end_data[1]);
        				String[] end_date = end_data[0].split("-");
        				//System.out.println(end_date[0] + " " + end_date[1] + " " + end_date[2]);
        				String[] end_time = end_data[1].split(":");
        				//System.out.println(end_time[0] + " " + end_time[1] + " " + end_time[2]);
        				int charge_time = (((Integer.parseInt(end_date[2]) - Integer.parseInt(start_date[2])) * 24 * 60))
        						+ (((Integer.parseInt(end_time[0])) - (Integer.parseInt(start_time[0]))) * 60)
        						+ (Integer.parseInt(end_time[1]) - Integer.parseInt(start_time[1]));
        				charge = charge_time *0.125;
              			
              		//	charge = ((current_time.get(Calendar.HOUR_OF_DAY)-start_time.get(Calendar.HOUR_OF_DAY))*60)+(current_time.get(Calendar.MINUTE)-start_time.get(Calendar.MINUTE))*0.125;
              	    JOptionPane.showMessageDialog( null, String.format("car Info 1"+"\n"+
                                                                         "ID : "+ user_id +"\n"+
                                                                         "Reservation code : "+ reservation_code+"\n"+
                                                                         "Start time : "+starttime+"\n"+
                                                                         "Occupy time : "+current_time+"\n"+
                                                                         "charge : "+charge));
              		}
              		
              	 }
              	 catch(SQLException ex){
              		 System.out.println("SQLException: " + ex.getMessage());
           			System.out.println("SQLState: " + ex.getSQLState());
           			System.out.println("VendorError: " + ex.getErrorCode());
              	 }    	   
             
            }
            else if(temp[1].equals("2")){
            	 String query = "select * from sure_park.reservation where" +"`"+ "ASSIGNED_PARKING_SPOT"+"`"+"="+'2';
            	 String user_id="";
            	 String reservation_code="";
            	
            	 double charge;
            	 //charge 부분 따오자
                 /*1,2,3,4 각 자동차 현재 세부사항 가져와서 띄워야한다
                  * 
                  * */
             	 try{
             		System.out.println("asdajbkwqjbcjbkwjcbjsbkcbaskjc");
              		db.set_statement(db.get_connection().prepareStatement(query));
              		db.set_resultset(db.get_statement().executeQuery());
              		if(db.get_resultset().next()){
              			user_id = db.get_resultset().getString("USER_ID");
              			reservation_code = db.get_resultset().getString("RESERVATION_ID");
              			String starttime =  db.get_resultset().getString("PARKING_START_TIME");
              			String _end_time = db.get_resultset().getString("PARKING_END_TIME");
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
        				charge = charge_time *0.125;
              			
              		//	charge = ((current_time.get(Calendar.HOUR_OF_DAY)-start_time.get(Calendar.HOUR_OF_DAY))*60)+(current_time.get(Calendar.MINUTE)-start_time.get(Calendar.MINUTE))*0.125;
              	    JOptionPane.showMessageDialog( null, String.format("car Info 1"+"\n"+
                                                                         "ID : "+ user_id +"\n"+
                                                                         "Reservation code : "+ reservation_code+"\n"+
                                                                         "Start time : "+starttime+"\n"+
                                                                         "Occupy time : "+_end_time+"\n"+
                                                                         "charge : "+charge));
              		}
              		
              	 }
              	 catch(SQLException ex){
              		 System.out.println("SQLException: " + ex.getMessage());
           			System.out.println("SQLState: " + ex.getSQLState());
           			System.out.println("VendorError: " + ex.getErrorCode());
              	 }    	   
             
            }
            else if(temp[1].equals("3")){
            	 String query = "select * from sure_park.reservation where" +"`"+ "ASSIGNED_PARKING_SPOT"+"`"+"="+'3';
            	 String user_id="";
            	 String reservation_code="";
            	
            	 double charge;
            	 //charge 부분 따오자
                 /*1,2,3,4 각 자동차 현재 세부사항 가져와서 띄워야한다
                  * 
                  * */
             	 try{
             		System.out.println("asdajbkwqjbcjbkwjcbjsbkcbaskjc");
              		db.set_statement(db.get_connection().prepareStatement(query));
              		db.set_resultset(db.get_statement().executeQuery());
              		if(db.get_resultset().next()){
              			user_id = db.get_resultset().getString("USER_ID");
              			reservation_code = db.get_resultset().getString("RESERVATION_ID");
              			String starttime =  db.get_resultset().getString("PARKING_START_TIME");
              			String _end_time = db.get_resultset().getString("PARKING_END_TIME");
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
        				charge = charge_time *0.125;
              			
              		//	charge = ((current_time.get(Calendar.HOUR_OF_DAY)-start_time.get(Calendar.HOUR_OF_DAY))*60)+(current_time.get(Calendar.MINUTE)-start_time.get(Calendar.MINUTE))*0.125;
              	    JOptionPane.showMessageDialog( null, String.format("car Info 1"+"\n"+
                                                                         "ID : "+ user_id +"\n"+
                                                                         "Reservation code : "+ reservation_code+"\n"+
                                                                         "Start time : "+starttime+"\n"+
                                                                         "Occupy time : "+_end_time+"\n"+
                                                                         "charge : "+charge));
              		}
              		
              	 }
              	 catch(SQLException ex){
              		 System.out.println("SQLException: " + ex.getMessage());
           			System.out.println("SQLState: " + ex.getSQLState());
           			System.out.println("VendorError: " + ex.getErrorCode());
              	 }    	   
             
            }
            else if(temp[1].equals("4")){
            	 String query = "select * from sure_park.reservation where" +"`"+ "ASSIGNED_PARKING_SPOT"+"`"+"="+'4';
            	 String user_id="";
            	 String reservation_code="";
            	
            	 double charge;
            	 //charge 부분 따오자
                 /*1,2,3,4 각 자동차 현재 세부사항 가져와서 띄워야한다
                  * 
                  * */
             	 try{
             		System.out.println("asdajbkwqjbcjbkwjcbjsbkcbaskjc");
              		db.set_statement(db.get_connection().prepareStatement(query));
              		db.set_resultset(db.get_statement().executeQuery());
              		if(db.get_resultset().next()){
              			user_id = db.get_resultset().getString("USER_ID");
              			reservation_code = db.get_resultset().getString("RESERVATION_ID");
              			String starttime =  db.get_resultset().getString("PARKING_START_TIME");
              			String _end_time = db.get_resultset().getString("PARKING_END_TIME");
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
        				charge = charge_time *0.125;
              			
              		//	charge = ((current_time.get(Calendar.HOUR_OF_DAY)-start_time.get(Calendar.HOUR_OF_DAY))*60)+(current_time.get(Calendar.MINUTE)-start_time.get(Calendar.MINUTE))*0.125;
              	    JOptionPane.showMessageDialog( null, String.format("car Info 1"+"\n"+
                                                                         "ID : "+ user_id +"\n"+
                                                                         "Reservation code : "+ reservation_code+"\n"+
                                                                         "Start time : "+starttime+"\n"+
                                                                         "Occupy time : "+_end_time+"\n"+
                                                                         "charge : "+charge));
              		}
              		
              	 }
              	 catch(SQLException ex){
              		 System.out.println("SQLException: " + ex.getMessage());
           			System.out.println("SQLState: " + ex.getSQLState());
           			System.out.println("VendorError: " + ex.getErrorCode());
              	 }    	   
             
         }
            }
      };
      
      for(int i = 0 ; i < 4 ; i++){
         PAUI_p2_parkingLot_infoButton[i].addActionListener(b1);
      }
      
      
      PAUI_p1_authenticationButton.addActionListener(b1);
      
      
     
      
     // JLabel PAUI_p2_info_gateState = new JLabel("GateState");
      
     // PAUI_p2_info.add(PAUI_p2_info_time);
     // PAUI_p2_info.add(PAUI_p2_info_carNum);
      //PAUI_p2_info.add(PAUI_p2_info_gateState);
      
      for(int i= 0 ; i < 2 ; i++){
         PAUI_p2_Info_gatestate_gate[i] = new JPanel();
         PAUI_p2_Info_gatestate_gate[i].setSize(100,100);
         PAUI_p2_Info_gatestate_gate[i].setBackground(Color.green);
      }
      JLabel PAUI_p2_Info_gatestate_gate_Entry = new JLabel("Entry");
      JLabel PAUI_p2_Info_gatestate_gate_Exit = new JLabel("Exit  ");
      
    //  PAUI_p2_Info_gatestate_gate[0].add(PAUI_p2_Info_gatestate_gate_Entry);
     // PAUI_p2_Info_gatestate_gate[1].add(PAUI_p2_Info_gatestate_gate_Exit);
      
     // PAUI_p2_info.add(PAUI_p2_Info_gatestate_gate[0]);
      //PAUI_p2_info.add(PAUI_p2_Info_gatestate_gate[1]);
      
      PAUI_p2.add(PAUI_p2_parkingLot);
      PAUI_p2.add(PAUI_p2_info);
      
      
      JPanel PAUI_p3 = new JPanel();
      //PAUI_p3.setLayout(new FlowLayout());
      
     
      PAUI_p3.add(PAUI_p3_popup);
      
      p.add(PAUI_p1,"North");
      p.add(PAUI_p2,"West");
      p.add(PAUI_p3,"South");
      
      jframe.add(p);
      
      //PAUI_p1.setLocation(0, 0);
      //PAUI_p1.setSize(800,50);
      //PAUI_p2.setLocation(0, 200);
      //PAUI_p2.setSize(800,500);
      //PAUI_p3.setLocation(0, 600);
      //PAUI_p3.setSize(600,40);
      }
   



   
}
