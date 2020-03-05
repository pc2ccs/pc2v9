// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class encapsulates a "judgement type" as defined by the CLICS Contest API.
 * That API defines each type of judgement which can be assigned to a submission as having the following fields:
 * <pre>
 *   id       - an acronym identifying the judgement -- e.g. "AC" (accepted), "RTE" (runtime error), "CE" (compiler error), etc.
 *   name     - a string giving a more complete description of the judgement -- e.g. "Correct" or "Compiler Error"
 *   penalty  - a boolean indicating whether this judgement causes penalty time to be assigned (if a penalty time value has been specified for the contest)
 *   solved   - a boolean indicating whether this judgement indicates that the problem has been correctly solved
 * </pre>
 * 
 * The CLICS Contest API specification includes a definition of what it calls the "Big 5" judgement types.  Roughly, these
 * amount to the judgement types which are supported at the ICPC World Finals and upon which all World Finals CCS's have
 * agreed.  The CLICS "Big 5" judgement types (listed by acronym) are:  AC (accepted, yes); WA (wrong answer); TLE (Time Limit Exceeded);
 * RTE (Run-Time Error); and CE (Compile Error).
 * 
 * The CLICS Contest API also defines a host of other judgement types known to be used by one or more Contest Control Systems,
 * and it also provides a definition of the recommended mapping of such judgement types into corresponding "Big 5" judgements
 * for situations where such a mapping is desired.
 * 
 * This class provides methods {@link #isBig5()} and {@link #getBig5Equivalent()} for determining whether the judgement type
 * defined by an instance of the class is one of the CLICS "Big 5" judgement types and for mapping the instance judgement type
 * into a recommended "Big 5" judgement.
 * 
 * The class also provides method {@link #getCLICSAcronym(String)} which accepts an arbitrary judgement string (for example,
 * "Incorrect Output Format") and returns the corresponding CLICS acronym if such an acronym has been defined (see the CLICS
 * Contest API for all currently-defined strings and their corresponding acronyms).
 * 
 * For a complete description of CLICS judgement types, including an extensive list of known acronyms used by various
 * Contest Control Systems, see https://clics.ecs.baylor.edu/index.php?title=Contest_API#Judgement_Types.
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class CLICSJudgementType {

    private String id ;  // the "acronym" of the judgement
    private String name ;
    private boolean penalty;  //whether or not this judgement causes a penalty during scoring
    private boolean solved ;  //whether or not this judgement represents a correct solution to a problem
    
    /**
     * An enumeration of the "Big 5" judgement types defined by the CLICS Contest API specification
     * (https://clics.ecs.baylor.edu/index.php?title=Contest_API#Judgement_Types).
     * Each element (judgement) defined by the enumeration has a "value" that is the string representation
     * (the acronym) for that judgement.
     *
     */
    public static enum CLICS_BIG5 {
        AC  ("AC"),  
        WA  ("WA"),  
        TLE ("TLE"),
        RTE ("RTE"),
        CE  ("CE")     ; // semicolon needed when fields / methods follow

        private final String acronym;

        private CLICS_BIG5(String acronym) {
            this.acronym = acronym;
        }
    }
    
    /**
     * An enumeration of the judgements defined by the CLICS Contest API specification which are NOT "Big 5" 
     * judgement types.   Each of these types is defined in the CLICS specification to map to a certain "Big 5" judgement
     * in the event the system is using only Big 5 judgements.
     * 
     * The "value" of each enum element indicates to what CLICS "Big 5" judgement the acronym element should map.
     *
     */
    public static enum CLICS_NON_BIG5 {
        APE  ("APE:AC"), //Accepted, Presentation Error    [Maps to AC]
        OLE  ("OLE:WA"), //Output Limit Exceeded           [Maps to WA]
        PE   ("PE:WA"),  //Presentation Error              [Maps to WA]
        EO   ("EO:WA"),  //Excessive Output                [Maps to WA]
        IO   ("IO:WA"),  //Incomplete Output               [Maps to WA]
        IOF  ("IOF:WA"), //Incorrect Output Format         [Maps to WA]
        NO   ("NO:WA"),  //No Output                       [Maps to WA]
        WTL  ("WTL:TLE"), //Wallclock TimeLimit Exceeded    [Maps to TLE]
        ILE  ("ILE:TLE"), //Idleness Limit Exceeded (no CPU time used for too long)                         [Maps to TLE]
        TCO  ("TCO:TLE"), //Time limit exceeded -- Correct Output (too slow but producing correct output)   [Maps to TLE]
        TWA  ("TWA:TLE"), //Time limit exceeded -- Wrong Answer (too slow and also incorrect output)        [Maps to TLE]
        TPE  ("TPE:TLE"), //Time limit exceeded -- Presentation Error (too slow and also presentation error)[Maps to TLE]
        TEO  ("TEO:TLE"), //Time limit exceeded -- Excessive Output (too slow and also excessive output)    [Maps to TLE]
        TIO  ("TIO:TLE"), //Time limit exceeded -- Incomplete Output (too slow and also incomplete output)  [Maps to TLE]
        TNO  ("TNL:TLE"), //Time limit exceeded -- No Output (too slow and also no output)                  [Maps to TLE]
        MLE  ("MLE:RTE"), //Memory Limit Exceeded           [Maps to RTE]
        SV   ("SV:RTE"),  //Security Violation (uses some functionality that is not allowed by the system)  [Maps to RTE]
        IF   ("IF:RTE"),  //Illegal Function (calls a function that is not allowed by the system)           [Maps to RTE]
        RCO  ("RCO:RTE"), //Runtime error -- Correct Output (crashing but producing correct output)         [Maps to RTE]
        RWA  ("RWA:RTE"), //Runtime error -- Wrong Answer (crashing and also incorrect output)              [Maps to RTE]
        RPE  ("RPE:RTE"), //Runtime error -- Presentation Error (crashing and also presentation error)      [Maps to RTE]
        REO  ("REO:RTE"), //Runtime error -- Excessive Output (crashing and also excessive output)          [Maps to RTE]
        RIO  ("RIO:RTE"), //Runtime error -- Incomplete Output (crashing and also incomplete output)        [Maps to RTE]
        RNO  ("RNO:RTE"), //Runtime error -- No Output (crashing and also no output)                        [Maps to RTE]
        CTL  ("CTL:CE"), //Compile Time Limit exceeded     [Maps to CE]
        JE   ("JE:UNDEFINED (JE)"),  //Judging Error (something went wrong with the system)            [No defined mapping]
        SE   ("SE:UNDEFINED (SE)"),  //Submission Error (something went wrong with the submission)     [No defined mapping]
        CS   ("CS:UNDEFINED (CS)");  //Contact Staff (something went wrong)                            [No defined mapping]
        
        private final String acronyms;

        private CLICS_NON_BIG5(String acronyms) {
            this.acronyms = acronyms;
        }
        
        public String getValue() {
            return acronyms;
        }

    }

    /**
     * An enumeration of the judgement acronyms defined by the CLICS Contest API specification (their "ID" in CLICS terminology)
     * together with their common string values (their "name" in CLICS terminology).
     * The string name (text) of each judgement (including case, spacing, and syntax) is based on the "Name" 
     * given in the CLICS specification (see the URL above).
     * 
     *
     */
    public static enum CLICS_JUDGEMENT_ACRONYM {
        AC  ("Accepted"),
        ACY ("Yes"),
        ACC ("Correct"),
        RE  ("Rejected"),
        WA  ("Wrong Answer"),  
        TLE ("Time Limit Exceeded"),
        RTE ("Run-Time Error"),
        CE  ("Compile Error"),     
        APE  ("Accepted - Presentation Error"),
        OLE  ("Output Limit Exceeded"),
        PE   ("Presentation Error"),
        EO   ("Excessive Output"),
        IO   ("Incomplete Output"),
        IOF  ("Incorrect Output Format"),
        NO   ("No Output"),
        WTL  ("Wallclock Time Limit Exceeded"),
        ILE  ("Idleness Limit Exceeded"),
        TCO  ("Time Limit Exceeded - Correct Output"),
        TWA  ("Time Limit Exceeded - Wrong Answer"),
        TPE  ("Time Limit Exceeded - Presentation Error"),
        TEO  ("Time Limit Exceeded - Excessive Output"),
        TIO  ("Time Limit Exceeded - Incomplete Output"),
        TNO  ("Time Limit Exceeded - No Output"),
        MLE  ("Memory Limit Exceeded"),
        SV   ("Security Violation"),
        IF   ("Illegal Function"),
        RCO  ("Run-Time Error - Correct Output"),
        RWA  ("Run-Time Error - Wrong Answer"),
        RPE  ("Run-Time Error - Presentation Error"),
        REO  ("Run-Time Error - Excessive Output"),
        RIO  ("Run-Time Error - Incomplete Output"),
        RNO  ("Run-Time Error - No Output"),
        CTL  ("Compile Time Limit Exceeded"),
        JE   ("Judging Error"),
        SE   ("Submission Error"),
        CS   ("Contact Staff") ;
        
        private final String name;

        private CLICS_JUDGEMENT_ACRONYM(String name) {
            this.name   = name;
        }
        
        public String getValue() {
            return name;
        }

    }

    /**
     * This List defines the mappings between all currently-known forms of CCS judgement messages
     * and the corresponding CLICS judgement acronym.
     * In particular, it provides a way to map between different "versions" of a judgement message
     * (for example, "Time Limit Exceeded" vs. "Time-limit Exceeded") and corresponding CLICS judgement acronyms.
     * 
     * To add support for a new judgement string, simply add a new element to this List giving the judgement
     * text string and the CLICS_JUDGEMENT_ACRONYM to which that text string should map. 
     */
    private static ArrayList<JudgementMapping> judgementStringMappings = new ArrayList<JudgementMapping>() {
 
        {
            add(new JudgementMapping("Accepted",CLICS_JUDGEMENT_ACRONYM.AC));
            
            add(new JudgementMapping("Yes",CLICS_JUDGEMENT_ACRONYM.AC));
            add(new JudgementMapping("Correct",CLICS_JUDGEMENT_ACRONYM.AC));

            add(new JudgementMapping("Rejected",CLICS_JUDGEMENT_ACRONYM.RE));
            add(new JudgementMapping("Incorrect",CLICS_JUDGEMENT_ACRONYM.RE));
            add(new JudgementMapping("No",CLICS_JUDGEMENT_ACRONYM.RE));
            
            
            add(new JudgementMapping("Wrong Answer",CLICS_JUDGEMENT_ACRONYM.WA));
            add(new JudgementMapping("No - Wrong Answer",CLICS_JUDGEMENT_ACRONYM.WA));
            
            add(new JudgementMapping("Time Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.TLE));
            add(new JudgementMapping("Time-Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.TLE));
            add(new JudgementMapping("No - Time Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.TLE));
            add(new JudgementMapping("No - Time-Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.TLE));
            
            add(new JudgementMapping("Run Time Error",CLICS_JUDGEMENT_ACRONYM.RTE));
            add(new JudgementMapping("Runtime Error",CLICS_JUDGEMENT_ACRONYM.RTE));
            add(new JudgementMapping("Run-Time Error",CLICS_JUDGEMENT_ACRONYM.RTE));
            add(new JudgementMapping("No - Run Time Error",CLICS_JUDGEMENT_ACRONYM.RTE));
            add(new JudgementMapping("No - Runtime Error",CLICS_JUDGEMENT_ACRONYM.RTE));
            add(new JudgementMapping("No - Run-Time Error",CLICS_JUDGEMENT_ACRONYM.RTE));
           
            add(new JudgementMapping("Compile Error",CLICS_JUDGEMENT_ACRONYM.CE));
            add(new JudgementMapping("Compiler Error",CLICS_JUDGEMENT_ACRONYM.CE));
            add(new JudgementMapping("Compilation Error",CLICS_JUDGEMENT_ACRONYM.CE));
            add(new JudgementMapping("No - Compile Error",CLICS_JUDGEMENT_ACRONYM.CE));
            add(new JudgementMapping("No - Compiler Error",CLICS_JUDGEMENT_ACRONYM.CE));
            add(new JudgementMapping("No - Compilation Error",CLICS_JUDGEMENT_ACRONYM.CE));
           
            add(new JudgementMapping("Accepted - Presentation Error",CLICS_JUDGEMENT_ACRONYM.APE));
            add(new JudgementMapping("Output Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.OLE));
            add(new JudgementMapping("No - Output Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.OLE));
            
            add(new JudgementMapping("Presentation Error",CLICS_JUDGEMENT_ACRONYM.PE));
            add(new JudgementMapping("Output Format Error",CLICS_JUDGEMENT_ACRONYM.PE));
            add(new JudgementMapping("Incorrect Output Format",CLICS_JUDGEMENT_ACRONYM.IOF));
            add(new JudgementMapping("No - Presentation Error",CLICS_JUDGEMENT_ACRONYM.PE));
            add(new JudgementMapping("No - Output Format Error",CLICS_JUDGEMENT_ACRONYM.PE));
            add(new JudgementMapping("No - Incorrect Output Format",CLICS_JUDGEMENT_ACRONYM.IOF));
            
            add(new JudgementMapping("Excessive Output",CLICS_JUDGEMENT_ACRONYM.EO));
            add(new JudgementMapping("Incomplete Output",CLICS_JUDGEMENT_ACRONYM.IO));
            add(new JudgementMapping("No Output",CLICS_JUDGEMENT_ACRONYM.NO));
            add(new JudgementMapping("Presentation Error",CLICS_JUDGEMENT_ACRONYM.PE));
            add(new JudgementMapping("No - Excessive Output",CLICS_JUDGEMENT_ACRONYM.EO));
            add(new JudgementMapping("No - Incomplete Output",CLICS_JUDGEMENT_ACRONYM.IO));
            add(new JudgementMapping("No - No Output",CLICS_JUDGEMENT_ACRONYM.NO));
            add(new JudgementMapping("No - Presentation Error",CLICS_JUDGEMENT_ACRONYM.PE));

            add(new JudgementMapping("Wallclock Time Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.WTL));
            add(new JudgementMapping("Wall-clock Time Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.WTL));
            add(new JudgementMapping("Wall Clock Time Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.WTL));
            add(new JudgementMapping("No - Wallclock Time Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.WTL));
            add(new JudgementMapping("No - Wall-clock Time Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.WTL));
            add(new JudgementMapping("No - Wall Clock Time Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.WTL));

            add(new JudgementMapping("Idleness Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.ILE));
            add(new JudgementMapping("Idle Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.ILE));
            add(new JudgementMapping("No - Idleness Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.ILE));
            add(new JudgementMapping("No - Idle Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.ILE));

            add(new JudgementMapping("Time Limit Exceeded - Correct Output",CLICS_JUDGEMENT_ACRONYM.TCO));
            add(new JudgementMapping("Time-Limit Exceeded - Correct Output",CLICS_JUDGEMENT_ACRONYM.TCO));
            add(new JudgementMapping("Time Limit Exceeded - Wrong Answer",CLICS_JUDGEMENT_ACRONYM.TWA));
            add(new JudgementMapping("Time-Limit Exceeded - Wrong Answer",CLICS_JUDGEMENT_ACRONYM.TWA));
            add(new JudgementMapping("Time Limit Exceeded - Presentation Error",CLICS_JUDGEMENT_ACRONYM.TPE));
            add(new JudgementMapping("Time-Limit Exceeded - Presentation Error",CLICS_JUDGEMENT_ACRONYM.TPE));
            add(new JudgementMapping("Time Limit Exceeded - Excessive Output",CLICS_JUDGEMENT_ACRONYM.TEO));
            add(new JudgementMapping("Time-Limit Exceeded - Excessive Output",CLICS_JUDGEMENT_ACRONYM.TEO));
            add(new JudgementMapping("Time Limit Exceeded - Incomplete Output",CLICS_JUDGEMENT_ACRONYM.TIO));
            add(new JudgementMapping("Time-Limit Exceeded - Incomplete Output",CLICS_JUDGEMENT_ACRONYM.TIO));
            add(new JudgementMapping("Time Limit Exceeded - No Output",CLICS_JUDGEMENT_ACRONYM.TNO));
            add(new JudgementMapping("Time-Limit Exceeded - No Output",CLICS_JUDGEMENT_ACRONYM.TNO));

            add(new JudgementMapping("Memory Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.MLE));
            add(new JudgementMapping("Memory-Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.MLE));
            add(new JudgementMapping("No - Memory Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.MLE));
            add(new JudgementMapping("No - Memory-Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.MLE));

            add(new JudgementMapping("Runtime Error - Correct Output",CLICS_JUDGEMENT_ACRONYM.RCO));
            add(new JudgementMapping("Run-time Error - Correct Output",CLICS_JUDGEMENT_ACRONYM.RCO));
            add(new JudgementMapping("Run Time Error - Correct Output",CLICS_JUDGEMENT_ACRONYM.RCO));
            
            add(new JudgementMapping("Runtime Error - Wrong Answer",CLICS_JUDGEMENT_ACRONYM.RWA));
            add(new JudgementMapping("Run-time Error - Wrong Answer",CLICS_JUDGEMENT_ACRONYM.RWA));
            add(new JudgementMapping("Run Time Error - Wrong Answer",CLICS_JUDGEMENT_ACRONYM.RWA));
            
            add(new JudgementMapping("Runtime Error - Presentation Error",CLICS_JUDGEMENT_ACRONYM.RPE));
            add(new JudgementMapping("Run-time Error - Presentation Error",CLICS_JUDGEMENT_ACRONYM.RPE));
            add(new JudgementMapping("Run Time Error - Presentation Error",CLICS_JUDGEMENT_ACRONYM.RPE));
            
            add(new JudgementMapping("Runtime Error - Excessive Output",CLICS_JUDGEMENT_ACRONYM.REO));
            add(new JudgementMapping("Run-time Error - Excessive Output",CLICS_JUDGEMENT_ACRONYM.REO));
            add(new JudgementMapping("Run Time Error - Excessive Output",CLICS_JUDGEMENT_ACRONYM.REO));

            add(new JudgementMapping("Runtime Error - Incomplete Output",CLICS_JUDGEMENT_ACRONYM.RIO));
            add(new JudgementMapping("Run-time Error - Incomplete Output",CLICS_JUDGEMENT_ACRONYM.RIO));
            add(new JudgementMapping("Run Time Error - Incomplete Output",CLICS_JUDGEMENT_ACRONYM.RIO));

            add(new JudgementMapping("Runtime Error - No Output",CLICS_JUDGEMENT_ACRONYM.RNO));
            add(new JudgementMapping("Run-time Error - No Output",CLICS_JUDGEMENT_ACRONYM.RNO));
            add(new JudgementMapping("Run Time Error - No Output",CLICS_JUDGEMENT_ACRONYM.RNO));

            add(new JudgementMapping("Compile Time Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.CTL));
            add(new JudgementMapping("Compile Time-Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.CTL));
            add(new JudgementMapping("No - Compile Time Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.CTL));
            add(new JudgementMapping("No - Compile Time-Limit Exceeded",CLICS_JUDGEMENT_ACRONYM.CTL));

            add(new JudgementMapping("Security Violation",CLICS_JUDGEMENT_ACRONYM.SV));
            add(new JudgementMapping("Illegal Function",CLICS_JUDGEMENT_ACRONYM.IF));
            add(new JudgementMapping("Judging Error",CLICS_JUDGEMENT_ACRONYM.JE));
            add(new JudgementMapping("Submission Error",CLICS_JUDGEMENT_ACRONYM.SE));

            add(new JudgementMapping("Contact Staff",CLICS_JUDGEMENT_ACRONYM.CS));
            add(new JudgementMapping("Other Contact Staff",CLICS_JUDGEMENT_ACRONYM.CS));
            add(new JudgementMapping("Other - Contact Staff",CLICS_JUDGEMENT_ACRONYM.CS));
        }
    } ;

    static class JudgementMapping {
        private String text ;
        private CLICS_JUDGEMENT_ACRONYM acronym ;
        
        protected JudgementMapping(String text, CLICS_JUDGEMENT_ACRONYM acronym) {
            this.text = text;
            this.acronym = acronym;
        }
        
        public String getText() {
            return text;
        }

        public CLICS_JUDGEMENT_ACRONYM getAcronym() {
            return acronym;
        }

    }
    
    

    private static Map<String,String> big5Mapping = new HashMap<String,String>();
    
    static {
        //put the big-5 acronyms in the mapping table (so that requests to map a big5 judgement return that judgement acronym)
        for (CLICS_BIG5 judgement : CLICS_BIG5.values()) {
            big5Mapping.put(judgement.name(), judgement.acronym);
        }
        //put each non-big5 judgement in the table, such that its key is the actual acronym (e.g. "OFE") and its value is 
        // the corresponding Big5 acronym (as specified in the CLICS_NON_BIG5 enumeration definition)
        for (CLICS_NON_BIG5 judgement : CLICS_NON_BIG5.values()) {
            String value = judgement.getValue();
//            System.out.println ("judgement value = '" + value + "'");
            String[] split = value.split(":");
            big5Mapping.put(split[0], split[1]);
        }
        
//        System.out.println("Big5 Mapping table contents: judgement acronym and corresponding Big5 acronym");
//        for (String key : big5Mapping.keySet()) {
//            System.out.println (" " + key + "=" + big5Mapping.get(key));
//        }
        
        
    };
    
    /**
     * Constructs a new CLICSJudgementType containing the specified CLICS judgement type values.
     * 
     * @param id the identifier (acronym) for the judgement (for example, "TLE")
     * @param name the text string name associated with the judgement (for example, "No - Time Limit Exceeded")
     * @param penalty whether or not this judgement causes a penalty during scoring
     * @param solved whether or not this judgement represents a correct solution to a problem
     */
    public CLICSJudgementType (String id, String name, boolean penalty, boolean solved) {
        this.id = id;
        this.name = name;
        this.penalty = penalty;
        this.solved = solved;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isPenalty() {
        return penalty;
    }
    public void setPenalty(boolean penalty) {
        this.penalty = penalty;
    }
    public boolean isSolved() {
        return solved;
    }
    public void setSolved(boolean solved) {
        this.solved = solved;
    }
    
    /**
     * Returns true if the acronym for this CLICSJudgementType is one of the CLICS "Big 5" judgement types.
     * @return true if this judgement is a CLICS "Big 5" judgement type; false if not
     */
    public boolean isBig5() {
        
        for (CLICS_BIG5 val : CLICS_BIG5.values()) {
            if (this.id.equalsIgnoreCase(val.toString())){
                return true;
            }
        }
        return false;

    }
    
    /**
     * Returns the CLICS "Big 5" judgement type for this judgement (as recommended by the CLICS Contest API
     * specification at https://clics.ecs.baylor.edu/index.php?title=Contest_API#Judgement_Types), or null
     * if there is no recommended mapping for the acronym defined in this judgement type.
     * 
     * @return the CLICS "Big 5" judgement 
     */
    public String getBig5EquivalentAcronym() {
        return big5Mapping.get(this.id);
    }
    
    /**
     * Returns the {@link CLICS_JUDGEMENT_ACRONYM} element which corresponds to the specified text string, or null
     * ff there is no CLICS_JUDGEMENT_ACRONYM whose text string matches the specified text.
     * 
     * @param text a String giving a judgement message; for example "Wrong Answer"
     * 
     * @return the CLICS_JUDGEMENT_ACRONYM element corresponding to the received text (e.g. CLICS_JUDGEMENT_ACRONYM.WA), or null 
     */
    public static CLICS_JUDGEMENT_ACRONYM getCLICSAcronym (String text) {
          
        for (JudgementMapping mapping : judgementStringMappings) {
            
            String mappingText = mapping.getText();
            
            if (mappingText.equalsIgnoreCase(text)) {
                return mapping.getAcronym();
            }
        }
        
        //Text string not found
        return null;
    }
    
    /**
     * This main() mimics the tests performed by JUnit CLICSJudgementTypeTest.
     * 
     * @param args
     */
    public static void main (String [] args) {
        
        CLICSJudgementType jt = new CLICSJudgementType("AC", "Accepted", false, true);
        System.out.println ("Accepted maps to " + jt.getBig5EquivalentAcronym()) ;
        if (!jt.isBig5()) {
            System.out.println ("Error: 'Accepted' should return isBig5() = true");
        }
        
        CLICSJudgementType jt2 = new CLICSJudgementType("NO", "No Output", true, false);
        System.out.println ("No Output maps to " + jt2.getBig5EquivalentAcronym()) ;
        if (jt2.isBig5()) {
            System.out.println ("Error: 'No Output' should NOT return isBig5() = true");
        }
        
        CLICSJudgementType jt3 = new CLICSJudgementType("JE", "Judging Error", true, false);
        System.out.println ("Judging Error maps to " + jt3.getBig5EquivalentAcronym()) ;

        if (jt3.isBig5()) {
            System.out.println ("Error: 'Judging Error' should NOT return isBig5() = true");
        }
        
        CLICSJudgementType jt4 = new CLICSJudgementType("NEVER", "Never heard of it", true, false);
        System.out.println ("Never heard of it maps to " + jt4.getBig5EquivalentAcronym()) ;
        if (jt4.isBig5()) {
            System.out.println ("Error: 'Never heard of it' should NOT return isBig5() = true");
        }
        
    }
    
}
