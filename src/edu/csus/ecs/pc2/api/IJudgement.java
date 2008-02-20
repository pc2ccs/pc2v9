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

    /**
     * Check whether this Judgement is the same as some other Judgement.
     * <P>
     * Determination of whether two Judgements are equal is based on whether they refer to the
     * same Judgement as originally created in PC<sup>2</sup> by the Contest Administrator.  
     * Note in particular that subsequent changes to a Judgement
     * made by the Contest Administrator (for example,
     * changes to the Judgement name) do <I>not</i> affect the result of the
     * <code>equals()</code> method; if this Judgement refers to the same Judgement as the one indicated by the 
     * specified parameter, this method will return true regardless of whether the internal contents of the two
     * Judgement objects is identical or not.
     * 
     * @param obj the Judgement which is to be compared with this Judgement for equality.
     * @return True if the specified object refers to the same Judgement as this Judgement
     *          (regardless of the actual content of the two Judgement objects).
     */
    boolean equals(Object obj);

    /**
     * Get the hashcode associated with this client.
     * @return An integer hashcode for this object.
     */
    int hashCode();

}
