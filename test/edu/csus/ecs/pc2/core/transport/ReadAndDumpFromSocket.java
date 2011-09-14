package edu.csus.ecs.pc2.core.transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * EchoClient from Sun.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ReadAndDumpFromSocket {

    public void echoStuff(String hostname, int port) throws IOException {

        Socket echoSocket = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket(hostname, port);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            String line = in.readLine();
            while (line != null){
                System.out.println(line);
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + hostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for " + "the connection to: "+hostname+" port "+port);
            System.exit(1);
        }

        in.close();
        echoSocket.close();

    }

    public static void main(String[] args)  {
        try {
            new ReadAndDumpFromSocket().echoStuff("localhost", 5555);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
