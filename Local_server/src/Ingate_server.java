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


class Ingate_server extends Thread{
	String local_server = "192.168.1.138";
	String central_server="192.168.1.5";
	 private int id = -1;
	 String parking_state="0000";
	 BlockingQueue queue;
	 int parking_lot_buff;
	 String user_id;
	   
	  
	   
	   public Ingate_server(int id, BlockingQueue _queue){
	      this.id = id; 
	      this.queue = _queue;
	   }
	   
	   public int auth_query(String _reservation_code){
		   Database db = new Database(local_server,"root","1234");
		   String query = "select * from sure_park.reservation where" +"`"+ "RESERVATION_ID"+"`"+"="+"'"+ _reservation_code+"'";
		   try {
			    db.set_statement(db.get_connection().prepareStatement(query));
		        db.set_resultset(db.get_statement().executeQuery());
		        if(db.get_resultset().next()){
		        	return 1;
		        }
		        else
		        	return 0;
		   } 
		   catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
	   }
	   public int update_parking_state(String _parking_state, String _trans_Msg){

		   
		   char []result = _parking_state.toCharArray();
		   if(_trans_Msg.compareTo("1AUTH")==0){
		   for(int i=0;i<3;i++){
			   if(result[i]=='0'){
				   result[i]='1';
				   this.parking_lot_buff =i;
				   return i;
			   }
			   
		   }
		  
		   } 
		   
		   return -1;
	   }
	   public float calculate_charge(String _end_time, int parking_spot){
		   System.out.println("_end_time :"+_end_time);
		   Database db = new Database(local_server,"root","1234");
		   System.out.println("calculate_charge_DB_Connection complete");
		   String query = "Select"+"`"+"PARKING_START_TIME"+"`"+"from"+"`"+"sure_park"+"`"+"."+"`"+"reservation"+"`"+"where"+ "`"+"ASSIGNED_PARKING_SPOT"+"`"+"="+parking_spot;
		   try{
			   
			   System.out.println("query :" + query);
			   
		   		db.set_statement(db.get_connection().prepareStatement(query));
		        db.set_resultset(db.get_statement().executeQuery());
		        if(db.get_resultset().next()){
		         Timestamp d_start = db.get_resultset().getTimestamp("PARKING_START_TIME");
		        
		        System.out.println("DB resultset setting complete");
		        System.out.println("d_start setting");
		        String starttime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d_start));
		        System.out.println("starttime :"+starttime);
		        String[] start_data = starttime.split(" ");
		        System.out.println(start_data[0]);
		        System.out.println(start_data[1]);
		        String[] start_date = start_data[0].split("-");
		        System.out.println(start_date[0]+" "+start_date[1]+" "+start_date[2]);
		        String[] start_time = start_data[1].split(":");
		        System.out.println(start_time[0]+" "+start_time[1]+" "+start_time[2]);
		        String[] end_data = _end_time.split(" ");
		        System.out.println("end_data :"+end_data[0]+" "+end_data[1]);
		        String[] end_date = end_data[0].split("-");
		        System.out.println(end_date[0]+" "+end_date[1]+" "+end_date[2]);
		        String[] end_time = end_data[1].split(":");
		        System.out.println(end_time[0]+" "+end_time[1]+" "+end_time[2]);
		        int charge_time = (((Integer.parseInt(end_date[2])-Integer.parseInt(start_date[2]))*24*60))+(((Integer.parseInt(end_time[0]))-(Integer.parseInt(start_time[0])))*60)+(Integer.parseInt(end_time[1])-Integer.parseInt(start_time[1]));
		        System.out.println("charge_time : "+charge_time);
		        return (float) (charge_time*0.125);
		        }
		        return 0;
		   	    }catch(Exception ex){
		   		 System.out.println(ex.getMessage());
		   		 return 0;
		   	  }
		   		
	   }
	   public int exit_process(int parking_slot, String time,float charge ){
	   		  Database db = new Database(local_server,"root","1234");
	   		  String query = "Update sure_park.reservation set"+"`"+"PARKING_END_TIME"+"`"+"="+"'"+time+"'"+","+"`"+"CHARGED_FEE"+"`"+"="+"'"+charge+"'"+","+"`"+"RESERVE_STATE"+"`"+"="+"'"+"2"+"'"+" where "+"`"+"ASSIGNED_PARKING_SPOT"+"`"+"=1";
	   		  System.out.println(query);
	   		try{
	   		db.set_statement(db.get_connection().prepareStatement(query));
	      db.get_statement().executeUpdate();
	        return 1;
	   	  }catch(SQLException ex){
	   		 System.out.println(ex.getMessage());
	   		 return -1;
	   		  
	   		  
	   	  }
	   	  }

	   public String get_user_id(String reservation_code){
		   Database db = new Database(local_server,"root","1234");
		   String query = "SELECT"+"USER_ID"+" from sure_park.reservation"+"where"+"`"+"reservation"+"`"+"." +"`"+ "RESERVATION_ID"+"`"+"="+"'"+ reservation_code+"'";
		   try {
			    db.set_statement(db.get_connection().prepareStatement(query));
		        db.set_resultset(db.get_statement().executeQuery());
		        if((db.get_resultset().next())){
		        	user_id = db.get_resultset().getString("USER_ID");
		        	return user_id;
		        }
		       
		        
		   } 
		   catch (SQLException e) {
				System.out.println("get user_id error");
				e.printStackTrace();
				
			}
		   return "";
	   }
	   public String get_user_id(int parking_spot){
		   char ch = (char)(parking_spot+48);
		   Database db = new Database(local_server,"root","1234");
		   String query = "SELECT * FROM sure_park.reservation";
		   

		   try {
			    db.set_statement(db.get_connection().prepareStatement(query));
		        db.set_resultset(db.get_statement().executeQuery());
		        if((db.get_resultset().next())){
		        	user_id = db.get_resultset().getString("USER_ID");
		        	return user_id;
		        }
		       
		        
		   } 
		   catch (SQLException e) {
				System.out.println("get user_id error");
				e.printStackTrace();
				
			}
		   return "";
	   }
	  public void ReportCentral(int parking_spot){
		  Database local_db = new Database(local_server,"root","1234");
		  Database central_db = new Database(central_server,"root","g0t9d2e2");
		  
		  String local_query = "SELECT * from sure_park.reservation where ="+parking_spot;
		  
		  
	  }
	   public void run() { 
		   int msgNum = 0;												// Message to display from serverMsg[]
	       String inputLine;											// Data from client
		   String resMsg="";
		   ServerSocket serverSocket = null;							// Server socket object
		   Socket clientSocket = null;
		   String reservation_code="";
		   int portNum=1005;
		   
		   
			
			while(true){
				/*****************************************************************************
	    	 	* First we instantiate the server socket. The ServerSocket class also does
	    	 	* the listen()on the specified port.
			 	*****************************************************************************/
	    		try
	    		{
	        		serverSocket = new ServerSocket(portNum);
	        		System.out.println ( "\n\nWaiting for connection on port " + portNum + "." );
	        	}
	    		catch (IOException e)
	        	{
	        		System.err.println( "\n\nCould not instantiate socket on port: " + portNum + " " + e);
	        		System.exit(1);
	        	}

				/*****************************************************************************
	    	 	* If we get to this point, a client has connected. Now we need to
	    	 	* instantiate a client socket. Once its instantiated, then we accept the
	    	 	* connection.
			 	*****************************************************************************/

		    	try
	    		{
	        		clientSocket = serverSocket.accept();
	        	}
	    		catch (Exception e)
	        	{
	        		System.err.println("Accept failed.");
	        		System.exit(1);
	        	}

				/*****************************************************************************
	    	 	* At this point we are all connected and we need to create the streams so
	    	 	* we can read and write.
			 	*****************************************************************************/
		    	System.out.println ("Connection successful");
		    	
	    		try{
	    		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
	      		BufferedReader in = new BufferedReader(new InputStreamReader( clientSocket.getInputStream()));
	    		
	      		if((resMsg = in.readLine())!=null){
	      			switch(resMsg.charAt(0)){
	      			case '1':
	      				
	      				System.out.println(resMsg);
	      				reservation_code =resMsg.substring(2,10);
	      				//System.out.println(reservation_code);
	      				this.parking_state = resMsg.substring(11,15);
	      				//System.out.println("Parking state :"+parking_state);
	      				int result = auth_query(reservation_code);
	      	
	      				if(result==1){
	      					queue.add("2"+"entering"+get_user_id(reservation_code));//User_id Entrance msg
	      					int update_parking_msg = update_parking_state(this.parking_state,"1AUTH");
	      					System.out.println("sending msg : "+"1Auth"+update_parking_msg);
	      					out.write("1Auth"+update_parking_msg);
	      					out.flush();
	      				}
	      				else{
	      					out.write("1Deny\n");
	      					out.flush();
	      				}
	      				break;	      				
	      			case '2':
	      				char[] buff_arry;
	      				System.out.println("case2 :"+resMsg);
	      				out.write("2Occupied\n");
	      				out.flush();
	      				buff_arry = this.parking_state.toCharArray();
	      				buff_arry[((int)resMsg.charAt(5)-48)-1] = '1';
	      				queue.add("2"+"occupied parking spot #"+(resMsg.charAt(5)-48));
	      				this.parking_state = new String(buff_arry,0,buff_arry.length);
	      				queue.add(this.parking_state);
	      				
	      				break;
	 
	      			case '3':
	      				System.out.println("case3 :"+resMsg);
	      				out.write("3Release\n");
	      				out.flush();
	      				buff_arry = this.parking_state.toCharArray();
	      				buff_arry[((int)resMsg.charAt(5)-48)-1] = '0';
	      				queue.add("2+Released parking spot #"+(resMsg.charAt(5)-48));
	      				this.parking_state = new String(buff_arry,0,buff_arry.length);
	      				queue.add("1"+this.parking_state);
	      				break;
	      			case '5':
	      				resMsg = resMsg.substring(3);
	      				System.out.println("case4 :"+resMsg);
	      				out.write("5Exit\n");
	      				out.flush();
	      				String[] data = resMsg.split(" ");
	      				Calendar cal =  Calendar.getInstance();
	      				Date date = cal.getTime();
	      				String today = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
	      				System.out.println("today:"+today);
	      				System.out.println("parking_lot : "+((int)resMsg.charAt(3)-48));
	      				float cal_charge = calculate_charge(today,(int)resMsg.charAt(3)-48);
	      				System.out.println("cal_charge ="+ cal_charge);
	      				exit_process((int)resMsg.charAt(3)-48,today,cal_charge);
	      				System.out.println("exit_process complete");
	      				String _user_id = get_user_id((int)resMsg.charAt(3)-48);
	      				System.out.println("_user_id :"+_user_id);
	      				System.out.println("Sending Message to Arduino : "+"5 "+_user_id+" "+cal_charge/0.125+" "+cal_charge+"\n");
	      				out.write("5 "+_user_id+" "+cal_charge/0.125+" "+cal_charge+"\n");
	      				out.flush();
	      				//drop data;
	      				break;
	      			default:
	      				System.out.println(resMsg);
      					break;
	      			}
	      		}
	      		    queue.add(this.parking_state);
	        		out.close();
	        		in.close();
	        		clientSocket.close();
	        		serverSocket.close();
	    		} catch(Exception e){
	    			System.out.println(e.getMessage());
	    		}
	           }
	   }
}
	
 