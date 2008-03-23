package RMI;

import MainPackage.Player;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author benbac
 */

public class Viewer extends Thread {

    private Player player;
    private ServerInterface rmiServer;
    private String server = "127.0.0.1";
    private int port = 6666;
    private ArrayList<Object> Objects;
       
    public Viewer (Player player, String args[])
    {
        this.player = player;
        this.server = args[1];
        this.port = Integer.parseInt(args[2]);
        
        Objects = new ArrayList<Object>();
    }   
    
    @Override
    public void run() {}
    
    public void connect() {        
        try{
            String serverName = "rmi://" + server + ":" + port + "/ServerImpl";
            rmiServer = (ServerInterface)Naming.lookup(serverName);
            System.out.println("Viewer connected to " + rmiServer);  
            rmiServer.startViewer(getLocalAdr());
            recieveData();
       }       
       catch(RemoteException e){
           System.out.println(e.getMessage());  
       }
       catch(MalformedURLException e){
           System.out.println(e.getMessage());  
       }        
       catch(NotBoundException e){
           System.out.println(e.getMessage());  
       }   
    }
    
    public void disconnect() {
        try {
              if (rmiServer != null) 
                rmiServer.stopViewer(getLocalAdr());
        }
        catch (Exception re) {
            System.out.println(re.getMessage());
        }  
        Objects = null;
        interrupt();
    }
    
    public InetAddress getLocalAdr() {
        try{
            return (InetAddress.getLocalHost());
        }
        catch(UnknownHostException uhe){
            System.out.println(uhe.getMessage());
            return null;
        }          
    }  
    
    public void SendObject(Object object) {
        try {rmiServer.SendObject(object);} 
        catch (Exception re) {
            System.out.println(re.getMessage());
        }        
    }
    
    public void AddObject(Object object) {
        Objects.add(object);
    }   
    
    public void sendData() {
        if (Objects == null) return;
        ArrayList SendObjects;        
        synchronized(Objects){
            Objects.add(player.screenPlayer.getCompressionQuality());
            Objects.add(player.screenPlayer.getPrefferedScreenRect()); 
            SendObjects = Objects;    
            Objects = new ArrayList<Object>();
        }
        SendObject(SendObjects);
    }     
    
    public void recieveData() {
        try {
            player.screenPlayer.setScreenRect(rmiServer.getScreenRect());
            if (player.screenPlayer.isCompressionEnabled())
                player.screenPlayer.UpdateScreen(rmiServer.compressedUpdateScreen());                
            else
                player.screenPlayer.UpdateScreen(rmiServer.updateScreen());
        }
        catch (RemoteException re) {
            re.getStackTrace();
        }              
    }
}
