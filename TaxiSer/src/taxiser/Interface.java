/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiser;

import java.awt.geom.Point2D;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author kabs67
 */
public class Interface {
    
    
    public int initialMenu(){
        int menu = Integer.MAX_VALUE;
        Scanner s = new Scanner( System.in );
            System.out.println("Welcome to our Taxi Service---------------");
            System.out.println("1.Login");
            System.out.println("2.Register");
            System.out.println("0.Exit");
 
             menu=s.nextInt();
            System.out.println("\n");
            while(menu>2 || menu<0){
                System.out.println("Invalid option. Choose again!");
                menu = s.nextInt();
            }
        return menu;
    }
    
    public int initialMenuGui(){
        InitialMenu iM = new InitialMenu();
        iM.start();
        synchronized(iM){
            try{
                System.out.println("Waiting for iM to complete...");
                iM.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            return iM.modo;
        }
    }
    
    public  String menuRegisto(){
        StringBuilder registo = new StringBuilder();
        Scanner s = new Scanner( System.in );
        System.out.println("-----------Register--------------");
                        System.out.print("Username:");
                        String username = s.next();
                        System.out.print("Password:");
                        String password = s.next();
                        Point2D.Float localization = random2D();
                        int option = 3;
                        while (option!=1 && option!=2){
                             System.out.print("1)Buyer or 2)Driver ?:");
                             option = s.nextInt();
                        }
                        if(option == 1){
                            registo.append("createAccount");
                            registo.append(" "+username);
                            registo.append(" "+password);
                            
                        }
                        else{
                            registo.append("createAccountDriver");
                            registo.append(" "+username);
                            registo.append(" "+password);
                            System.out.print("Car Registrationl:");
                            String registration = s.next();
                            registo.append(" "+registration);
                        }
          return registo.toString();
                        // falta adicionar o user à base de dados do erlang
    }
    
    public String menuRegistoGui(){
        RegisterMenu rM = new RegisterMenu();
        rM.start();
        synchronized(rM){
            try{
                System.out.println("Waiting for iM to complete...");
                rM.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        StringBuilder registo = new StringBuilder();
        Scanner s = new Scanner( System.in );
        if(rM.tf3.getText().equals("Driver")){
            registo.append("createAccountDriver ");
            registo.append(rM.tf2.getText());
            String a = new String(rM.p1.getPassword());
            registo.append(" ");
            registo.append(a);
            registo.append(" ");
            registo.append(rM.tf4.getText());
        }
        else{
            registo.append("createAccount ");
            registo.append(rM.tf2.getText());
            String a = new String(rM.p1.getPassword());
            registo.append(" ");
            registo.append(a); 
        }
            return registo.toString();
        }
    }
    
    public Point2D.Float random2D(){
        Random randomGenerator = new Random();
        Float randomValue =   (100) * randomGenerator.nextFloat();
        Float randomValue2 =   (100) * randomGenerator.nextFloat();
        return new Point2D.Float(randomValue, randomValue2);
    }
    
    public String menuLogin(){
         StringBuilder registo = new StringBuilder();
          Scanner s = new Scanner( System.in );
         System.out.print("Username:");
                        String username = s.next();
                        System.out.print("Password:");
                        String password = s.next();
                        /*ver se o username tem a password correcta e se estiver com um get saber o utilizador com este mail, client*/
                        registo.append("login ");
                        registo.append(username);
                        registo.append(" "+password);
                        return registo.toString();
     }
    
    public String menuLoginGui(){
        LoginMenu log = new LoginMenu();
        log.start();
        synchronized(log){
            try{
                System.out.println("Waiting for iM to complete...");
                log.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        StringBuilder registo = new StringBuilder();
        registo.append("login ");
        registo.append(log.tf1.getText());
        registo.append(" ");
        String a = new String(log.p1.getPassword());
        registo.append(a);
            return registo.toString();
        }        
    }
    
public int menuBuyer(){
          Scanner s = new Scanner( System.in );
         int a = Integer.MAX_VALUE;
           out.println("\n\n\n---------------------------PERFIL----------------------------------");
           out.println("| 1 - Call a taxi                                                                                                                    ");
           out.println("| 2 - Cancel taxi                                                                                                                    ");
           out.println("| 3 - Notifications                                                                                                                    ");
           out.println("| 4 - Become a driver                                                                                                                    ");
           out.println("|                                                                                                                                                ");
           out.println("| 0 - Exit                                                                                                                                 ");
           out.println("-------------------------------------------------------------------------");
           a = s.nextInt();
           return a;
         
     }

    public int menuBuyerGui(){
        BuyerMenu buyer = new BuyerMenu();
        buyer.start();
        synchronized(buyer){
            try{
                System.out.println("Waiting for iM to complete...");
                buyer.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        return buyer.modo;
        }  
    }
    
    
    public int menuDriver(){
          Scanner s = new Scanner( System.in );
         int a = Integer.MAX_VALUE;
           out.println("\n\n\n---------------------------PERFIL----------------------------------");
           out.println("| 1 - I am available                                                                                                                    ");
           out.println("| 2 - Notifications                                                                                                                    ");
           out.println("|                                                                                                                                                ");
           out.println("| 0 - Exit                                                                                                                                 ");
           out.println("-------------------------------------------------------------------------");
           a = s.nextInt();
         return a;
     }
    
    public int menuDriverGui(){
        DriverMenu driver = new DriverMenu();
        driver.start();
        synchronized(driver){
            try{
                System.out.println("Waiting for iM to complete...");
                driver.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        return driver.modo;
        }  
    }
    
    public Point2D.Float menuAvailable(){
        Scanner s = new Scanner( System.in );
        float x = 0;
        float y = 0;
        out.print("Coord X:  ");
        x = s.nextFloat();
        out.print("Coord Y:  ");
        y= s.nextFloat();
        Point2D.Float ret = new Point2D.Float(x,y);
        return ret;
    }
    
    public Point2D.Float menuAvailableGui(){
        AvailableMenu aM = new AvailableMenu();
        aM.start();
        synchronized(aM){
            try{
                System.out.println("Waiting for iM to complete...");
                aM.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        float x = Float.parseFloat(aM.tf2.getText());
        float y = Float.parseFloat(aM.tf3.getText());
        Point2D.Float ret = new Point2D.Float(x,y);
        return ret;
        }  
    }
    
    public Point2D.Float menuLocalizationGui(){
        LocalizationMenu lM = new LocalizationMenu();
        lM.start();
        synchronized(lM){
            try{
                System.out.println("Waiting for iM to complete...");
                lM.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        float x = Float.parseFloat(lM.tf2.getText());
        float y = Float.parseFloat(lM.tf3.getText());
        Point2D.Float ret = new Point2D.Float(x,y);
        return ret;
        }  
    }
    
    public Point2D.Float menuDestinationGui(){
        DestinationMenu dM = new DestinationMenu();
        dM.start();
        synchronized(dM){
            try{
                System.out.println("Waiting for iM to complete...");
                dM.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        float x = Float.parseFloat(dM.tf2.getText());
        float y = Float.parseFloat(dM.tf3.getText());
        Point2D.Float ret = new Point2D.Float(x,y);
        return ret;
        }  
    }
    
    public void automaticDriverMenu(Client c){
        AutomaticDMenu aDM = new AutomaticDMenu(c);
        aDM.start();
        synchronized(aDM){
            try{
                System.out.println("Waiting for iM to complete...");
                aDM.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
    }
    }
    
    public void automaticBuyerMenu(Client c){
        AutomaticBMenu aBM = new AutomaticBMenu(c);
        aBM.start();
        synchronized(aBM){
            try{
                System.out.println("Waiting for iM to complete...");
                aBM.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
    }
    }
  
       public void messageMenu(String message){
        MessageMenu mM = new MessageMenu(message);
        mM.start();
        synchronized(mM){
            try{
                System.out.println("Waiting for iM to complete...");
                mM.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
    }
    }

    
}


//ver se é preciso colocar o /n no fim do append, fazer o serializable