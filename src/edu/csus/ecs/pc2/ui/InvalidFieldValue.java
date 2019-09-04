// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

/**
 * Invalid (field) value.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class InvalidFieldValue extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 9215504286414120733L;

    public InvalidFieldValue(String string) {
        super(string);
    }

}
