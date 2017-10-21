package edu.csus.ecs.pc2.services.core;

/**
 * Operations for event feed
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public enum EventFeedOperation {
    
    UNDEFINED("Undefined"),
    /**
     * Create.
     */
    CREATE("create"),
    
    /**
     * Update
     */
    UPDATE("update"),
    
    /**
     * Delete.
     */
    DELETE("delete");

    private final String name;
    
    EventFeedOperation(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }

}
