// Copyright (C) 1989-2020 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.wti.core.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
 * <P>
 * This class creates an archive containing the files required to store the
 * current state of PC^2 WTI.
 */

public class ZipWTI {

	/**
	 * Target directory for .zip file
	 */
	private static final String ARCHIVE_DIR_NAME = "archive";

	private static final String ZIP_PREFIX = "pc2wtiarchive";

	private String zipFileName = "";

//	private String date = "";

	private Hashtable<String, String> filesToInclude = new Hashtable<String, String>();

	private Hashtable<String, Long> lastModified = new Hashtable<String, Long>();

	/**
	 * Max buffer size in bytes.   Used to pack/create files.
	 */
	private final int MAXIUM_BUFFER_SIZE = 65536;

	public ZipWTI() {
		super();

        zipFileName = createZipFileName();

        // Remove potential blanks from filename

        String newname = zipFileName;
        while (newname.indexOf(" ") > 0) {
            newname = newname.replace(' ', '-');
        }
        zipFileName = newname;

        zipFileName = getDate() + "-" + zipFileName + ".zip";
    }

	/**
	 * If the ini file exists lookup the archive/site value to get the base zip
	 * filename (defaults to {@value #ZIP_PREFIX}).
	 *
	 * @return the base zip file name
	 */
	private String createZipFileName() {
		zipFileName = ZIP_PREFIX;

		if (IniFile.isFilePresent()) {
			new IniFile();

			if (IniFile.containsKey("global.archive")) {
				zipFileName = IniFile.getValue("global.archive");
			} else {
				zipFileName = ZIP_PREFIX;
			}
		}

		return zipFileName;
	}

	/**
	 * Adds directory dirName and contents if dirName exists
	 *
	 * @param dirName java.lang.String
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
	 * @param fileName java.lang.String
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
	 * 
	 * Uses {@link #addFileToList(String)} to add files created or used by pc2wti. 
	 * 
	 */
	private void buildFileList() {

		/**
		 * List of file extensions to pack.
		 */
		String[] simpleFileExtensions = { "log", "html", "ini", "set", "tab", "tpl", "properties" };

		/**
		 * Masks with glob pattern
		 */
		String[] FILE_MASKS = { "opts.*", "*.sh", "*.log", "nohup*" };

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

			for (String mask : FILE_MASKS) {
				if (matchesGlob(mask, dirEntryName)) {
					addFileToList(dirEntryName);
				}
			}

			if (dirEntryName.equalsIgnoreCase("logs")) {
				addDirToList(dirEntryName);
			} else if (dirEntryName.equalsIgnoreCase("packets")) {
				addDirToList(dirEntryName);
			} else if (dirEntryName.equalsIgnoreCase("html")) {
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
			} else if (dirEntryName.startsWith("db.")) {  // startsWith
				addDirToList(dirEntryName);
			} else if (dirEntryName.startsWith("execute")) {
				addDirToList(dirEntryName);
			} else if (dirEntryName.startsWith("inputValidate")) {
				addDirToList(dirEntryName);
			}
		}
		
		String [] otherFiles = {
				"score.dat", //
				"results.xml", //
				// Baylor upload file, only found on scoreboard module at this time.
				"pc2export.dat", //
				
		};
		
		for (String otherFile : otherFiles) {
			addFileToList(otherFile);
		}

	}

	/**
	 * Match filename to file glob mask.
	 * 
	 * @param mask
	 * @param dirEntryName
	 * @return true if dirEntryName matches glob matches
	 */
	protected boolean matchesGlob(String mask, String dirEntryName) {
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + mask);
		Path path = Paths.get(dirEntryName);
		return matcher.matches(path);
	}

	/**
	 * Creates archive based on list of files created by buildFileList()
	 */
	public String createArchive() {
		
		byte[] byteBuffer = new byte[MAXIUM_BUFFER_SIZE];
		
		try {
			buildFileList();
			String[] fileList = sortFileList();
			if (fileList == null || fileList.length <= 0) {
				writelog("No files to archive");
				return null;
			}
			File dir = new File(ARCHIVE_DIR_NAME);
			if (!dir.exists()) {
				dir.mkdir();
			}
			if (dir.exists() && dir.isDirectory()) {
				zipFileName = ARCHIVE_DIR_NAME + File.separatorChar + zipFileName;
			}
			ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFileName));
			zip.setComment(getComment());
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
						if (count >= MAXIUM_BUFFER_SIZE) {
							count = MAXIUM_BUFFER_SIZE;
							in.read(byteBuffer);
						} else {
							in.read(byteBuffer, 0, count);
						}
						zip.write(byteBuffer, 0, count);
					}
					if (filesToInclude == null) {
						writelog("filesToInclude is null");
					}
					o2 = lastModified.get(inputFile);
					if (o2 != null) {
						ze.setTime(((Long) (o2)).longValue());
					} else {
						writelog("Last modified date for " + inputFile + " could not be determined");
					}
					in.close();
					zip.closeEntry();
					count = 0;
					seq = 0;
					inputFile = "";
				} catch (Exception e) {
					writelog("error (" + inputFile + " seq=" + seq + ",count=" + count + ") " + e.getMessage(), e);
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
	 * 
	 * @param listOFiles
	 * @param packStdFilesToo if true, pack standard pc2 files see {@link #buildFileList())
	 * @return
	 */
	public String createArchive(Vector<String> listOFiles, boolean packStdFilesToo) {
		byte[] b = new byte[MAXIUM_BUFFER_SIZE];

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
			File dir = new File(ARCHIVE_DIR_NAME);
			if (!dir.exists()) {
				dir.mkdir();
			}
			if (dir.exists() && dir.isDirectory()) {
				zipFileName = ARCHIVE_DIR_NAME + File.separatorChar + zipFileName;
			}
			ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFileName));
			zip.setComment(getComment());
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
						if (count >= MAXIUM_BUFFER_SIZE) {
							count = MAXIUM_BUFFER_SIZE;
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
						writelog("Last modified date for " + inputFile + " could not be determined");
					}
					in.close();
					zip.closeEntry();
					count = 0;
					seq = 0;
					inputFile = "";
				} catch (Exception e) {
					writelog("error (" + inputFile + " seq=" + seq + ",count=" + count + ") " + e.getMessage(), e);
				}
			}
			writelog("Closing " + zipFileName);
			zip.close();
		} catch (Exception e) {
			writelog("CreateArchive: error creating " + zipFileName + ": " + e.getMessage(), e);
			return null;
		}
		return zipFileName;
	}

	private String getComment() {
		TimeZone tz = TimeZone.getTimeZone("GMT");
		GregorianCalendar cal = new GregorianCalendar(tz);
		return  "Created by PC^2 WTI " + new VersionInfo().getSystemVersionInfo() + " " + cal.getTime().toString();
	}

	/**
	 * Returns GMT date/time in YYYYMMDDHH format. 
	 *
	 * @return java.lang.String
	 */
    private String getDate() {
        TimeZone tz = TimeZone.getTimeZone("GMT");
        GregorianCalendar cal = new GregorianCalendar(tz);
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
    	formatter.setTimeZone(tz);
    	return formatter.format(cal.getTime());
    }

	/**
	 * @param args
	 */
	public static void main(java.lang.String[] args) {

		processArgs (args);
		
	
	}

	private static void processArgs(String[] args) {
		
		try {
			ParseArguments pa = null;
			try {
				String[] reqArgs = { "--ini" };
				pa = new ParseArguments(args, reqArgs);

				if (pa.isOptPresent("--help") || pa.isOptPresent("-h")) {
					// print help
					usage();
					
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
					if (pa.getOptValue("--ini") != null && pa.getOptValue("--ini").length() > 0) {
						IniFile.setIniURLorFile(pa.getOptValue("--ini"));
					}
				}

			} catch (Exception ex99) {
				writelog("Exception ", ex99);
				System.exit(99);
			}

			ZipWTI aZipPC2;
			aZipPC2 = new ZipWTI();

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

			
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.err.println("Exception message: "+e.getMessage());
		}
	}

	private static void usage() {

		String [] usageMessage = {
				"Usage:  ZipWTI [OPTION] [dirs|files]", //
				"Creates a date stamped zip file in "+ARCHIVE_DIR_NAME+" of files used and created by pc2wti", //
				"",
				"  -h, --help     display this information and exit", //
				"      --ini FILE ini file or URL for settings (or uses pc2 default .ini file name) ",//
				"  -v, --version  output version information and exit", //

		};

		System.out.println();
		for (String messageLine : usageMessage) {
			System.out.println(messageLine);
		}
		System.out.println();

	}

	/**
	 * String compare case in-sensitive.
	 * 
	 * @author Douglas A. Lane <pc2@ecs.csus.edu>
	 *
	 */
	protected class StringIgnoreCaseComparator implements Comparator<String>, Serializable {

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
	 * @param dirName java.lang.String
	 */
	private static void writelog(String str) {
		writelog(str, null);
	}

	private static void writelog(String str, Exception ex) {
		Date curDate = new Date();
		System.err.println(curDate + " " + str);
		if (ex != null) {
			ex.printStackTrace(System.err);
		}
	}
}
