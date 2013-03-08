package edu.csus.ecs.pc2.core.transport;

import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * A service which will write events to the input socket.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
class FeederThread implements Runnable {

    private boolean debugFlag = true;

    private ServerSocket server;

    private EventFeeder eventFeeder = null;

    /**
     * Is thread running?. Set to false to halt thread.
     */
    private boolean running = true;

    private IInternalContest contest;

    private boolean filteredFeed = false;

    public FeederThread(ServerSocket server, IInternalContest contest, boolean filteredFeed) {
        super();
        this.server = server;
        this.contest = contest;
        this.filteredFeed = filteredFeed;
    }
    
    public IInternalContest getContest() {
        return contest;
    }

    public void run() {
        
        while (running){

            try {
                Socket connection = server.accept();
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());

                if (filteredFeed){
                    System.out.println("debug 22 - starting a filtered feed");
                    // freeze time 1 hour before end of contest.
                    long freezeTimeMins = (getContest().getContestTime().getContestLengthSecs() / Constants.SECONDS_PER_MINUTE) - (Constants.MINUTES_PER_HOUR);
                    eventFeeder = new EventFeeder(contest, out, freezeTimeMins);
                } else {
                    System.out.println("debug 22 - starting a un-filtered feed");
                    // no freeze time
                    eventFeeder = new EventFeeder(contest, out, 0);
                }
                new Thread(eventFeeder).start();

                if (debugFlag) {
                    System.out.println("Opened and sent event feed. filtered="+filteredFeed);
                }
            } catch (SocketException ex) {
                running = false; // NOP, needed to avoid CC warning.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (debugFlag) {
            System.out.println("FeederThread done");
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void halt() {
        running = false;
        if (eventFeeder != null) {
            eventFeeder.halt();
        }
    }
}
