/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import static java.lang.Thread.sleep;

/**
 *
 * @author kabs67
 */
public class LoginMenu extends Thread{
    private static JFrame frame;
    JLabel l1, l2, l3;
    JTextField tf1;
    private JButton btn1;
    JPasswordField p1;
    //closeListener cLis;
    int ok;
    
    LoginMenu()
    {
        frame = new JFrame();
        frame.setVisible(true);
        frame.setTitle("Login Menu");
        frame.setVisible(true);
        frame.setSize(800, 800);
        //nao tava a dar por causa do setLayout que estava a null
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
 
        l1 = new JLabel("Login:");
        l1.setForeground(Color.blue);
        l1.setFont(new Font("Serif", Font.BOLD, 20));
 
        l2 = new JLabel("Enter Username:");
        l3 = new JLabel("Enter Password:");
        tf1 = new JTextField();
        p1 = new JPasswordField();
        btn1 = new JButton("Submit");
 
        l1.setBounds(100, 30, 400, 30);
        l2.setBounds(80, 70, 200, 30);
        l3.setBounds(80, 110, 200, 30);
        tf1.setBounds(300, 70, 200, 30);
        p1.setBounds(300, 110, 200, 30);
        btn1.setBounds(150, 160, 100, 30);
 
        frame.add(l1);
        frame.add(l2);
        frame.add(tf1);
        frame.add(l3);
        frame.add(p1);
        frame.add(btn1);
        //cLis = new closeListener();
        //btn1.addActionListener(cLis);
        ok = 0;
    }
    
    public class closeListener implements ActionListener
    {
        
        @Override
        public void actionPerformed(ActionEvent event)
        {
            if(event.getSource() == btn1 )
            {
                // getString();
                ok = 1;
                frame.dispose();
            }
        }
    }
    
    @Override
    public void run(){
        synchronized(this){
            //System.out.println("Esta no run");
            closeListener cLis = new closeListener();
             btn1.addActionListener(cLis);
            System.out.println(ok);
            while(ok!=1){
                try {
                    sleep(2000);
                    //System.out.println("Esta no while");
                    //System.out.println("fjsdfjsjfsjfsoidjfsdjfslkdjfslkdjflskjflsjfslkdjfslkdfj");
                } catch (InterruptedException ex) {
                    Logger.getLogger(InitialMenu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //System.out.println("Saiu");
            notify();
            
        }
    }
 
    
}
