/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiser;

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

/**
 *
 * @author kabs67
 */
public class DriverMenu extends Thread{

    private static JFrame frame;
    private JButton available, notifications, exit;
     //closeListener cLis;
     public int modo;
     
    DriverMenu()
    {
        frame = new JFrame();
        frame.setVisible(true);
        frame.setTitle("Driver Menu");
        frame.setSize(800, 800);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
 
        
        available = new JButton("I am Available");
        exit = new JButton("Exit");
 
        available.setBounds(300, 200, 200, 100);
        exit.setBounds(350, 350, 100, 50);
 
        frame.add(available);
        frame.add(exit);
        //cLis = new closeListener();
       // login.addActionListener(cLis);
        //register.addActionListener(cLis);
        //exit.addActionListener(cLis);
        this.modo = 100;
    }
    
    public class closeListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {
                if(event.getSource() == available )
            {
                // getString();
                System.out.println(modo);
                modo = 1;
                System.out.println(modo);
                frame.dispose();
                
            }
            else if(event.getSource() == exit){
                modo = 0;
                frame.dispose();
            }
        }
    }
    
    @Override
    public void run(){
        synchronized(this){
            //System.out.println("Esta no run");
            closeListener cLis = new closeListener();
             available.addActionListener(cLis);
            //notifications.addActionListener(cLis);
            exit.addActionListener(cLis);
            System.out.println(modo);
            while(modo!=1 && modo!=0){
                try {
                    sleep(2000);
                    //sem este sleep o codigo nao funciona
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
