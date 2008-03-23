package MainPackage;

import RMI.Server;
import java.util.Properties;

/**
 *
 * @author benbac
 */

public class main {
    
    public static void main (String args[]) {  

        Properties properties = new Properties();           
        properties.put("sun.rmi.activation.execTimeout", 60000);                
        System.setProperty("java.rmi.server.hostname", args[1]);    
        
        if (args[0].equals("server"))
            new Server(args[1], Integer.parseInt(args[2]));        
        else if (args[0].equals("viewer"))       
            Player.main(args);                   
    }
}
