package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.csus.ecs.pc2.core.Utilities;


/**
 * Data files and programs (validator) for a Problem.
 * 
 * Multiple data sets are supported.  
 * <P>
 * 
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
 * @version $Id$
 * @author pc2@ecs.csus.edu
 *
 */
// $HeadURL$
public class ProblemDataFiles implements IElementObject {

    /**
     *
     */
    private static final long serialVersionUID = -3590638930073985058L;

    private ElementId elementId = new ElementId("ProblemDF");

    private ElementId problemId = null;
    
    private SerializedFile outputValidatorFile;
    
    private SerializedFile inputValidatorFile;

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
     * This should be invoked with the new Problem and will attempt to copy
     * the existing ProblemDataFiles into a new ProblemDataFiles.
     * 
     * @param problem
     * @return a copy of this ProblemDataFiles
     * @throws CloneNotSupportedException
     */
    public ProblemDataFiles copy(Problem problem) throws CloneNotSupportedException {
        ProblemDataFiles clone = new ProblemDataFiles(problem);
        // inherited field
        clone.setSiteNumber(getSiteNumber());
        
        // local fields
        clone.elementId = elementId;
        clone.problemId = getProblemId();

        clone.setOutputValidatorFile(cloneSerializedFile(getOutputValidatorFile()));
        clone.setInputValidatorFile(cloneSerializedFile(getInputValidatorFile()));

        clone.setJudgesAnswerFiles(cloneSFArray(getJudgesAnswerFiles()));
        clone.setJudgesDataFiles(cloneSFArray(getJudgesDataFiles()));
        
        return clone;
    }

    private SerializedFile[] cloneSFArray(SerializedFile[] fileArray) throws CloneNotSupportedException {
        SerializedFile[] clone;
        if (fileArray != null) {
            clone = new SerializedFile[fileArray.length];
            for (int i = 0; i < fileArray.length; i++) {
                clone[i] = cloneSerializedFile(fileArray[i]);
            }
        } else {
            clone = null;
        }
        return clone;
    }

    private SerializedFile cloneSerializedFile(SerializedFile file) throws CloneNotSupportedException {
        SerializedFile clone = null;
        if (file != null) {
            clone = (SerializedFile)file.clone();
        }
        return clone;
    }

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
    
    /**
     * Returns the output validator program file.
     * 
     * @return a SerializedFile containing the output validator code, or null if no output validator has been specified
     */
    public SerializedFile getOutputValidatorFile() {
        return outputValidatorFile;
    }

    public void setOutputValidatorFile(SerializedFile validatorFile) {
        this.outputValidatorFile = validatorFile;
    }
    
    /**
     * Returns the input validator program file.
     * 
     * @return a SerializedFile containing the input validator code, or null if no input validator has been specified
     */
    public SerializedFile getInputValidatorFile() {
        return inputValidatorFile;
    }

    public void setInputValidatorFile(SerializedFile validatorFile) {
        this.inputValidatorFile = validatorFile;
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
        // checks same file, different location
        if (!oldFile.getAbsolutePath().equals(newFile.getAbsolutePath())) {
            return false;
        }
        // the sha1sum lies if a conversion has occurred
        byte[] oldBuffer = oldFile.getBuffer();
        byte[] newBuffer = newFile.getBuffer();
        if (oldBuffer == null) {
            if (newBuffer != null) {
                return false;
            }
        } else if (newBuffer == null) {
            // oldBuffer not null, but new buffer is
            return false;
        }
        // sizes no not match
        if (oldBuffer.length != newBuffer.length) {
            return false;
        }
        for (int i = 0; i < newBuffer.length; i++) {
            if (newBuffer[i] != oldBuffer[i]) {
                return false;
            }
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
            if (!compareSerializedFileArrays(this.getJudgesAnswerFiles(), newProblemDataFiles.getJudgesAnswerFiles())) {
                return false;
            }
            if (!compareSerializedFileArrays(this.getJudgesDataFiles(), newProblemDataFiles.getJudgesDataFiles())) {
                return false;
            }
            if (!compareSerializedFiles(this.getOutputValidatorFile(), newProblemDataFiles.getOutputValidatorFile())) {
                return false;
            }
            if (!compareSerializedFiles(this.getInputValidatorFile(), newProblemDataFiles.getInputValidatorFile())) {
                return false;
            }
            
            // TODO 917 should compare the other problemDataFile fields too.
                
            return true;
        } catch (Exception e) {
            // TODO Log to static exception Log
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public String toString() {

        StringBuffer buf = new StringBuffer();

        buf.append(problemId);
        buf.append(" ");

        int numDataFiles = judgesDataFiles.length;
        int numAnsFiles = judgesAnswerFiles.length;
        
        buf.append(numDataFiles);
        buf.append(" data files, ");
        
        buf.append(numAnsFiles);
        buf.append(" answer files. ");
        
        buf.append("Data: ");
        SerializedFile [] list = judgesDataFiles;
        for (SerializedFile file : list) {
            buf.append(file.getName());
            buf.append(" ");
        }
        
        buf.append("Answer: ");
        list = judgesAnswerFiles;
        for (SerializedFile file : list) {
            buf.append(file.getName());
            buf.append(" ");
        }

        buf.append(" Output Validator: ");
        if (outputValidatorFile != null) {
            buf.append(outputValidatorFile.getName());
            buf.append(" SHA1: ");
            buf.append (outputValidatorFile.getSHA1sum());
        } else {
            buf.append("null");
        }

        buf.append(" Input Validator: ");
        if (inputValidatorFile != null) {
            buf.append(inputValidatorFile.getName());
            buf.append(" SHA1: ");
            buf.append (inputValidatorFile.getSHA1sum());
        } else {
            buf.append("null");
        }

        return buf.toString();
    }
    
    
    /**
     * Returns the full path for judge data filenames.
     * 
     * Expected locations vary depending on the client type (Admin or Judge) and
     * whether the problem has external or internal files.
     * 
     * See {@link Utilities#getProblemfullFilenames(IInternalContest, Problem, SerializedFile[], String)} for details.
     * 
     * <pre>
     * Sample code: for Judge
     * String [] filenames = Utilities.getFullJudgesDataFilenames(contest, executable.getExecuteDirectoryName());
     * 
     * Sample code: for Admin
     * String [] filenames = Utilities.getFullJudgesDataFilenames(contest, null);
     * 
     * </pre>
     * 
     * @param contest
     * @param executableDir 
     */
    public String[] getFullJudgesDataFilenames(IInternalContest contest, String executableDir) {
        return Utilities.fullJudgesDataFilenames(contest, this, executableDir);
    }

    /**
     * Returns the full path for judge answer filenames.
     * 
     * <pre>
     * Sample code:
     * String [] filenames = Utilities.getFullJudgesAnswerFilenames(contest, executable.getExecuteDirectoryName());
     * </pre>     
     *  
     * @param contest
     * @param executableDir
     */
    public String[] getFullJudgesAnswerFilenames(IInternalContest contest, String executableDir) {
        return Utilities.fullJudgesAnswerFilenames(contest, this, executableDir);
    }
    
    /**
     * Check for existence of all judge data and answer files, may create data files if needed.
     * 
     * Will not create files if problem has external files, {@link Problem#isUsingExternalDataFiles()} set true.
     * <P>
     * Will create judges answer and and data files (in exedcutableDir) if problem has internal files.
     * <P>
     *
     * <pre>
     * Sample code:
     * checkAndCreateFiles(contest, executable.getExecuteDirectoryName());
     * </pre>     
     * 
     * @param contest
     * @param executableDir directory where internal files are expected.
     * @throws FileNotFoundException if external files not found or cannot create internal file
     */
    public void checkAndCreateFiles(IInternalContest contest, String executableDir) throws FileNotFoundException {

        Utilities.insureDir(executableDir);
        String[] datafilenames = getFullJudgesDataFilenames(contest, executableDir);
        for (int i = 0; i < datafilenames.length; i++) {
            String filename = datafilenames[i]; // actual file expected to exist
            if (!new File(filename).exists()) {
                SerializedFile file = judgesDataFiles[i];

                if (file.isExternalFile()) {
                    // the external file should already exists, file not found
                    throw new FileNotFoundException(filename);
                } else {
                    // Internal files create them.
                    try {
                        file.writeFile(filename);
                    } catch (IOException e) {
                        throw new RuntimeException("Unable to write " + filename, e);
                    }

                    if (!new File(filename).exists()) {
                        throw new FileNotFoundException(filename);
                    }
                }
            }
        }

        String[] answerfilenames = getFullJudgesAnswerFilenames(contest, executableDir);
        for (int i = 0; i < answerfilenames.length; i++) {
            String filename = answerfilenames[i];
            if (!new File(filename).exists()) {
                SerializedFile file = judgesAnswerFiles[i];

                if (file.isExternalFile()) {
                    // the external file should already exists, file not found
                    throw new FileNotFoundException(filename);
                } else {
                    try {
                        // create file from internal file
                        file.writeFile(filename);
                    } catch (IOException e) {
                        throw new RuntimeException("Unable to write " + filename, e);
                    }
                    if (!new File(filename).exists()) {
                        throw new FileNotFoundException(filename);
                    }
                }
            }
        }

    }

    /**
     * Remove data set from problem data sets.
     * 
     * @param index zero based data set row
     */
    public void removeDataSet(int index) {

        SerializedFile[] newDataFiles = new SerializedFile[0];
        SerializedFile[] newAnswerFiles = new SerializedFile[0];

        int curDataFileCount = judgesDataFiles.length;
        int curAnswerFileCount = judgesAnswerFiles.length;

        if (curDataFileCount > 1) {
            newDataFiles = new SerializedFile[judgesDataFiles.length - 1];
            int newIdx = 0;
            for (int i = 0; i < judgesDataFiles.length; i++) {
                if (index != i) {
                    newDataFiles[newIdx] = judgesDataFiles[i];
                    newIdx++;
                }
            }
        }

        if (curAnswerFileCount > 1) {
            newAnswerFiles = new SerializedFile[judgesAnswerFiles.length - 1];
            int newIdx = 0;
            for (int i = 0; i < judgesAnswerFiles.length; i++) {
                if (index != i) {
                    newAnswerFiles[newIdx] = judgesAnswerFiles[i];
                    newIdx++;
                }
            }
        }

        judgesDataFiles = newDataFiles;
        judgesAnswerFiles = newAnswerFiles;

    }

    public void removeAll() {
        judgesDataFiles = new SerializedFile[0];
        judgesAnswerFiles = new SerializedFile[0];
    }

}
