// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

/**
 * Constants for pc2.
 *
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public final class Constants {

    public static final String AWARDS_JSON_FILENAME = "awards.json";

    public final static String SCOREBOARD_JSON_FILENAME = "scoreboard.json";

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


    public static final int DEFAULT_MAX_OUTPUT_SIZE_K = 512;

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
     * Command substitution variable for name of thing to execute (basename of source file)
     */
    public static final String CMDSUB_BASENAME_VARNAME = "{:basename}";

    /**
     * Command substitution variable for all files submitted. Note the missing ':' - legacy.
     */
    public static final String CMSSUB_FILES_VARNAME = "{files}";

    /**
     * Command substitution variable for conditional suffix
     * This can be used for languages (such as Kotlin), that has a different main class name than the basename of
     * the source.  eg.  MyClass.kt  uses MyClassKt as the main class name.  This is conditional in that if the
     * trailing string (suffix) isn't already immediately to the left of the operand of this substitution variable, then it gets added.
     * eg. {:basename}{:ensuresuffix=Kt}  if {:basename} is MyClassKt then, the {:ensuresuffix=Kt} is a no-op (empty string).  If
     * {:basename} is MyClass then the {:ensuresuffix=Kt} evaluates to "Kt" making the result: MyClassKt.
     * Use of this is not just restricted to executables or basename; it can be used anywhere in the string.
     */
    public static final String CMDSUB_COND_SUFFIX = "{:ensuresuffix=}";

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

    public static final String SCORING_ADJUSTMENT_COLUMN_NAME = "scoreadjustment";

    public static final String INST_CODE_COLUMN_NAME = "institution";

    public static final String DEFAULT_INSTITUTIONNAME = "undefined";

    public static final String DEFAULT_INSTITUTIONSHORTNAME = "undefined";

    public static final String DEFAULT_COUNTRY_CODE = "XXX";

    public static final String DEFAULT_INSTITUTIONCODE = "undefined";

    public static final int INPUT_VALIDATOR_SUCCESS_EXIT_CODE = 42;

    public static final int INPUT_VALIDATOR_FAILED_EXIT_CODE = 43;

    public static final int INPUT_VALIDATOR_EXECUTION_ERROR_CODE = -39;

    public static final String ACCOUNTS_LOAD_FILENAME = "accounts_load.tsv";

    public static final int BYTES_PER_KIBIBYTE = 1024;

    public static final int KIBIBYTE_PER_MEBIBYTE = 1024;

    /**
     * Directory where scripts go that PC2 executes for sandbox and interactive submissions.
     * This folder is in the PC2 home folder (install folder).
     */
    public static final String PC2_SCRIPT_DIRECTORY = "scripts";

    /**
     * Command line to run submission in a sandbox
     */
    public static final String PC2_INTERNAL_SANDBOX_COMMAND_LINE = "./{:sandboxprogramname} {:memlimit} {:timelimit}";

    /**
     * File name of script to run submission in sandbox ({:sandboxprogramname} if non-interactive)
     */
    public static final String PC2_INTERNAL_SANDBOX_PROGRAM_NAME = "pc2sandbox.sh";

    /**
     * Filename of the script to run submission for an interactive problem in a sandbox ({:sandboxprogramname} if interactive)
     */
    public static final String PC2_INTERNAL_SANDBOX_INTERACTIVE_NAME = "pc2sandbox_interactive.sh";

    /**
     *  Command to execute to run an interactive problem in a sandbox
     */
    public static final String PC2_INTERNAL_SANDBOX_INTERACTIVE_COMMAND_LINE = "./{:sandboxprogramname} {:memlimit} {:timelimit} {:validator} {:infilename} {:ansfilename} {:testcase}";

    /**
     *  Filename of the script to run for an interactive problem when not using a sandbox
     */
    public static final String PC2_INTERACTIVE_NAME = "pc2_interactive.sh";

    /**
     *  Command to execute to run an interactive problem when not using a sandbox
     */
    public static final String PC2_INTERACTIVE_COMMAND_LINE = "./pc2_interactive.sh {:validator} {:infilename} {:ansfilename} {:testcase}";

    /**
     *  Filename of the script to run during the validate phase AFTER the interactive validator has completed
     */
    public static final String PC2_INTERACTIVE_VALIDATOR_NAME = "pc2validate_interactive.sh";

    /**
     *  Command to execute to validate results of an interactive run AFTER the interactive validator has completed
     */
    public static final String PC2_INTERACIVE_VALIDATE_COMMAND = "./" + PC2_INTERACTIVE_VALIDATOR_NAME + " {:resfile} {:feedbackdir} {:testcase}";

    /**
     * Execution info for entire run (all testcases)
     */
    public static final String PC2_EXECUTION_RESULTS_NAME_SUFFIX = "executeinfo.ndjson";

    /**
     * OS Compatibility constants
     */
    public static final String WINDOWS_CHECK_SANDBOX_SCRIPT = "pc2syscheck.cmd";
    public static final String UNIX_CHECK_SANDBOX_SCRIPT = "pc2syscheck.sh";

    /**
     * CLICS validation Types
     */
    public static final String VALIDATION_CUSTOM = "custom";
    public static final String VALIDATION_DEFAULT = "default";
    public static final String VALIDATION_INTERACTIVE = "interactive";
    public static final String VALIDATION_SCORE = "score";

    /**
     * Prefix for deleted runs.
     */
    public static final String DEL_RUN_PREFIX = "DEL ";

    /**
     * output directory for reports.
     */
    public static final String REPORT_DIRECTORY_NAME = "reports";

    /**
     * default results directory for CDP.
     */
    public static final String CDP_RESULTS_DIR ="results";

    public static final String TAB = "\t";

    public static final String NL = System.getProperty("line.separator");

}
