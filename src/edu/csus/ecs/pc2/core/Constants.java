package edu.csus.ecs.pc2.core;

public class Constants {

    public Constants() {
        super();
    }
    /** Used by Serialized file to specify type of file **/
    public static int FILETYPE_BINARY = 1;
    public static String FILETYPE_BINARY_TEXT = "Binary";
    public static int FILETYPE_DOS = 2;
    public static String FILETYPE_DOS_TEXT = "DOS";
    public static int FILETYPE_MAC = 4;
    public static String FILETYPE_MAC_TEXT= "Apple";
    public static int FILETYPE_UNIX = 8;
    public static String FILETYPE_UNIX_TEXT= "Unix";
    public static int FILETYPE_ASCII_GENERIC = 16;
    public static String FILETYPE_ASCII_GENERIC_TEXT= "ASCII_Generic";
    public static int FILETYPE_ASCII_OTHER = 32;
    public static String FILETYPE_ASCII_OTHER_TEXT = "ASCII_Other";
}
