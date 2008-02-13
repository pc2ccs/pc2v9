package edu.csus.ecs.pc2.api;

/**
 * This interface describes the PC<sup>2</sup> API view of a contest <I>Judgement</i>.
 * 
 * A <I>Judgement</i> represents one of the judgement values which the Contest Administrator has configured into the contest settings and from which a Judge may choose when assigning a result to any
 * particular submitted run.
 * 
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IJudgement {

    /**
     * Get the name for this judgement.
     * 
     * @return A String containing the name of this judgement.
     */
    String getName();

    // TODO document
    boolean equals(Object obj);

    int hashCode();

}
