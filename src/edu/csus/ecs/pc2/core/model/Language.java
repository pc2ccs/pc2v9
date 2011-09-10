package edu.csus.ecs.pc2.core.model;


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
     * Execute program command line.
     * 
     * Command line to execute the program created using compileCommandLine.
     */
    private String programExecuteCommandLine;
    
    private boolean interpreted = false;

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
            if (!displayName.equals(language.getDisplayName())) {
                return false;
            }
            if (isActive() != language.isActive()) {
                return false;
            }
            if (!compileCommandLine.equals(language.getCompileCommandLine())) {
                return false;
            }
            if (!programExecuteCommandLine.equals(language.getProgramExecuteCommandLine())) {
                return false;
            }
            if (interpreted != language.isInterpreted()) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            // TODO log to static Exception log
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
}
