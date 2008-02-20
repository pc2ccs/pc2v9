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
    
    /**
     * Check whether this Group is the same as some other Group.
     * <P>
     * Determination of whether two Groups are equal is based on whether they refer to the
     * same Group as originally created in PC<sup>2</sup> by the Contest Administrator.  
     * Note in particular that subsequent changes to a Group
     * made by the Contest Administrator (for example,
     * changes to the Group name) do <I>not</i> affect the result of the
     * <code>equals()</code> method; if this Group refers to the same Group as the one indicated by the 
     * specified parameter, this method will return true regardless of whether the internal contents of the two
     * Group objects is identical or not.
     * 
     * @param obj the Group which is to be compared with this Group for equality.
     * @return True if the specified object refers to the same Group as this Group
     *          (regardless of the actual content of the two Groups).
     */
    boolean equals(Object obj);

    /**
     * Get the hashcode associated with this Group.
     * @return An integer hashcode for this object.
     */
    int hashCode();
}
