package edu.csus.ecs.pc2.core.transport;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

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
            InputStreamReader inputReader = new InputStreamReader(socket.getInputStream());
//            BufferedReader reader = new BufferedReader(inputStReader);
            
            int data = inputReader.read();
            while(data != -1){
                char theChar = (char) data;
                System.out.print(theChar);
                data = inputReader.read();
            }
//
//            String str;
//            while ((str = reader.read()) != null) {
//                System.out.println(str);
//                System.out.flush();
//            }
//            reader.close();
            inputReader.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Dump socket.
     */
    void dumpSocketInput (int port) {
            Socket socket;
            try {
                socket = new Socket("localhost", port);
                
                System.out.println("Reading from locahost "+ port);
                
                readAndPrintSocketInput(socket);
                System.out.println("*EOF* socket read locahost "+ port);
            } catch (ConnectException e) {
                System.out.println(e.getMessage());
            } catch (UnknownHostException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
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
        new EventFeedServerTest().dumpSocketInput(4713);
    }

}
