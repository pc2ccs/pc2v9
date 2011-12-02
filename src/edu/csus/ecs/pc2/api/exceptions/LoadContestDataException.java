package edu.csus.ecs.pc2.api.exceptions;

/**
 * Exception loading contest data (contest.yaml).
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class LoadContestDataException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -1464326705379248277L;

    public LoadContestDataException(String string) {
        super(string);
    }

}
