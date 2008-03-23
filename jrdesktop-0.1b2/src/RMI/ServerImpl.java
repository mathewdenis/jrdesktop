package RMI;

import java.awt.Rectangle;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.ImageIcon;

/**
 *
 * @author benbac
 */

public class ServerImpl 
        extends UnicastRemoteObject 
        implements ServerInterface , Runnable{
    
    Server server;
        
    public ServerImpl (Server server) throws RemoteException {
        this.server = server;       
    }
    
    public void run() {}
     
    @Override
    public void startViewer(InetAddress inetAddress) throws RemoteException {
        server.addViewer(inetAddress);
    }
    
    @Override
    public void stopViewer(InetAddress inetAddress) throws RemoteException {
        server.removeViewer(inetAddress);
    }
    
    @Override
    public void SendObject(Object object) throws RemoteException {        
        server.AnalyseObject(object);
    }
    
    @Override
    public ImageIcon updateScreen() throws RemoteException {
       return new ImageIcon(server.screenUpdate());
    }
    @Override
    public byte[] compressedUpdateScreen() throws RemoteException {
       return server.compressedScreenUpdate();
    }    
    
    @Override
    public Rectangle getScreenRect() throws RemoteException {
        return server.getScreenRect();
    } 
}
