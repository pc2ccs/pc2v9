package edu.csus.ecs.pc2.core.transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;

/**
 * Test for Event Feed Server.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeedServerTest extends TestCase {

    /**
     * Read from socket and write to stdout.
     * @param socket
     */
    void readAndPrintSocketInput(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String str;
            while ((str = reader.readLine()) != null) {
                System.out.println(str);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Dump socket.
     */
    void dumpSocketInput () {
        try {
            int port = 4545;
            Socket socket = new Socket("localhost", port);
            
            System.out.println("Reading from locahost "+ port);
            
            readAndPrintSocketInput(socket);
            System.out.println("*EOF* socket read locahost "+ port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    void startServer()  {
        
        try {
            int port = 5555;

            EventFeedServer sock = new EventFeedServer();

            SampleContest sample = new SampleContest();

            IInternalContest contest = sample.createContest(1, 1, 12, 6, true);
            sock.startSocketListener(port, contest);
    
        } catch (Exception e) {
            e.printStackTrace();
        }
            }

    public static void main(String[] args) throws IOException {
        new EventFeedServerTest().dumpSocketInput();
    }

}
