package jrdesktop.utilities;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * @author benbac
 */

public class InetAdrUtility {

   public static InetAddress getLocalAdr() {
        try{
            return (InetAddress.getLocalHost());
        }
        catch(UnknownHostException uhe){
            uhe.getStackTrace();
            return null;
        }          
    }        
            
    public static Set<InetAddress> localAddresses() throws SocketException {
        Set<InetAddress> localAddrs = new HashSet<InetAddress>();
        Enumeration<NetworkInterface> ifaces =
                NetworkInterface.getNetworkInterfaces();
        while (ifaces.hasMoreElements()) {
            NetworkInterface iface = ifaces.nextElement();
            Enumeration<InetAddress> addrs = iface.getInetAddresses();
            while (addrs.hasMoreElements())                
                localAddrs.add(addrs.nextElement());
        }
        return localAddrs;
    }

    public static String addressString(Collection<InetAddress> addrs) {
        String s = "";
        for (InetAddress addr : addrs) {
           // if (addr.isLoopbackAddress())
           //     continue;
            if (s.length() > 0)
                s += "!";
            s += addr.getHostAddress();
        }
        return s;
    }  
    
    public static String[] getLocalInetAdrses() {
        try {
            return addressString(InetAdrUtility.localAddresses()).split("!");
        } catch (Exception e) {
            return null;
        }
    }
    
    /** Return true if IPAdr is a valid ip adresse */
    public static boolean validIPAddress(String IPAdr) {
        StringTokenizer st = new StringTokenizer(IPAdr, ".");
        if (st.countTokens()!= 4) return false;
        while (st.hasMoreTokens()){
            try {
                int value = Integer.parseInt(st.nextToken());
                if (value < 0 || value > 255) return false;     
            } catch (NumberFormatException e) { return false;}    
        }
        return true;
    }    
}
