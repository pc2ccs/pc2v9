package edu.csus.ecs.pc2.api.implementation;

import java.util.Vector;

import edu.csus.ecs.pc2.api.listener.IConnectionEventListener;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.ConnectionEvent;
import edu.csus.ecs.pc2.core.model.IConnectionListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Connection Listener implementation.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ConnectionEventListenerList {

    private Vector<IConnectionEventListener> listenerList = new Vector<IConnectionEventListener>();

    private IInternalContest contest = null;

    @SuppressWarnings("unused")
    private IInternalController controller = null;

    private Contest iContest = null;

    private void fireConnectionListener(ConnectionEvent connectionEvent) {

        for (int i = 0; i < listenerList.size(); i++) {

            switch (connectionEvent.getAction()) {
                case DROPPED:
                    listenerList.elementAt(i).connectionDropped();
                    break;
                case ESTABLISHED:
                    break; // not used
                default:
                    break; // not used
            }
        }
    }

    public void addConnectionListener(IConnectionEventListener runEventListener) {
        listenerList.add(runEventListener);
    }

    public IInternalContest getContest() {
        return contest;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController, Contest contest2) {
        this.contest = inContest;
        this.controller = inController;
        this.iContest  = contest2;
        contest.addConnectionListener(new Listener());
    }

    public void removeConnectionListener(IConnectionEventListener runEventListener) {
        listenerList.remove(runEventListener);

    }

    /**
     * Connection listener implemenation.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    class Listener implements IConnectionListener {

        public void connectionEstablished(ConnectionEvent connectionEvent) {
            /**
             * Not used by API at this time.
             */
        }

        public void connectionDropped(ConnectionEvent connectionEvent) {
            iContest.setLoggedIn(false);
            fireConnectionListener(connectionEvent);

        }

        public void connectionRefreshAll(ConnectionEvent connectionEvent) {
            // FIXME implement API code
        }
    }
}
