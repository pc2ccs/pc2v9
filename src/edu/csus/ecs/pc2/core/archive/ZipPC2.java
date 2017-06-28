package edu.csus.ecs.pc2.core.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.ParseArguments;

/**
 * Archive/backup of pc2 configuration, settings and generated files.
 * 
 * This class creates an archive containing the files required to store the current state of PC^2.
 * <P>
 *
 * @author pc2@ecs.csus.edu
 */

public class ZipPC2 {

    private String zipFileName = "";

    private String date = "";

    private Hashtable<String, String> filesToInclude = new Hashtable<String, String>();

    private Hashtable<String, Long> lastModified = new Hashtable<String, Long>();

    private String comment = "";

    public ZipPC2() {
        super();

        zipFileName = createZipFileName();

        // Remove potential blanks from filename

        String newname = zipFileName;
        while (newname.indexOf(" ") > 0) {
            newname = newname.replace(' ', '-');
        }
        zipFileName = newname;

        date = getDate();
        zipFileName = date + "-" + zipFileName + ".zip";
    }

    /**
     * If the ini file exists lookup the archive/site value to get the base zip filename (defaults to pc2archive).
     *
     * @return the base zip file name
     */
    private String createZipFileName() {
        zipFileName = "pc2archive";

        if (IniFile.isFilePresent()) {
            new IniFile();

            if (IniFile.containsKey("global.archive")) {
                zipFileName = IniFile.getValue("global.archive");
            } else if (IniFile.containsKey("server.site")) {
                zipFileName = IniFile.getValue("server.site");
            } else if (IniFile.containsKey("client.site")) {
                zipFileName = IniFile.getValue("client.site");
            } else {
                zipFileName = "pc2archive";
            }
        }

        return zipFileName;
    }

    /**
     * Adds directory dirName and contents if dirName exists
     *
     * @param dirName
     *            java.lang.String
     */
    public void addDirToList(String dirName) {
        File file = new File(dirName);
        String fileName = "";
        if (file.exists() && file.isDirectory()) {
            String[] fileList = file.list();
            for (int i = 0; i < fileList.length; i++) {
                fileName = dirName + File.separator + fileList[i];
                file = new File(fileName);
                if (file.isDirectory()) {
                    addDirToList(fileName);
                } else {
                    addFileToList(fileName);
                }
            }
        }
    }

    /**
     * Adds file fileName to list of files to archive if fileName exists
     *
     * @param fileName
     *            java.lang.String
     */
    public void addFileToList(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            filesToInclude.put(fileName, fileName);
            lastModified.put(fileName, new Long(file.lastModified()));
        }
    }

    /**
     * Builds list of files to archive.
     */
    private void buildFileList() {

        /**
         * List of file extensions to pack.
         */
        String[] simpleFileExtensions = { "log", "html", "ini", "set", "tab",
                "tpl", "properties" };

        File file = new File(".");
        String[] dirEntryList = file.list();

        for (int i = 0; i < dirEntryList.length; i++) {
            String dirEntryName = dirEntryList[i];
            file = new File(dirEntryName);

            for (String name : simpleFileExtensions) {
                if (dirEntryName.endsWith(name)) {
                    addFileToList(dirEntryName);
                }
            }

            if (dirEntryName.equalsIgnoreCase("logs")) {
                addDirToList(dirEntryName);
            } else if (dirEntryName.equalsIgnoreCase("packets")) {
                addDirToList(dirEntryName);
            } else if (dirEntryName.equalsIgnoreCase("html")) {
                addDirToList(dirEntryName);
            } else if (dirEntryName.startsWith("db.")) {
                addDirToList(dirEntryName);
            } else if (dirEntryName.startsWith("execute")) {
                addDirToList(dirEntryName);
            } else if (dirEntryName.startsWith("inputValidate")) {
                addDirToList(dirEntryName);
            } else if (dirEntryName.equalsIgnoreCase("reports")) {
                addDirToList(dirEntryName);
            } else if (dirEntryName.equalsIgnoreCase("old")) {
                addDirToList(dirEntryName);
            } else if (dirEntryName.equalsIgnoreCase("data")) {
                addDirToList(dirEntryName);
            } else if (dirEntryName.equalsIgnoreCase("profiles")) {
                addDirToList(dirEntryName);
            } else if (dirEntryName.equalsIgnoreCase("results")) {
                addDirToList(dirEntryName);
            }
        }

        addFileToList("score.dat");
        addFileToList("results.xml");

        // Baylor upload file, only found on scoreboard module at this time.

        addFileToList("pc2export.dat");

    }

    /**
     * Creates archive based on list of files created by buildFileList()
     */
    public String createArchive() {
        byte[] b = new byte[1024];
        try {
            buildFileList();
            String[] fileList = sortFileList();
            if (fileList == null || fileList.length <= 0) {
                writelog("No files to archive");
                return null;
            }
            File dir = new File("archive");
            if (!dir.exists()) {
                dir.mkdir();
            }
            if (dir.exists() && dir.isDirectory()) {
                zipFileName = "archive" + File.separatorChar + zipFileName;
            }
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(
                    zipFileName));
            zip.setComment(comment);
            String inputFile = "";
            Object o2 = null;
            int seq = 0;
            int count = 0;
            writelog("Creating " + zipFileName);
            for (int i = 0; i < fileList.length; i++) {
                try {
                    inputFile = fileList[i];
                    writelog("Adding " + inputFile);
                    FileInputStream in = new FileInputStream(inputFile);
                    ZipEntry ze = new ZipEntry(inputFile);
                    zip.putNextEntry(ze);
                    while (in.available() > 0) {
                        seq++;
                        count = in.available();
                        if (count >= 1024) {
                            count = 1024;
                            in.read(b);
                        } else {
                            in.read(b, 0, count);
                        }
                        zip.write(b, 0, count);
                    }
                    if (filesToInclude == null) {
                        writelog("filesToInclude is null");
                    }
                    o2 = lastModified.get(inputFile);
                    if (o2 != null) {
                        ze.setTime(((Long) (o2)).longValue());
                    } else {
                        writelog("Last modified date for " + inputFile
                                + " could not be determined");
                    }
                    in.close();
                    zip.closeEntry();
                    count = 0;
                    seq = 0;
                    inputFile = "";
                } catch (Exception e) {
                    writelog("error (" + inputFile + " seq=" + seq + ",count="
                            + count + ") " + e.getMessage(), e);
                }
            }
            writelog("Closing " + zipFileName);
            zip.close();
        } catch (Exception e) {
            writelog("error creating " + zipFileName + ": " + e.getMessage(), e);
        }
        return zipFileName;
    }

    /**
     * Creates archive based on list of files created by buildFileList()
     */
    public String createArchive(Vector<String> listOFiles, boolean packStdFilesToo) {
        byte[] b = new byte[1024];

        File file;

        try {
            if (listOFiles == null) {
                writelog("createArchive(Vector, boolean), Vector is null ");
                return null;
            }

            if (packStdFilesToo) {
                buildFileList();
            }

            if (filesToInclude.size() == 0) {
                writelog("createArchive(Vector, boolean), no files to archive ");
                return null;
            }

            for (int i = 0; i < listOFiles.size(); i++) {
                String filename = (String) listOFiles.elementAt(i);
                file = new File(filename);

                if (file.isFile()) {
                    addFileToList(filename);
                } else if (file.isDirectory()) {
                    addDirToList(filename);
                }
            }

            String[] fileList = sortFileList();
            if (fileList == null || fileList.length <= 0) {
                writelog("No files to archive");
                return null;
            }
            File dir = new File("archive");
            if (!dir.exists()) {
                dir.mkdir();
            }
            if (dir.exists() && dir.isDirectory()) {
                zipFileName = "archive" + File.separatorChar + zipFileName;
            }
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(
                    zipFileName));
            zip.setComment(comment);
            String inputFile = "";
            Object o2 = null;
            int seq = 0;
            int count = 0;
            writelog("Creating " + zipFileName);
            for (int i = 0; i < fileList.length; i++) {
                try {
                    inputFile = fileList[i];
                    writelog("Adding " + inputFile);
                    FileInputStream in = new FileInputStream(inputFile);
                    ZipEntry ze = new ZipEntry(inputFile);
                    zip.putNextEntry(ze);
                    while (in.available() > 0) {
                        seq++;
                        count = in.available();
                        if (count >= 1024) {
                            count = 1024;
                            in.read(b);
                        } else {
                            in.read(b, 0, count);
                        }
                        zip.write(b, 0, count);
                    }
                    if (filesToInclude == null) {
                        writelog("filesToInclude is null");
                    }
                    o2 = lastModified.get(inputFile);
                    if (o2 != null) {
                        ze.setTime(((Long) (o2)).longValue());
                    } else {
                        writelog("Last modified date for " + inputFile
                                + " could not be determined");
                    }
                    in.close();
                    zip.closeEntry();
                    count = 0;
                    seq = 0;
                    inputFile = "";
                } catch (Exception e) {
                    writelog("error (" + inputFile + " seq=" + seq + ",count="
                            + count + ") " + e.getMessage(), e);
                }
            }
            writelog("Closing " + zipFileName);
            zip.close();
        } catch (Exception e) {
            writelog("CreateArchive: error creating " + zipFileName + ": "
                    + e.getMessage(), e);
            return null;
        }
        return zipFileName;
    }

    /**
     * Returns date in YYYYMMDDHH format, also sets zipfile comment
     *
     * @return java.lang.String
     */
    private String getDate() {
        TimeZone tz = TimeZone.getTimeZone("GMT");
        GregorianCalendar cal = new GregorianCalendar(tz);
        comment = "Create by PC^2 " + new VersionInfo().getSystemVersionInfo()
                + " " + cal.getTime().toString();
        StringBuffer dateSB;
        DecimalFormat df = new DecimalFormat("00");
        StringBuffer t = new StringBuffer();
        dateSB = df.format(cal.get(Calendar.YEAR), t, new FieldPosition(0));
        dateSB = df
                .format(cal.get(Calendar.MONTH) + 1, t, new FieldPosition(0));
        dateSB = df.format(cal.get(Calendar.DAY_OF_MONTH), t,
                new FieldPosition(0));
        dateSB = df.format(cal.get(Calendar.HOUR_OF_DAY), t, new FieldPosition(
                0));
        dateSB = df.format(cal.get(Calendar.MINUTE), t, new FieldPosition(0));
        return (dateSB.toString());
    }

    /**
     * @param args
     */
    public static void main(java.lang.String[] args) {
        ParseArguments pa = null;
        try {
            String[] reqArgs = { "--ini" };
            pa = new ParseArguments(args, reqArgs);

            if (pa.isOptPresent("--help") || pa.isOptPresent("-h")) {
                // print help
                System.out.println();
                System.out.println("Usage:  ZipPC2 [OPTION] [dirs|files]");
                System.out
                        .println("Creates a zip of the specified dirs & files");
                System.out.println();
                System.out
                        .println("  -h, --help     display this help and exit");
                System.out
                        .println("      --ini FILE file or URL for (defaults to pc2v8.ini)");
                System.out
                        .println("  -v, --version  output version information and exit");
                System.out.println();
                System.exit(0);
            }
            if (pa.isOptPresent("-v") || pa.isOptPresent("--version")) {
                VersionInfo sri = new VersionInfo();
                System.out.println(sri.getSystemName());
                System.out.println(sri.getContactEMail());
                System.out.println(sri.getSystemVersionInfo());
                System.exit(0);
            }
            if (pa.isOptPresent("--ini")) {
                if (pa.getOptValue("--ini") != null
                        && pa.getOptValue("--ini").length() > 0) {
                    IniFile.setIniURLorFile(pa.getOptValue("--ini"));
                }
            }

        } catch (Exception ex99) {
            writelog("Exception ", ex99);
            System.exit(99);
        }

        ZipPC2 aZipPC2;
        aZipPC2 = new ZipPC2();

        for (int i = 0; i < args.length; i++) {
            String filename = args[i];
            File file = new File(filename);

            if (file.isFile()) {
                aZipPC2.addFileToList(filename);
            } else if (file.isDirectory()) {
                aZipPC2.addDirToList(filename);
            }
        }

        String arcFileName = aZipPC2.createArchive();

        if (arcFileName == null) {
            writelog("Could not create archive ");
        }

    }

    /**
     * 
     * @author Douglas A. Lane
     */
    
    // $HeadURL$
    protected class StringIgnoreCaseComparator implements Comparator<String>, Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 8901841329708366388L;

        public int compare(String s1, String s2) {
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }

    /**
     * Returns a sorted list of files to archive
     *
     * @return java.lang.String[]
     */
    private String[] sortFileList() {
        Enumeration<String> enumeration = filesToInclude.elements();
        String[] strings = new String[filesToInclude.size()];
        int i = 0;
        while (enumeration.hasMoreElements()) {
            String s = enumeration.nextElement();
            if (s == null) {
                continue;
            }
            strings[i++] = s;
        }
        if (strings.length <= 1) {
            return strings;
        }

        Arrays.sort(strings, new StringIgnoreCaseComparator());

        return strings;
    }

    /**
     * Adds directory dirName and contents if dirName exists
     *
     * @param dirName
     *            java.lang.String
     */
    private static void writelog(String str) {
        writelog(str, null);
    }

    /**
     * Adds directory dirName and contents if dirName exists
     *
     * @param dirName
     *            java.lang.String
     */
    private static void writelog(String str, Exception ex) {

        Date d = new Date();

        System.err.println(d + " " + str);
        if (ex != null) {
            ex.printStackTrace(System.err);
        }
    }
}
