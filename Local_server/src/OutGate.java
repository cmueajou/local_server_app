import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class OutGate extends Thread{
   protected int id = -1;
   protected static JFrame jframe= new JFrame("OutGate");
   Database db = new Database("localhost","root","1234");
   protected static Container cp = jframe.getContentPane();
   int totalTime;
   double charge;
   
   public OutGate(int id){
      this.id = id;
   }
   
   @SuppressWarnings("deprecation")
public void run() {
   
      String parking_spot= "2";//
      String ID ="";
     
      String query = "select * from sure_park.reservation where" +"`"+ "ASSIGNED_PARKING_SPOT"+"`"+"="+"'"+parking_spot+"'";
      JPanel Out_on = new JPanel();
      JLabel Out_message = new JLabel("Bye!");
      
      try{
  		db.set_statement(db.get_connection().prepareStatement(query));
  		db.set_resultset(db.get_statement().executeQuery());
  		if(db.get_resultset().next()){
  			ID = db.get_resultset().getString("USER_ID");
  			Date d_start = db.get_resultset().getTime("PARKING_START_TIME");
  			Date d_end = db.get_resultset().getTime("PARKING_END_TIME");
  			Calendar start_time = Calendar.getInstance();
  			Calendar end_time=Calendar.getInstance();
  			start_time.setTime(d_start);
  			end_time.setTime(d_end);
  			totalTime=((end_time.get(Calendar.HOUR_OF_DAY)-start_time.get(Calendar.HOUR_OF_DAY))*60)+(end_time.get(Calendar.MINUTE)-start_time.get(Calendar.MINUTE));
  			charge = totalTime*0.125;
  			System.out.println(totalTime);
  			
  		}
  		
      }
      catch(SQLException ex){
 		 System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
      }
      String time =Integer.toString(totalTime);

      
      JLabel User = new JLabel("ID:");
      JLabel Out_ID = new JLabel(ID);
      JLabel totalTime = new JLabel("Total time:");
      JLabel Out_totalTime = new JLabel(time);
      JLabel l_charge = new JLabel("total charge:");
      JLabel Out_charge = new JLabel(Double.toString(charge)+"$");

      Box b = new Box(BoxLayout.Y_AXIS);
      
      b.add(Out_message);
      b.add(User);
      b.add(Out_ID);
      b.add(totalTime);
      b.add(Out_totalTime);
      b.add(l_charge);
      b.add(Out_charge);
      
      Out_on.add(b);
      
      cp.add(Out_on);
      
      jframe.pack();
      
      jframe.setSize(300, 200);
      jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jframe.setVisible(true);
      try {
          Thread.sleep(9000);
          System.out.println("g");
       } catch (InterruptedException e) {
          e.printStackTrace();
       }
       jframe.remove(Out_on);
       jframe.revalidate();
       jframe.repaint();

      
   }
  
}