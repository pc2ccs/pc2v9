package edu.csus.ecs.pc2.api.implementation;

import java.util.Vector;

import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.listener.IRunEventListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.RunEvent;

/**
 * Run Listener implementation. 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunListenerList {

    private Vector<IRunEventListener> listenerList = new Vector<IRunEventListener>();

    private IInternalContest contest = null;

    private void fireRunListener(RunEvent runEvent) {
        for (int i = 0; i < listenerList.size(); i++) {

            IRun run = new RunImplementation(runEvent.getRun(), contest);

            switch (runEvent.getAction()) {
            case ADDED:
                listenerList.elementAt(i).runAdded(run);
                break;

            case DELETED:
                listenerList.elementAt(i).runRemoved(run);
                break;

            case CHANGED:
                if (run.isJudged()) {
                    listenerList.elementAt(i).runJudged(run);
                } else {
                    listenerList.elementAt(i).runUpdated(run);
                }
                break;
                
            case CHECKEDOUT_REJUDGE_RUN:
            case CHECKEDOUT_RUN:
            case RUN_AVAILABLE:
            case RUN_HELD:
            case RUN_NOT_AVIALABLE:
            case RUN_REVOKED:
                break;
            default:
                break;
            }
        }
    }

    public void addRunListener(IRunEventListener runEventListener) {
        listenerList.add(runEventListener);
    }

    public IInternalContest getContest() {
        return contest;
    }

    public void setContest(IInternalContest contest) {
        this.contest = contest;
        contest.addRunListener(new Listener());
    }

    public void removeRunListener(IRunEventListener runEventListener) {
        listenerList.remove(runEventListener);

    }

    /**
     * Run listener implemenation.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    class Listener implements IRunListener {

        public void runAdded(RunEvent event) {
            fireRunListener(event);
        }

        public void runChanged(RunEvent event) {
            fireRunListener(event);
        }

        public void runRemoved(RunEvent event) {
            fireRunListener(event);
        }
    }
}
