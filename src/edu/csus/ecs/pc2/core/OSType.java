package edu.csus.ecs.pc2.core;

/**
 * Name of OS.
 * 
 * Values returned for {@link Utilities#getOSType()}.
 * 
 * @see Utilities#getOSType()
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public enum OSType {

    /**
     * Indicates that no OS has been set.
     * 
     * 
     */
    UNDEFINED("Undefined"),

    /**
     * Windows OS.
     */
    WINDOWS("Windows"),

    /**
     * Unix/Linux 64 bit (AMD) OS.
     * 
     */
    AMD64("Unit/Linux (64 bit, AMD64)"),

    /**
     * Unix/Linux 32 bit (Intel) OS.
     * 
     */
    I386("Uni/Linux (32 bit, i386)"),

    /**
     * The OS could not be determined.
     * 
     * This value is different than UNDEFINED.  This value
     * indicates that an attempt to determine the OS was made
     * but 
     */
    UNCLASSIFIED("Unclassified");

    private String name = "Undefined";

    /**
     * Set override name/description for enum element.
     */
    OSType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
