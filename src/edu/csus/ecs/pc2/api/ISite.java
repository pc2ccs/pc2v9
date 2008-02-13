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
    
    // TODO document
    boolean equals(Object obj);

    int hashCode();
}
