package edu.csus.ecs.pc2.tools;

import java.util.List;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.tools.PasswordType2;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class PasswordGeneratorTest extends AbstractTestCase {

	/**
	 * test generatePasswords.
	 * 
	 */
	public void testgeneratePasswords() {

		int numberPasswords = 22;
		int passlen = 32;

		PasswordType2 type = PasswordType2.DIGITS;
		//		type = PasswordType2.LETTERS_AND_DIGITS;
		//		type = PasswordType2.LETTERS;

		//		List<String> list = PasswordGenerator.generatePasswords(8, type, 8, "bark");
		List<String> list = PasswordGenerator.generatePasswords(numberPasswords, type, passlen, "thisisverylong");

		//		for (String string : list) {
		//			System.out.println(string);
		//		}

		assertEquals("Expecting number of passwords ", numberPasswords, list.size());
		
		assertEquals("Expecting password length ",  passlen, list.get(0).length());
	}

	/**
	 * Test to insure that PasswordType2.JOE and PasswordType2.NONE cannot be specified for password gen.
	 */
	public void testgeneratePasswordsWJoe() throws Exception {

		try {
			PasswordType2 type = PasswordType2.JOE;
			PasswordGenerator.generatePasswords(8, type, 8, "foo");
			fail("Expected to throw IllegalArgumentException for JOE PasswordType2 ");
		} catch (IllegalArgumentException e) {
			// java.lang.IllegalArgumentException: Password type JOE not allowed
			assertEquals("Password type JOE not allowed", e.getMessage());
			//			e.printStackTrace();
		}
		
		try {
			PasswordType2 type = PasswordType2.NONE;
			PasswordGenerator.generatePasswords(8, type, 8, "foo");
			fail("Expected to throw IllegalArgumentException for NONE PasswordType2 ");
		} catch (IllegalArgumentException e) {
			// java.lang.IllegalArgumentException: Password type JOE not allowed
			assertEquals("Password type NONE not allowed", e.getMessage());
			//			e.printStackTrace();
		}

	}

}
