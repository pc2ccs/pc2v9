import java.io.File;

import edu.csus.pc2.ewuteam.VersionInfo;

// 
/**
 * PC2JavaMiniserver is designed to run in the background allowing PHP scripts to create java objects via
 * a PHP - Java bridge.
 * 
 * @version $Id$
 */

// $HeadURL$
public class PC2JavaMiniserver {

	public static final String JAVABRIDGE_PORT = "50005";
	static final php.java.bridge.JavaBridgeRunner runner = php.java.bridge.JavaBridgeRunner
			.getInstance(JAVABRIDGE_PORT);

	// run server
	public static void main(String[] args) throws Exception {
	    
	    VersionInfo versionInfo = new VersionInfo();
	    
		File uploadsDir = new File(".." + File.separator + "uploads");
		if (!uploadsDir.exists()) {
			System.err.println("Error: " + uploadsDir.getAbsolutePath()
					+ " does not exist");
		} else {
			if (!uploadsDir.canWrite()) {
				System.err.println("Error: cannot write to "
						+ uploadsDir.getAbsolutePath());
			}
			// these will fail quietly :(
			uploadsDir.setWritable(true, false);
			uploadsDir.setExecutable(true, false);
		}
		for (String verline : versionInfo.getSystemVersionInfoMultiLine()) {
            System.out.println(verline);
        }
		System.out.println();
        System.out.println("Using port " + JAVABRIDGE_PORT);
        System.out.println();
		System.out.println("JavaBridge is ready");
		runner.waitFor();
		System.exit(0);
	}
}
