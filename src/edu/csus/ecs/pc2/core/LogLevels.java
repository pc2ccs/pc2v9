package edu.csus.ecs.pc2.core;

import java.util.logging.Level;

/**
 * Log levels. 
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public enum LogLevels {

    OFF(Level.OFF), //
    SEVERE(Level.SEVERE), //
    WARNING(Level.WARNING), //
    INFO(Level.INFO), //
    CONFIG(Level.CONFIG), //
    FINE(Level.FINE), //
    FINER(Level.FINER), //
    FINEST(Level.FINEST), //
    ALL(Level.ALL); //

    private final Level level;
    
    private LogLevels(final Level level) {
        this.level = level;
    }
    
    public Level getLevel() {
        return level;
    }
}
