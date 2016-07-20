import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class InGate extends Thread{
   private int id = -1;
  
   
   public InGate(int id){
      this.id = id;
     
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
         public void actionPerformed(ActionEvent e) {
        	 String reservation_code = In_CodeText.getText();
        	 String parking_spot="";
        	 String query = "select * from sure_park.reservation where" +"`"+ "RESERVATION_ID"+"`"+"="+"'"+reservation_code+"'";
        	 try{
        		db.set_statement(db.get_connection().prepareStatement(query));
        		db.set_resultset(db.get_statement().executeQuery());
        		db.get_resultset().next();
        		parking_spot=db.get_resultset().getString("ASSIGNED_PARKING_SPOT");
        		 JOptionPane.showMessageDialog( null, String.format("Welcome \n parking lot NO: " + parking_spot) );
        		 // 아두이노에 값을 전달해야한다.
        	 }
        	 catch(SQLException ex){
        		 System.out.println("SQLException: " + ex.getMessage());
     			System.out.println("SQLState: " + ex.getSQLState());
     			System.out.println("VendorError: " + ex.getErrorCode());
     			JOptionPane.showMessageDialog( null, String.format("Wrong Code. please check again") );
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
      jframe.setDefaultCloseOperation(jframe.EXIT_ON_CLOSE);
      jframe.setSize(300, 200);
      jframe.setVisible(true);
   }
}

