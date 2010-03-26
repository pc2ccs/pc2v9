package edu.csus.ecs.pc2.core.log;

/**
 * Listener for all StreamListeners.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IStreamListener {

    void messageAdded(String inString);
}
