package edu.csus.ecs.pc2.ws;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;

/**
 * Web server (main module).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ServerModule.java 2391 2011-10-29 02:07:55Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/ui/server/ServerModule.java $
public class WebServer {

    private static final int DEAFAULT_PORT = 80;

    VersionInfo versionInfo = new VersionInfo();

    private static final String VERSION = "$Id$";

    // private static String PROPERTY_FILENAME = "www-server.properties";

    boolean exitServerFlag = false;

    protected static void printStderr(String s) {
        System.err.println(Thread.currentThread().getName() + " " + s);
    }

    // protected static void log(String s) {
    // synchronized (log) {
    // log.println(s);
    // log.flush();
    // }
    // }
    //
    // static PrintStream log = null;

    /**
     * Servers config information.
     */
    protected static Properties props = new Properties();

    // Where worker threads stand idle

    static Vector<Worker> threads = new Vector<Worker>();

    /**
     * the web server's virtual root
     */
    protected static File root;

    /* timeout on client connections */
    static int timeout = 0;

    /**
     * max # worker threads
     */
    static int workers = 5;

    /**
     * Port to listen on.
     */
    private int port = DEAFAULT_PORT;

    /*
     * Load properties from property file.
     * 
     * load www-server.properties from java.home, if file not there load from current directory, if not there set default values.
     */
    // private void loadProps() throws IOException {
    // String propFilename = System.getProperty("java.home") + File.separator + "lib" + File.separator + PROPERTY_FILENAME;
    // File f = new File(propFilename);
    //
    // if (!f.exists()) {
    // propFilename = PROPERTY_FILENAME;
    // f = new File(propFilename);
    // }
    //
    // if (f.exists()) {
    // InputStream is = new BufferedInputStream(new FileInputStream(f));
    // props.load(is);
    // is.close();
    // String r = props.getProperty("root");
    // if (r != null) {
    // root = new File(r);
    // if (!root.exists()) {
    // throw new Error(root + " doesn't exist as server root");
    // }
    // }
    // r = props.getProperty("timeout");
    // if (r != null) {
    // timeout = Integer.parseInt(r);
    // }
    // r = props.getProperty("workers");
    // if (r != null) {
    // workers = Integer.parseInt(r);
    // }
    // r = props.getProperty("log");
    // if (r != null) {
    // printStderr("opening log file: " + r);
    // log = new PrintStream(new BufferedOutputStream(new FileOutputStream(r)));
    // }
    // } else {
    // System.out.println("No properties file found at: " + propFilename);
    // }
    //
    // /*
    // * if no properties were specified, choose defaults
    // */
    // if (root == null) {
    // root = new File(System.getProperty("user.dir"));
    // }
    // if (timeout <= 1000) {
    // timeout = 5000;
    // }
    // if (workers == 25) {
    // workers = 5;
    // }
    // if (log == null) {
    // printStderr("logging to stdout");
    // log = System.out;
    // }
    // }

    // /**
    // * Print server properties.
    // *
    // */
    // private void printProps() {
    // printStderr("root=" + root);
    // printStderr("timeout=" + timeout);
    // printStderr("workers=" + workers);
    // if (log == System.out) {
    // printStderr("# Logging to stdout ");
    // printStderr("# log=");
    // } else {
    // printStderr("log=" + log);
    // }
    // }

    /**
     * Start Web Server on port.
     * 
     * @param port
     */
    public void startServer(int port) {

        try {
            // loadProps();
            // printProps();

            for (int i = 0; i < workers; ++i) {
                Worker w = new Worker();
                (new Thread(w, "worker #" + i)).start();
                threads.addElement(w);
            }

            ServerSocket ss = new ServerSocket(port);
            System.out.println("Web server on port " + port + " STARTED");
            while (!exitServerFlag) {

                Socket s = null;
                try {
                    s = ss.accept();
                } catch (IOException e) {
                    System.err.println("Could not accept connections on " + port);
                    e.printStackTrace();
                    System.exit(4);
                }

                Worker w = null;

                synchronized (threads) {
                    if (threads.isEmpty()) {
                        Worker ws = new Worker();
                        ws.setSocket(s);
                        (new Thread(ws, "additional worker")).start();
                    } else {
                        w = (Worker) threads.elementAt(0);
                        threads.removeElementAt(0);
                        w.setSocket(s);
                    }
                }
            }
            ss.close();

        } catch (java.net.BindException bindException) {
            System.err.println("Halting server - port " + port + " already in use");
        } catch (Exception e) {
            System.err.println("Halting server ");
            e.printStackTrace(System.err);
        }

    }

    // /**
    // * Start Server.
    // *
    // * @param args
    // * @throws Exception
    // */
    // public static void main(String[] args) throws Exception {
    //
    // int port = DEAFAULT_PORT;
    //
    // if (args.length > 0) {
    // if (args[0].equals("--help")) {
    // printUsage();
    // System.exit(0);
    // }
    // port = Integer.parseInt(args[0]);
    // }
    // WebServer webServer = new WebServer();
    // webServer.startServer(port);
    // }

    // private void usage() {
    // System.out.println("Simple Web Server version " + getVersionNumber());
    //
    // String[] usage = { "", "Usage: WebServer [--help] [port]", "", "where: ", "--help   - this message ", "port     - the port for the server to bind to (listen on)", "",
    // "Looks for properties files to set settings like root, log filename, etc.", "under java.home, if not found searches current directory, if not found there", "uses default settings", "" };
    //
    // for (int i = 0; i < usage.length; i++) {
    // System.out.println(usage[i]);
    // }
    //
    // System.out.println("Property filename is: " + PROPERTY_FILENAME);
    // System.out.println("Default port is: " + DEAFAULT_PORT);
    //
    // }

    // public static File getRoot() {
    // return root;
    // }

    public String getVersionNumber() {
        return VERSION;
    }

    public String getVersionString() {
        return "pc2WebServer/" + getVersionNumber() + " pc2 build " + versionInfo.getBuildNumber();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
