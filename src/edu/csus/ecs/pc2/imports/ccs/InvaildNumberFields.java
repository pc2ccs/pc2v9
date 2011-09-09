package edu.csus.ecs.pc2.imports.ccs;

/**
 * Invalid number Exception.
 * @author pc2@ecs.csus.edu
 * @version $Id: InvaildNumberFields.java 181 2011-04-11 03:21:46Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/imports/ccs/InvaildNumberFields.java $
public class InvaildNumberFields extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -8115212667671511354L;

    public InvaildNumberFields() {
        super();
    }

    public InvaildNumberFields(String message, Throwable cause) {
        super(message, cause);
    }

    public InvaildNumberFields(String message) {
        super(message);
    }

    public InvaildNumberFields(Throwable cause) {
        super(cause);
    }

}
