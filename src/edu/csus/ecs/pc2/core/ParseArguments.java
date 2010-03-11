package edu.csus.ecs.pc2.core;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Parses command line options and arguments, give access to options, values after options and arguments after options.
 * <P>
 * Note that -- as an option is the end of options and start of arguments.
 *
 * <pre>
 * //  Example of usage
 *
 * ParseArgs pa = null;
 *
 * try {
 *     pa = new ParseArgs(args);
 *
 *     if (pa.isOptPresent(&quot;-sample&quot;) || pa.isOptPresent(&quot;-s&quot;)) {
 *         // do sample
 *         System.exit(4);
 *     }
 *
 *     if (pa.isOptPresent(&quot;-help&quot;) || pa.isOptPresent(&quot;-h&quot;)) {
 *         // print help
 *         System.exit(4);
 *     }
 *
 * } catch (Exception ex99) {
 *     log.config(&quot;Exception &quot;, ex99);
 *     System.exit(4);
 * }
 *
 * if (pa.getArgCount() &gt; 0) {
 *     loadFile(pa.getArg(0));
 * } else {
 *     log.config(&quot;Missing filename to load &quot;);
 * }
 * </pre>
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ParseArguments {

    /**
     * "FILE_OPTION file" instructs ParseArguements to pre-load values from file
     */


    private Hashtable<String, String> argHash = new Hashtable<String, String>();

    private Vector<String> filelist = new Vector <String> ();

    private static final String NULL_VALUE = "<GAA" + Long.MAX_VALUE;

    private String[] requireArgOpts = null;

    /**
     * ParseArgs constructor comment.
     */
    public ParseArguments() {
        super();
    }

    /**
     * ParseArgs constructor comment.
     *
     * @param args
     *            java.lang.String [] - command line arguments
     */
    public ParseArguments(String[] args) {
        super();
        loadArgs(args);
    }

    /**
     * Dumps the args to pw.
     */
    @SuppressWarnings("unchecked")
    public void dumpArgs(java.io.PrintStream pw) {
        pw.println();
        pw.println("There are " + argHash.size() + " options ");

        TreeSet ts = new TreeSet();
        ts.addAll(argHash.keySet());

        Iterator it = ts.iterator();

        while (it.hasNext()) {
            String key = (String) it.next();
            Object obj = argHash.get(key);
            String value = "<null>";

            if (obj != null) {
                value = (String) obj;
            }

            if (value.equals(NULL_VALUE)) {
                value = "<null>";
            }

            pw.println(key + " = " + value);
        }

        pw.println("There are " + filelist.size() + " arguments ");

        for (int i = 0; i < filelist.size(); i++) {
            String value = (String) filelist.elementAt(i);
            pw.println("[" + i + "] " + value);
        }

    }

    /**
     * List all options and arguments
     */
    @SuppressWarnings("unchecked")
    public void dumpArgs(java.io.PrintWriter pw) {
        pw.println();
        pw.println("There are " + argHash.size() + " options ");

        TreeSet ts = new TreeSet();
        ts.addAll(argHash.keySet());

        Iterator it = ts.iterator();

        while (it.hasNext()) {
            String key = (String) it.next();
            Object obj = argHash.get(key);
            String value = "<null>";

            if (obj != null) {
                value = (String) obj;
            }

            if (value.equals(NULL_VALUE)) {
                value = "<null>";
            }

            pw.println(key + " = " + value);
        }

        pw.println("There are " + filelist.size() + " arguments ");

        for (int i = 0; i < filelist.size(); i++) {
            String value = (String) filelist.elementAt(i);
            pw.println("[" + i + "] " + value);
        }

    }

    /**
     * Get value for argument N, arguments start index zero.
     * <p>
     * getArg(0) returns the first argument.
     * <P>
     *
     * <pre>
     *   cmd -s 100  file1 file2
     *
     *   getArg returns 2
     * </pre>
     *
     */
    public String getArg(int idx) {
        if (filelist.size() < 1) {
            return null;
        }

        if (idx < 0) {
            return null;
        }

        return (String) filelist.elementAt(idx);
    }

    /**
     * returns number of arguments.
     */
    public int getArgCount() {
        return filelist.size();
    }

    /**
     * Get value (Long) after option.
     * <P>
     *
     * <pre>
     *   cmd -s 100
     *
     *   getLongOpt(&quot;-s&quot;)  returns new Long(100)
     * </pre>
     */
    public Long getLongOptionValue(String argKey) {
        String s = getOptValue(argKey);

        if (s == null) {
            return null;
        }

        long v = Long.parseLong(s);
        return new Long(v);
    }

    /**
     * returns value after option.
     * <P>
     *
     * <pre>
     *   cmd -s 100
     *
     *   getOptValue(&quot;-s&quot;)  returns &quot;100&quot;
     * </pre>
     */
    public String getOptValue(String argKey) {
        if (argKey == null) {
            throw new IllegalArgumentException("argKey is null");
        }

        Object obj = argHash.get(argKey);

        if (obj != null || obj instanceof String) {
            String outString = (String) obj;

            if (outString.equals(NULL_VALUE)) {
                return null;
            }

            return outString;
        }

        return null;
    }

    /**
     * Returns true of command line option is present.
     * <P>
     *
     * <pre>
     *   -a            isOptPresent(&quot;-a&quot;) would return true
     *   -b 123        isOptPresent(&quot;-b&quot;) would return true
     *   -c NNN        isOptPresent(&quot;-c&quot;) would return true
     *   -c NNN        isOptPresent(&quot;-D&quot;) would return false
     * </pre>
     *
     *
     */
    public boolean isOptPresent(String argKey) {
        if (argKey == null) {
            throw new IllegalArgumentException("argKey is null");
        }

        Object obj = argHash.get(argKey);

        return obj != null;
    }

    /**
     * Load in command line args.
     */
    public void loadArgs(String[] args) {
        if (args == null) {
            throw new IllegalArgumentException("args is null");
        }

        String curOpt = null; // hold the current option being processed
        boolean pastArgs = false;

        for (int i = 0; i < args.length; i++) {
            String value = args[i];

            if (pastArgs) {
                filelist.addElement(value);
            } else if (value.equals("--")) {
                pastArgs = true;
            } else if (value.startsWith("-")) {
                if (curOpt != null) {
                    argHash.put(curOpt, NULL_VALUE);
                }

                curOpt = value;
            } else if (curOpt != null) {
                // found a value (not an option)

                if (hasArgOpt(curOpt)) {
                    // cur arg must have value
                    argHash.put(curOpt, value);
                } else {
                    // cur arg must not have value
                    argHash.put(curOpt, NULL_VALUE);
                    pastArgs = true;
                    filelist.addElement(value);
                }
                curOpt = null;
            } else {
                pastArgs = true;
                filelist.addElement(value);
            }
        }

        if (curOpt != null) {
            argHash.put(curOpt, NULL_VALUE);
        }
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

        System.out.println("Using -l option ");
        pa = new ParseArguments();
        pa.setRequireArgOpts("-l");
        pa.loadArgs(args);
        pa.dumpArgs(System.out);

    }

    /**
     * Returns true if command line option has a value.
     * <P>
     *
     * <pre>
     *   -a            optHasValue(&quot;-a&quot;) would return false
     *   -b 123        optHasValue(&quot;-b&quot;) would return true
     *   -c NNN        optHasValue(&quot;-c&quot;) would return true
     *   -c NNN        optHasValue(&quot;-D&quot;) would return false
     * </pre>
     *
     */
    public boolean optHasValue(String argKey) {
        if (argKey == null) {
            throw new IllegalArgumentException("argKey is null");
        }

        Object obj = argHash.get(argKey);

        if (obj == null) {
            return false;
        }

        return ((String) obj).equals(NULL_VALUE);
    }

    /**
     * Returns requiredArgOpts.
     *
     * @return java.lang.String[]
     */
    public java.lang.String[] getRequireArgOpts() {
        return requireArgOpts;
    }

    /**
     * return true if arg has an option.
     */
    private boolean hasArgOpt(String arg) {
        // System.err.println("hasArgOpt - "+arg);

        if (requireArgOpts == null) {
            return false;
        }

        for (int i = 0; i < requireArgOpts.length; i++) {
            // System.err.println(" "+i+" <"+arg+"> vs <"+noArgOptions[i]+">");
            if (requireArgOpts[i].equals(arg.trim())) {
                return true;
            }
        }

        return false;

    }

    /**
     * Reads properties from file as if they were from the command line.
     * 
     * @param fileName
     * @throws IOException 
     */
    public void overRideOptions (String fileName) throws IOException {
        Properties fileProps = new Properties();
        InputStream inStream = new BufferedInputStream(new FileInputStream(fileName));
        fileProps.load(inStream);
        inStream.close();
        Set<Object> s = fileProps.keySet();
        for (Object key : s) {
            String argKey = (String) key;
            /**
             * only overwrite it not already present, so if they specify
             * a command line argument it has higher precedence than these
             * in the property file  
             */
            if (!isOptPresent(argKey)) {
                argHash.put(argKey, fileProps.getProperty(argKey));
            }
        }
    }

    /**
     * Set options which require a value.
     *
     * @param newRequireArgOpts
     *            java.lang.String[]
     */
    public void setRequireArgOpts(java.lang.String[] newRequireArgOpts) {
        requireArgOpts = newRequireArgOpts;
    }

    /**
     * Set option which requires a value.
     *
     * @param newRequireArgOpts
     *            java.lang.String
     */
    public void setRequireArgOpts(String newRequireArgOpts) {
        requireArgOpts = new String[1];
        requireArgOpts[0] = newRequireArgOpts;
    }

    /**
     * ParseArgs args and options requiring values.
     *
     * @param args
     *            java.lang.String [] - command line arguments
     * @param requiredArgs
     *            java.lang.String [] - options requiring args
     */
    public ParseArguments(String[] args, String[] requiredArgs) {
        super();
        setRequireArgOpts(requiredArgs);
        loadArgs(args);
    }
}
