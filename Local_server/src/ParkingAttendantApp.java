
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.io.*;

public class ParkingAttendantApp implements Runnable{
   protected static final JFrame jframe= new JFrame("Parking-Attendant-App");
   protected static JPanel PAUI_p2_parkingLot_space[] = new JPanel[4];
   private int id = -1;
   protected JTextField AccountText;
   protected JTextField PINText;
   
   protected JLabel AccountLabel;
   protected JLabel PINLabel;
   
   protected String ID = "ad1";
   protected String Pin = "1";
   private String Broadcast="";
   
   InputStream pipe_in;
  // OutputStream pipe_out;
   
   public ParkingAttendantApp(int id){
      this.id = id;
    //  this.pipe_in=_pipe_in;
     // this.pipe_out=_pipe_out;
   }
   public void run() {
   
      Container cp = jframe.getContentPane();
      
      jframe.setLayout(new BorderLayout());
      
      final JPanel LoginPanel = new JPanel();
      
      AccountText = new JTextField(5);
      PINText = new JTextField(5);
      
      AccountLabel = new JLabel("Account number:");
      PINLabel = new JLabel("PIN number:");
      
      JButton App_submitButton = new JButton("Submit");
      
      LoginPanel.setLayout(new FlowLayout());
      LoginPanel.add(AccountLabel);
      LoginPanel.add(AccountText);
      LoginPanel.add(PINLabel);
      LoginPanel.add(PINText);
      LoginPanel.add(App_submitButton);
      
      jframe.add(LoginPanel);
      
      jframe.pack();

      jframe.setSize(600, 300);
      jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jframe.setVisible(true);
      
      ActionListener a1 = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if(AccountText.getText().equals(ID)&& PINText.getText().equals(Pin)){
               jframe.remove(LoginPanel);
               PAUI UI = new PAUI(id);
               jframe.revalidate();
               jframe.repaint();
                  
               }
               else{
                  JOptionPane.showMessageDialog( null, String.format("Wrong Id or pin number") );
               }
            
         }
      };
      
      App_submitButton.addActionListener(a1);
      
      /*
      cp.setLayout(new FlowLayout());
      
      JPanel ParkingPanel = new JPanel();
      ParkingPanel.setBackground(Color.green);
      JLabel ParkingLabel = new JLabel("Parking");
      ParkingPanel.add(ParkingLabel);
      
      JPanel emptyPanel = new JPanel();
      emptyPanel.setBackground(Color.red);
      JLabel emptyLabel = new JLabel("empty");
      emptyPanel.add(emptyLabel);
      
      
      
      cp.add(ParkingPanel);
      cp.add(emptyPanel); 
      */
      
   }
   
   public void changeParkinglotColor(int no,int state){
      if(state == 2)
         PAUI_p2_parkingLot_space[no].setBackground(Color.red); // 점유
      else if(state == 1)
         PAUI_p2_parkingLot_space[no].setBackground(Color.blue); // 예약
      if(state == 0)
         PAUI_p2_parkingLot_space[no].setBackground(Color.green); // 빈공간
   }

}