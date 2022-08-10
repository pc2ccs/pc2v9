package edu.csus.ecs.pc2.core.list;

import java.util.Iterator;
import java.util.Vector;

import edu.csus.ecs.pc2.core.model.AvailableAJRun;

/**
 * This class encapsulates a list of {@link AvailableAJRuns}s -- that is, it maintains a list of runs which have 
 * been submitted and are awaiting assignment to an AutoJudge.
 * 
 * @author John Clevenger, PC2 Development Team
 *
 */
public class AvailableAJRunList implements Iterable<AvailableAJRun> {

    //class Vector is used to hold the list because, unlike ArrayList, Vector is thread-safe
    private Vector<AvailableAJRun> ajRunList = new Vector<AvailableAJRun>();
    
    /**
     * Constructs an (initially empty) list of {@link AvailableAJRun}s.
     */
    public AvailableAJRunList () {
        
    }
    
    /**
     * Adds the specified {@link AvailableAJRun} to the list of runs available for autojudging.
     * If the specified AvailableAJRun is null, this method does nothing.
     * 
     * @param ajRun the {@link AvailableAJRun} to be added to the list.
     */
    public void add (AvailableAJRun ajRun) {
        if (ajRun != null) {
            ajRunList.add(ajRun);
        }
    }
    
    /**
     * Removes the specified {@link AvailableAJRun} from the list of AvailableAJRuns.
     * If the specified AvailableAJRun is not currently in the list, this method does nothing.
     * 
     * @param ajRun the run which is to be removed from the list.
     */
    public void remove (AvailableAJRun ajRun) {
        ajRunList.remove(ajRun);
    }
    
    /**
     * Returns an indication of the number of elements currently in this AvailableAJRunList.
     */
    public int size() {
        return ajRunList.size();
    }

    /**
     * Returns an Iterator over the {@link AvailableAJRun}s currently in this list.
     * @return an Iterator<AvailableAJRun>.
     */
    @Override
    public Iterator<AvailableAJRun> iterator() {
        return ajRunList.iterator();
    }
}
