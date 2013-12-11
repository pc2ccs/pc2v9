/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class LanguageNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public LanguageNotFoundException() {
		super("Language does not exist.");
	}
}