import java.io.File;
import java.io.IOException;

import edu.csus.ecs.pc2.VersionInfo;

// PC2JavaMiniserver is designed to run in the background allowing PHP scripts to create java objects via
//	a PHP - Java bridge.
public class PC2JavaMiniserver {

	public static final String JAVABRIDGE_PORT = "50005";
	static final php.java.bridge.JavaBridgeRunner runner = php.java.bridge.JavaBridgeRunner
			.getInstance(JAVABRIDGE_PORT);
    private static VersionInfo versionInfo = new VersionInfo();

	// run server
	public static void main(String[] args) throws Exception {
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
		try {
			System.out.println("Starting JavaBridge version "+getVersionNumber()+"-"+getBuildNumber()+" with PC^2 version "+getVersionNumberforPC2()+"-"+getBuildNumberForPC2());
		} catch (IOException e) {
			System.out.println("Trouble reading the version and build numbers "+e.getMessage());
		}
		System.out.println("JavaBridge is ready");
		runner.waitFor();
		System.exit(0);
	}
    /**
     * @return build number for pc2.
     */
    public static String getBuildNumberForPC2()
    {
        return versionInfo.getBuildNumber();
    }

    /**
     * 
     * @return
     */
    public static String getVersionNumberforPC2()
    {
        /*
         * $ cat MANIFEST.MF 
         * Manifest-Version: 1.0
         * Ant-Version: Apache Ant 1.7.0
         * Created-By: 1.5.0_17-b04 (Sun Microsystems Inc.)
         * Specification-Version: 9.3beta
         * Implementation-Title: CSUS Programming Contest Control System
         * Implementation-Version: 2697
         * Built-On: Wednesday, September 25 2013 03:33 UTC
         * Built-On-Date: 20130925
         * Main-Class: edu.csus.ecs.pc2.Starter
         */

        return versionInfo.getVersionNumber();
    }

    public static String getBuildNumber() throws IOException
    {
        return getManifestValue("Implementation-Version");
    }

    public static String getVersionNumber() throws IOException
    {
        /*
         * $ cat MANIFEST.MF 
         * Manifest-Version: 1.0
         * Ant-Version: Apache Ant 1.7.0
         * Created-By: 24.0-b56 (Oracle Corporation)
         * Main-Class: PC2JavaMiniserver
         * Specification-Version: 2.0
         * Implementation-Title: EWU Web Team Client
         * Implementation-Version: 60
         * Built-On: Saturday, October 26 2013 06:21 UTC
         * Class-Path: pc2.jar JavaBridge.jar
         */
        return getManifestValue("Specification-Version");
    }

    public static String getManifestValue(String name) throws IOException
    {
        Package corePackage = Package.getPackage(null);
        if (corePackage != null) {
            String manifestSpecificationVersion = corePackage
                    .getSpecificationVersion();
            String manifestImplementationVersion = corePackage
                    .getImplementationVersion();
            if (name.equalsIgnoreCase("Specification-Version")) {
            	return manifestSpecificationVersion;
            }
            if (name.equalsIgnoreCase("Implementation-Version")) {
            	return manifestImplementationVersion;
            }
        }
        return "unknown";
    }
}
