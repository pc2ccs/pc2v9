package edu.csus.ecs.pc2.core.transport;

/**
 * Transport Manager Exception Class. Used for all exceptions going up to the application
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 * 
 */
// $HeadURL$
public class TransportException extends Exception {
    public static final String SVN_ID = "$Id$";

    /**
     * Transport related Exceptions
     * 
     */
    private static final long serialVersionUID = -6333266176304198528L;
    
    /**
     * Type of TransportExceptions.
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    public enum Type {

        /**
         * General Error
         */
        ERROR,
        /**
         * Connection reset
         */
        CONNECTION_RESET,
        /**
         * On intial handshake
         */
        INITIALIZATION, 
        /**
         * While sending a object.
         */
        SEND, 
        /**
         * While receiving an object.
         */
        RECEIVE, 
        /**
         * On connection dropped
         */
        DROPPED,
    }

    private Type type = Type.ERROR;

    // TODO re-code do not use CONNECTION_RESET, assuming that this English phrase will not change
    // is a potential bug waiting to happen.
    
    /**
     * String that indicates that a connection has been dropped
     */
    public static final String CONNECTION_RESET = "connection reset";


    public TransportException() {
        super();
    }
    
    public TransportException(String errorMessage) {
        super(errorMessage);
    }

    
    public TransportException(String errorMessage, Type type) {
        super(errorMessage);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
