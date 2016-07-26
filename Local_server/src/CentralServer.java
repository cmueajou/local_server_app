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

public class CentralServer extends Thread {
	 private int id = -1;
	   int portNum;
	 
	   
	   public CentralServer(int id){
	      this.id = id;
	      
	     
	   }
	   public String get_reservation_code(){
		   String code="";
		   Reserve_code r1 = new Reserve_code();
		   code = r1.nextSessionId();
		   
		   return r1.generate_Code(code);
	   }
	   public void RequestReservation(String user_id, String reserve_time){
		   Database db_local = new Database("192.168.1.138","root","1234");
		   Database db_central = new Database("192.168.1.5","root","g0t9d2e2");
		   String reservation_code = get_reservation_code();
		   System.out.println(reservation_code);

		   String query_local = "INSERT INTO sure_park.reservation"
		   		+ "(USER_ID"+","+"RESERVATION_ID"+","+"RESERVATION_START_TIME"+","+"PARKING_START_TIME"+","+"PARKING_END_TIME"+","+"RESERVATION_TIME"+","+"CHARGED_FEE"+","+"ASSIGNED_PARKING_SPOT"+","+"RESERVE_STATE"+")"
				   +"VALUES("+
		   		"'"+user_id+"'"+","+"'"+reservation_code+"'"+","+"'"+reserve_time+"'"+","+"'"+"'"+","+"'"+"'"+","+"'"+"'"+","+"'"+0.0+"'"+","+"'"+0+"'"+","+"'"+0+"'"+")";
		   System.out.println(query_local);
		   String query_central ="Update"+"`"+"surepark"+"`"+"."+"`"+"reservation"+"`"+"set"+"`"+"RESERVATION_ID"+"`"+"="+"'"+reservation_code+"'"+"where USER_ID ="+"'"+user_id+"'";
		   try {
			   db_local.set_statement(db_local.get_connection().prepareStatement(query_local));
				db_local.get_statement().executeUpdate();
		        System.out.println("local RequestReservation Complete ");
		        db_central.set_statement(db_central.get_connection().prepareStatement(query_central));
				db_central.get_statement().executeUpdate();
		        System.out.println("central RequestReservation Complete");
		        
		   } 
		   catch (SQLException e) {
				System.out.println("get user_id error");
				e.printStackTrace();
				
			}
		  
		   
	   }
	   
	   public void run() { 
		   
		  ServerSocket serverSocket = null;							// Server socket object
		   Socket clientSocket = null;
		   int portNum = 1004;
		   int msgNum = 0;												// Message to display from serverMsg[]
	       String inputLine;											// Data from client
		   String resMsg="";
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
		    	System.out.println ("Connection successful");
		    	
	    		try{
	    	  	BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
	      		BufferedReader in = new BufferedReader(new InputStreamReader( clientSocket.getInputStream()));
	    		String result ="";
	    		
	    		System.out.println("in.readline");
	    		if((result=in.readLine())!=null){
	    			String data[];
	    			data = result.split(" ");
	    			String id = data[0];
	    			//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		    		String reserve_data = data[1].concat(" "+data[2]);
	    			//String reserve_data ="2016-04-14 08:00:34";
		    		//Timestamp reserve_time = Timestamp.valueOf(reserve_data);
	    			RequestReservation(id,reserve_data);
	    			System.out.println("data[0] : "+data[0]+"data[1] : "+data[1]+"data[2] :"+data[2]);
	    			System.out.println("result : "+result);
	    		}
	    		out.write("Hello Central? C88");
	    		System.out.println("data transfer complete");
	    		out.flush();
	        		
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
