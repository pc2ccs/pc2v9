package edu.csus.ecs.pc2.core;

/**
 * Constants for pc2.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public final class Constants {

    public static final long HOURS_PER_DAY = 24;

    private Constants() {
        super();
    }

    // SerializedFile constants.
    
    public static final int FILETYPE_BINARY = 1;

    public static final String FILETYPE_BINARY_TEXT = "Binary";

    public static final int FILETYPE_DOS = 2;

    public static final String FILETYPE_DOS_TEXT = "DOS";

    public static final int FILETYPE_MAC = 4;

    public static final String FILETYPE_MAC_TEXT = "Apple";

    public static final int FILETYPE_UNIX = 8;

    public static final String FILETYPE_UNIX_TEXT = "Unix";

    public static final int FILETYPE_ASCII_GENERIC = 16;

    public static final String FILETYPE_ASCII_GENERIC_TEXT = "ASCII_Generic";

    public static final int FILETYPE_ASCII_OTHER = 32;

    public static final String FILETYPE_ASCII_OTHER_TEXT = "ASCII_Other";

    /**
     * number of ms in a second.
     */
    // SOMEDAY deprecate use MS_PER_SECOND (singlular)
    public static final long MS_PER_SECONDS = 1000;

    public static final long MS_PER_SECOND = 1000;

    /**
     * number of seconds in a minute.
     */
    public static final long SECONDS_PER_MINUTE = 60;

    /**
     * number of ms in a minute.
     */
    public static final long MS_PER_MINUTE = SECONDS_PER_MINUTE * MS_PER_SECONDS;

    /**
     * number of minutes in an hour.
     */
    public static final long MINUTES_PER_HOUR = 60;

    /**
     * number of seconds in an hour.
     */
    public static final long SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    
    public static final long SECONDS_PER_DAY = 86400;

    /**
     * Default contest length.
     */
    public static final long DEFAULT_CONTEST_LENGTH_SECONDS = 18000; // 5 * 60 * 60


    public static final String DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND = "{:validator} {:infile} {:outfile} {:ansfile} {:resfile} ";
    
    /**
     * Default file name for judgement ini file.
     */
    public static final String JUDGEMENT_INIT_FILENAME = "reject.ini";
    

    /**
     * Minimum java major version value.
     */
    public static final int MIN_MAJOR_JAVA_VERSION = 1;

    /**
     * Minimum Java minor version value
     */
    public static final int MIN_MINOR_JAVA_VERSION = 7;

    /**
     * Default port that a pc2 server listens on.
     * 
     */
    public static final int DEFAULT_PC2_PORT = 50002;
    
}
