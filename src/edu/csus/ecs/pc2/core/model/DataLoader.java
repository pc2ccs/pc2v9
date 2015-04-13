package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Vector;

/**
 * Load external data into model classes.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class DataLoader {

    /**
     * Load data files using extnesions in and ans.
     * 
     * @see #loadDataSets(String, boolean, String, String)
     */
    public static void loadDataSets(ProblemDataFiles files, String directoryName, boolean externalFiles) throws FileNotFoundException {
        loadDataSets(files, directoryName, externalFiles, "in", "ans");
    }

    /**
     * Load data sets (judgee and answer files)
     * 
     * Reads the directory for file with extension dataExtension, the searches for matchhing answer file names with answerFileExtension. If there are answer files matching judge data files then will
     * load/set those files.
     * 
     * <li>There must be data and answer files in the directory.
     * 
     * @throws FileNotFoundException
     *             if answer file missing will throw
     */
    public static void loadDataSets(ProblemDataFiles files, String directoryName, boolean externalFiles, String dataExtension, String answerFileExtension) throws FileNotFoundException {

        String[] datafiles = getFileNames(directoryName, dataExtension);

        if (datafiles.length == 0) {
            throw new FileNotFoundException("No files found " + directoryName + " ext = " + dataExtension);

        }

        for (String name : datafiles) {

            String fullName = directoryName + File.separator + name;
            String fullAnsName = replaceExtension(fullName, dataExtension, answerFileExtension);

            if (!new File(fullAnsName).isFile()) {
                throw new FileNotFoundException("Expecting - ans file " + fullAnsName);
            }
        }

        SerializedFile[] dataFilelist = new SerializedFile[datafiles.length];
        SerializedFile[] answerFilelist = new SerializedFile[datafiles.length];

        int idx = 0;
        for (String name : datafiles) {

            String fullName = directoryName + File.separator + name;
            String fullAnsName = replaceExtension(fullName, dataExtension, answerFileExtension);

            SerializedFile dataFile = new SerializedFile(fullName, externalFiles);
            SerializedFile ansFile = new SerializedFile(fullAnsName, externalFiles);

            dataFilelist[idx] = dataFile;
            answerFilelist[idx] = ansFile;
            idx++;
        }

        files.setJudgesDataFiles(dataFilelist);
        files.setJudgesAnswerFiles(answerFilelist);
    }

    private static String replaceExtension(String fullName, String dataExtension, String answerFileExtension) {
        return fullName.replaceFirst(dataExtension + "$", answerFileExtension);
    }

    /**
     * Get list of filenames with extension in directory, return in sorted order.
     * 
     * @param directoryName
     * @param extension
     * @return
     */
    public static String[] getFileNames(String directoryName, String extension) {

        Vector<String> list = new Vector<String>();
        File dir = new File(directoryName);

        String[] entries = dir.list();
        if (entries == null) {
            return new String[0];
        }
        Arrays.sort(entries);

        for (String name : entries) {
            if (name.endsWith(extension)) {
                list.addElement(name);
            }
        }

        return (String[]) list.toArray(new String[list.size()]);
    }

}
