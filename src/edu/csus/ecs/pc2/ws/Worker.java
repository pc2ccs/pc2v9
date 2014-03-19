package edu.csus.ecs.pc2.ws;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

/**
 * Handles each HTTP request.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$

class Worker extends WebServer implements Runnable {
    final static int BUF_SIZE = 2048;

    static final byte[] EOL = { (byte) '\r', (byte) '\n' };

    /* buffer to use for requests */
    byte[] buf;

    /* Socket to client we're handling */
    private Socket s;

    private ResponseHandler responseHandler;

    /*
     * mapping of file extensions to content-types
     */
    static Hashtable<String, String> map = new Hashtable<>();

    /**
     * Static Block.
     */
    static {
        fillMap();
    }

    static void setSuffix(String k, String v) {
        map.put(k, v);
    }

    protected Worker() {
        buf = new byte[2048];
        s = null;
        System.out.println("debug 22 Starting worker");
    }
    
    public Worker(ResponseHandler responseHandler) {
        this();
        this.responseHandler = responseHandler;
    }

    synchronized void setSocket(Socket s) {
        this.s = s;
        notify();
    }

    public synchronized void run() {
        while (true) {
            if (s == null) {
                /* nothing to do */
                try {

                    wait();
                } catch (InterruptedException e) {
                    /* should not happen */
                    continue;
                }
            }
            try {
                handleClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*
             * go back in wait queue if there's fewer than numHandler connections.
             */
            s = null;
            Vector<Worker> pool = WebServer.threads;
            synchronized (pool) {
                if (pool.size() >= WebServer.workers) {
                    /* too many threads, exit this one */
                    return;
                } else {
                    pool.addElement(this);
                }
            }
        }
    }

    void handleClient() throws IOException {
        System.out.println("debug 22 handleClient");
        InputStream is = new BufferedInputStream(s.getInputStream());
        PrintStream ps = new PrintStream(s.getOutputStream());
        /*
         * we will only block in read for this many milliseconds before we fail with java.io.InterruptedIOException, at which point we will abandon the connection.
         */
        s.setSoTimeout(WebServer.timeout);

        /* zero out the buffer from last time */
        for (int i = 0; i < buf.length; i++) {
            buf[i] = 0;
        }

        try {
            /*
             * We only support HTTP GET/HEAD, and don't support any fancy HTTP options, so we're only interested really in the first line.
             */
            int nread = 0, r = 0;

            outerloop: while (nread < BUF_SIZE) {
                r = is.read(buf, nread, BUF_SIZE - nread);
                if (r == -1) {
                    /* EOF */
                    return;
                }
                int i = nread;
                nread += r;
                for (; i < nread; i++) {
                    if (buf[i] == (byte) '\n' || buf[i] == (byte) '\r') {
                        break outerloop;
                    }
                }
            }

            /* are we doing a GET or just a HEAD */
            boolean doingGet;
            /* beginning of file name */
            int index;

            String httpCommand = new String(buf);
            
            System.out.println("debug 22 http <"+httpCommand+"> http end");

            if (httpCommand.startsWith("GET ")) {
                // if (buf[0] == (byte) 'G' && buf[1] == (byte) 'E' && buf[2] == (byte) 'T' && buf[3] == (byte) ' ') {
                doingGet = true;
                index = 4;
            } else if (httpCommand.startsWith("HEAD ")) {
                // } else if (buf[0] == (byte) 'H' && buf[1] == (byte) 'E' && buf[2] == (byte) 'A' && buf[3] == (byte) 'D'
                // && buf[4] == (byte) ' ') {
                doingGet = false;
                index = 5;
            } else if (httpCommand.startsWith("PUT ")) {
                doingGet = true; // a white lie
                index = 4;
            } else {
                /* we don't support this method */
                ps.print("HTTP/1.0 " + HttpConstants.HTTP_BAD_METHOD + " unsupported method type: ");
                ps.write(buf, 0, 5);
                ps.write(EOL);
                ps.flush();
                s.close();
                return;
            }

            int i = 0;
            for (i = index; i < nread; i++) {
                if (buf[i] == (byte) ' ') {
                    break;
                }
            }
            
            // String fname = (new String(buf, 0, index, i - index)).replace('/', File.separatorChar);
//            String fname = new String(buf).substring(index, i).replace('/', File.separatorChar);
//            if (fname.startsWith(File.separator)) {
//                fname = fname.substring(1);
//            }
            
//            File targ = new File(WebServer.getRoot(), fname);
//            if (targ.isDirectory()) {
//                File ind = new File(targ, "index.html");
//                if (ind.exists()) {
//                    targ = ind;
//                }
//            }
//            
//            
//            boolean OK = printHeaders(targ, ps);
//            if (doingGet) {
//                if (OK) {
//                    sendFile(targ, ps);
//                } else {
//                    send404(targ, ps);
//                }
//            }
            
            boolean OK = printHeaders(ps);
            if (doingGet) {
                if (OK) {
                    postResponse (httpCommand, ps);
                } else {
                    send404(ps);
                }
            }
        } finally {
            s.close();
        }
    }

    private void postResponse(String httpCommand, PrintStream ps) throws IOException {
        
        ps.write(EOL);

        String [] requestParts = parseHttpCommand (httpCommand);
        
        String path = requestParts[1];
        Map<String, String> parameters = mapParams (requestParts[2]);
        try {
            String response = responseHandler.getResponse(path, parameters);
            ps.println(response);
        } catch (Exception e) {
            try {
                sendErrorResponse(ps, e);
            } catch (Exception e2) {
                e.printStackTrace(System.err);
            }
        }
        
//        ps.println("http: "+httpCommand);
//        ps.println(EOL);
    }
    
    void sendErrorResponse(PrintStream ps,Exception e) throws IOException {
        ps.write(EOL);
        ps.write(EOL);
        ps.println("<h3><b>Internal Error</b><P>");
        if (e != null) {
            ps.println(e.getLocalizedMessage());
            e.printStackTrace(System.err);
        }
    }

    /**
     * 
     * <pre>
     * [0] - command
     * [1] - path
     * [2] - arguments
     * 
     * @param httpCommand
     * @return parts of html 
     */
    private String[] parseHttpCommand(String httpCommand) {
        
        // limit this to 2 we can get to the content  
        String [] fields = httpCommand.split(" ", 2);
        
        String path = fields[1];
        String parameters  = null;
        
        int questIndex = path.indexOf('?');
        if (questIndex > -1){
            // this only does the GET /starttime?{"absolute": foo
            int endSpace = path.indexOf(' ');
            try {
                parameters = URLDecoder.decode(path.substring(questIndex+1, endSpace), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            path = path.substring(0,questIndex);
        } else if (fields[0].startsWith("PUT")) {
            // this handles the PUT
            int httpIndex = path.indexOf("HTTP");
            // start by ignoring rest of headers
            int bodyIndex = path.indexOf("\r\n\r\n");
            // end when we see the 1st }
            int curlyIndex = path.indexOf('}');
            // this is likely a "Content-Type: application/x-www-form-urlencoded"
            // NOTE we are in trouble if this a base64 object
            try {
                // do not include the CRLFCRLF, but do include the }
                parameters = URLDecoder.decode(path.substring(bodyIndex+4,curlyIndex+1),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // do not include the space before HTTP
            path = path.substring(0, httpIndex-1 );
        }
        
        String [] outFields = { //
                fields[0],
                path,
                parameters
        };

        return outFields;
    }

    /**
     * Maps the http parameters.
     * 
     * If there is no value then 
     * @param string
     * @return
     */
    private Map<String, String> mapParams(String string) {
        JsonParserFactory factory=JsonParserFactory.getInstance();
        JSONParser parser=factory.newJsonParser();
        @SuppressWarnings("unchecked")
        Map<String, String> map = parser.parseJson(string);

        return map;
    }

    /**
     * Print HTML header text.
     * 
     * @param targ
     * @param ps
     * @return
     * @throws IOException
     */
//    boolean printHeaders(File targ, PrintStream ps) throws IOException {
//        boolean ret = false;
////        int rCode = 0;
//        if (!targ.exists()) {
////            rCode = HttpConstants.HTTP_NOT_FOUND;
//            ps.print("HTTP/1.0 " + HttpConstants.HTTP_NOT_FOUND + " not found");
//            ps.write(EOL);
//            ret = false;
//        } else {
////            rCode = HttpConstants.HTTP_OK;
//            ps.print("HTTP/1.0 " + HttpConstants.HTTP_OK + " OK");
//            ps.write(EOL);
//            ret = true;
//        }
////        log("From " + s.getInetAddress().getHostAddress() + ": GET " + targ.getAbsolutePath() + "-->" + rCode);
//        ps.print("Server: Simple java");
//        ps.write(EOL);
//        ps.print("Date: " + (new Date()));
//        ps.write(EOL);
//        if (ret) {
//            if (!targ.isDirectory()) {
//                ps.print("Content-length: " + targ.length());
//                ps.write(EOL);
//                ps.print("Last Modified: " + (new Date(targ.lastModified())));
//                ps.write(EOL);
//                String name = targ.getName();
//                int ind = name.lastIndexOf('.');
//                String ct = null;
//                if (ind > 0) {
//                    ct = (String) map.get(name.substring(ind));
//                }
//                if (ct == null) {
//                    ct = "unknown/unknown";
//                }
//                ps.print("Content-type: " + ct);
//                ps.write(EOL);
//            } else {
//                ps.print("Content-type: text/html");
//                ps.write(EOL);
//            }
//        }
//        return ret;
//    }
    
    boolean printHeaders(PrintStream ps) throws IOException {
        boolean ret = false;
//        System.out.println("debug 22 HTTP/1.0 " + HttpConstants.HTTP_OK + " OK");
        ps.print("HTTP/1.0 " + HttpConstants.HTTP_OK + " OK");
        ps.write(EOL);
        ret = true;
        if (ret) {
            ps.print("Content-type: text/html");
            ps.write(EOL);
        }
        return ret;
    }

    /**
     * Show 404 to users.
     * 
     * @param targ
     * @param ps
     * @throws IOException
     */
    void send404(File targ, PrintStream ps) throws IOException {
        ps.write(EOL);
        ps.write(EOL);
        ps.println("<h3><b>404 Error</b><P>Resource not found ");
        if (targ != null) {
            ps.write(EOL);
            ps.println("File: '" + targ.getAbsolutePath() + "'");
        }
    }
    
    void send404(PrintStream ps) throws IOException {
        ps.write(EOL);
        ps.write(EOL);
        ps.println("<h3><b>404 Error</b><P>Nothing found that matches stuff. ");
    }

    /**
     * send file to browser (via PrintStream).
     * 
     * @param targ
     * @param ps
     * @throws IOException
     */
//    void sendFile(File targ, PrintStream ps) throws IOException {
//        InputStream is = null;
//        ps.write(EOL);
//
//        try {
//
//            if (targ.isDirectory()) {
//
//                File[] files = targ.listFiles();
//                listFilesInDirectory(ps, targ, files);
//            } else {
//                is = new FileInputStream(targ.getAbsolutePath());
//                int n;
//                while ((n = is.read(buf)) > 0) {
//                    ps.write(buf, 0, n);
//                }
//            }
//        } finally {
//            if (is != null) {
//                is.close();
//            }
//        }
//
//    }

//    private String href(String url, String title) {
//        return "<A HREF=\"" + url + "\">" + title + "</A>";
//    }

//    private String relativePath(File file) {
//        return file.getAbsolutePath().substring(WebServer.getRoot().getAbsolutePath().length());
//    }

    /**
     * Returns pretty string for input number of bytes.
     * 
     * @param numberBytes
     * @return
     */
//    private String sizeString(long numberBytes) {
//
//        if (numberBytes < 1000) {
//            return numberBytes + "";
//        } else if (numberBytes < 1000000L) {
//            return (numberBytes / 1000) + "K";
//        } else {
//            return (numberBytes / 1000000) + "M";
//        }
//    }

    /**
     * List files from directory in HTML, similar to Apache format.
     * 
     * @param ps
     * @param directory
     * @param files
     */
//    private void listFilesInDirectory(PrintStream ps, File directory, File[] files) {
//
//        try {
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
//
//            // Relative path
//            String thePath = relativePath(directory);
//            ps.println("<title>Index of " + thePath + "</title>");
//            ps.println("<h1>Index of " + thePath + "</h1>");
//
//            ps.println("<pre>");
//            // ps.println("<title>Index of "+directory.getCanonicalPath()+"<br>");
//
//            outputLine(ps, "Name", "Last Modified", "Size");
//            ps.print("<hr>");
//
//            for (int i = 0; i < files.length; i++) {
//                String filename = files[i].getName();
//                String path = relativePath(files[i]);
//
//                // replace \ with /
//                // String pa = path.replaceAll("[\\\\]", "/");
//
//                String pa = path;
//                while (pa.indexOf('\\') > -1) {
//                    pa = pa.replace('\\', ' ');
//                }
//
//                Date modDate = new Date(files[i].lastModified());
//
//                String namePad = leftPad(" ", 22 - filename.length());
//                outputLine(ps, href(pa, filename) + namePad, simpleDateFormat.format(modDate), sizeString(files[i].length()));
//            }
//            ps.println("</pre><hr>");
//
//            ps.println(WebServer.getVersionString());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//    }

//    private void outputLine(PrintStream ps, String name, String dateString, String sizeString) {
//
//        ps.print(leftPad(name, 22));
//        ps.print(leftPad(dateString, 20));
//        ps.print(" " + sizeString);
//        ps.println();
//
//    }

    public String leftPad(String string, int minLength) {
        if (string.length() >= minLength) {
            return string;
        } else {
            int numPad = minLength - string.length();
            StringBuffer stringBuffer = new StringBuffer(string);
            for (int i = 0; i < numPad; i++) {
                stringBuffer.append(' ');
            }
            // char [] fillArray = new char[numPad];
            // Arrays.fill(fillArray,' ');
            // return string + new String(fillArray);
            return new String(stringBuffer);
        }
    }

    static void fillMap() {
        setSuffix("", "content/unknown");
        setSuffix(".uu", "application/octet-stream");
        setSuffix(".exe", "application/octet-stream");
        setSuffix(".ps", "application/postscript");
        setSuffix(".zip", "application/zip");
        setSuffix(".sh", "application/x-shar");
        setSuffix(".tar", "application/x-tar");
        setSuffix(".snd", "audio/basic");
        setSuffix(".au", "audio/basic");
        setSuffix(".wav", "audio/x-wav");
        setSuffix(".gif", "image/gif");
        setSuffix(".jpg", "image/jpeg");
        setSuffix(".jpeg", "image/jpeg");
        setSuffix(".htm", "text/html");
        setSuffix(".html", "text/html");
        setSuffix(".text", "text/plain");
        setSuffix(".c", "text/plain");
        setSuffix(".cc", "text/plain");
        setSuffix(".c++", "text/plain");
        setSuffix(".h", "text/plain");
        setSuffix(".pl", "text/plain");
        setSuffix(".txt", "text/plain");
        setSuffix(".java", "text/plain");
    }

}
