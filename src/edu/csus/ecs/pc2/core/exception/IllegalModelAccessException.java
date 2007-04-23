package edu.csus.ecs.pc2.core.exception;

/**
 * This class represents the exception which is thrown whenever
 * a ModelReadOnlyProxy object detects an attempt to modify the
 * underlying Model object.
 * @author pc2.ecs.csus.edu
 *
 */
public class IllegalModelAccessException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 4359084650633700946L;

    public IllegalModelAccessException(String message){
        super(message);
    }
}
