// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.net.URL;

/**
 * This class sets up monitoring (listening to) a remote CCS CLICS Contest API Event Feed.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class RemoteRunMonitor {

    URL remoteURL;
    String login;
    String password;
    Runnable submitter;
    
    Thread listenerThread = null;
    
    boolean listening;
    private RemoteEventFeedListener listener;

    /**
     * constructs a RemoteRunMonitor with the specified values.
     * 
     * @param remoteURL the URL to the remote CCS
     * @param login the login (account) on the remote CCS
     * @param password the password to the remote CCS account
     * @param submitter a {@link Runnable} which knows how to submit a receive remote run to PC2
     */
    public RemoteRunMonitor(URL remoteURL, String login, String password, Runnable submitter) {

        this.remoteURL = remoteURL;
        this.login = login;
        this.password = password;
        this.submitter = submitter;
    }

    public void startListening() {
        // TODO Bug 1625 startRunEventFeeder
        
        try {
            //construct a listener for the remote API
            listener = new RemoteEventFeedListener(remoteURL,login,password,submitter);
  
            //start the listener running as a thread
            listenerThread = new Thread(listener);
            listenerThread.start();
            listening = true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
