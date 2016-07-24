import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class CentralServer extends Thread {
	 private int id = -1;
	   int portNum;
	  
	   
	   public CentralServer(int id){
	      this.id = id;
	      
	     
	   }
	   public String get_reservation_code(){
		   String code="";
		   Reserve_code = new Reserve_code();
		   
	   }
	   public void RequestReservation(String user_id, String reserve_time){
		   Database db_local = new Database("192.168.1.138","root","1234");
		   Database db_central = new Database("192.168.1.5","root","g0t9d2e2");
		   String query = "Update"+"`"+"sure_park"+"`"+"."+"`"+"reservation"+"`"+"set"+"`"+"USER_ID"+"`"+"="+"'"+user_id+"'"+","+"`"+"RESERVAION_START_TIME"+"`"+"="+"'"+reserve_time+"'";
		   try {
			    db_local.set_statement(db_local.get_connection().prepareStatement(query));
		        db_local.set_resultset(db_local.get_statement().executeQuery());
		        System.out.println("local RequestReservation Complete ");
		        
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
		   Database db = new Database("localhost","root","1234");
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
	    		if((result=in.readLine())!=null){
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
