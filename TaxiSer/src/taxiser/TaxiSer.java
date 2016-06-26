/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kabs67
 */
public class TaxiSer {
    
    static void threadMessage(String message) {
        String threadName =
            Thread.currentThread().getName();
        System.out.format("%s: %s%n",
                          threadName,
                          message);
    }


    public static class Message extends Thread{
        private Client client;
        private String taxista;
        private static int flag;
        Message(Client client){
            this.client = client;
            this.taxista = " ";
            this.flag = 0;
        }
        @Override
       public void run(){
            try {
                Socket clientSocket = new Socket("localhost", 5000);
 
                while (true){
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    String messageToServer = client.getMessageToServer();
                    

                   
                    if(!messageToServer.equals(" ") ){
                        System.out.println("To Server: "+messageToServer);
                        client.setMessageToServer(" ");
                    
                        /* if(splited[0].equals("callTaxi")){
                            flag = 1;
                          }*/
                         
                             //cancelar taxi
                            
                            //caso message to server = call ou available fazer um contador e receber x mensagens diferentes
                            outToServer.writeBytes(messageToServer+'\n');
                            outToServer.flush();
                                    String messageFromServer = inFromServer.readLine();
                                    if (messageFromServer == null){
                                        System.out.println("ola");
                                        messageFromServer = " ";
                                    }
                                    System.out.println("From SERVER: "+messageFromServer);
                                      client.setMessageFromServer(messageFromServer);
                                    
                            
                                    System.out.println("FROM SERVER: " + messageFromServer);
                    }
                    
                   
                    //System.out.println(flag);
                }
            } catch (IOException ex) {
                    Logger.getLogger(TaxiSer.class.getName()).log(Level.SEVERE, null, ex);
            }                    
         }
       }
    
    public static class InterfaceIO extends Thread{
        private Client client;
        private Interface inter;
        InterfaceIO(Client client){
            this.client = client;
            this.inter = new Interface();
        }
        @Override
       public void run(){
           int option = 100;
           int optionB = Integer.MAX_VALUE;
           String message = " ";
           String message2 = " ";
           float x;
           float y;
           float xdes;
           float ydes;
           StringBuilder s = new StringBuilder();
           String message3 = " ";
           while (option!=0){
               //System.out.println("Ola");
               option= inter.initialMenuGui();
               //System.out.println("Ola2");
               
               System.out.println(option);
               optionB = Integer.MAX_VALUE;
               switch(option){
                    case 1 :
                        String login = inter.menuLoginGui();

                       
                        String messageToServer = login;
                        client.setMessageToServer(messageToServer);
                        String[] splited = messageToServer.split(" ");
                        {
                            try {
                                sleep(5);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(TaxiSer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            message = client.getMessageFromServer();
                            if(message.equals("Utilizador_entrou")){
                                client.setMessageFromServer(" ");
                                System.out.println("User Entered");
                                inter.messageMenu("O utilizador "+client.getUsername()+"fez o login");
                                client.setUsername(splited[1]);
                                System.out.println(client.getType());
                                if(client.getType().equals("User")){
                                    //mudar para receber do servidor a dizer que tipo de user sou
                                    while(optionB!=0){
                                        optionB = inter.menuBuyerGui();
                                        if(optionB==1){
                                            System.out.println("Entrei no callTaxi");
                                            client.setLocalization(inter.menuLocalizationGui());
                                            x = client.getLocalization().x;
                                            y = client.getLocalization().y;
                                            client.setDestination(inter.menuDestinationGui());
                                            xdes = client.getDestination().x;
                                            ydes = client.getDestination().y;
                                            s = new StringBuilder();
                                            s.append("callTaxi ");
                                            s.append(client.getUsername());
                                            s.append(" "+x);
                                            s.append(" "+y);
                                            s.append(" "+xdes);
                                            s.append(" "+ydes);
                                            client.setMessageToServer(s.toString());
                                            while(message2.equals(" ")){
                                                try {
                                                    sleep(5);
                                                } catch (InterruptedException ex) {
                                                    Logger.getLogger(TaxiSer.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                                message2 = client.getMessageFromServer();
                                                
                                            }
                                            System.out.println("A mensagem imprimida é"+message2+"ola");
                                            String part[] = message2.split(" ");
                                            if(part[0].equals("Taxi_a_caminho_das_coordenadas")){
                                                    client.setMessageFromServer(" ");
                                                    System.out.println(message2);
                                                    
                                            }
                                            
                                            inter.messageMenu(message2);
                                            
                                            message2 = " ";

                                            //ver aqui o menu (alterar)
                                            inter.automaticBuyerMenu(client);
                                            client.setMessageFromServer(" ");
                                                    client.setMessageToServer(" ");
                                            
                                            
                                            /*StringBuilder s2 = new StringBuilder();
                                            s2.append("cancelarTaxi ");
                                            s2.append(client.getUsername());
                                            
                                            client.setMessageToServer(s2.toString());*/
                                            
                                            
                                        }
                                       //call a taxi, become a driver, cancel trip
                                    }
                                    client.setMessageToServer("logout "+client.getUsername());
                                     try {
                                        sleep(5);
                                        message2 = client.getMessageFromServer();
                                        System.out.println(message2);
                                        } catch (InterruptedException ex) {
                                            Logger.getLogger(TaxiSer.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                     inter.messageMenu("O utilizador "+client.getUsername()+" fez o logout");
                                }
                                else if(client.getType().equals("Driver"))
                                    while(optionB!=0){
                                        optionB = inter.menuDriverGui();
                                        if(optionB==1){
                                            System.out.println("Entrei no available");
                                            client.setLocalization(inter.menuAvailableGui());
                                            x = client.getLocalization().x;
                                            y = client.getLocalization().y;
                                            s = new StringBuilder();
                                            s.append("availabletaxi");
                                            s.append(" "+client.getUsername());
                                            s.append(" "+x);
                                            s.append(" "+y);
                                            client.setMessageToServer(s.toString());
                                            
                                            while(message2.equals(" ")){
                                                try {
                                                    sleep(5);
                                                } catch (InterruptedException ex) {
                                                    Logger.getLogger(TaxiSer.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                                message2 = client.getMessageFromServer();
                                            }
                                            String part[] = message2.split(" ");
                                            if(part[0].equals("Taxi_a_caminho_das_coordenadas")){
                                                    client.setMessageFromServer(" ");
                                                    client.setMessageToServer(" ");
                                                    System.out.println(message2);
                                                    
                                            }
                                            System.out.println("A mensagem imprimida é"+message2+"ola");
                                             inter.messageMenu(message2);
                                            message2 = " ";
                                            
                                            inter.automaticDriverMenu(client);
                                            client.setMessageFromServer(" ");
                                                    client.setMessageToServer(" ");
                                        }
                                    }
                                client.setMessageToServer("logout "+client.getUsername());
                                try {
                                    sleep(5);
                                    message2 = client.getMessageFromServer();
                                System.out.println(message2);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(TaxiSer.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                inter.messageMenu("O utilizador "+client.getUsername()+" fez o logout");
                                
                                    
                            }
                            else if (message.equals("Password_errada")){
                                client.setMessageFromServer(" ");
                                System.out.println("Password Errada!!");
                                 inter.messageMenu("Password Errada, tente novamente!");
                            }
                            else{
                                System.out.println("Utilizador não Existe!!!");
                                inter.messageMenu("Utilizador não existe");
                            }
                        }
                        break;
                    case 2 :
                        client.setMessageToServer(inter.menuRegistoGui());
                        {
                            try {
                                sleep(5);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(TaxiSer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            message = client.getMessageFromServer();
                            if(message.equals("User_Created")){
                                client.setMessageFromServer(" ");
                                System.out.println("User Created");
                                client.setType("User");
                                inter.messageMenu("O utilizador "+client.getUsername()+" foi registado com sucesso");
                                System.out.println(client.getType());
                            }
                            else if (message.equals("Driver_Created")){
                                client.setMessageFromServer(" ");
                                System.out.println("Driver Created");
                                inter.messageMenu("O utilizador "+client.getUsername()+" foi registado com sucesso");
                                client.setType("Driver");
                                System.out.println(client.getType());
                            }
                            else{
                                System.out.println("Utilizador já existe");
                                inter.messageMenu("O utilizador já existe!");
                                client.setMessageFromServer(" ");
                            }
                        }
                        break;
                    case 0 :
                        
                            inter.messageMenu("Saiu do Taxi Service. Volte sempre :)");
                            System.out.println("\n\n--------------You exited Taxi Service-------------------");
                        break;
                 }   
           }
       }
    }   
}


// static só há um para varias instancias
//falta fazer no available uma thread que fica à espera de notificaçoes
//se os condutores estiverem todos ocupados esperar, isto no erlang
//colocar uma flag a dizer se os condutores estao ocupados no erlang
//fazer uma janela depois das coordenadas a mostrar as notificaçoes no driver, depois de a viagem chegar ao final e mostrar o preço é me permitido fazer exit... nao esquecer de tirar a flag de condutor ocupado
//no buyer na mesma janela é permitido um botao que permite cancelar a viagem (era fixe mostrar o preço da mesma a alterar em tempo real - o custo da viagem e o tempo passado)
// retirar a opçao, ver notificaçoes, no final do trabalho estar feito