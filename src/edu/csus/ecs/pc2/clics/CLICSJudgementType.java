// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics;

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
     * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
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
     * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
     *
     */
    public static enum CLICS_NON_BIG5 {
        APE  ("APE:AC"), //Accepted, Presentation Error    [Maps to AC]
        OLE  ("OLE:WA"), //Output Limit Exceeded           [Maps to WA]
        PE   ("PE:WA"),  //Presentation Error              [Maps to WA]
        EO   ("EO:WA"),  //Excessive Output                [Maps to WA]
        IO   ("IO:WA"),  //Incomplete Output               [Maps to WA]
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
        JE   ("JE:UNDEFINED"),  //Judging Error (something went wrong with the system)            [No defined mapping]
        SE   ("SE:UNDEFINED"),  //Submission Error (something went wrong with the submission)     [No defined mapping]
        CS   ("CS:UNDEFINED");  //Contact Staff (something went wrong)                            [No defined mapping]
        
        private final String acronyms;

        private CLICS_NON_BIG5(String acronyms) {
            this.acronyms = acronyms;
        }
        
        public String getValue() {
            return acronyms;
        }

    }

    private static Map<String,String> big5Mapping = new HashMap<String,String>();
    static {
        //put the big-5 acronyms in the mapping table (so that requests to map a big5 judgement return that judgement acronym)
        for (CLICS_BIG5 judgement : CLICS_BIG5.values()) {
            big5Mapping.put(judgement.name(), judgement.acronym);
        }
        //put each non-big5 judgement in the table, such that its key is the actual acronym (e.g. "OFE") and its value is 
        // the corresponding Big5 acronym (as specified in the enumeration definition)
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
