package edu.csus.ecs.pc2.core.model;

/**
 * PC<sup>2</sup> client types.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public class ClientType {

    /**
     * Client Types.
     * 
     * These are the kinds of accounts/clients in PC<sup>2</sup>.
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    public enum Type {
        /**
         * Unset or Unknown.
         */
        UNKNOWN,
        /**
         * All accounts.
         */
        ALL,
        /**
         * Server type.
         */
        SERVER,
        /**
         * Admin or root accounts.
         */
        ADMINISTRATOR,
        /**
         * Team.
         */
        TEAM,
        /**
         * Judge.
         */
        JUDGE,
        /**
         * Scoreboard.
         */
        SCOREBOARD,
        /**
         * Spectator.
         */
        SPECTATOR,
        /**
         * Auto Judger.
         */
        EXECUTOR,
        /**
         * Other or Custom.
         */
        OTHER
    }

}
