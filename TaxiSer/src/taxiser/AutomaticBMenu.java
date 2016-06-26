/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiser;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author kabs67
 */
public class AutomaticBMenu extends Thread {
    
    private static JFrame frame;
     private JTextArea area;
     private JPanel panel;
     private JPanel buttonPanel;
     private JScrollPane scroller;
     private JButton button;
    private DefaultCaret caret;
     //closeListener cLis;
     public int modo;
     public Client c;
     
    AutomaticBMenu(Client c)
    {
        frame = new JFrame();
        frame.setVisible(true);
        frame.setTitle("Service Buyer Menu");
        frame.setSize(800, 800);
        //frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        area = new JTextArea(15, 50);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setFont(Font.getFont(Font.SANS_SERIF));
        scroller = new JScrollPane(area);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        button = new JButton("Cancel");
        
        caret = (DefaultCaret) area.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        buttonPanel.add(button);
        panel.add(buttonPanel);
        panel.add(scroller);
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.pack();
        //frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.setResizable(false);
        
        
        //cLis = new closeListener();
       // login.addActionListener(cLis);
        //register.addActionListener(cLis);
        //exit.addActionListener(cLis);
        this.modo = 100;
        this.c = c;
    }
   

    public class cancelListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {
                if(event.getSource() == button )
            {
                // getString();
                System.out.println(modo);
                modo = 1;
                System.out.println(modo);
                
            }
        }
    }
    
    @Override
    public void run(){
        synchronized(this){
            //System.out.println("Esta no run");
            cancelListener cLis = new cancelListener();
             button.addActionListener(cLis);
             System.out.println(modo);
             int flag = 0;
             while(flag == 0){
                 if(modo==1){
                     c.setMessageToServer("cancelUser "+c.getUsername());
                     try {
                         sleep(5);
                     } catch (InterruptedException ex) {
                         Logger.getLogger(AutomaticBMenu.class.getName()).log(Level.SEVERE, null, ex);
                     }
                     if(!c.getMessageFromServer().equals(" ")){
                         String part[] = c.getMessageFromServer().split(" ");
                        if(part[0].equals("canceled")){
                            area.append(c.getMessageFromServer()+"\n");
                            flag=2;
                        } 
                     }
                 }
                 else{
                     c.setMessageToServer("okchegoutaxiUser "+c.getUsername());
                     try {
                         sleep(5);
                     } catch (InterruptedException ex) {
                         Logger.getLogger(AutomaticBMenu.class.getName()).log(Level.SEVERE, null, ex);
                     }
                     if(!c.getMessageFromServer().equals(" ")){
                         String part[] = c.getMessageFromServer().split(" ");
                        if(part[0].equals("taxi")){
                            area.append(c.getMessageFromServer()+"\n");
                            flag=1;
                        } 
                    }
                 }
             }
             while(flag==1){
                if(modo==1){
                     c.setMessageToServer("cancelUser "+c.getUsername());
                     try {
                         sleep(5);
                     } catch (InterruptedException ex) {
                         Logger.getLogger(AutomaticBMenu.class.getName()).log(Level.SEVERE, null, ex);
                     }
                      if(!c.getMessageFromServer().equals(" ")){
                         String part[] = c.getMessageFromServer().split(" ");
                        if(part[0].equals("canceled")){
                            area.append(c.getMessageFromServer()+"\n");
                            flag=2;
                        } 
                     }
                 }
                 else{
                     c.setMessageToServer("okchegoudestinoUser "+c.getUsername());
                     try {
                         sleep(5);
                     } catch (InterruptedException ex) {
                         Logger.getLogger(AutomaticBMenu.class.getName()).log(Level.SEVERE, null, ex);
                     }
                      if(!c.getMessageFromServer().equals(" ")){
                         String part[] = c.getMessageFromServer().split(" ");
                        if(part[0].equals("destino")){
                            area.append(c.getMessageFromServer()+"\n");
                            flag=2;
                        } 
                     }
                 }
             }
             
             notify();
            //notifications.addActionListener(cLis);
            //System.out.println("Saiu");
            
        }
    }
    
}
