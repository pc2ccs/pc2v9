package edu.csus.ecs.pc2.shadow;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import edu.csus.ecs.pc2.util.HTTPSSecurity;

public class RemoteContestAPIAdapter implements IRemoteContestAPIAdapter {
    
    URL remoteURL;
    String login;
    String password;
    
    /**
     * constructs a RemoteRunMonitor with the specified values.
     * 
     * @param remoteURL the URL to the remote CCS
     * @param login the login (account) on the remote CCS
     * @param password the password to the remote CCS account
     */
    public RemoteContestAPIAdapter(URL remoteURL, String login, String password){
        this.remoteURL = remoteURL;
        this.login = login;
        this.password = password;
    }

    @Override
    public boolean testConnection() {
        try {
            HttpURLConnection conn = createConnection(remoteURL);
            return conn != null;
        } catch (Exception e) {
            ; // ignore exception, return false
        } 
        
        return false;
    }
    

    protected URL getChildURL(String path) {
        if (path == null || path.isEmpty())
            return remoteURL;

        // check for root url
        if (path.startsWith("http"))
            try {
                return new URL(path);
            } catch (Exception e) {
                return null;
            }

        if (path.startsWith("/"))
            try {
                return new URL(remoteURL, path);
            } catch (Exception e) {
                return null;
            }

        String extForm = remoteURL.toExternalForm();
        try {
            return new URL(extForm + "/" + path);
        } catch (Exception e) {
            return remoteURL;
        }
    }

    
    protected HttpURLConnection createConnection(String path) throws IOException {
        return createConnection(getChildURL(path));
    }
    
    
    protected  HttpURLConnection createConnection(URL url2) throws IOException {
        try {
            return HTTPSSecurity.createConnection(url2, login, password);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Connection error", e);
        }
    }
    
    private InputStream connect(String path) throws IOException {
        try {
            HttpURLConnection conn = createConnection(path);
            conn.setReadTimeout(130000);

            int status = conn.getResponseCode();
            if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER) {
                conn = createConnection(new URL(conn.getHeaderField("Location")));
            } else if (status == HttpURLConnection.HTTP_UNAUTHORIZED)
                throw new IOException("Not authorized (HTTP response code 401)");
            else if (status == HttpURLConnection.HTTP_BAD_REQUEST)
                throw new IOException("Bad request (HTTP response code 400)");

            return conn.getInputStream();
        } catch (ConnectException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Connection error", e);
        }
    }
    
    /**
     * Open input stream for event feed.
     * 
     * @param url
     * @param user
     * @param password
     * @return
     * @throws IOException
     */
    private InputStream getHTTPInputStream(String url, String user, String password) throws IOException {
        HttpURLConnection conn = HTTPSSecurity.createConnection(new URL(url), user, password);
        conn.setReadTimeout(15 * 1000); // 15s timeout
        return new BufferedInputStream(conn.getInputStream());
    }

    @Override
    public RemoteContestConfiguration getRemoteContestConfiguration() {
        // TODO write code
        return null;
    }

    @Override
    public String getRemoteJSON(String endpoint) {
        
        String url = remoteURL.toString() + endpoint;
        try {
            HttpURLConnection conn = createConnection(url);
            return toString(conn.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
    }

    private String toString(InputStream inputStream) throws IOException {
        
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int result = bufferedInputStream.read();
        while(result != -1) {
            byteArrayOutputStream.write((byte) result);
            result = bufferedInputStream.read();
        }
        return byteArrayOutputStream.toString();
    }

    public static void main(String[] args) throws MalformedURLException {
    
        String addr = "Https://localhost:50443/submission_files?id=1";
        URL url = new URL(addr);
        RemoteContestAPIAdapter ad = new RemoteContestAPIAdapter(url, "admin", "admin");

        String s = ad.getRemoteJSON("");
        System.out.println("s = " + s);
    }
    
}
