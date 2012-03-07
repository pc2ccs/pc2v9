package edu.csus.ecs.pc2.core.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import edu.csus.ecs.pc2.ui.EventFeedServerPane;

/**
 * Utilities for reading sockets.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class SocketUtilities {

    private SocketUtilities() {

    }

    public static String[] readLinesFromPort(int port) {

        try {
            Socket socket = new Socket("localhost", port);
            return readLinesFromPort(socket);
        } catch (Exception e) {
            return createStringArray("Unable to read socket: " + e.getMessage());
        }
    }

    public static String[] readLinesFromPort(Socket socket) {

        char lineFeed = (char) 10;
        try {

            InputStreamReader inputReader = new InputStreamReader(socket.getInputStream());
            ArrayList<String> list = new ArrayList<String>();
            StringBuffer buffer = new StringBuffer();

            int data = inputReader.read();
            while (data != -1) {
                char theChar = (char) data;
                if (theChar == lineFeed) {
                    list.add(buffer.toString());
                    buffer = new StringBuffer();
                }
                data = inputReader.read();
            }
            inputReader.close();

            list.add(buffer.toString());
            buffer = null;

            return (String[]) list.toArray(new String[list.size()]);

        } catch (IOException e) {
            return createStringArray("Unable to read socket: " + e.getMessage());
        }
    }

    private static String[] createStringArray(String msg) {
        String[] lines = { msg };
        return lines;
    }

    public static void readAndPrintSocketInput(Socket socket) throws IOException {
        InputStreamReader inputReader = new InputStreamReader(socket.getInputStream());

        int data = inputReader.read();
        while (data != -1) {
            char theChar = (char) data;
            System.out.print(theChar);
            data = inputReader.read();
        }
        inputReader.close();
    }

    public static void main(String[] args) {

        String host = "localhost";
        int port = EventFeedServerPane.DEFAULT_EVENT_FEED_PORT_NUMBER;

        if (args.length == 1) {
            host = args[0];
            if (host.equals("--help")) {
                System.out.println("Usage: [--help] [host [port]]");
                System.out.println("Default host is localhost, default port is " + port);
                System.exit(0);
            }
        }
        if (args.length > 1) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        try {
            System.err.println("Reading "+host+":"+port);
            
            Socket socket = new Socket(host, port);
            Arrays.toString(readLinesFromPort(socket));
        } catch (Exception e) {
            System.err.println("Exception reading socket " + e.getMessage());
        }
        System.exit(0);
    }
}
