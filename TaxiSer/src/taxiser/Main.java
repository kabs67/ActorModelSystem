/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiser;

/**
 *
 * @author kabs67
 * 
 * 
 * 
 */

import taxiser.TaxiSer.Message;
import taxiser.TaxiSer.InterfaceIO;

public class Main {
    
     public static void main(String[] args) {
        // TODO code application logic here
            TaxiSer taxiService = new TaxiSer();
            Client novo = new Client();
            Thread message = new Message(novo);
            Thread interfaceIO = new InterfaceIO(novo);
            message.start();
            interfaceIO.start();
    }
    
}
