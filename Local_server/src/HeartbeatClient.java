import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

public class HeartbeatClient extends Thread {
	private int id = -1;
	int portNum;
	String resMsg = "";
	BufferedWriter out;
	BufferedReader in;
	String localhost = "192.168.1.136";
	String server_ip = "192.168.1.3";
	int term_of_transmit = 30;

	public HeartbeatClient(int id) {
		this.id = id;

	}

	public void run() {

		ServerSocket serverSocket = null; // Server socket object
		Socket clientSocket = null;
		int portNum = 1818;
		int msgNum = 0; // Message to display from serverMsg[]
		String inputLine; // Data from client

		String reservation_code = "";

		while (true) {
			/*****************************************************************************
			 * First we instantiate the server socket. The ServerSocket class
			 * also does the listen()on the specified port.
			 *****************************************************************************/
			try {
				clientSocket = new Socket(server_ip, portNum);
				System.out.println("\n\nWaiting for connection on port " + portNum + ".");
			} catch (IOException e) {
				System.err.println("\n\nCould not instantiate socket on port: " + portNum + " " + e);
				System.exit(1);
			}

			/*****************************************************************************
			 * If we get to this point, a client has connected. Now we need to
			 * instantiate a client socket. Once its instantiated, then we
			 * accept the connection.
			 *****************************************************************************/

			System.out.println("Socket : " + portNum + "Connection successful");

			try {
				out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				
				Thread.sleep(1000 * this.term_of_transmit);
				System.out.println("HeratBeat : "+localhost + " "+ "Heart_beat");
				out.write(localhost + " Heart_Beat");
				out.flush();

				out.close();
				in.close();
				clientSocket.close();
				//serverSocket.close();
			} catch (ConnectException ex) {
				System.out.println(ex.getMessage());
				
				try {
					in.close();
					out.close();
					clientSocket.close();
					//serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}catch(SocketException socket_exception){
				
				System.out.println("SocketException occur");
				try {
					in.close();
					out.close();
					clientSocket.close();
					//serverSocket.close();
				} catch (IOException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				
			}
			catch( Exception e){
				System.out.println(e.getMessage());
				try {
					in.close();
					out.close();
					clientSocket.close();
					//serverSocket.close();
				} catch (IOException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				
			} 
			
		}
	}
}
