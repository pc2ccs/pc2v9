package edu.csus.ecs.pc2.api;

/**
 * This interface describes the PC<sup>2</sup> API view of a contest <I>Problem</i>. 
 * Each contest problem consists of a variety of attributes configured by the Contest Administrator, 
 * including
 * <ul>
 *    <li>The problem <I>name</i>;</li>
 *    <li>A flag indicating whether the problem includes a requirement for reading input data;</li>
 *    <li>The input data for the problem (referred to as the &quot;Judge's data file&quot;) if required;</li>
 *    <li>A flag indicating whether solutions to the problem should expect any input
 *          data to come from a file or from <I>stdin</i>;</li>
 *    <li>A flag indicating whether the problem includes a specification of the expected output for
 *          a correct solution to the problem (referred to as a &quot;Judge's answer file&quot;);</li>
 *    <li>A flag indicating whether the problem includes a <I>validator</i> (a program designed to
 *          accept as input (1) the output of a program submitted by a Team, and (2) a Judge's answer file, 
 *          and makes an automated determination as to whether the Team's program is a correct solution
 *          to the problem);</li>
 *    <li>The executable code for the validator;</li>
 *    <li>The command line necessary to invoke the validator.</li>
 * </ul>
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IProblem {

    /**
     * Get the name for this problem as configured by the Contest Administrator.
     * 
     * @return A String containing the name of the problem.
     */
    String getName();
    
    /**
     * Get the short name for this problem as configured by the Contest Administrator.
     * 
     * @return A String containing the short name of the problem.
     */
    String getShortName();
    
    /**
     * Get the name of the data file which the Judges have provided for this problem.
     * 
     * @see #hasDataFile()
     * @return A String containing the Judge's data file name.
     */
    String getJudgesDataFileName();
    

    /**
     * Get the data contained in the Judge's data file for this problem.
     * 
     * @see #hasDataFile()
     * @return An array of bytes containing the Judge's data file contents.
     */
    byte [] getJudgesDataFileContents();
    

    /**
     * Get the name of the <I>answer file</i> for this problem.
     * The &quot;answer file&quot; for a problem is a file which the Judges provide
     * for purposes of comparing with the output of a Team's submitted program.
     * The answer file is also typically used as one of the inputs to a <I>Validator</i>
     * program intended to do automatic judging.
     * 
     * @see #hasAnswerFile()
     * @return A String containing the name of the Judge's Answer File for the problem.
     */
    String getJudgesAnswerFileName();
    
    /**
     * Get the data contained in the Judge's answer file for this problem.
     * 
     * @see #hasAnswerFile()
     * @return An array of bytes containing the Judge's answer file contents.
     */
    byte [] getJudgesAnswerFileContents();
    
    /**
     * Get the name of the <I>Validator</i> program file for this problem.
     * A &quot;validator&quot; is a program intended to be used to perform
     * automated judging, for example by comparing the output of a Team's submitted
     * program with the Judge's Answer File for a problem.
     * 
     * @see #hasExternalValidator()
     * @return A String containing the name of the Validator program associated with
     * this problem.
     */
    String getValidatorFileName();
    
    
    /**
     * Get the command line which has been specified by the Contest Administrator as
     * being the command required to execute the validator program associated with this problem.
     * <P>
     * Note that when a Contest Administrator enters a &quot;validator command line&quot; into
     * PC<sup>2</sup>, the entry may contain &quot;substitution parameters&quot; to be filled in
     * at run time.  The validator command line obtained by calling this method will have already 
     * had any such command line parameter substitutions performed prior to returning the command line.
     *  
     * @see #hasExternalValidator()
     * @return A String containing the command required to execute the validator associated with this problem.
     */
    String getValidatorCommandLine();
    
    /**
     * Get the contents of the file containing the validator associated with this problem.
     * In most cases the validator will be an executable program; in this case the byte array returned by this
     * method contains the executable (binary) contents of that executable program.
     * <p>
     * Note that PC<sup>2</sup> supports three possible &quot;validator configuration modes&quot; for
     * a problem:  
     * <ul>
     *   <li>No Validator;</li>
     *   <li>Use PC<sup>2</sup> Internal Validator; and</li>
     *   <li>Use External Validator.</li>
     * </ul>
     * If a problem has been configured with &quot;No Validator&quot; or &quot;Use PC<sup>2</sup> Internal Validator&quot;, the
     * contents of the array returned by this method are undefined; the only time the information returned by
     * this method is meaningful is when a problem has been configured with an External Validator by the 
     * Contest Administrator.  
     * 
     * @see #hasExternalValidator()
     * @return A byte array containing the contents of the External Validator program associated with this problem.
     */
    byte [] getValidatorFileContents();

    /**
     * Returns true if this problem has had an External Validator assigned to it by the Contest Administrator;
     * false otherwise.
     * 
     * @return True if this problem has had a validator program associated with it; false otherwise.
     */
    boolean hasExternalValidator();
    
    /**
     * Returns true if the Contest Administrator has specified that solutions to this problem are supposed
     * to read their input from a data file;  false otherwise.
     * Note that this condition is mutually exclusive with {@link #readsInputFromStdIn()}.
     * 
     * @see #readsInputFromStdIn()
     * @return True if solutions to this problem are supposed to read input from a file; false otherwise.
     */
    boolean readsInputFromFile();
    
    /**
     * Returns true if the Contest Administrator has specified that solutions to this problem are supposed
     * to read their input from a data file; false otherwise.
     * Note that this condition is mutually exclusive with {@link #readsInputFromFile()}.
     * 
     * @see #readsInputFromFile()
     * @return True if solutions to this problem are supposed to read input from stdin; false otherwise.
     */
    boolean readsInputFromStdIn();
    
    /**
     * Returns true if the Contest Administrator has defined a data file for this problem;
     * false otherwise.
     * 
     * @return True if the Contest Administrator has defined a data file for this problem; false otherwise.
     */
    boolean hasDataFile();
    
    /**
     * Returns true if the Contest Administrator has defined a Judge's Answer File for this problem;
     * false otherwise.
     * 
     * @return True if the Contest Administrator has defined a Judge's Answer File for this problem; false otherwise.
     */
    boolean hasAnswerFile();
    
    /**
     * Check whether this problem is the same as some other contest problem.
     * <P>
     * Determination of whether two problems are equal is based on whether they refer to the
     * same contest problem as originally entered into PC<sup>2</sup> by the Contest Administrator.  
     * Note in particular that subsequent changes to a problem made by the Contest Administrator (for example,
     * changes to the problem name, the Judge's data file, etc.) do <I>not</i> affect the result of the
     * <code>equals()</code> method; if this problem refers to the same contest problem as the one indicated by the 
     * specified parameter, this method will return true regardless of whether the internal contents of the two
     * problem objects is identical or not.
     * 
     * @param obj the Problem which is to be compared with this problem for equality.
     * @return True if the specified object refers to the same contest problem as this problem
     *          (regardless of the actual content of the two problems).
     */
    boolean equals(Object obj);
    
    /**
     * Get the hashcode associated with this contest problem.
     * @return An integer hashcode for this object.
     */
    int hashCode();
}
