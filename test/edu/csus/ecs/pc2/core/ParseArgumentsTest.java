package edu.csus.ecs.pc2.core;

import junit.framework.TestCase;

/**
 * Test for Parse Arguments.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ParseArgumentsTest extends TestCase {

    public void testNull() {

        try {
            @SuppressWarnings("unused")
            ParseArguments parseArguments = new ParseArguments(null);
        } catch (IllegalArgumentException e) {
            nullFakeRoutine("Passed test threw IllegalArgumentException");
        }

    }

    public void testEmpty() {
        @SuppressWarnings("unused")
        ParseArguments parseArguments = new ParseArguments(new String[0]);
    }

    private void nullFakeRoutine(String reason) {
        // TODO Auto-generated method stub

    }
    
    public void testOptHasValue() {

        String opt = "--login";
        String value = "name";

        String[] required = { opt };
        String[] args = { opt, value };
        ParseArguments parseArguments = new ParseArguments(args, required);

        assertTrue(opt + " option should be present", parseArguments.isOptPresent(opt));
        assertTrue("Option should have value", parseArguments.optHasValue(opt));
        assertEquals(value, parseArguments.getOptValue(opt));

        assertFalse("Option should not have value ", parseArguments.optHasValue("--bogus"));
    }
    
    public void testIsOptPresent() {

        String opt = "--login";
        String opt2 = "--debug";
        String optp = "-pass";
        String value = "name";

        String[] required = { opt, opt2 };
        String[] args = { opt, value, optp};
        ParseArguments parseArguments = new ParseArguments(args, required);
        
//        parseArguments.dumpArgs(System.out);

        assertTrue(opt + " option should be present", parseArguments.isOptPresent(opt));
        assertFalse("Option should not have value ", parseArguments.isOptPresent("--bogus"));
        assertTrue(optp+" option should be present", parseArguments.isOptPresent(optp));
        assertEquals(opt + " value ", value, new String(parseArguments.getOptValue(opt)));
        
        assertFalse(opt2+" option should not be present", parseArguments.isOptPresent(opt2));
        assertNull(opt2+" option should be null value", parseArguments.getOptValue(opt2));
    }
    
    /**
     * main test routine for ParseArgs <br>
     *
     * @param args
     *            java.lang.String[]
     */
    public static void main(String[] args) {
        ParseArguments pa = new ParseArguments();
        pa.loadArgs(args);
        pa.dumpArgs(System.out);
        System.out.println();
        
        String [] reqArgs = { "--l", "--file" };

        System.out.print("Using required arguments:");
        for (String s : reqArgs){
            System.out.print(" "+s);
        }
        System.out.println();
        
        pa = new ParseArguments();
        pa.setRequireArgOpts(reqArgs);
        pa.loadArgs(args);
        pa.dumpArgs(System.out);

    }

}
