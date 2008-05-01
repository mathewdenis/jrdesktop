package jrdesktop.server.rmi.socketFactory;

import java.io.*;
import java.net.*;
import java.rmi.server.*;
import javax.net.ssl.*;
import javax.net.*;
import javax.net.ssl.*;

public class serverSocketFactory
	implements RMIServerSocketFactory, Serializable {

    public ServerSocket createServerSocket(int port)
	throws IOException
	{ 
            ServerSocketFactory factory =  
                   (ServerSocketFactory) ServerSocketFactory.getDefault();
            ServerSocket socket = (ServerSocket) factory.createServerSocket(port);
            return socket;
	}
}