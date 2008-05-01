package jrdesktop.server.rmi.socketFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import jrdesktop.utilities.InetAdrUtility;

public class MultihomeRMIClientSocketFactory
        implements RMIClientSocketFactory, Serializable {
    private static final long serialVersionUID = 7033753601964541325L;
    
    private final RMIClientSocketFactory factory;
    
    public MultihomeRMIClientSocketFactory(RMIClientSocketFactory wrapped) {
        System.out.println("wrapped : " + wrapped);
        this.factory = wrapped;
    }
    
    public Socket createSocket(String hostString, int port) throws IOException {
        if (hostString.equals(null)) hostString = 
                InetAdrUtility.addressString(InetAdrUtility.localAddresses());
        final String[] hosts = hostString.split("!");
        System.out.println(hosts.length + " host(s) : " + hostString);
        if (hosts.length < 2)
            return factory().createSocket(hostString, port);

        List<IOException> exceptions = new ArrayList<IOException>();
        Selector selector = Selector.open();
        for (String host : hosts) {
            System.out.println("open host : " + host);            
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_CONNECT);
            SocketAddress addr = new InetSocketAddress(host, port);
            channel.connect(addr);
        }
        SocketChannel connectedChannel = null;
        
        connect:
        while (true) {
            if (selector.keys().isEmpty()) {
                throw new IOException("Connection failed for " + hostString +
                        ": " + exceptions);
            }
            selector.select();  // you can add a timeout parameter in millseconds
            Set<SelectionKey> keys = selector.selectedKeys();
            if (keys.isEmpty()) {
                throw new IOException("Selection keys unexpectedly empty for " +
                        hostString + "[exceptions: " + exceptions + "]");
            }
            for (SelectionKey key : keys) {
                SocketChannel channel = (SocketChannel) key.channel();
                key.cancel();
                try {
                    channel.configureBlocking(true);
                    channel.finishConnect();
                    connectedChannel = channel;
                    break connect;
                } catch (IOException e) {
                    exceptions.add(e);
                }
            }
        }
        
        assert connectedChannel != null;
        
        // Close the channels that didn't connect
        for (SelectionKey key : selector.keys()) {
            Channel channel = key.channel();
            if (channel != connectedChannel)
                channel.close();
        }
        
        final Socket socket = connectedChannel.socket();
        if (factory == null && RMISocketFactory.getSocketFactory() == null)
            return socket;
        
        // We've determined that we can connect to this host but we didn't use
        // the right factory so we have to reconnect with the factory.
        String host = socket.getInetAddress().getHostAddress();
        socket.close();
        System.out.println("Chosed host : " + host);
        return factory().createSocket(host, port);
    }
    
    private RMIClientSocketFactory factory() {
        if (factory != null)
            return factory;
        RMIClientSocketFactory f = RMISocketFactory.getSocketFactory();
        if (f != null)
            return f;
        return RMISocketFactory.getDefaultSocketFactory();
    }

    // Thanks to "km" for the reminder that I need these:
    public boolean equals(Object x) {
        if (x.getClass() != this.getClass())
            return false;
        MultihomeRMIClientSocketFactory f = (MultihomeRMIClientSocketFactory) x;
        return ((factory == null) ?
                (f.factory == null) :
                (factory.equals(f.factory)));
    }

    public int hashCode() {
        int h = getClass().hashCode();
        if (factory != null)
            h += factory.hashCode();
        return h;
    }
}