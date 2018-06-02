import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.pc2.ewuteam.VersionInfo;

/**
 * PC2JavaMiniserver runs in the background allowing PHP scripts to create java objects via
 * a PHP - Java bridge.
 */
public class PC2JavaMiniserver {

	public static final String JAVABRIDGE_PORT = "50005";
	
	static final php.java.bridge.JavaBridgeRunner runner = php.java.bridge.JavaBridgeRunner
			.getInstance(JAVABRIDGE_PORT);
	
	private static final String DATE_TIME_FORMAT_STRING = "yyyyMMdd-hhmmss";

	private static final SimpleDateFormat formatterYYYYMMDDHHMMSS = new SimpleDateFormat(DATE_TIME_FORMAT_STRING);


	// run server
	public static void main(String[] args) throws Exception {
	    
	    VersionInfo versionInfo = new VersionInfo();
	    
	    try {
	    	startLog(); // start StartLog
		} catch (Exception e) {
			fatalError("Unable to start log "+e, e, 6);
		}
	    
	    
	    String uploadDirectoryName = ".." + File.separator + "uploads"; 
	    
		File uploadsDir = new File(uploadDirectoryName);
		if (!uploadsDir.exists()) {
			
			System.err.println("Error: " + uploadsDir.getAbsolutePath()
					+ " does not exist");
			StaticLog.warning("Error: " + uploadsDir.getAbsolutePath() + " does not exist");
		} else {
			if (!uploadsDir.canWrite()) {
				System.err.println("Error: cannot write to " + uploadsDir.getAbsolutePath());
				StaticLog.warning("Error: cannot write to " + uploadsDir.getAbsolutePath());
			}
			// these will fail quietly :(
			uploadsDir.setWritable(true, false);
			uploadsDir.setExecutable(true, false);
		}
		for (String verline : versionInfo.getSystemVersionInfoMultiLine()) {
            System.out.println(verline);
            StaticLog.info(verline);
        }
		System.out.println();
        System.out.println("Using port " + JAVABRIDGE_PORT);
        System.out.println();
		System.out.println("JavaBridge is ready");
		
		StaticLog.info("Using port " + JAVABRIDGE_PORT);
		StaticLog.info("JavaBridge is ready");
		
		runner.waitFor();
		System.exit(0);
	}

	private static void fatalError(String message, Exception e, int exitCode) {

		System.err.println(message);
		if (e != null) {
			e.printStackTrace(System.err);
		}
		System.err.println("Exiting with exit code = " + exitCode);
		System.exit(exitCode);
	}

	/**
	 * Start static log.
	 * 
	 * Start pc2's static log to provide output log file.
	 */
	private static void startLog() {

		String directoryName = Log.LOG_DIRECTORY_NAME;
		
		Utilities.insureDir(directoryName);

		String logFileName = "ewteam." + getDateTimeString() + ".log";
		
		System.out.println("Writing to log file "+logFileName);

		Log log = new Log(directoryName, logFileName);
		StaticLog.setLog(log);
	}

	/**
	  * Return date/time string for now.
	  *
	  * Uses format {@value #FORMAT_YYYY_MM_DD_HH_MM_SS}.
	  */
	public static String getDateTimeString() {
		return formatterYYYYMMDDHHMMSS.format(new Date());
	}

}
