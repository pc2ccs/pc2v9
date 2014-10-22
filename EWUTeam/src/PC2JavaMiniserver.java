import java.io.File;

// PC2JavaMiniserver is designed to run in the background allowing PHP scripts to create java objects via
//	a PHP - Java bridge.
public class PC2JavaMiniserver {

	public static final String JAVABRIDGE_PORT = "50005";
	static final php.java.bridge.JavaBridgeRunner runner = php.java.bridge.JavaBridgeRunner
			.getInstance(JAVABRIDGE_PORT);

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
		System.out.println("JavaBridge is ready");
		runner.waitFor();
		System.exit(0);
	}
}
