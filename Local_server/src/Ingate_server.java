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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.sql.SQLException;
import java.io.*;


class Ingate_server extends Thread{
	
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
		   Database db = new Database("localhost","root","1234");
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
	   	  
	   public void get_reservation_state(String reservation_code){
		   Database db = new Database("localhost","root","1234");
		   String query = "Update"+"RESERVE_STATE"+" from sure_park.reservation set"+"`"+"RESERVE_CODE"+"'"+"where" +"`"+ "RESERVATION_ID"+"`"+"="+"'"+ reservation_code+"'";
		   try {
			    db.set_statement(db.get_connection().prepareStatement(query));
		        db.set_resultset(db.get_statement().executeQuery());
		        
		   } 
		   catch (SQLException e) {
				System.out.println("update reserve_state error");
				e.printStackTrace();
				
			}
	   }
	   public String get_user_id(String reservation_code){
		   Database db = new Database("localhost","root","1234");
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
	
 