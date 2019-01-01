package edu.csus.ecs.pc2.tools;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.ParseArguments;

/**
 * PC2 Tools - pc2 utilities front end.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class PC2Tools {

    private static void usage() {

        String[] usage = { //
        //
                "Usage PC2Tools [--help] [option] params", //
                "", //
                "--gen   - generate passwords to stdout, use --help --gen for usage", //
                "--help  - this message", //
                "", };

        for (String s : usage) {
            System.out.println(s);
        }

        String[] multiLineVersion = new VersionInfo().getSystemVersionInfoMultiLine();
        for (String string : multiLineVersion) {
            System.out.println(string);
        }
    }

    public void run(String[] args) {

        ParseArguments parseArguments = new ParseArguments(args);

        if (parseArguments.isOptPresent("--gen")) {

            PasswordGenerator.main(args);

        } else if (args.length == 0 || args[0].equals("--help")) {
            usage();
            System.exit(4);
        } else {
            System.err.println("Unknown option, check usage using --help ");
        }
    }

    public static void main(String[] args) {
        new PC2Tools().run(args);
    }
}
