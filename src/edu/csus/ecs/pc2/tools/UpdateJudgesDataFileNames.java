// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.OSType;
import edu.csus.ecs.pc2.core.ParseArguments;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.export.ExportYAML;

/**
 * Update Judge's filenames to add samples and insure that
 * 
 * <br>
 * <br>
 * See: https://github.com/pc2ccs/pc2v9/issues/244 Provide a way to add sample data files to the test data files
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class UpdateJudgesDataFileNames {

    /**
     * Sample files prefix
     */
    private static final String SAMPLE_PREFIX = "sample__";

    /**
     * Judge's data file prefix
     */
    private static final String SECRET_PREFIX = "secret__";

    private static final String LIST_OPTION = "--list";

    private ParseArguments arguments = new ParseArguments();

    /**
     * total data files examined/scanned
     */
    private int fileCount = 0;

    /**
     * Number of files copied
     */
    private int copyCount = 0;

    /**
     * Number of files renamed.
     */
    private int renameCount = 0;

    /**
     * Number of problems
     * 
     */
    private int dataDirsScanned = 0;

    /**
     * total number of sample files
     */
    private int totalNumberSampleFiles = 0;

    /**
     * Number of errors/exceptions during copy/renames
     */
    private int numberErrors = 0;

    private enum ProgramActions {
        /**
         * Update files, move/copy as needed
         */
        UDPDATE_FILES,
        /**
         * Write output script to rename/move files.
         */
        OUTPUT_SCRIPT
    }

    public static void usage() {

        String[] lines = {
                //
                "Usage: UpdateJudgesDataFileNames [options] cpDir ", //
                "", //
                "Purpose: Update files under data/samples and data/secret ", //
                "", //
                "Aa needed, copies and renames sample and seecret data files, so samples are tested first", //
                "", //

                "Where: ", //
                "", //
                LIST_OPTION + "  list if any files should be copied/renamed", //
                "", //
                "cpDir - a directory that has data under it, CDP config dir or a config/<short_name> dir", //

                "", "Ex. UpdateJudgesDataFileNames contests/sumit/config", //
                "  UpdateJudgesDataFileNames contests/sumit/config/hello", //
                "", //
        };

        for (String s : lines) {
            System.out.println(s);
        }

        System.out.println("OS = \"" + Utilities.getOSType() + "\" Unix? " + isUnix());
        System.out.println();

        VersionInfo info = new VersionInfo();
        System.out.println(info.getSystemVersionInfo());
    }

    private static void fatalError(String message, Exception e, int exitCode) {

        System.err.println(message);
        if (e != null) {
            e.printStackTrace(System.err);
        }
        System.err.println("Exiting with exit code = " + exitCode);
        System.exit(exitCode);
    }

    private static void fatalError(String message, int exitCode) {
        fatalError(message, null, exitCode);
    }

    public void run(String[] args) {

        String[] requiredArguments = new String[0];

        arguments = new ParseArguments(args, requiredArguments);

        if (args.length == 0 || arguments.isOptPresent("--help")) {
            usage();

        } else {

            String argList[] = arguments.getArgList();

            // Check for valid CDP or config dir
            for (String dir : argList) {

                File file = new File(dir);

                if (!file.isDirectory()) {
                    fatalError("No such directory: " + dir, 6);
                }

                String foundConfigDir = findConfigDirectory(dir);

                if (foundConfigDir == null) {
                    fatalError("Could not find config directory for: " + dir, 6);
                }
            }

            // First find out if there are problem directories, somewhere
            for (String dir : argList) {

                String configDirectory = findConfigDirectory(dir);

                System.err.println("Update data directories under " + configDirectory);

//                arguments.dumpArgs(System.out);

                if (arguments.isOptPresent(LIST_OPTION)) {
                    updateDataFiles(configDirectory, ProgramActions.OUTPUT_SCRIPT);
                } else {
                    updateDataFiles(configDirectory, ProgramActions.UDPDATE_FILES);
                }
            }
            printSummary();
        }
    }

    private void printSummary() {

        System.out.flush();
        System.err.flush();

        System.err.println("Data dirs scanned " + dataDirsScanned + ", Files to copy " + copyCount + //
                ", files to rename " + renameCount + ", There are " + totalNumberSampleFiles + " sample files");

        int totToUpdate = copyCount + renameCount + numberErrors;

        if (totToUpdate == 0) {
            System.err.println("NO actions required, all (" + fileCount + ") files copy/renamed.");
        } else {
            System.err.println("There are " + totToUpdate + " files updated.  There were " + fileCount + " files scanned.");
        }

        if (numberErrors != 0) {
            System.err.println("There were " + numberErrors + " copying/renaming files.");
        }
    }

    /**
     * Update data file names.
     * 
     * @param configDirectory
     *            CDP root directory
     * @param updateFileNames
     *            true change filenames, false output script to change filenames.
     */
    private void updateDataFiles(String configDirectory, ProgramActions action) {

        List<String> dataDirs = Utilities.getCDPDataDirectories(configDirectory);

        for (String dataDirectory : dataDirs) {

            dataDirsScanned++;

            /**
             * sample dir for problem
             */
            String sampleDataDirectory = dataDirectory + File.separator + ExportYAML.SAMPLE_DIRECTORY_NAME;

            /**
             * secret/jude's data for problem
             */
            String secretDataDirectory = dataDirectory + File.separator + ExportYAML.SECRET_DIRECTORY_NAME;

            /**
             * Copy renamed samples into secret dir.
             */

            String[] sampleInputs = Utilities.getFileNames(sampleDataDirectory, ".in");

            for (String sampIn : sampleInputs) {
                fileCount++;
                totalNumberSampleFiles++;

                String sampName = sampleDataDirectory + File.separator + sampIn;
                String newName = secretDataDirectory + File.separator + SAMPLE_PREFIX + sampIn;

                if (!Utilities.fileExists(newName)) {
                    copyFile(sampName, newName, action);
                }
            }

            String[] sampleAnswers = Utilities.getFileNames(sampleDataDirectory, ".ans");
            for (String sampAns : sampleAnswers) {
                fileCount++;
                totalNumberSampleFiles++;

                String sampName = sampleDataDirectory + File.separator + sampAns;
                String newName = secretDataDirectory + File.separator + SAMPLE_PREFIX + sampAns;

                if (!Utilities.fileExists(newName)) {
                    copyFile(sampName, newName, action);
                }
            }

            /**
             * Rename Judge's data files.
             */

            String[] secretInputs = Utilities.getFileNames(secretDataDirectory, ".in");
            for (String secretIn : secretInputs) {
                fileCount++;

                String secretName = secretDataDirectory + File.separator + secretIn;
                String newName = secretDataDirectory + File.separator + SECRET_PREFIX + secretIn;

                if (secretIn.startsWith(SECRET_PREFIX) || secretIn.startsWith(SAMPLE_PREFIX)) {
                    // ignore file if already has prefix
                    ;
                } else {
                    if (!Utilities.fileExists(newName)) {
                        renameFile(secretName, newName, action);
                    }
                }

            }

            String[] secretAnswers = Utilities.getFileNames(secretDataDirectory, ".ans");
            for (String secretAns : secretAnswers) {
                fileCount++;

                String secretName = secretDataDirectory + File.separator + secretAns;
                String newName = secretDataDirectory + File.separator + SECRET_PREFIX + secretAns;

                if (secretAns.startsWith(SECRET_PREFIX) || secretAns.startsWith(SAMPLE_PREFIX)) {
                    // ignore file if already has prefix
                    ;
                } else {
                    if (!Utilities.fileExists(newName)) {
                        renameFile(secretName, newName, action);
                    }
                }
            }
        }
    }

    private void renameFile(String filename, String newName, ProgramActions action) {

        renameCount++;

        switch (action) {
            case OUTPUT_SCRIPT:
                System.out.println(getRenameCommand() + " " + filename + " " + newName);
                break;

            default:

                Path source = Paths.get(filename);
                Path target = Paths.get(newName);

                try {
                    Files.move(source, target);
                } catch (IOException e) {
                    System.err.println("Unable to rename file: " + filename + " to " + newName + " " + e.getMessage());
                    renameCount--;
                    numberErrors++;
                }
                break;
        }
    }

    private void copyFile(String filename, String newName, ProgramActions action) {

        copyCount++;

        switch (action) {
            case OUTPUT_SCRIPT:
                System.out.println(getCopyCommand() + " " + filename + " " + newName);
                break;

            default:
                Path source = Paths.get(filename);
                Path target = Paths.get(newName);

                try {
                    Files.copy(source, target);
                } catch (IOException e) {
                    System.err.println("Unable to copy file: " + filename + " to " + newName + " " + e.getMessage());
                    copyCount--;
                    numberErrors++;
                }
                //
                break;
        }
    }

    /**
     * Return configuration directory or null if cannot find config directory
     * 
     * Expects a config directory name or a parent directory with a child directory config.
     * 
     * @param directory
     * @return null if config directory not found.
     */
    private String findConfigDirectory(String directory) {

        File file = new File(directory);

        if (!file.isDirectory()) {
            fatalError("No such directory: " + directory, 6);
        }

        /**
         * CDP Config directory
         */
        String configDir = file.getAbsolutePath();

        if (!"config".contentEquals(file.getName())) {
            String configTestDir = file.getAbsoluteFile() + File.separator + "config";
            File file2 = new File(configTestDir);

            if (!"config".contentEquals(file2.getName())) {
                return null;
            }

            configDir = configTestDir;
        }

        return configDir;
    }

    /**
     * OS specific copy file command.
     * 
     * @return command to copy file(s)
     */
    private static String getCopyCommand() {
        if (isUnix()) {
            return "cp -p";
        } else {
            return "COPY";
        }
    }

    /**
     * OS specific rename/move file command.
     * 
     * @return command to rename/move file(s)
     */
    private static String getRenameCommand() {
        if (isUnix()) {
            return "mv";
        } else {
            return "MOVE";
        }
    }

    private static boolean isUnix() {

        OSType osType = Utilities.getOSType();
        return osType != OSType.WINDOWS;
    }

    public static void main(String[] args) {
        try {
            new UpdateJudgesDataFileNames().run(args);
        } catch (Exception e) {
            fatalError("Exception " + e.getMessage(), e, 5);
        }
    }
}
