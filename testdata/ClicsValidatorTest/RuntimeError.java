// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
/**
 * Program that returns a "run time error".
 * 
 * Source from: https://pc2.ecs.csus.edu/bugzilla/show_bug.cgi?id=1351#c2
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class RuntimeError {

	/**
	 * Create condition that emulates a run time error.
	 * 
	 * In pc2 if the return code is -1 that is considered a run-time error.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Exiting with exit code '-1'");
		System.err.println("Exiting with exit code '-1'");
		System.exit(-1);
	}
}
