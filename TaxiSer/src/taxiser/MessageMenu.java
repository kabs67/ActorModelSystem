/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiser;

import java.awt.BorderLayout;
import java.awt.Font;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
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
public class MessageMenu extends Thread {
    
        private static JFrame frame;
     private JTextArea area;
     private JPanel panel;
     private JScrollPane scroller;
    private DefaultCaret caret;
     //closeListener cLis;
     public int modo;
     public String c;
     
    MessageMenu(String c)
    {
        frame = new JFrame();
        frame.setVisible(true);
        frame.setTitle("Message");
        frame.setSize(200, 200);
        //frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        area = new JTextArea(15, 150);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setFont(Font.getFont(Font.SANS_SERIF));
        scroller = new JScrollPane(area);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        caret = (DefaultCaret) area.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
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
    
    
    @Override
    public void run(){
        synchronized(this){
            area.append(this.c);
            //System.out.println("Esta no run");
           
             
             notify();
            //notifications.addActionListener(cLis);
            //System.out.println("Saiu");
            
        }
    }
    
}
