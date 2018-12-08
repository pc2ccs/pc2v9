package edu.csus.ecs.pc2.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import edu.csus.ecs.pc2.tools.PasswordType2;

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

	/**
	 * Generate a list of passwords.
	 * 
	 * @param count number of passwords to generate
	 * @param passwordType password type.  JOE and NONE are not allowed.
	 * @param length length in characters for each password
	 * @param prefix an optional prefix for every password
	 * @return list of passwords.
	 */
	public static List<String> generatePasswords(int count, PasswordType2 passwordType, int length, String prefix) {

		if (PasswordType2.JOE.equals(passwordType))
		{
			// Not allowed because need the client login to assign a JOE password
			throw new IllegalArgumentException("Password type " + passwordType.toString() + " not allowed");
		}

		if (PasswordType2.NONE.equals(passwordType))
		{
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
	 * @param passwordType password type
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

}
