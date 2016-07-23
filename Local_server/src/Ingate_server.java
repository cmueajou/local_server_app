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
import java.sql.SQLException;
import java.io.*;


class Ingate_server extends Thread{
	
	 private int id = -1;
	   int portNum;
	  
	   
	   public Ingate_server(int id){
	      this.id = id;
	      
	     
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
	   public void run() { 
		   
		  ServerSocket serverSocket = null;							// Server socket object
		   Socket clientSocket = null;
		   int portNum = 1005;
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
	      				reservation_code =resMsg.substring(1);
	      				int result = auth_query(reservation_code);
	      				if(result==1){
	      					out.write("1Auth\n");
	      					out.flush();
	      				}
	      				else{
	      					out.write("1Auth_deny\n");
	      					out.flush();
	      				}
	      			case '2':
	      				// parking spot 관련 메소드
	      			case '3':
	      				// endgate_관련 메소드
	      			default:
	      			}
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
	
 