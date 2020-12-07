package config;

import edu.csus.ecs.pc2.core.log.Log;


/**
 * This class provides Logging services for the WTI-API project.  Clients can obtain an instance of the WTI logger
 * by invoking method {@link #getLogger()}, which returns a singleton {@link edu.csus.ecs.pc2.core.log.Log} object.
 * 
 * @author John Clevenger, PC^2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class Logging {

	//the singleton PC2 Log object 
	static private Log logger = null;
	
	
	/**
	 * Returns a singleton instance of the {@link Log} class which can be used for logging WTI messages.  
	 *  
	 * @return a {@link Log} object on which logging operations can be invoked
	 */
	static public Log getLogger() {
		if (logger==null) {
			logger = new Log("WTILog");
		}
		return logger;
	}

}
