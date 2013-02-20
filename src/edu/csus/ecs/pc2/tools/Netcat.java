package edu.csus.ecs.pc2.tools;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.csus.ecs.pc2.core.ParseArguments;

/**
 * A very simple version of netcat.
 * 
 * @author pc2@ecs.csue.edu
 * @version $Id$
 */
public class Netcat {
    private boolean debugFlag = false;

    private static final int DEFAULT_PORT = 4713;

    private static final String DEFAULT_HOST = "localhost";

    /**
     * Read from socket and write to stdout.
     * 
     * @param socket
     */
    void readAndPrintSocketInput(Socket socket) {
        try {
            InputStreamReader inputReader = new InputStreamReader(socket.getInputStream());
            int data = inputReader.read();
            while (data != -1) {
                char theChar = (char) data;
                System.out.print(theChar);
                data = inputReader.read();
            }
            inputReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dump socket.
     */
    void dumpSocketInput(String host, int port) {
        Socket socket;
        try {
            System.out.println("Reading from " + host + ":" + port);
            socket = new Socket(host, port);

            readAndPrintSocketInput(socket);
            System.out.println("*EOF* socket read locahost " + port);
        } catch (ConnectException e) {
            System.out.println(e);
        } catch (UnknownHostException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void testClientRunner() throws Exception {
        if (debugFlag) {
            dumpSocketInput(DEFAULT_HOST, DEFAULT_PORT);
        }
    }

    public static void main(String[] args) throws IOException {

        ParseArguments pa = new ParseArguments(args);

        if (pa.isOptPresent("-h") || pa.isOptPresent("--help")) {
            System.out.println("Usage: Netcat [--help] [hostname [port]]\n" + //
                    "\n" + //
                    "Default port = " + DEFAULT_PORT + "\n" + //
                    "\n" + //
                    "$Id$\n");
        } else if (args.length >= 2) {
            // host port
            String hostName = args[0];
            if ("-".equals(hostName)) {
                hostName = DEFAULT_HOST;
            }
            int portNumber = Integer.parseInt(args[1]);
            new Netcat().dumpSocketInput(hostName, portNumber);
        } else if (pa.getArgCount() == 1) {
            // host port
            String hostName = pa.getArg(0);
            int portNumber = DEFAULT_PORT;
            new Netcat().dumpSocketInput(hostName, portNumber);
        } else {
            new Netcat().dumpSocketInput(DEFAULT_HOST, DEFAULT_PORT);
        }
    }
}
