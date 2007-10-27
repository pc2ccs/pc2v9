package edu.csus.ecs.pc2.core.model;

/**
 * Data files for a Problem.
 * 
 * Multiple data sets are supported.  
 * <P>
 * 
 * @see #getJudgesAnswerFile()
 * @see #getJudgesDataFile()
 * 
 * @see #getJudgesAnswerFiles()
 * @see #getJudgesDataFiles()
 * 
 * @see edu.csus.ecs.pc2.core.model.Problem
 * @see edu.csus.ecs.pc2.core.model.ProblemDataFilesList
 *
 * @author pc2@ecs.csus.edu
 *
 */
// $HeadURL$
public class ProblemDataFiles implements IElementObject {

    /**
     *
     */
    private static final long serialVersionUID = -3590638930073985058L;

    public static final String SVN_ID = "$Id$";

    private ElementId elementId = new ElementId("ProblemDF");

    private ElementId problemId = null;
    
    private SerializedFile validatorFile;

    public ProblemDataFiles(Problem problem) {
        problemId = problem.getElementId();
    }

    /**
     * Judge's data file.
     *
     * This is the contents of the judge's data file. The contents will be written to the dataFileName.
     */
    private SerializedFile[] judgesDataFiles = new SerializedFile[0];

    /**
     * Answer files (correct answer files).
     *
     *
     * This is contents of the judge's answer file. The contents will be written to the answerFileName.
     */
    private SerializedFile[] judgesAnswerFiles = new SerializedFile[0];

    /**
     * Return array of answer files.
     * 
     * @return the judge answer files or a single zero length array.
     */
    public SerializedFile[] getJudgesAnswerFiles() {
        return judgesAnswerFiles;
    }

    /**
     * Return array of data files.
     * 
     * @return Returns the judge data files or a single zero length array.
     */
    public SerializedFile[] getJudgesDataFiles() {
        return judgesDataFiles;
    }

    /**
     * @param judgesAnswerFiles
     *            The judgesAnswerFiles to set.
     */
    public void setJudgesAnswerFiles(SerializedFile[] judgesAnswerFiles) {
        this.judgesAnswerFiles = judgesAnswerFiles;
    }

    /**
     * set a single answer file.
     *
     * This overwrites (replaces) existing answer files.
     *
     * @param judgesAnswerFile
     *            new judge answer file.
     */
    public void setJudgesAnswerFile(SerializedFile judgesAnswerFile) {
        SerializedFile[] files = new SerializedFile[1];
        files[0] = judgesAnswerFile;
        setJudgesAnswerFiles(files);
    }

    /**
     * @param judgesDataFiles
     *            The judgesDataFiles to set.
     */
    public void setJudgesDataFiles(SerializedFile[] judgesDataFiles) {
        this.judgesDataFiles = judgesDataFiles;
    }

    /**
     * set a single data file.
     *
     * This overwrites (replaces) existing data files.
     *
     * @param judgesDataFile -
     *            new judge data file.
     */
    public void setJudgesDataFile(SerializedFile judgesDataFile) {
        SerializedFile[] files = new SerializedFile[1];
        files[0] = judgesDataFile;
        setJudgesDataFiles(files);
    }
    
    /**
     * Get the judge data file.
     * @return a file or null if not present.
     */
    public SerializedFile getJudgesDataFile() {
        SerializedFile[] files = getJudgesDataFiles();
        if (files.length == 0) {
            return null;
        } else {
            return files[0];
        }
    }
    
    /**
     * Get the judge answer file.
     * 
     * @return a file or null if not present.
     */
    public SerializedFile getJudgesAnswerFile() {
        SerializedFile[] files = getJudgesAnswerFiles();
        if (files.length == 0) {
            return null;
        } else {
            return files[0];
        }
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

    public ElementId getProblemId() {
        return problemId;
    }

    protected void setProblemId(ElementId problemId) {
        this.problemId = problemId;
    }

    public SerializedFile getValidatorFile() {
        return validatorFile;
    }

    public void setValidatorFile(SerializedFile validatorFile) {
        this.validatorFile = validatorFile;
    }

    private boolean compareSerializedFiles(SerializedFile oldFile, SerializedFile newFile) {
        if (oldFile == null) {
            return(newFile == null);
        }
        if (newFile == null) {
            return false;
        }
        if (!oldFile.getSHA1sum().equals(newFile.getSHA1sum())) {
            return false;
        }
        // the sha1sum lies if a conversion has occurred
        if (!oldFile.getBuffer().equals(newFile.getBuffer())) {
            return false;
        }
        return true;
    }
    
    private boolean compareSerializedFileArrays(SerializedFile[] oldList, SerializedFile[] newList) {
        if (oldList == null) {
            return(newList == null);
        }
        // newList is null, but oldList is not
        if (newList == null) {
            return false;
        }
        // different length, obviously different
        if (oldList.length != newList.length) {
           return false;
        }
        for (int i = 0; i < oldList.length; i++) {
            if (!compareSerializedFiles(oldList[i], newList[i])) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isSameAs(ProblemDataFiles newProblemDataFiles) {
        try {
            if (newProblemDataFiles == null) {
                return false;
            }
            if (!compareSerializedFileArrays(judgesAnswerFiles, newProblemDataFiles.getJudgesAnswerFiles())) {
                return false;
            }
            if (!compareSerializedFileArrays(judgesDataFiles, newProblemDataFiles.getJudgesDataFiles())) {
                return false;
            }
            if (!compareSerializedFiles(validatorFile, newProblemDataFiles.getValidatorFile())) {
                return false;
            }
                
            return true;
        } catch (Exception e) {
            // TODO Log to static exception Log
            e.printStackTrace();
            return false;
        }
    }
}
