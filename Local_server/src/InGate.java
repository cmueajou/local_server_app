import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.io.*;

public class InGate extends Thread{
   InGate result;
   private int id = -1;
   static String contrl_msg="";
   boolean value = false;
   
   public InGate get_result(){
	   return this.result;
   }
   InGate(){
	   
   }
  
   public String get_contrl_msg(){
	   return this.contrl_msg;
   }
   public InGate(int id){
      this.id = id;
      result = new InGate();
      
     
   }
   public void run() {
      final JFrame jframe= new JFrame("InGate");
      
      Container cp = jframe.getContentPane();
      
      JPanel In_p1 = new JPanel();
      
      JLabel In_Code = new JLabel("Code:");
      final JTextField In_CodeText = new JTextField();
      
      JButton In_submitButton = new JButton("Submit");
      Database db = new Database("localhost","root","1234");
 	 
      ActionListener a1 = new ActionListener() {
         public synchronized void actionPerformed(ActionEvent e) {
        	 
        	 String reservation_code = In_CodeText.getText();
        	 String parking_spot="";
        	 String query = "select * from sure_park.reservation where" +"`"+ "RESERVATION_ID"+"`"+"="+"'"+reservation_code+"'";
        	 try{
        		 String ctrl_Msg = "Auth";
        		db.set_statement(db.get_connection().prepareStatement(query));
        		db.set_resultset(db.get_statement().executeQuery());
        		db.get_resultset().next();
        		parking_spot=db.get_resultset().getString("ASSIGNED_PARKING_SPOT");
        		 JOptionPane.showMessageDialog( null, String.format("Welcome \n parking lot NO: " + parking_spot) );
        		 contrl_msg = ctrl_Msg; 
        		 value = true;
        		 synchronized(this){
        			 notify();
        		 }
        		jframe.dispose();
        		
        		
        		 
        	 }
        	 catch(SQLException ex){
        		 String ctrl_Msg = "Auth_deny";
        		 System.out.println("SQLException: " + ex.getMessage());
     			System.out.println("SQLState: " + ex.getSQLState());
     			System.out.println("VendorError: " + ex.getErrorCode());
     			JOptionPane.showMessageDialog( null, String.format("Wrong Code. please check again") );
     			contrl_msg = ctrl_Msg;
        	 }
        	
            
         }
      };
      
      In_submitButton.addActionListener(a1);
      
      
      Box b = new Box(BoxLayout.Y_AXIS);
      
      b.add(In_Code);
      b.add(In_CodeText);
      b.add(In_submitButton);
      
      In_p1.add(b);
      
      cp.add(In_p1);
      
      jframe.pack();
      jframe.setSize(300, 200);
      jframe.setVisible(true);
      synchronized(this){
    	  while(value){
    		  try {
				this.wait();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	  }
      }
      while(true);
   }
}

