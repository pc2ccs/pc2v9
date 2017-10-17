package edu.csus.ecs.pc2.core.model;

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

    public String toString() {
        return displayName;
    }

    /**
     * @return Returns the elementId.
     */
    public ElementId getElementId() {
        return elementId;
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

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

    public void setID(String id) {
        this.id = id;
    }
    
    public String getID() {
        return id;
    }
}
