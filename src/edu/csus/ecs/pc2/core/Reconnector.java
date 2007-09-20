package edu.csus.ecs.pc2.core;

import java.util.Random;

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
public class Reconnector implements Runnable {

    private RetryDialog retryDialog = null;
    
    private IContest contest = null;

    private int maxConnectionRetries = 3;
    
    private Random r = new Random();

    /**
     * Is Connection currently active?
     * <P>
     */
    private boolean connected = true;

    private String loginName = null;

    private String password = null;

    private int retryCount = 0;

    public Reconnector(String loginName, String password) {
        super();
        this.loginName = loginName;
        this.password = password;
        
        r.setSeed(System.nanoTime());
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
        
        connected = false;
        
        maxConnectionRetries = contest.getMaxConnectionRetries();
        
        retryCount = 0;
        
        while (!connected && retryCount < maxConnectionRetries) {

            retryConnection();
        }
    }

    private void showRetryDialog() {
        
        if (retryDialog == null){
            retryDialog = new RetryDialog();
            retryDialog.setReconnectRunnable(this);
        }
        
        retryDialog.setTitle("Disconnected from server");
        retryDialog.setMessage("Disconnected from server");
        
        retryDialog.setVisible(true);
    }

    private void addPacketToList(Packet packet) {

        // TODO add packet to list of to be sent packets

    }

    private void retryConnection() {

        // TODO fetch msecs to pause
        
        int maxMSecs = contest.getMaxRetryMSecs();
        
        try {
            int msec = r.nextInt(maxMSecs);
            System.out.println("debug msecs = "+msec);
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // TODO attempt connection

        retryCount++;
    }

    public void run() {
        retryDialog.setTitle("Attempting to reconnect...");
        retryDialog.setMessage("Attempting to reconnect to server");
        attemptReconnection (loginName, password);
    }
    
    public static void main(String[] args) {
        Reconnector reconnector = new Reconnector("login","password");
        reconnector.handleDisconnection(null);
    }
}
