// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;

/**
 * The files that were submitted with a Run.
 *
 * @see Run
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunFiles implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7676377417419464772L;

    private Submission submission = null;

    /**
     * Unique id for this instance.
     */
    private ElementId elementId = new ElementId("RunFiles");

    /**
     * The runId for the Run associated with these files.
     */
    private ElementId runId;

    /**
     * The main file that was submitted.
     */
    private SerializedFile mainFile;

    /**
     *
     */
    private SerializedFile[] otherFiles;

    /**
     * @param run
     * @param filename
     */
    public RunFiles(Run run, String filename) {
        super();
        this.runId = run.getElementId();
        this.mainFile = new SerializedFile(filename);
        this.submission = run;
    }

    /**
     * @param run
     * @param filename
     */
    public RunFiles(Run run, IFile iFile) {
        super();
        this.runId = run.getElementId();
        this.mainFile = new SerializedFile(iFile);
        this.submission = run;
    }

    public RunFiles(Run run, IFile mainFile, IFile[] auxFiles) {
        super();
        this.runId = run.getElementId();
        this.mainFile = new SerializedFile(mainFile);
        this.otherFiles = createArray(auxFiles);
        this.submission = run;
    }

    private SerializedFile[] createArray(IFile[] auxFiles) {

        SerializedFile[] outArray = new SerializedFile[auxFiles.length];
        for (int i = 0; i < auxFiles.length; i++) {
            outArray[i] = new SerializedFile(auxFiles[i]);
        }
        return outArray;
    }

    /**
     * @param run
     * @param mainFile
     * @param otherFiles
     */
    public RunFiles(Run run, SerializedFile mainFile, SerializedFile[] otherFiles) {
        super();
        this.runId = run.getElementId();
        this.mainFile = mainFile;
        this.otherFiles = otherFiles;
        this.submission = run;
    }

    /**
     * Unique identifier for this class.
     *
     * @return Returns the elementId.
     */
    public ElementId getElementId() {
        return elementId;
    }

    /**
     * @return Returns the mainFile.
     */
    public SerializedFile getMainFile() {
        return mainFile;
    }

    /**
     * @return Returns the otherFiles.
     */
    public SerializedFile[] getOtherFiles() {
        return otherFiles;
    }

    /**
     * @return Returns the runId.
     */
    public ElementId getRunId() {
        return runId;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public Submission getSubmission() {
        return submission;
    }

    /**
     * Returns a String representation of this RunFiles object consisting of a list of the names of the files in the RunFiles object.
     * The returned string is in JSON format: an array containing two elements:  the MainFile and a sub-array containing the OtherFiles (if any;
     * otherwise the subarray is an empty JSON array ([]) ).
     */
    @Override
    public String toString() {
        String retStr = "";

        retStr += "[" + "\"MainFile\"" + ":" + "\"";

        if (getMainFile()!=null) {
            String mainFileName = getMainFile().getName();
            if (mainFileName!=null) {
                retStr += mainFileName ;
            } else {
                retStr += "null";
            }
        } else {
            retStr += "null";
        }

        retStr += "\",";

        retStr += "\"OtherFiles\":[" ;

        if (getOtherFiles()!=null) {
            if (getOtherFiles().length>0) {
                boolean first = true;
                for (SerializedFile file : getOtherFiles()) {
                    if (file!=null) {
                        String fileName = file.getName();
                        if (!first) {
                            retStr += ",";
                        }
                        retStr += "\"name\"" + ":\"";
                        if (fileName!=null) {
                            retStr += fileName ;
                        }
                    } else {
                        retStr += "null";
                    }
                    retStr += "\"";
                    first = false;
                }

            }

        }
        //close OtherFiles list
        retStr += "]";

        //close retStr object
        retStr += "]";

        return retStr ;

    }

    /**
     * createCatalogJSON - Generate detailed JSON information about the files submitted and it to the
     * supplied BufferedWriter.  Similar to toString above, but supplies more details.
     *
     * @param catlog Where to send the detailed JSON about the submission files
     */
    public void createCatalogJSON(BufferedWriter catlog) {
        try {
            boolean needComma = false;
            catlog.write("[");
            if(mainFile != null) {
                catlog.newLine();
                catlog.write(" {\"mainfile\":");
                catlog.write(serializedFileToJSON(mainFile));
                catlog.write(" }");
                needComma = true;
            }
            if(otherFiles != null && otherFiles.length > 0) {
                boolean needComma2 = false;
                if(needComma) {
                    catlog.write(',');
                }
                catlog.newLine();
                catlog.write(" {\"otherfiles\": {");
                for(SerializedFile f : otherFiles) {
                    if(needComma) {
                        catlog.write(',');
                        catlog.newLine();
                    }
                    catlog.write("  ");
                    catlog.write(serializedFileToJSON(f));
                }
                catlog.newLine();
                catlog.write(" }");
                // in case we add more stuff to the array later
                needComma = true;
            }
            catlog.write(']');
            catlog.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * serializedFileToJSON - pick out the detailed info we want from the SerializedFile
     *     and create a JSON string.  Ex.
     *     { "absolutePath":"C:/home/icpc/pc2/current/config/atotientquotient/submissions/time_limit_exceeded/evouga-brute.cpp",
     *       "name":"evouga-brute.cpp",
     *       "fileType":2,
     *       "externalFile":false
     *     }
     * @param f the serialized file
     * @return JSON string representation of the detailed info
     */
    private String serializedFileToJSON(SerializedFile f) {
        StringBuilder str = new StringBuilder();

        str.append('{');
        if(f != null) {
            str.append("\"absolutePath\":\"");
            str.append(f.getAbsolutePath().replace('\\', '/'));
            str.append("\",\"name\":\"");
            str.append(f.getName());
            str.append("\",\"fileType\":");
            str.append(f.getFileType());
            str.append(",\"externalFile\":");
            str.append(f.isExternalFile());
        }
        str.append('}');
        return(str.toString());
    }
}
