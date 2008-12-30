package edu.csus.ecs.pc2.api.implementation;

import java.util.Vector;

import edu.csus.ecs.pc2.api.listener.IConnectionEventListener;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.ConnectionEvent;
import edu.csus.ecs.pc2.core.model.IConnectionListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Connection Listener implementation. 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ConnectionEventListenerList {

    private Vector<IConnectionEventListener> listenerList = new Vector<IConnectionEventListener>();

    private IInternalContest contest = null;
    
    private IInternalController controller = null;

    private void fireConnectionListener(ConnectionEvent runEvent) {
        for (int i = 0; i < listenerList.size(); i++) {

//            IIConnectionEventListener run = new ConnectionImplementation(runEvent.getConnection(), contest, controller);
//          IConnectionEventListener connectionEventListener = new ConnectionImplementation(runEvent.getConnection(), contest, controller);

            // TODO code
            System.out.println(i); // debug
            
            
//            switch (runEvent.getAction()) {
//            case ADDED:
//                listenerList.elementAt(i).connectionDropped(connectionEvent);
//                break;
//
//            case DELETED:
//                listenerList.elementAt(i).runRemoved(run);
//                break;
//
//   
//            default:
//                break;
//            }
        }
    }

    public void addConnectionListener(IConnectionEventListener runEventListener) {
        listenerList.add(runEventListener);
    }

    public IInternalContest getContest() {
        return contest;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
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
            // TODO Auto-generated method stub
            
        }

        public void connectionDropped(ConnectionEvent connectionEvent) {
            // TODO Auto-generated method stub
            
        }
    }
}
