package edu.csus.ecs.pc2.api;

/**
 * This interface describes the PC<sup>2</sup> API view of a contest <I>Problem</i>.
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IProblem {

    /**
     * Get the name for this problem as configured by the Contest Administrator.
     * 
     * @return A String containing the name of the problem.
     */
    String getName();
    
    String getJudgesDataFileName();
    
    byte [] getJudgesDataFileContents();

    String getJudgesAnswerFileName();
    
    byte [] getJudgesAnswerFileContents();
    
    // TODO validator methods.

}
