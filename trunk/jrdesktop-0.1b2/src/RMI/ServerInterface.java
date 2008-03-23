package RMI;

import java.awt.Rectangle;
import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.swing.ImageIcon;

/**
 *
 * @author benbac
 */

public interface ServerInterface extends Remote {
    public void SendObject(Object object) throws RemoteException;
    public void stopViewer(InetAddress inetAddress) throws RemoteException;
    public void startViewer(InetAddress inetAddress) throws RemoteException;
    public ImageIcon updateScreen() throws RemoteException;
    public byte[] compressedUpdateScreen() throws RemoteException;  
    public Rectangle getScreenRect() throws RemoteException;
}
