package edu.csus.ecs.pc2.imports.ccs;

/**
 * Invalid File Format.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: InvalidFileFormat.java 181 2011-04-11 03:21:46Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/imports/ccs/InvalidFileFormat.java $
public class InvalidFileFormat extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 3693109330495149868L;

    public InvalidFileFormat() {
        super();
    }

    public InvalidFileFormat(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFileFormat(String message) {
        super(message);
    }

    public InvalidFileFormat(Throwable cause) {
        super(cause);
    }

}
