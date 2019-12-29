// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics;

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
     * Constructs a new CLICSJudgementType containing the specified CLICS judgement type values.
     * 
     * @param id the identifier (acronym) for the judgement
     * @param name the text string name associated with the judgement
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
    
    
}
