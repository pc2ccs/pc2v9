// Copyright (C) 1989-2020 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import edu.csus.ecs.pc2.core.model.IFile;
import edu.csus.ecs.pc2.core.model.IFileImpl;
import edu.csus.ecs.pc2.shadow.AbstractRemoteConfigurationObject.REMOTE_CONFIGURATION_ELEMENT;
import edu.csus.ecs.pc2.util.HTTPSSecurity;

public class RemoteContestAPIAdapter implements IRemoteContestAPIAdapter {
    
    URL remoteURL;
    String login;
    String password;
    
    /**
     * Constructs a RemoteRunMonitor with the specified values.
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
    
    //This method can be removed
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
     * @param remoteURL2
     * @param user
     * @param password
     * @return
     * @throws IOException
     */
    private InputStream getHTTPInputStream(URL remoteURL2, String user, String password) throws IOException {
        HttpURLConnection conn = HTTPSSecurity.createConnection(remoteURL2, user, password);
        conn.setReadTimeout(5 * 60 * 1000); // 5 min timeout
        return new BufferedInputStream(conn.getInputStream());
    }

    @Override
    public RemoteContestConfiguration getRemoteContestConfiguration() {
        
        Map<REMOTE_CONFIGURATION_ELEMENT, List<AbstractRemoteConfigurationObject>> remoteConfigMap = new HashMap<AbstractRemoteConfigurationObject.REMOTE_CONFIGURATION_ELEMENT, List<AbstractRemoteConfigurationObject>>();

        // TODO TODAY implement me - add mock data into RemoteContestConfiguration
        System.err.println("Write getRemoteContestConfiguration()");

        return new RemoteContestConfiguration(remoteConfigMap);
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
    
    private byte[] toByteArray(InputStream inputStream) throws IOException {
        
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int result = bufferedInputStream.read();
        while(result != -1) {
            byteArrayOutputStream.write((byte) result);
            result = bufferedInputStream.read();
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public InputStream getRemoteEventFeedInputStream() {
        
        String eventFeedURLString = remoteURL.toString();
        eventFeedURLString = appendIfMissing(eventFeedURLString, "/") +"event-feed";
        
        InputStream stream = null;
        try {
            URL url = new URL(eventFeedURLString);
            stream = getHTTPInputStream(url, login, password);
        } catch (IOException e) {
            // TODO shadow Need to decide how to handle this exception/error
            e.printStackTrace();
        }
        return stream ;
    }

    private String appendIfMissing(String s, String appendString) {
        if (!s.endsWith(appendString)){
            s += appendString;
        }
        return s;
    }

    @Override
    public List<IFile> getRemoteSubmissionFiles(String submissionID) {
        String endpoint = "/submissions/" + submissionID + "/files";
        String url = remoteURL.toString() + endpoint;
        return getRemoteSubmissionFilesNew(url);
    }

    // TODO shadow rename this method to getRemoteSubmissionFiles 
    public List<IFile> getRemoteSubmissionFilesNew(String submissionPath) {

        String fullSubmissionURL = remoteURL.toString() + submissionPath;
        if (-1 != submissionPath.indexOf(':')) {
            // has full URL with protocol
            fullSubmissionURL = submissionPath;
        }

        try {
            URL url = new URL(fullSubmissionURL);

            HttpURLConnection conn = createConnection(url);
            /**
             * Bytes fetched from endpoint
             */
            byte[] bytes = toByteArray(conn.getInputStream());

            /**
             * Convert bytes/zipfile into individual IFiles.
             */
            List<IFile> files = getIFiles(bytes);
            return files;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Get files from a zipfile's bytes.
     * 
     * @param bytes bytes from a zip file.
     * @return list of IFiles from input bytes 
     */
    private List<IFile> getIFiles(byte[] bytes) {
        
        List<IFile> files = new ArrayList<IFile>();
        
        ZipInputStream zipStream = null;
        
        try {
            zipStream = new ZipInputStream(new ByteArrayInputStream(bytes));
            ZipEntry entry = null;
            /**
             * Read each zip entry, add IFile.
             */
            while ((entry = zipStream.getNextEntry()) != null) {
                
                String entryName = entry.getName();
                
//                ByteOutputStream byteOutputStream = new ByteOutputStream();
                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                
                byte[] buffer = new byte[8096];
                int bytesRead = 0;
                while ((bytesRead = zipStream.read(buffer)) != -1)
                {
                    byteOutputStream.write(buffer, 0, bytesRead);
                }

//                String base64Data = getBase64Data(byteOutputStream.getBytes());
                String base64Data = getBase64Data(byteOutputStream.toByteArray());
                IFile iFile = new IFileImpl(entryName, base64Data);
                files.add(iFile);
                
                byteOutputStream.close();
                
                zipStream.closeEntry();
            }
            zipStream.close(); 
            
        } catch (Exception e) {
            if (zipStream != null){
                try {
                    zipStream.close();
                } catch (Exception ze) {
                    ; // problem closing stream, ignore.
                }
            }
            throw new RuntimeException(e);
        }
        
        return files;
        
    }
    
    /**
     * Encode bytes into BASE64.
     * @param data
     * @return
     */
    public String getBase64Data( byte [] bytes) {
        // TODO REFACTOR move to FileUtilities
        Base64.Encoder encoder = Base64.getEncoder();
        String base64String = encoder.encodeToString(bytes);
        return base64String;
    }



    
}
