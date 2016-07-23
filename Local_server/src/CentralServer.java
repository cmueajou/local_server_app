import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class CentralServer extends Thread {
	 private int id = -1;
	   int portNum;
	  
	   
	   public CentralServer(int id){
	      this.id = id;
	      
	     
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
