// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
/**
 * Program that causes a RTE.
 * 
 * Source: https://pc2.ecs.csus.edu/bugzilla/show_bug.cgi?id=1351#c2
 * 
 * @author PC^2 Team, pc2@ecs.csus.edu
 */
public class RuntimeError {

    /**
     * Write lines and exit with -1 exit code.
     * 
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Exiting with exit code '-1'");
        System.err.println("Exiting with exit code '-1'");
        System.exit(-1);
    }
}
