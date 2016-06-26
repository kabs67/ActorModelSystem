/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiser;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author kabs67
 */
public class Client {
    
    private String messageToServer;
    private String messageFromServer;
    private String type;
    private Point2D.Float destination;
    private Point2D.Float localization;
    private long timeStartOrder;
    private String carRegistration;
    private String username;
    private String taxiArrivedMessage;
    private String canceledMessage;
    private String taxionDestinationMessage;
    private ArrayList<String> notifications;
    
    public Client(){
        this.messageToServer = " ";
        this.messageFromServer = " ";
        this.type = null;
        this.destination = null;
        this.localization = null;
        this.timeStartOrder = 0;
        this.carRegistration = null;
        this.username = null;
        this.notifications = new ArrayList<String>();
        this.taxionDestinationMessage = " ";
        this.taxiArrivedMessage = " ";
        this.canceledMessage = " ";
        
    }

    public String getCanceledMessage() {
        return canceledMessage;
    }

    public void setCanceledMessage(String canceledMessage) {
        this.canceledMessage = canceledMessage;
    }
    
    

    public String getTaxiArrivedMessage() {
        return taxiArrivedMessage;
    }

    public void setTaxiArrivedMessage(String taxiArrivedMessage) {
        this.taxiArrivedMessage = taxiArrivedMessage;
    }

    public String getTaxionDestinationMessage() {
        return taxionDestinationMessage;
    }

    public void setTaxionDestinationMessage(String taxionDestinationMessage) {
        this.taxionDestinationMessage = taxionDestinationMessage;
    }
    
    

    public ArrayList<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<String> notifications) {
        this.notifications = notifications;
    }

    
   synchronized public String getMessageToServer() {
        return messageToServer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    

   synchronized public void setMessageToServer(String messageToServer) {
        this.messageToServer = messageToServer;
    }

   synchronized public String getMessageFromServer() {
        return messageFromServer;
    }

  synchronized  public void setMessageFromServer(String messageFromServer) {
        this.messageFromServer = messageFromServer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Point2D.Float getDestination() {
        return destination;
    }

    public void setDestination(Point2D.Float destination) {
        this.destination = destination;
    }

    public Point2D.Float getLocalization() {
        return localization;
    }

    public void setLocalization(Point2D.Float localization) {
        this.localization = localization;
    }

    public long getTimeStartOrder() {
        return timeStartOrder;
    }

    public void setTimeStartOrder(long timeStartOrder) {
        this.timeStartOrder = timeStartOrder;
    }

    public String getCarRegistration() {
        return carRegistration;
    }

    public void setCarRegistration(String carRegistration) {
        this.carRegistration = carRegistration;
    }
    
    public String toNotifString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Notifications: \t");
        for (String string : notifications)
        {
            sb.append(string);
            sb.append("\t");
        }
        return sb.toString();
    }
    
    
    
}
