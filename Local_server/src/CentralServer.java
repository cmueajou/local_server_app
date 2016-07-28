import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class CentralServer extends Thread {
	 private int id = -1;
	   int portNum;
	  BlockingQueue queue;
	  String resMsg="";
	  BufferedWriter out;
	  Database db_local;
	  String user_id;
	  String reservation_start_time;
	   public CentralServer(int id, BlockingQueue _queue){
	      this.id = id;
	      this.queue = _queue;
	      this.db_local = new Database("localhost","root","1234");
	      
	     
	   }
	   public String get_reservation_code(){
		   String code="";
		   Reserve_code r1 = new Reserve_code();
		   code = r1.nextSessionId();
		   
		   return r1.generate_Code(code);
	   }
	   public void transfer_to_central(String msg, BufferedWriter _out){
		   try {
			_out.write(msg);
			_out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   
		   
	   }
	   public int number_of_reservation(){
		   
		   String query = "SELECT * from sure_park.reservation";
		   int result=0;
		   try {
			   db_local.set_statement(db_local.get_connection().prepareStatement(query));
			   db_local.set_resultset(db_local.get_statement().executeQuery());
			   System.out.println("query execute");
			   if(db_local.get_resultset().next())
			    result =db_local.get_resultset().getRow();
			   user_id = db_local.get_resultset().getString("USER_ID");
			   reservation_start_time = db_local.get_resultset().getString("RESERVATION_START_TIME");
			   System.out.println("result : "+result);
			   return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		   
	   }
	   public void RequestReservation(String user_id, String reserve_time,BufferedWriter out){
		   System.out.println("Request Reservation DB connection complete");
		   String reservation_code = get_reservation_code();
		   System.out.println(reservation_code);

		   String query_local =  "INSERT INTO sure_park.reservation"
			   		+ "(USER_ID"+","+"RESERVATION_ID"+","+"RESERVATION_START_TIME"+","+"PARKING_START_TIME"+","+"PARKING_END_TIME"+","+"RESERVATION_TIME"+","+"CHARGED_FEE"+","+"ASSIGNED_PARKING_SPOT"+","+"RESERVE_STATE"+")"
					   +"VALUES("+
			   		"'"+user_id+"'"+","+"'"+reservation_code+"'"+","+"'"+reserve_time+"'"+","+"'"+"'"+","+"'"+"'"+","+"'"+"'"+","+"'"+0.0+"'"+","+"'"+0+"'"+","+"'"+0+"'"+")";
		   System.out.println(query_local);
		   
		   try {
			   db_local.set_statement(db_local.get_connection().prepareStatement(query_local));
				db_local.get_statement().executeUpdate();
		        System.out.println("local RequestReservation Complete ");
		        out.write("1 "+user_id+" "+reservation_code);
		        out.flush();
		     
		        
		   } 
		   catch (SQLException e) {
				System.out.println("request_reservation_error");
				e.printStackTrace();
				
			}
		   catch(IOException e){
			   System.out.println("transfer to central message error");
			   e.printStackTrace();
		   }
		   queue.add("4 reserve_request");//reserve_state 최신화 요청
		   try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		   System.out.println("transfer reserve_request to ParkingEvent.java");
		   try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		   
	   }

	   
	   
	   public void run() { 
		   
		  ServerSocket serverSocket = null;							// Server socket object
		   Socket clientSocket = null;
		   int portNum = 1000;
		   int msgNum = 0;												// Message to display from serverMsg[]
	       String inputLine;											// Data from client
		   
		   String reservation_code="";
			
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
	    			System.out.println(e.getMessage());
	        		System.err.println("Accept failed.");
	        		System.exit(1);
	        	}

				/*****************************************************************************
	    	 	* At this point we are all connected and we need to create the streams so
	    	 	* we can read and write.
			 	*****************************************************************************/
    	    	System.out.println ("Socket : "+portNum+" Connection successful");
    	    	
    	    
		        System.out.println("Central Server Buffer load");
	    		try{
	    	  	out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
	      		BufferedReader in = new BufferedReader(new InputStreamReader( clientSocket.getInputStream()));
	    		String result ="";
	    		
	    	
	    		if((result = in.readLine())!= null){
	    			System.out.println("result : "+result);
	    			String data[];
	    			data = result.split(" ");
	    			
	    			String id = data[0];
	    			
		    		String reserve_data = data[1].concat(" "+data[2]);
		    		System.out.println("ready number_of_reservation");
		    		int number_of_reservation = number_of_reservation();
		    		System.out.println("number_of_reservation : "+number_of_reservation);
	    			if((number_of_reservation>-1)&&(number_of_reservation<4)){
	    			System.out.println("resevation accept");
	    			RequestReservation(id,reserve_data,out);
	    			}
	    			else{
	    				out.write("2");
	    			    out.flush();
	    			}
	    			
	    		}
	    		String resMsg = (String)queue.take();
	    		if(resMsg.charAt(0)=='7'){
	    			out.write("7 "+user_id+" "+reservation_start_time);
	    			out.flush();
	    			
	    		}
	    		
	    		
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
