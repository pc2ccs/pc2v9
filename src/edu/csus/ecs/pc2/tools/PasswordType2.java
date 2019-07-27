package edu.csus.ecs.pc2.tools;

/**
 * Password types.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */

// TODO Bug 1509 merge this with PasswordType
public enum PasswordType2 {

	/**
	 * None specified.
	 */
	NONE,
	/**
	 * Letters only.
	 * @see PasswordGenerator#LETTERS
	 */
	LETTERS,
	/**
	 * Digits only.
	 */
	DIGITS,
	/**
	 * password same as login.
	 */
	JOE,
	/**
	 * Letters and digits.
	 * @see PasswordGenerator#ALL_CHARS
	 */
	LETTERS_AND_DIGITS,

}
