package edu.csus.ecs.pc2.api;

/**
 * This interface describes the PC<sup>2</sup> API view of a contest <I>Language</i>.
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface ILanguage {
    /**
     * Get the name for this language as configured by the Contest Administrator.
     * 
     * @return A String containing the name of the language.
     */
    String getName();

    /**
     * Check whether this Language is the same as some other Language.
     * <P>
     * Determination of whether two Languages are equal is based on whether they refer to the
     * same Language as originally created in PC<sup>2</sup> by the Contest Administrator.  
     * Note in particular that subsequent changes to a language definition
     * made by the Contest Administrator (for example,
     * changes to the language name, invocation command, etc.) do <I>not</i> affect the result of the
     * <code>equals()</code> method; if this Language refers to the same Language as the one indicated by the 
     * specified parameter, this method will return true regardless of whether the internal contents of the two
     * Language objects is identical or not.
     * 
     * @param obj the Language which is to be compared with this Language for equality.
     * @return True if the specified object refers to the same Language as this Language
     *          (regardless of the actual content of the two Languages).
     */
    boolean equals(Object obj);

    /**
     * Get the hashcode associated with this client.
     * @return An integer hashcode for this object.
     */
    int hashCode();
}
