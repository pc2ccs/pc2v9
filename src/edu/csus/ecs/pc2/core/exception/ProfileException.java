package edu.csus.ecs.pc2.core.exception;

import edu.csus.ecs.pc2.core.model.Profile;

/**
 * Exceptions for Profiles activities.
 * 
 * Adding, removing, creating directories, establishing encrypting of contest password, etc.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfileException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -4955153900325717474L;
    
    private Profile profile = null;


    public ProfileException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ProfileException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public ProfileException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public ProfileException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public ProfileException(Profile profile, Throwable cause) {
        super(cause);
        this.profile = profile;
    }

    public ProfileException(Profile profile, String string) {
        super(string);
        this.profile = profile;
    }

    public ProfileException(Profile profile, String message, Throwable cause) {
        super(message, cause);
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }}
