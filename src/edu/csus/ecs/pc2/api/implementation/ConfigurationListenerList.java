package edu.csus.ecs.pc2.api.implementation;

import java.util.Vector;

import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.listener.ContestEvent;
import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;
import edu.csus.ecs.pc2.api.listener.ContestEvent.EventType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.ProblemEvent;

/**
 * API Configuration Listener list.
 *   
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// TODO lots of stuff
// $HeadURL$
public class ConfigurationListenerList {

    private IInternalContest contest = null;

    private Vector<IConfigurationUpdateListener> listenerList = new Vector<IConfigurationUpdateListener>();

    private void fireProblemListener(ProblemEvent problemEvent) {
        for (int i = 0; i < listenerList.size(); i++) {

            IProblem problem = new ProblemImplementation(problemEvent.getProblem().getElementId(), contest);
            ContestEvent contestEvent = new ContestEvent(EventType.PROBLEM, problem);

            switch (problemEvent.getAction()) {
            case ADDED:
                listenerList.elementAt(i).elementAdded(contestEvent);
                break;
            case DELETED:
                listenerList.elementAt(i).elementRemoved(contestEvent);
                break;
            case CHANGED:
            default:
                listenerList.elementAt(i).elementUpdated(contestEvent);
                break;
            }
        }
    }

    public void addContestUpdateConfigurationListener(IConfigurationUpdateListener contestUpdateConfigurationListener) {
        listenerList.addElement(contestUpdateConfigurationListener);
    }

    public void removeContestUpdateConfigurationListener(IConfigurationUpdateListener contestUpdateConfigurationListener) {
        listenerList.remove(contestUpdateConfigurationListener);
    }

    public void setContest(IInternalContest contest) {
        this.contest = contest;
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    class ProblemListener implements IProblemListener {

        public void problemAdded(ProblemEvent event) {
            fireProblemListener(event);
        }

        public void problemChanged(ProblemEvent event) {
            fireProblemListener(event);
        }

        public void problemRemoved(ProblemEvent event) {
            fireProblemListener(event);
        }
    }

}
