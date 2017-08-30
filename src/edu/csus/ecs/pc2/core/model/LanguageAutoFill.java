package edu.csus.ecs.pc2.core.model;

/**
 * Language auto fill values.
 * 
 * List of language (titles) {@link #getLanguageList()}, to
 * get individual values {@link #getAutoFillValues(String)}.
 *
 * @author pc2@ecs.csus.edu
 */
public final class LanguageAutoFill {

    public static final String JAVATITLE = "Java";

    public static final String DEFAULTTITLE = "Default";

    public static final String GNUCPPTITLE = "GNU C++ (Unix / Windows)";

    public static final String GNUCTITLE = "GNU C (Unix / Windows)";
    
    public static final String PERLTITLE = "Perl";

    public static final String MSCTITLE = "Microsoft C++";

    public static final String KYLIXTITLE = "Kylix Delphi";

    public static final String KYLIXCPPTITLE = "Kylix C++";

    public static final String FPCTITLE = "Free Pascal";
    
    public static final String PHPTITLE = "PHP";
    
    public static final String PYTHONTITLE = "Python";
    
    public static final String RUBYTITLE = "Ruby";
    
    public static final String PYTHON3TITLE = "Python 3";
    
    public static final String MONOCSHARPTITLE = "Mono C#";
    
    public static final String MSCSHARPTITLE = "Microsoft C#";
    
    public static final String WINDOWS_KOTLINTITLE = "Kotlin (Windows)";
    
    public static final String UNIX_KOTLINTITLE = "Kotlin (Linux/MacOS)";

    /**
     * Constant string for an interpreted language.
     */
    public static final String INTERPRETER_VALUE = "interpeter";
    
    private static final String NULL_LANGUAGE_NAME = "";

    private static String[] languageList = { DEFAULTTITLE, JAVATITLE, //
            GNUCPPTITLE, GNUCTITLE, MONOCSHARPTITLE, MSCSHARPTITLE, PERLTITLE, PHPTITLE, PYTHONTITLE, PYTHON3TITLE, 
            RUBYTITLE, //
            MSCTITLE, KYLIXTITLE, KYLIXCPPTITLE, FPCTITLE, WINDOWS_KOTLINTITLE, UNIX_KOTLINTITLE };

    /**
     * Constructor is private as this is a utility class which
     * should not be extended or invoked.
     */
    private LanguageAutoFill() {
        super();
    }

    /**
     * Returns auto populate values for a language. 
     * 
     * For each key returns an array containing:
     * <ol>
     * <li>Title for Language
     * <li>Compiler Command Line
     * <li>Executable Identifier Mask
     * <li>Execute command line
     * <li>Title for Language
     * <li>Interpreted language, value will be {@link #INTERPRETER_VALUE} if interpreter.
     * </ol>
     * 
     * @param key a name in the auto populate language list, see {@link #getLanguageList()}.
     * @return array for auto-populating {@link Language}
     */
    public static String[] getAutoFillValues(String key) {
        /**
         * Per the static final strings this returns the fill values for given titles
         */

        /**
         * Directory separator, ie / or \ depending on OS.
         */
        String fs = java.io.File.separator;

        if (key.equals(JAVATITLE)) {
            String[] dVals = { JAVATITLE, "javac {:mainfile}", //
                    "{:basename}.class", "java {:basename}", JAVATITLE, "" };
            return dVals;
        } else if (key.equals(KYLIXCPPTITLE)) {
            String[] dVals = { KYLIXCPPTITLE, "bc++ -A  {:mainfile}", //
                    "{:basename}", "." + fs + "{:basename}", KYLIXCPPTITLE, "" };
            return dVals;
        } else if (key.equals(MONOCSHARPTITLE)) {
            String[] dVals = { MONOCSHARPTITLE, "mcs {:mainfile}", //
                    "{:basename}.exe", "mono {:basename}.exe", MONOCSHARPTITLE, "" };
            return dVals;
        } else if (key.equals(MSCSHARPTITLE)) {
            String[] dVals = { MSCSHARPTITLE, "csc {:mainfile}", //
                    "{:basename}.exe", "." + fs + "{:basename}.exe", MSCSHARPTITLE, "" };
            return dVals;
        } else if (key.equals(MSCTITLE)) {
            String[] dVals = { MSCTITLE, "cl.exe {:mainfile}", //
                    "{:basename}.exe", "." + fs + "{:basename}.exe", MSCTITLE , ""};
            return dVals;
        } else if (key.equals(GNUCPPTITLE)) {
            String[] dVals = { GNUCPPTITLE, "g++ -o {:basename}.exe {:mainfile}", "{:basename}.exe", //
                    "." + fs + "{:basename}.exe", "GNU C++", "" };
            return dVals;
        } else if (key.equals(GNUCTITLE)) {

            String[] dVals = { GNUCTITLE, "gcc -o {:basename}.exe {:mainfile} -lm", //
                    "{:basename}.exe", "." + fs + "{:basename}.exe", "GNU C", "" };
            return dVals;
        } else if (key.equals(KYLIXTITLE)) {

            String[] dVals = { KYLIXTITLE, "dcc {:mainfile}", "{:basename}", //
                    "." + fs + "{:basename}", KYLIXTITLE, "" };
            return dVals;
        } else if (key.equals(FPCTITLE)) {

            String[] dVals = { FPCTITLE, "fpc {:mainfile}", "{:basename}", //
                    "." + fs + "{:basename}", FPCTITLE, "" };
            return dVals;
        } else if (key.equals(PERLTITLE)) {

            String[] dVals = { PERLTITLE, "perl -c {:mainfile}", "{:noexe}", //
                    "perl {:mainfile}", PERLTITLE, INTERPRETER_VALUE };
            return dVals;

        } else if (key.equals(PHPTITLE)) {

            String[] dVals = { PHPTITLE, "php -l {:mainfile}", "{:noexe}", //
                    "php {:mainfile}", PHPTITLE, INTERPRETER_VALUE };
            return dVals;

        } else if (key.equals(PYTHONTITLE)) {

            String[] dVals = { PYTHONTITLE, "python -m py_compile {:mainfile}", "{:noexe}", //
                    "python {:mainfile}", PYTHONTITLE, INTERPRETER_VALUE };
            return dVals;

        } else if (key.equals(PYTHON3TITLE)) {

            String[] dVals = { PYTHONTITLE, "python3 -m py_compile {:mainfile}", "{:noexe}", //
                    "python3 {:mainfile}", PYTHONTITLE, INTERPRETER_VALUE };
            return dVals;

        } else if (key.equals(RUBYTITLE)) {

            String[] dVals = { RUBYTITLE, "ruby -c {:mainfile}", "{:noexe}", //
                    "ruby {:mainfile}", RUBYTITLE, INTERPRETER_VALUE };
            return dVals;
            
        } else if (key.equals(WINDOWS_KOTLINTITLE)) {

            String[] dVals = { WINDOWS_KOTLINTITLE, "cmd /c kotlinc {:mainfile} -include-runtime -d {:basename}.jar", "{:basename}.jar", //
                    "java -jar {:basename}.jar", WINDOWS_KOTLINTITLE, "" };
            return dVals;

        } else if (key.equals(UNIX_KOTLINTITLE)) {

            String[] dVals = { UNIX_KOTLINTITLE, "kotlinc {:mainfile} -include-runtime -d {:basename}.jar", "{:basename}.jar", //
                    "java -jar {:basename}.jar", UNIX_KOTLINTITLE, "" };
            return dVals;

       } else {
            // default / DEFAULTTITLE

            String[] dVals = { NULL_LANGUAGE_NAME, "<Compiler> {:mainfile}", "{:basename}.exe", 
                    "{:basename}.exe", "", "" };

            return dVals;
        }
    }

    /**
     * Return list of language names in the autopopulate list.
     * 
     * @return list of language titles.
     */
    public static String[] getLanguageList() {
        return languageList;
    }
    
    public static boolean isInterpretedLanguage(String key) {
        String [] values = getAutoFillValues(key);
        return INTERPRETER_VALUE.equals(values[5]);
    }
    
    /**
     * Match name and return Language definition.
     * @param name
     * @return null if no language matches
     */
    public static Language languageLookup(String languageName) {
        
        String [] names = getLanguageList();
        for (String name : names){
            if (name.equalsIgnoreCase(languageName)){
                return createAutoFilledLanguage(name);
            }
        }
        
        for (String name : names){
             if (name.length() > languageName.length()){
                if (name.startsWith(languageName + " ")){
                    return createAutoFilledLanguage(name);
                }
            }
        }
        
        if (languageName.equalsIgnoreCase("C")){
            return createAutoFilledLanguage(GNUCTITLE);
        }
        
        if (languageName.equalsIgnoreCase("C++")){
            return createAutoFilledLanguage(GNUCPPTITLE);
        }
        
        return null;
    }

    /**
     * Create language from default values.
     *  
     * @param name language name from {@link LanguageAutoFill#getLanguageList()} 
     * @return null if name is not found.
     */
    public static Language createAutoFilledLanguage(String name) {

        String[] values = LanguageAutoFill.getAutoFillValues(name);
        
        if (! values[0].equals(NULL_LANGUAGE_NAME)){
            Language language = new Language(values[4]);
            language.setCompileCommandLine(values[1]);
            language.setExecutableIdentifierMask(values[2]);
            language.setProgramExecuteCommandLine(values[3]);
            boolean isScript = LanguageAutoFill.isInterpretedLanguage(name);
            language.setInterpreted(isScript);
            
            // set Judge's same as Team's.
            language.setJudgeProgramExecuteCommandLine(values[3]);
            return language;
        } else {
            return null;
        }

     
    }

}
