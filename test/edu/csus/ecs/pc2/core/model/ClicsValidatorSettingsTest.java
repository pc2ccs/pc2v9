package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 *
 * Tests various combinations of these settings (from ClicsValidatorSettings toString())
 * 
 * <pre>
 * case_sensitive - indicates that comparisons should be case-sensitive.
 * space_change_sensitive - indicates that changes in the amount of whitespace should be rejected (the default is that any sequence of 1 or more whitespace characters are equivalent).
 * float_relative_tolerance e - indicates that floating-point tokens should be accepted if they are within relative error = e (see below for details).
 * float_absolute_tolerance e - indicates that floating-point tokens should be accepted if they are within absolute error = e (see below for details).
 * float_tolerance e - short-hand for applying e as both relative and absolute tolerance.
 * </pre>
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ClicsValidatorSettingsTest extends AbstractTestCase {
    
    /**
     * Emulate float_tolerance, both settings.
     * 
     * float_tolerance - short-hand for applying e as both relative and absolute tolerance.
     * 
     * @param settings
     * @param tolerance
     */
    protected void setFloatTolerance(ClicsValidatorSettings settings, double tolerance) {
        settings.setFloatAbsoluteTolerance(tolerance);
        settings.setFloatRelativeTolerance(tolerance);
    }

    /**
     * To String for ClicsValidatorSettings.
     * 
     * @param settings
     * @return
     */
    protected String toString(ClicsValidatorSettings settings) {

        return settings.toString();

    }

    /**
     * Test toString with no settings/options.
     * 
     * @throws Exception
     */
    public void testDefaultSettings() throws Exception {

        ClicsValidatorSettings settings = new ClicsValidatorSettings();

        String actual = toString(settings);
        String expected = "";

        assertEquals(expected, actual);

    }

    /**
     * Test case, space and float_tolerance.
     * 
     * @throws Exception
     */
    public void testThreeSettings() throws Exception {

        ClicsValidatorSettings settings = new ClicsValidatorSettings();

        settings.setCaseSensitive(true);
        settings.setSpaceSensitive(true);

        String actual = toString(settings);

        String expected = ClicsValidatorSettings.CLICS_VTOKEN_CASE_SENSITIVE + " " + ClicsValidatorSettings.CLICS_VTOKEN_SPACE_CHANGE_SENSITIVE;

        assertEquals(expected, actual);

        setFloatTolerance(settings, 4.0042354);
        actual = toString(settings);

        expected = ClicsValidatorSettings.CLICS_VTOKEN_CASE_SENSITIVE + " " + 
                ClicsValidatorSettings.CLICS_VTOKEN_SPACE_CHANGE_SENSITIVE + " " +
                ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_ABSOLUTE_TOLERANCE + " 4.0042354 " + 
                ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_RELATIVE_TOLERANCE + " 4.0042354";

        assertEquals(expected, actual);

    }

    /**
     * Test case, space and float_absolute_tolerance options.
     * 
     * @throws Exception
     */
    public void testThreeSettingsAbs() throws Exception {

        ClicsValidatorSettings settings = new ClicsValidatorSettings();

        settings.setCaseSensitive(true);
        settings.setSpaceSensitive(true);

        String actual = toString(settings);

        String expected = ClicsValidatorSettings.CLICS_VTOKEN_CASE_SENSITIVE + " " + 
                ClicsValidatorSettings.CLICS_VTOKEN_SPACE_CHANGE_SENSITIVE;

        assertEquals(expected, actual);

        settings.setFloatAbsoluteTolerance(5.666334);

        expected = ClicsValidatorSettings.CLICS_VTOKEN_CASE_SENSITIVE + " " + 
                ClicsValidatorSettings.CLICS_VTOKEN_SPACE_CHANGE_SENSITIVE + " " +
                ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_ABSOLUTE_TOLERANCE + " 5.666334" ;

        actual = toString(settings);

        assertEquals(expected, actual);
    }

    /**
     * Test case, space and float_relative_tolerance options.
     * 
     * @throws Exception
     */
    public void testThreeSettingsRel() throws Exception {

        ClicsValidatorSettings settings = new ClicsValidatorSettings();

        settings.setSpaceSensitive(true);

        String actual = toString(settings);

        String expected = ClicsValidatorSettings.CLICS_VTOKEN_SPACE_CHANGE_SENSITIVE ;

        assertEquals(expected, actual);

        settings.setFloatRelativeTolerance(0.0030303);

        expected = ClicsValidatorSettings.CLICS_VTOKEN_SPACE_CHANGE_SENSITIVE + " " +
                    ClicsValidatorSettings.CLICS_VTOKEN_FLOAT_RELATIVE_TOLERANCE + " 0.0030303" ;

        actual = toString(settings);

        assertEquals(expected, actual);
    }

}
