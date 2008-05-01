package jrdesktop.server.rmi.socketFactory;

import java.io.*;
import java.net.*;
import java.rmi.server.*;
import javax.net.SocketFactory;
import javax.net.ssl.*;

public class clientSocketFactory
	implements RMIClientSocketFactory, Serializable {

    public Socket createSocket(String host, int port) throws IOException {
        SocketFactory factory =
		(SocketFactory) SocketFactory.getDefault();
	    Socket socket = (Socket)factory.createSocket(host, port);
	    return socket;
    }
}
