// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
/**
 * 
 */
package edu.csus.ecs.pc2.core.exception;

/**
 * Signals that a contest is in an illegal state.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class IllegalContestState extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -8484343906357576436L;

    public IllegalContestState(String message){
        super(message);
    }

}
