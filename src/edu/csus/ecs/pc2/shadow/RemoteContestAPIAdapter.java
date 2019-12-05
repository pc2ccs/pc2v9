package edu.csus.ecs.pc2.shadow;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import edu.csus.ecs.pc2.core.model.IFile;
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
        
        // TODO  code test to access API
        
        return false;
    }
    
    private InputStream getHTTPInputStream(URL url, String user, String password) throws IOException {
        HttpURLConnection conn = HTTPSSecurity.createConnection(url, user, password);
        conn.setReadTimeout(15 * 1000); // 15s timeout
        return new BufferedInputStream(conn.getInputStream());
    }

    @Override
    public RemoteContestConfiguration getRemoteContestConfiguration() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRemoteJSON(String endpoint) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public InputStream getRemoteEventFeedInputStream() {
        InputStream stream = null;
        try {
            stream = getHTTPInputStream(remoteURL, login, password);
        } catch (IOException e) {
            // TODO Need to decide how to handle this exception
            e.printStackTrace();
        }
        return stream ;
    }

    @Override
    public List<IFile> getRemoteSubmissionFiles(int submissionID) {
        // TODO Auto-generated method stub
        return null;
    }

}
