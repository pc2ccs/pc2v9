package edu.csus.ecs.pc2.api.implementation;

import java.util.Vector;

import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.IClarificationEventListener;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.ClarificationEvent;
import edu.csus.ecs.pc2.core.model.IClarificationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Clarification Listener implementation. 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ClarificationListenerList {

    private Vector<IClarificationEventListener> listenerList = new Vector<IClarificationEventListener>();

    private IInternalContest contest = null;
    
    private IInternalController controller = null;

    private void fireClarificationListener(ClarificationEvent clarificationEvent) {
        for (int i = 0; i < listenerList.size(); i++) {

            IClarification clarification = new ClarificationImplementation(clarificationEvent.getClarification(), contest, controller);

            switch (clarificationEvent.getAction()) {
            case ADDED:
                listenerList.elementAt(i).clarificationAdded(clarification);
                break;

            case DELETED:
                listenerList.elementAt(i).clarificationRemoved(clarification);
                break;
                
            case CHECKEDOUT_REANSWER_CLARIFICATION:
            case CHANGED:
                listenerList.elementAt(i).clarificationUpdated(clarification);
                break;
                
            case ANSWERED_CLARIFICATION:
                listenerList.elementAt(i).clarificationAnswered(clarification);
                break;
                
            case CHECKEDOUT_CLARIFICATION:
            case CLARIFICATION_AVIALABLE:
            case CLARIFICATION_REVOKED:
            case CLARIFICATION_HELD:
            case CLARIFICATION_NOT_AVAILABLE:
                
                break;
            default:
                break;
            }
        }
    }

    public void addClarificationListener(IClarificationEventListener clarificationEventListener) {
        listenerList.add(clarificationEventListener);
    }

    public IInternalContest getContest() {
        return contest;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        contest.addClarificationListener(new Listener());
    }

    public void removeClarificationListener(IClarificationEventListener clarificationEventListener) {
        listenerList.remove(clarificationEventListener);

    }

    /**
     * Clarification listener implementation.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    class Listener implements IClarificationListener {

        public void clarificationAdded(ClarificationEvent event) {
            fireClarificationListener(event);
        }

        public void clarificationChanged(ClarificationEvent event) {
            fireClarificationListener(event);
        }

        public void clarificationRemoved(ClarificationEvent event) {
            fireClarificationListener(event);
        }
    }
}
