package edu.csus.ecs.pc2.core.model;

import java.util.ArrayList;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 *
 * Tests various combinations of these settings (from DefaultValidatorSettings toString())
 * 
 * <pre>
 * case_sensitive case_sensitive - indicates that comparisons should be case-sensitive.
 * space_change_sensitive - indicates that changes in the amount of whitespace should be rejected (the default is that any sequence of 1 or more whitespace characters are equivalent).
 * float_relative_tolerance e - indicates that floating-point tokens should be accepted if they are within relative error = e (see below for details).
 * float_absolute_tolerance e - indicates that floating-point tokens should be accepted if they are within absolute error = e (see below for details).
 * float_tolerance e - short-hand for applying e as both relative and absolute tolerance.
 * </pre>
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class DefaultValidatorSettingsTest extends AbstractTestCase {

    public final String VTO_CASE_SENSITIVE = "case_sensitive";

    public final String VTO_SPACE_CHANGE_SENSITIVE = "space_change_sensitive";

    public final String VTO_FLOAT_RELATIVE_TOLERANCE = " float_relative_tolerance";

    public final String VTO_FLOAT_ABSOLUTE_TOLERANCE = " float_absolute_tolerance";

    /**
     * Emulate float_tolerance, both settings.
     * 
     * float_tolerance - short-hand for applying e as both relative and absolute tolerance.
     * 
     * @param settings
     * @param tolerance
     */
    protected void setFloatTolerance(DefaultValidatorSettings settings, double tolerance) {
        settings.setFloatAbsoluteTolerance(tolerance);
        settings.setFloatRelativeTolerance(tolerance);
    }

    /**
     * To String for DefaultValidatorSettings.
     * 
     * @param settings
     * @return
     */
    protected String toString(DefaultValidatorSettings settings) {

        // TODO replace this entire method code with
        // return settings.toString();

        ArrayList<String> list = new ArrayList<>();

        if (settings.isCaseSensitive()) {
            list.add(VTO_CASE_SENSITIVE);
        }
        if (settings.isFloatAbsoluteToleranceSpecified()) {
            list.add(VTO_FLOAT_ABSOLUTE_TOLERANCE);
            list.add(Double.toString(settings.getFloatAbsoluteTolerance()));
        }

        if (settings.isFloatRelativeToleranceSpecified()) {
            list.add(VTO_FLOAT_RELATIVE_TOLERANCE);
            list.add(Double.toString(settings.getFloatRelativeTolerance()));
        }
        if (settings.isSpaceSensitive()) {
            list.add(VTO_SPACE_CHANGE_SENSITIVE);
        }

        return String.join(" ", list);
    }

    /**
     * Test toString with no settings/options.
     * 
     * @throws Exception
     */
    public void testDefaultSettings() throws Exception {

        DefaultValidatorSettings settings = new DefaultValidatorSettings();

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

        DefaultValidatorSettings settings = new DefaultValidatorSettings();

        settings.setCaseSensitive(true);
        settings.setSpaceSensitive(true);

        String actual = toString(settings);

        String expected = VTO_CASE_SENSITIVE + " " + VTO_SPACE_CHANGE_SENSITIVE;

        assertEquals(expected, actual);

        setFloatTolerance(settings, 4.0042354);
        actual = toString(settings);

        expected = VTO_CASE_SENSITIVE + " " + //
                VTO_FLOAT_ABSOLUTE_TOLERANCE + " 4.0042354 " + //
                VTO_FLOAT_RELATIVE_TOLERANCE + " 4.0042354 " + //
                VTO_SPACE_CHANGE_SENSITIVE;

        assertEquals(expected, actual);

    }

    /**
     * Test case, space and float_absolute_tolerance options.
     * 
     * @throws Exception
     */
    public void testThreeSettingsAbs() throws Exception {

        DefaultValidatorSettings settings = new DefaultValidatorSettings();

        settings.setCaseSensitive(true);
        settings.setSpaceSensitive(true);

        String actual = toString(settings);

        String expected = VTO_CASE_SENSITIVE + " " + VTO_SPACE_CHANGE_SENSITIVE;

        assertEquals(expected, actual);

        settings.setFloatAbsoluteTolerance(5.666334);

        expected = VTO_CASE_SENSITIVE + " " + //
                VTO_FLOAT_ABSOLUTE_TOLERANCE + " 5.666334" + //
                " " + VTO_SPACE_CHANGE_SENSITIVE;

        actual = toString(settings);

        assertEquals(expected, actual);
    }

    /**
     * Test case, space and float_relative_tolerance options.
     * 
     * @throws Exception
     */
    public void testThreeSettingsRel() throws Exception {

        DefaultValidatorSettings settings = new DefaultValidatorSettings();

        settings.setSpaceSensitive(true);

        String actual = toString(settings);

        String expected = VTO_SPACE_CHANGE_SENSITIVE;

        assertEquals(expected, actual);

        settings.setFloatRelativeTolerance(0.0030303);

        expected = VTO_FLOAT_RELATIVE_TOLERANCE + " 0.0030303 " + VTO_SPACE_CHANGE_SENSITIVE;

        actual = toString(settings);

        assertEquals(expected, actual);
    }

}
