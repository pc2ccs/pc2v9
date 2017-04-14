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

    /**
     * PC<sup>2</sup> Validator Program Name.
     */
    public static final String PC2_VALIDATOR_NAME = "edu.csus.ecs.pc2.validator.pc2Validator.PC2Validator";
    
    /**
     * CLICS Validator Program Name.
     */
    public static final String CLICS_VALIDATOR_NAME = "edu.csus.ecs.pc2.validator.clicsValidator.ClicsValidator";

    /**
     * The Default PC2 Validator Command Line
     */
    public static final String DEFAULT_PC2_VALIDATOR_COMMAND = "{:validator} {:infile} {:outfile} {:ansfile} {:resfile} ";
    
    /**
     * The Default CLICS Validator Command Line
     */
    public static final String DEFAULT_CLICS_VALIDATOR_COMMAND = "{:validator} {:infile} {:ansfile} {:feedbackdir} ";
    
    
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
    
    
    // Column names for load/save accounts
    
    public static final String SITE_COLUMN_NAME = "site";

    public static final String ACCOUNT_COLUMN_NAME = "account";

    public static final String DISPLAYNAME_COLUMN_NAME = "displayname";

    public static final String PASSWORD_COLUMN_NAME = "password";

    public static final String GROUP_COLUMN_NAME = "group";

    public static final String PERMDISPLAY_COLUMN_NAME = "permdisplay";

    public static final String PERMLOGIN_COLUMN_NAME = "permlogin";

    public static final String EXTERNALID_COLUMN_NAME = "externalid";

    public static final String ALIAS_COLUMN_NAME = "alias";

    public static final String PERMPASSWORD_COLUMN_NAME = "permpassword";

    public static final String LONGSCHOOLNAME_COLUMN_NAME = "longschoolname";

    public static final String SHORTSCHOOLNAME_COLUMN_NAME = "shortschoolname";

    public static final String TEAMNAME_COLUMN_NAME = "teamname";
    
    public static final String COUNTRY_CODE_COLUMN_NAME = "countrycode";
    
    public static final String DEFAULT_INSTITUTIONNAME = "undefined";

    public static final String DEFAULT_INSTITUTIONSHORTNAME = "undefined";

    public static final String DEFAULT_COUNTRY_CODE = "XXX";

    public static final String DEFAULT_INSTITUTIONCODE = "undefined";
    
    public static final int INPUT_VALIDATOR_SUCCESS_EXIT_CODE = 42;
    
    public static final int INPUT_VALIDATOR_FAILED_EXIT_CODE = 43;
    
    public static final int INPUT_VALIDATOR_EXECUTION_ERROR_CODE = -39;
    
}
