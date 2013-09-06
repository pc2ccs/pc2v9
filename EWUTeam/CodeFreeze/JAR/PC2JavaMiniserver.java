
// PC2JavaMiniserver is designed to run in the background allowing PHP scripts to create java objects via
//	a PHP - Java bridge.

/**
 * 
 * @author EWU Senior Project Team 2013
 * @version $Id$
 */

// $HeadURL$
public class PC2JavaMiniserver
{
	public static final String JAVABRIDGE_PORT="3306";
	static final php.java.bridge.JavaBridgeRunner runner = php.java.bridge.JavaBridgeRunner.getInstance(JAVABRIDGE_PORT);
	
	//run server
	public static void main(String [] args) throws Exception
	{
		runner.waitFor();
		System.exit(0);
	}
}
