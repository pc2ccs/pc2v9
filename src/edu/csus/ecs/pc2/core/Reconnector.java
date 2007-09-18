package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.packet.Packet;

/**
 * Reconnection logic.
 * 
 * 
 * <P>
 * Assumption: this class is only invoked after a module has logged in.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// TODO Make this handle all connections and reconnectons ??

//$HeadURL$
public class Reconnector {

    private int maxRetry = 3;

    /**
     * Is Connection currently active?
     * <P>
     */
    private boolean connected = true;

    private String loginName = null;

    private String password = null;

    private int retryCount;

    public Reconnector(String loginName, String password) {
        super();
        this.loginName = loginName;
        this.password = password;
    }

    // required to access contest settings, like retry times and duration
    void setContestAndController(IContest inContest, IController inController) {

    }

    public void handleDisconnection(Packet packet) {

        addPacketToList(packet);

        connected = false;
        
        attemptReconnection(loginName, password);
        
        showRetryDialog();
    }

    private void attemptReconnection(String loginName2, String password2) {
        
        // TODO get maxRetry from IContest

        while (!connected && retryCount < maxRetry) {

            retryConnection();
        }
    }

    private void showRetryDialog() {
        
        // TODO display Retry dialog
        
    }

    private void addPacketToList(Packet packet) {

        // TOD add packet to list of to be sent packets

    }

    private void retryConnection() {

        // TODO fetch msecs to pause, pause for msecs

        // TODO attempt connection

        // if NOT connected increment retry count

    }
}
