// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import edu.csus.ecs.pc2.core.StringUtilities;
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
            //create a connection
            HttpURLConnection conn = createConnection(remoteURL);
            //if we got a connection object, try connecting (merely creating a connection doesn't actually "connect")
            if (conn!=null) {
                conn.connect();
                //if we get here, the connection worked; otherwise, either an exception or a fall-through to return false occurs
                return true;
            }
        } catch (Exception e) {
            return false; // ignore exception, return false
        } 
        
        //if we get here, either the createConnection() returned null or else we got an exception 
        // which fell through the catch block
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
            HttpURLConnection conn = HTTPSSecurity.createConnection(url2, login, password);
            conn.setReadTimeout(60000); //note: it seems VERY unlikely this works; see https://github.com/pc2ccs/pc2v9/issues/286
            return conn;
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
        // passing null here will start the feed from the beginning
        return(getRemoteEventFeedInputStream(null));
    }
    
    @Override
    /**
     * {@inheritDoc}
     */
    public InputStream getRemoteEventFeedInputStream(String token) {
        
        String eventFeedURLString = remoteURL.toString();
        eventFeedURLString = appendIfMissing(eventFeedURLString, "/") +"event-feed";
        
        // Add on optional starting point token
        if(token != null && !token.isEmpty()) {
            eventFeedURLString += "?since_token=" + token;
        }
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

    /**
     * Fetches the submission files corresponding to the specified submissionID.
     * The fetch is done by constructing a URL consisting of the concatenation of
     * the current "Primary CCS URL" (the "remote URL") with the string
     * "/submissions/<submissionID>/files" and then invoking the method
     * {@link #getRemoteSubmissionFiles(URL)} with that URL, returning the result
     * of that method call.
     * 
     * Note that if the "Primary CCS URL" ends with a "/" character then this method
     * avoids adding a duplicate "/" when concatenating the "submissions" endpoint to the URL;
     * see https://github.com/pc2ccs/pc2v9/issues/528.
     * 
     * @param submissionID a String representation of the submission ID from the remote system
     * @return the result of calling {@link #getRemoteSubmissionFiles(URL)} with the constructed URL,
     *         or null if an exception is thrown during URL construction
     */
    @Override
    public List<IFile> getRemoteSubmissionFiles(String submissionID) {
        
        //define the CLICS endpoint for fetching the files associated with a submission
        String endpoint = "/submissions/" + submissionID + "/files";
        
        //get the configured Primary CCS URL
        String urlString = remoteURL.toString();
        //ensure the URL doesn't end with "/" (because we're going to add a "/" as part of the "endpoint")
        if (urlString.endsWith("/")){
            urlString = StringUtilities.removeLastChar(urlString);
        }
        
        //build the full URL to the submissions/files endpoint
        urlString = urlString + endpoint;
        
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return getRemoteSubmissionFiles(url);
    }

    /**
     * Fetches submission files from the specified URL.
     * The URL is expected to reference an endpoint which returns a zip file
     * containing the files comprising a contest submission.  
     * 
     * @param submissionFilesURL a URL where a zip file containing submitted files may be found
     * @return a List of {@link IFile}s  containing the contents of the submission files obtained 
     *          from the specified URL
     * @throws {@link RuntimeException} if an {@link IOException} occurs while connecting to the
     *          remote system at the specified URL or while reading bytes from the input stream
     *          associated with the URL
     */

    public List<IFile> getRemoteSubmissionFiles(URL submissionFilesURL) {

        try {

            //make a connection to the specified URL
            HttpURLConnection conn = createConnection(submissionFilesURL);
            
            //get the bytes (comprising a zipfile) from the specified URL's input stream
            byte[] bytes = toByteArray(conn.getInputStream());

            // Unzip the bytes/zipfile into individual IFiles
            List<IFile> files = getIFiles(bytes);
            return files;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Get files from a zipfile's bytes.
     * 
     * @param bytes bytes comprising a zip file.
     * @return list of IFiles extracted from the input bytes 
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
