package edu.csus.ecs.pc2.core.list;

import java.util.Iterator;
import java.util.Vector;

import edu.csus.ecs.pc2.core.model.AvailableAJ;

/**
 * This class encapsulates a list of {@link AvailableAJ}s -- that is, it maintains a list of all Judge Clients
 * which have currently registered as "available to auto-judge run submissions".
 * 
 * @author John Clevenger, PC2 Development Team
 *
 */
public class AvailableAJList implements Iterable<AvailableAJ> {

    //class Vector is used to hold the list because, unlike ArrayList, Vector is thread-safe
    private Vector<AvailableAJ> availableAJList = new Vector<AvailableAJ>();
    
    /**
     * Constructs an (initially empty) list of {@link AvailableAJ}s.
     */
    public AvailableAJList () {
        
    }
    
    /**
     * Adds the specified {@link AvailableAJ} to the list of Available AutoJudges.
     * 
     * If the specified AvailableAJ is null, this method does nothing.
     * 
     * @param aj the {@link AvailableAJ} to be added to the list.
     */
    public void add (AvailableAJ aj) {
        if (aj != null) {
            availableAJList.add(aj);
        }
    }
    
    /**
     * Removes the specified {@link AvailableAJ} from the list of Available AutoJudges.
     * 
     * If the specified AvailableAJ is not currently in the list (as determined by its ClientId),
     * this method does nothing.
     * 
     */
    public void remove (AvailableAJ aj) {
        availableAJList.remove(aj);
    }
    
    /**
     * Returns an indication of the number of elements currently in this AvailableAJList.
     */
    public int size() {
        return availableAJList.size();
    }
    
    /**
     * Returns an Iterator over the {@link AvailableAJ}s currently in this list.
     * @return an Iterator<AvailableAJ>.
     */
    @Override
    public Iterator<AvailableAJ> iterator() {
        return availableAJList.iterator();
    }
}
