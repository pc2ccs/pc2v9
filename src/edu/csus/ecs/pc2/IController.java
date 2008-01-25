package edu.csus.ecs.pc2;

/**
 * Methods to make requests of server.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IController {

    /**
     * Logoff this user.
     * 
     * @return true if logged in, false if not logged in.
     */
    boolean logoff();

}
