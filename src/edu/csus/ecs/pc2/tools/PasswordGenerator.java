// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.ParseArguments;

/**
 * Password generator.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class PasswordGenerator {

    /**
     * Letters and numbers excluding chars that would be confused like 1, I, and l.
     */
    public static final String ALL_CHARS = "23456789ABCDEFGHJKLMNPRSTUVWXYZabcdefghijkopqrstuvwxyz";

    /**
     * Only letters, excluding chars that would be confused like 1, I, and l.
     */
    public static final String LETTERS = "ABCDEFGHJKLMNPRSTUVWXYZabcdefghijkopqrstuvwxyz";

    private static Random random = new Random(new Date().getTime());

    private static int DEFAULT_PASSWORD_NUMBER = 210;

    /**
     * Generate a list of passwords.
     * 
     * @param count
     *            number of passwords to generate
     * @param passwordType
     *            password type. JOE and NONE are not allowed.
     * @param length
     *            length in characters for each password
     * @param prefix
     *            an optional prefix for every password
     * @return list of passwords.
     */
    public static List<String> generatePasswords(int count, PasswordType2 passwordType, int length, String prefix) {

        if (PasswordType2.JOE.equals(passwordType)) {
            // Not allowed because need the client login to assign a JOE password
            throw new IllegalArgumentException("Password type " + passwordType.toString() + " not allowed");
        }

        if (PasswordType2.NONE.equals(passwordType)) {
            // Not allowed because NONE is not a valid password type, there is no NONE type to gernerate a password
            throw new IllegalArgumentException("Password type " + passwordType.toString() + " not allowed");
        }

        List<String> outList = new ArrayList<String>();

        for (int i = 0; i < count; i++) {

            String password = "";
            if (prefix != null) {
                password = prefix;
            }

            for (int l = password.length(); l < length; l++) {
                char nextChar = nextRandomChar(passwordType);
                password += nextChar;
            }

            outList.add(password);

        }

        return outList;

    }

    /**
     * Fetch next random character.
     * 
     * @param passwordType
     *            password type
     * @return character based on password type
     */
    private static char nextRandomChar(PasswordType2 passwordType) {

        switch (passwordType) {

            case LETTERS_AND_DIGITS:
                return ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length()));
            case LETTERS:
                return LETTERS.charAt(random.nextInt(LETTERS.length()));
            default:
            case DIGITS:
                int offset = random.nextInt(9);
                char c = '0';
                c += offset;
                return c;

        }
    }

    private static void usage() {

        String[] usage = { //
        //
                "Usage PasswordGenerator [--help] num type|- length|- prefix ", //
                "", //
                "Generate passwords to stdout, by default outputs " + DEFAULT_PASSWORD_NUMBER + " passwords.", //
                "", //
                "Does not use all letter, uses a subset to avoid confusing password chars.  Char set is " + LETTERS, //
                "", //
                "num    - number of passwords to generate", //
                "type   - type of password, default letters&digits, d digits, j joe", //
                "length - password length, default 8", //
                "prefix - prefix for all passwords", //
                "-      - use default value", //
                "", //
        };

        for (String s : usage) {
            System.out.println(s);
        }

        String[] multiLineVersion = new VersionInfo().getSystemVersionInfoMultiLine();
        for (String string : multiLineVersion) {
            System.out.println(string);
        }
    }

    // TODO REFACTOR move to Utilities class
    public static void printArray(List<String> list) {
        for (String string : list) {
            System.out.println(string);
        }
    }

    public static void main(String[] args) {

        ParseArguments parseArguments = new ParseArguments(args);

        if (args.length == 0 || parseArguments.isOptPresent("--help")) {
            usage();
        } else

        if (parseArguments.getArgCount() == 0) {
            printArray(PasswordGenerator.generatePasswords(120, PasswordType2.LETTERS_AND_DIGITS, 8, null));
        } else {

            // 1 number of passwords
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
                    default:
                        type = PasswordType2.valueOf(argValue);
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
    }

    /**
     * Generate Joe Passwords.
     */
    public static List<String> generateJoePasswords(String prefix, int count) {
        
        List<String> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(prefix + (i+1));
        }
        
        return list;
    }
}
