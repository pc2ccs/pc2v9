package edu.csus.ecs.pc2.core;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$

public final class Constants {

    private Constants() {
        super();
    }
    /** Used by Serialized file to specify type of file **/
    public static final int FILETYPE_BINARY = 1;
    public static final String FILETYPE_BINARY_TEXT = "Binary";
    public static final int FILETYPE_DOS = 2;
    public static final String FILETYPE_DOS_TEXT = "DOS";
    public static final int FILETYPE_MAC = 4;
    public static final String FILETYPE_MAC_TEXT= "Apple";
    public static final int FILETYPE_UNIX = 8;
    public static final String FILETYPE_UNIX_TEXT= "Unix";
    public static final int FILETYPE_ASCII_GENERIC = 16;
    public static final String FILETYPE_ASCII_GENERIC_TEXT= "ASCII_Generic";
    public static final int FILETYPE_ASCII_OTHER = 32;
    public static final String FILETYPE_ASCII_OTHER_TEXT = "ASCII_Other";
}
