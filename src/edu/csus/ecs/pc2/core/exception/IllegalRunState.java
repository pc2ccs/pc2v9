/**
 * 
 */
package edu.csus.ecs.pc2.core.exception;

/**
 * Signals that a Run is in an illegal state.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class IllegalRunState extends IllegalContestState {

    /**
     * 
     */
    private static final long serialVersionUID = 4354597345499633383L;

    public IllegalRunState(String message){
        super(message);
    }
}
