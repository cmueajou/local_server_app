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
import java.io.*;


class Server extends Thread{
	InputStream input;
	
	Server(InputStream _input){
		this.input = _input;
	}
	
	public void run(){
		ServerSocket serverSocket = null;							// Server socket object
		Socket clientSocket = null;
		int portNum = 551;
		int msgNum = 0;												// Message to display from serverMsg[]
   		String inputLine;											// Data from client
   		String resMsg="";
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
    		System.out.println ("Waiting for input.....");
    		
            try{
    		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    		BufferedReader in = new BufferedReader(new InputStreamReader( clientSocket.getInputStream()));
    		BufferedReader pipe_in = new BufferedReader(new InputStreamReader(input));

			/*****************************************************************************
    		 * Now we can read and write to and from the client. Our protocol is to wait
    		 * for the client to send us strings which we read until we get a "Bye." string
    	 	 * from the client.
		 	 *****************************************************************************/
    		/***
    		 * Received Message Format 
    		 * 1: Authentication //Authentication procedure on a reservation code
    		 * 2: Occupy the Parking Slot //if the server receives this message after receiving an authentication message, 
    		 * 							the message is sent from the same client. 2 slot#
    		 * 3: Release the Parking Slot */ 
    		
    		
    		
    			if((resMsg = pipe_in.readLine())!=null){
    				out.write(resMsg);
    				out.flush();
    			}
    				
    				//pipe_in.close();
    		
    			
    		final char RESERVED='0';
    		final char AUTH='1';
    		final char OCCUPY='2';
    		final char RELEASE='3';
    		
 	    	try
 	    	{
 	    		while ((inputLine = in.readLine()) != null)
    			{
      				System.out.println ("Client Message: " + inputLine);
      				
      				switch(inputLine.charAt(0)){
      				case RESERVED:
      					//resMsg = RESEVERD_FUNC();
      					break;
      				case AUTH:
      					//resMsg = RESEVERD_FUNC();
      					break;
      				case OCCUPY:
      					//resMsg = RESEVERD_FUNC();
      					break;
      				case RELEASE:
      					//resMsg = RESEVERD_FUNC();
      					break;
      				default:
      				}
	   				if (inputLine.equals("END"))
    	    		 	break;
   				} // while

			} catch (Exception e) {

				System.err.println("readLine failed::");
        		//System.exit(1);

			}


			/*****************************************************************************
			 * Print out the fact that we are sending a message to the client, then we
			 * send the message to the client.
			 *****************************************************************************/

 	    	try
 	    	{
 				System.out.println( "Sending message to client...." );
   				//out.write( serverMsg[msgNum], 0, serverMsg[msgNum].length() );
				out.write("0\n");
 				out.flush();

				/*if ( msgNum == 0 )
					msgNum = 1;
				else
					msgNum = 0;*/
			} catch (Exception e) {

				System.err.println("write failed::");
        		//System.exit(1);

			}

			/*****************************************************************************
    		 * Close up the I/O ports then close up the sockets
		 	 *****************************************************************************/

	    	out.close();
	    	in.close();
   		 	clientSocket.close();
	    	serverSocket.close();
			

			System.out.println ( "\n.........................\n" );

		}catch(Exception ex){
			System.out.println(ex.getMessage());
			
			}
		}
	}
}
	
 