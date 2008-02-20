package edu.csus.ecs.pc2.api;

/**
 * This interface describes the PC<sup>2</sup> API view of a contest <I>Site</i>.
 * Each site in a contest has a unique number (which never changes), and a unique name 
 * (which can be changed by the Contest Administrator).
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 *  
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface ISite {

    /**
     * Get the name of this site.
     * 
     * @return A String containing the site name.
     */
    String getName();
    
    /**
     * Get the number of this site.
     * 
     * @return An integer giving the site number.
     */
    int getNumber();
    
    /**
     * Check whether this Site is the same as some other Site.
     * <P>
     * Determination of whether two Sites are equal is based on whether they refer to the
     * same Site as originally created in PC<sup>2</sup> by the Contest Administrator.  
     * Note in particular that subsequent changes to a Site
     * made by the Contest Administrator (for example,
     * changes to the Site name) do <I>not</i> affect the result of the
     * <code>equals()</code> method; if this Site refers to the same Site as the one indicated by the 
     * specified parameter, this method will return true regardless of whether the internal contents of the two
     * Site objects is identical or not.
     * 
     * @param obj the Site which is to be compared with this Site for equality.
     * @return True if the specified object refers to the same Site as this Site
     *          (regardless of the actual content of the two Sites).
     */
    boolean equals(Object obj);

    /**
     * Get the hashcode associated with this client.
     * @return An integer hashcode for this object.
     */
    int hashCode();
}
