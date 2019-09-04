// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
import java.io.*;

/**
 * A Java program which throws a Runtime Error, for testing.
 * 
 * @author John
 *
 */
public class ISumitRuntimeError {
    public static void main(String[] args) {
        System.out.println("Exiting with exit code '-1'");
        System.err.println("Exiting with exit code '-1'");
        System.exit(-1);
    }
}
