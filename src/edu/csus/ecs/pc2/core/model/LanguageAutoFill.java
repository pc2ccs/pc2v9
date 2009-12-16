package edu.csus.ecs.pc2.core.model;

/**
 * Language auto fill values.
 * 
 * List of language (titles) {@link #getLanguageList()}, to
 * get individual values {@link #getAutoFillValues(String)}.
 *
 * @author pc2@ecs.csus.edu
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/admin/gui/LanguageAutoFill.java$
public final class LanguageAutoFill {

    public static final String SVN_ID = "$Id$";

    public static final String JAVATITLE = "Java";

    public static final String DEFAULTTITLE = "Default";

    public static final String GNUCPPTITLE = "GNU C++ (Unix / Windows)";

    public static final String GNUCTITLE = "GNU C (Unix / Windows)";
    
    public static final String PERLTITLE = "Perl";

    public static final String MSCTITLE = "Microsoft C++";

    public static final String KYLIXTITLE = "Kylix Delphi";

    public static final String KYLIXCPPTITLE = "Kylix C++";

    public static final String FPCTITLE = "Free Pascal";

    private static String[] languageList = { DEFAULTTITLE, JAVATITLE,
            GNUCPPTITLE, GNUCTITLE, PERLTITLE, MSCTITLE, KYLIXTITLE, KYLIXCPPTITLE,
            FPCTITLE };

    /**
     * Constructor is private as this is a utility class which
     * should not be extended or invoked.
     */
    private LanguageAutoFill() {
        super();
    }

    /**
     * Returns auto populate values for input language. 
     * 
     * For each key returns an array of:
     * <ol>
     * <li>Title for Language
     * <li>Compiler Command Line
     * <li>Executable Identifier Mask
     * <li>Execute command line
     * </ol>
     * 
     * @param key
     * @return array for autopopulating {@link Language}
     */
    public static String[] getAutoFillValues(String key) {
        /**
         * Per the static final strings this returns the fill values for given titles
         */

        /**
         * Directory seperator, ie / or \ depending on OS.
         */
        String fs = java.io.File.separator;

        if (key.equals(JAVATITLE)) {
            String[] dVals = { JAVATITLE, "javac {:mainfile}",
                    "{:basename}.class", "java {:basename}", JAVATITLE };
            return dVals;
        } else if (key.equals(KYLIXCPPTITLE)) {
            String[] dVals = { KYLIXCPPTITLE, "bc++ -A  {:mainfile}",
                    "{:basename}", "." + fs + "{:basename}", KYLIXCPPTITLE};
            return dVals;
        } else if (key.equals(MSCTITLE)) {
            String[] dVals = { MSCTITLE, "cl.exe {:mainfile}",
                    "{:basename}.exe", "." + fs + "{:basename}.exe", MSCTITLE };
            return dVals;
        } else if (key.equals(GNUCPPTITLE)) {
            String[] dVals = { GNUCPPTITLE,
                    "g++ -lm -o {:basename}.exe {:mainfile}", "{:basename}.exe",
                    "." + fs + "{:basename}.exe", "GNU C++"};
            return dVals;
        } else if (key.equals(GNUCTITLE)) {

            String[] dVals = { GNUCTITLE, "gcc -lm -o {:basename}.exe {:mainfile}",
                    "{:basename}.exe", "." + fs + "{:basename}.exe", "GNU C"};
            return dVals;
        } else if (key.equals(KYLIXTITLE)) {

            String[] dVals = { KYLIXTITLE, "dcc {:mainfile}", "{:basename}",
                    "." + fs + "{:basename}", KYLIXTITLE };
            return dVals;
        } else if (key.equals(FPCTITLE)) {

            String[] dVals = { FPCTITLE, "fpc {:mainfile}", "{:basename}",
                    "." + fs + "{:basename}", FPCTITLE };
            return dVals;
        } else if (key.equals(PERLTITLE)) {

            String[] dVals = { PERLTITLE, "compilePerl {:mainfile}", "OK",
                    "perl {:mainfile}", PERLTITLE };
            return dVals;
        } else {
            // default / DEFAULTTITLE

            String[] dVals = { "", "<Compiler> {:mainfile}", "{:basename}.exe",
                    "{:basename}.exe", "" };

            return dVals;
        }
    }

    /**
     * Return list of autopopulate titles.
     * @return list of language titles.
     */
    public static String[] getLanguageList() {
        return languageList;
    }

}
