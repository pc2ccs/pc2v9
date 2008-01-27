package edu.csus.ecs.pc2.core.security;

/**
 * Security Message, handle a new one.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public interface ISecurityMessageListener {

    /**
     * A run has been added to the system.
     * <P>
     * Typically this means that a run has been submitted by a team. Though it also may be caused by a remote server sending its runs to the local server.
     * <P>
     * The run may be added on the local server or a remote server.
     * 
     * @param run
     *            the run that has changed
     */
    void newMessage (SecurityMessageEvent event);

}
