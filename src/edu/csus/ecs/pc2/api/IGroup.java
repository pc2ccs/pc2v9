package edu.csus.ecs.pc2.api;

/**
 * This interface describes the PC<sup>2</sup> API view of a contest <I>Group</i>.
 * Groups can be used by the Contest Administrator to associate Teams together.
 * For example, all teams from a certain geographical region, or with an equivalent background
 * (say, Undergraduate vs. Graduate) can be put together in the same group.  
 * The PC<sup>2</sup> scoring algorithm implementation can then be used to compute
 * standings on a per-group basis.
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IGroup {

    /**
     * Get the name of this group.
     * 
     * @return A String containing the name of the group.
     */
    String getName();
    
    // TODO document
    boolean equals(Object obj);

    int hashCode();
}
