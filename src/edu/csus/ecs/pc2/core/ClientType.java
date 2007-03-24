package edu.csus.ecs.pc2.core;

/**
 * PC<sup>2</sup> client types.
 *
 * @author pc2@ecs.csus.edu
 */
// $HeadURL$
public class ClientType {

    public static final String SVN_ID = "$Id$";

    /**
     * Client Types.
     * 
     * These are the kinds of accounts/clients in PC<sup>2</sup>.
     * @author pc2@ecs.csus.edu
     */
    
    public enum Type {
        /**
         * Unset or Unknown.
         */
        UNKNOWN,
        /**
         * All. (used for filtering and is not an actual file type)
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
         * Other or Custom.
         */
        OTHER
    }

}
