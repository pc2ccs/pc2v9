// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.net.URL;

/**
 * This class sets up monitoring (listening to) a remote CCS CLICS Contest API Event Feed.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class RemoteRunMonitorObsolete {

    private IRemoteContestAPIAdapter remoteContestAPIAdapter;
    private URL remoteURL;
    private String login;
    private String password;
    private RemoteRunSubmitter submitter;
    
    Thread listenerThread = null;
    
    boolean listening;
    private RemoteEventFeedListenerObsolete listener;


    /**
     * Constructs a RemoteRunMonitor with the specified values.
     * 
     * @param remoteContestAPIAdapter an adapter for accessing the remote contest API
     * @param remoteURL the URL to the remote CCS
     * @param login the login (account) on the remote CCS
     * @param password the password to the remote CCS account
     * @param submitter a {@link Runnable} which knows how to submit a receive remote run to PC2
     */
    public RemoteRunMonitorObsolete(IRemoteContestAPIAdapter remoteContestAPIAdapter, URL remoteURL, String login, String password, RemoteRunSubmitter submitter) {

        this.remoteContestAPIAdapter = remoteContestAPIAdapter;
        this.remoteURL = remoteURL;
        this.login = login;
        this.password = password;
        this.submitter = submitter;
    }

    public boolean startListening() {
        // TODO Bug 1625 startRunEventFeeder
        
        try {
            //construct a listener for the remote API
            listener = new RemoteEventFeedListenerObsolete(remoteContestAPIAdapter, remoteURL, login, password, submitter);
  
            //start the listener running as a thread
            listenerThread = new Thread(listener);
            listenerThread.start();
            listening = true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

    public void stopListening() {
        // TODO Bug 1625 stop RunEventFeeder
        
        if (listener != null && listenerThread != null) {
            listener.stop();
            listenerThread = null;
        }
        listening = false;
    }

    public boolean isListening() {
        return listening;
    }
}
