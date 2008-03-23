package RMI;

import MainPackage.robot;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

/**
 *
 * @author benbac
 */

public class Server {
    
    String server = "127.0.0.1";
    int port = 6666;
    
    Registry registry;
    ServerImpl rmiServer;
    
    robot rt;
        
    public ArrayList<InetAddress> viewerList = new ArrayList<InetAddress>(); 
    //public Thread notifier = null;
    
    public Server(String server, int port) {
        this.server = server;
        this.port = port;
        
        Start();
        StartRobot();
    }
    
    public void Start() { 
        try{
            registry = LocateRegistry.createRegistry(port);            
            rmiServer = new ServerImpl(this);
            String url = "rmi://" + server + ":" + port + "/ServerImpl";
            Naming.rebind(url, rmiServer);        
            System.out.println("Server started : " + rmiServer);
        }
        catch(MalformedURLException re) {
            System.out.println(re.getMessage());
        }        
        catch(RemoteException re) {
            System.out.println(re.getMessage());
        }
    }    
    
    public String getLocalAdr() {
        try{
            return (InetAddress.getLocalHost()).toString();
        }
        catch(UnknownHostException uhe){
            System.out.println(uhe.getMessage());
            return null;
        }          
    }       
    
    public void StartRobot() {
        try
        {               
            rt = new robot(this);
        }
        catch (AWTException awte)
        {
            System.out.print(awte.getMessage());
        }        
    }    
    
    public void AnalyseObject(Object object) {
        rt.AnalyseObject(object);
    }
    
    public BufferedImage screenUpdate() {
        return rt.captureScreen();
    }
    
    public byte[] compressedScreenUpdate() {
        return rt.compressedCaptureScreen();
    }
    
    public Rectangle getScreenRect() {
        return rt.getScreenRect();
    }
    
    protected synchronized void addViewer(InetAddress inetAddress) {
        /*if (notifier == null) {
            notifier = new Thread(rmiServer);
            notifier.start();
        } */           
        
        int viewerPos = viewerList.indexOf(inetAddress);
        if (viewerPos == -1) {
            System.out.println("Viewer connected: " + inetAddress);      
            viewerList.add(inetAddress);
        }
    }
            
    protected synchronized void removeViewer(InetAddress inetAddress) {
        System.out.println("Viewer disconnected: " + inetAddress);
        viewerList.remove(inetAddress);
       
        /*if (viewerList.isEmpty()) {        
            Thread thread = notifier;
            notifier = null;
            thread.interrupt();
        }*/
    }    
}
