package edu.csus.ecs.pc2.tools;

import java.util.List;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.ParseArguments;
import edu.csus.ecs.pc2.tools.PasswordGenerator;
import edu.csus.ecs.pc2.tools.PasswordType2;

/**
 * PC2 Tools - pc2 utilities.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class PC2Tools {

    public PC2Tools(String[] args) {

        ParseArguments parseArguments = new ParseArguments(args);

        if (parseArguments.isOptPresent("--gen")) {

            if (parseArguments.getArgCount() == 0)
            {
                printArray(PasswordGenerator.generatePasswords(120, PasswordType2.LETTERS_AND_DIGITS, 8, null));
            } else {

                int num = Integer.parseInt(parseArguments.getArg(0));

                PasswordType2 type = PasswordType2.LETTERS_AND_DIGITS;
                int passwordLength = 8;
                String prefix = null;

                String argValue;

                if (parseArguments.getArgCount() > 1) {
                    // 2 type

                    argValue = parseArguments.getArg(1).toUpperCase();

                    switch (argValue) {
                        case "-":
                            break;
                        case "D":
                            type = PasswordType2.DIGITS;
                            break;
                    }
                }

                if (parseArguments.getArgCount() > 2) {
                    // 3 password length

                    argValue = parseArguments.getArg(2);
                    if (!"-".equals(argValue)) {
                        passwordLength = Integer.parseInt(argValue);
                    }
                }

                if (parseArguments.getArgCount() > 3) {
                    // 4 prefix

                    argValue = parseArguments.getArg(3);
                    prefix = argValue;
                }

                printArray(PasswordGenerator.generatePasswords(num, type, passwordLength, prefix));
            }

        } else {
            System.err.println("Illegal command line ");
        }

    }

    private void printArray(List<String> list) {
        for (String string : list) {
            System.out.println(string);
        }
    }

    private static void usage() {

        String[] usage = { //
        // 
                "Usage PC2Tools [--help] --gen num type length prefix ", //
                "", //
                "num - number of passwords to generate", //
                "type - type of password, default letters&digits, d digits, j joe", //
                "length - password length, default 8", //
                "prefix - prefix for all passwords", //
        };

        for (String s : usage) {
            System.out.println(s);
        }

        String[] multiLineVersion = new VersionInfo().getSystemVersionInfoMultiLine();
        for (String string : multiLineVersion) {
            System.out.println(string);
        }
    }

    public static void main(String[] args) {
        if (args.length == 0 || args[0].equals("--help")) {
            usage();
            System.exit(4);
        }

        new PC2Tools(args);

    }
}
