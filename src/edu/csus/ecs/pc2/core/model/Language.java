// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import java.util.ArrayList;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.log.StaticLog;


/**
 * Single Language Definition.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public class Language implements IElementObject {
    /**
     *
     */
    private static final long serialVersionUID = 7782777481422759344L;

    /**
     * Well-known CLICS language ids
     * TODO: We have to find a better way to deal with language-specific things inside PC2.
     *       Languages require specific processing for things like "entry_point".  It's non-trivial
     *       to make this "dynamic" and have PC2 be language agnostic. This has to be discussed.
     *       For now, we will use the somewhat "well-known" CLICS "unofficial" language Identifiers for
     *       ICPC contests.  JB 03/20/2024
     */
    public static final String CLICS_LANGID_JAVA = "java";
    private static final String [] DEFAULT_EXT_JAVA = { "java" };
    public static final String CLICS_LANGID_KOTLIN = "kotlin";
    private static final String [] DEFAULT_EXT_KOTLIN = { "kt" };
    public static final String CLICS_LANGID_PYTHON3 = "python3";
    private static final String [] DEFAULT_EXT_PYTHON3 = { "py" };
    public static final String CLICS_LANGID_C = "c";
    private static final String [] DEFAULT_EXT_C = { "c" };
    public static final String CLICS_LANGID_CPP = "cpp";
    private static final String [] DEFAULT_EXT_CPP = { "cc", "cpp", "cxx", "c++" };

    /**
     * Title for the Language.
     */
    private String displayName = null;

    /**
     * Unique identifier for this instance of Language.
     */
    private ElementId elementId = null;

    private boolean active = true;

    /**
     * Compiler/Language command line.
     *
     * Command line with field substitution to compile source.
     */
    private String compileCommandLine;

    /**
     * Executable Identifier Mask.
     *
     */
    private String executableIdentifierMask;


    /**
     * Judge program command line.
     *
     * Command line for judges to execute the program created using compileCommandLine.
     */
    private String judgeProgramExecuteCommandLine;

    /**
     * Flag to indicate whether using judge execut command line.
     */
    private boolean usingJudgeProgramExecuteCommandLine = false;

    /**
     * Execute program command line.
     *
     * Command line to execute the program created using compileCommandLine.
     */
    private String programExecuteCommandLine;

    private boolean interpreted = false;

    private String id = "";

    private ArrayList<String> extensions = new ArrayList<String>();

    public Language(String displayName) {
        super();
        this.displayName = displayName;
        elementId = new ElementId(displayName);
        setSiteNumber(0);
    }

    /**
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @return Returns the compileCommandLine.
     */
    public String getCompileCommandLine() {
        return compileCommandLine;
    }

    /**
     * @return Returns the title for this Language.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return Returns the programExecuteCommandLine.
     */
    public String getProgramExecuteCommandLine() {
        return programExecuteCommandLine;
    }

    /**
     * @param active
     *            The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @param compileCommandLine
     *            The compileCommandLine to set.
     */
    public void setCompileCommandLine(String compileCommandLine) {
        this.compileCommandLine = compileCommandLine;
    }

    /**
     * @param displayName
     *            The displayName to set.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @param programExecuteCommandLine
     *            The programExecuteCommandLine to set.
     */
    public void setProgramExecuteCommandLine(String programExecuteCommandLine) {
        this.programExecuteCommandLine = programExecuteCommandLine;
    }

    /**
     * @see Object#equals(java.lang.Object).
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Language) {
            Language otherLanguage = (Language) obj;
            return elementId.equals(otherLanguage.elementId);
        } else {
            throw new ClassCastException("expected an Language found: " + obj.getClass().getName());
        }
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * @return Returns the elementId.
     */
    @Override
    public ElementId getElementId() {
        return elementId;
    }

    @Override
    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    @Override
    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    @Override
    public void setSiteNumber(int siteNumber) {
        elementId.setSiteNumber(siteNumber);
    }

    public boolean isSameAs(Language language) {
        try {

            if (! StringUtilities.stringSame(displayName, language.getDisplayName())) {
                return false;
            }
            if (isActive() != language.isActive()) {
                return false;
            }
            if (! StringUtilities.stringSame(compileCommandLine, language.getCompileCommandLine())){
                return false;
            }
            if (! StringUtilities.stringSame(programExecuteCommandLine, language.getProgramExecuteCommandLine())) {
                return false;
            }
            if (!StringUtilities.stringSame(judgeProgramExecuteCommandLine, language.getJudgeProgramExecuteCommandLine())){
                return false;
            }
            if (!StringUtilities.stringSame(executableIdentifierMask,  language.getExecutableIdentifierMask())) {
                return false;
            }
            if (usingJudgeProgramExecuteCommandLine != language.isUsingJudgeProgramExecuteCommandLine()) {
                return false;
            }
            if (interpreted != language.isInterpreted()) {
                return false;
            }
            if (! StringUtilities.stringSame(id, language.getID())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            StaticLog.log("Exception in Language.isSameAs",  e);
            return false;
        }
    }

    @Override
    public int hashCode() {
        // use elementId to be consistent with equals()
        return elementId.hashCode();
    }

    /**
     * Check if extensions array is allocated.  If not allocate it.  This can happen on deserialization of an older object.
     */
    private void checkExtensions() {
        if(extensions == null) {
            extensions = new ArrayList<String>();
        }
    }

    public String getExecutableIdentifierMask() {
        return executableIdentifierMask;
    }

    public void setExecutableIdentifierMask(String executableIdentifierMask) {
        this.executableIdentifierMask = executableIdentifierMask;
    }

    public void setInterpreted(boolean interpreted) {
        this.interpreted = interpreted;
    }

    public boolean isInterpreted() {
        return interpreted;
    }

    public String getJudgeProgramExecuteCommandLine() {
        if (usingJudgeProgramExecuteCommandLine){
            return judgeProgramExecuteCommandLine;
        } else {
            return getProgramExecuteCommandLine();
        }
    }

    public void setJudgeProgramExecuteCommandLine(String judgeProgramExecuteCommandLine) {
        this.judgeProgramExecuteCommandLine = judgeProgramExecuteCommandLine;
    }

    public void setUsingJudgeProgramExecuteCommandLine(boolean usingJudgeProgramExecuteCommandLine) {
        this.usingJudgeProgramExecuteCommandLine = usingJudgeProgramExecuteCommandLine;
    }

    public boolean isUsingJudgeProgramExecuteCommandLine() {
        return usingJudgeProgramExecuteCommandLine;
    }

    public void setID(String newId) {
        id = newId;
        checkExtensions();
        // set default extensions for the language based on its CLICS id
        if(extensions.isEmpty()) {
            String [] ext = null;
            if(newId.equals(CLICS_LANGID_C)) {
                ext = DEFAULT_EXT_C;
            } else if(newId.equals(CLICS_LANGID_CPP)) {
                ext = DEFAULT_EXT_CPP;
            } else if(newId.equals(CLICS_LANGID_PYTHON3)) {
                ext = DEFAULT_EXT_PYTHON3;
            } else if(newId.equals(CLICS_LANGID_JAVA)) {
                ext = DEFAULT_EXT_JAVA;
            } else if(newId.equals(CLICS_LANGID_KOTLIN)) {
                ext = DEFAULT_EXT_KOTLIN;
            }
            if(ext != null) {
                copyExtensions(ext);
            }
        }
    }

    public String getID() {
        return id;
    }

    public void setExtensions(ArrayList<String> exts) {
        checkExtensions();
        extensions.clear();
        extensions.addAll(exts);
    }

    public ArrayList<String> getExtensions() {
        checkExtensions();
        if(extensions.isEmpty()) {
            setDefaultExtensions();
        }
        return(extensions);
    }

    /**
     * In the event the user did not specify a CLICS ID for the language, we have to make a guess.
     * We use is the lower-case display name, minus spaces, and + changed to p, and compare to our known
     * list of CLICS ids.
     */
    private void setDefaultExtensions()
    {
        // Make lower case, get rid of spaces and convert all plus signs to p's (eg c++ -> cpp)
        String fauxId = getDisplayName().toLowerCase().replaceAll("\\s", "").replaceAll("\\+", "p");
        // Let's use it as the clics ID
        setID(fauxId);
        // if it didn't work, then pretend we didn't try.
        if(extensions.isEmpty()) {
            id = null;
        }
    }

    /**
     * Utility method to convert between a string array and ArrayList<String>
     *
     * @param exts array of strings to convert
     */
    private void copyExtensions(String [] exts) {
        for(String ext : exts) {
            extensions.add(ext);
        }
    }
}
