import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

public class CentralClient extends Thread {
	 private int id = -1;
	   int portNum;
	  BlockingQueue queue;
	  String resMsg="";
	  BufferedWriter out;
	  String server_ip = "192.168.1.3";
	   public CentralClient(int id, BlockingQueue _queue){
	      this.id = id;
	      this.queue = _queue;
	      
	     
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
	 
	   public void run() { 
		   
		  ServerSocket serverSocket = null;							// Server socket object
		   Socket clientSocket = null;
		   int portNum = 1006;
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
	        		clientSocket = new Socket(server_ip,portNum);
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

		    	
  	    	System.out.println ("Socket : "+portNum+"Connection successful");
  	    	
  	    
		    	
	    		try{
	    	  	out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
	      		BufferedReader in = new BufferedReader(new InputStreamReader( clientSocket.getInputStream()));
	    		String result ="";
	    		System.out.println("port :"+portNum+"start");
	    		resMsg = (String)queue.take();
	    		System.out.println("port :"+portNum+" Mesg : "+ resMsg);
	    		out.write(resMsg);
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
