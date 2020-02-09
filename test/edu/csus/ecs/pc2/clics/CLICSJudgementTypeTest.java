package edu.csus.ecs.pc2.clics;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

public class CLICSJudgementTypeTest extends AbstractTestCase{
    
    
    /**
     * Test that both CLICS-defined and User-defined acronyms map properly to CLICS "Big-5" judgement types. 
     * @throws Exception
     */
    public void testAcronymMappings() throws Exception {
        
        CLICSJudgementType jt = new CLICSJudgementType("AC", "Accepted", false, true);
        assertEquals("'Accepted' does not map to AC: ", "AC", jt.getBig5EquivalentAcronym()) ;
        assertTrue("'Accepted' should return isBig5() = true", jt.isBig5());
                
        CLICSJudgementType jt2 = new CLICSJudgementType("NO", "No Output", true, false);
        assertEquals("'No Output' does not map to WA: ", "WA", jt2.getBig5EquivalentAcronym()) ;
        assertFalse("'No Output' should return isBig5() = false", jt2.isBig5());
        
        CLICSJudgementType jt3 = new CLICSJudgementType("JE", "Judging Error", true, false);
        assertEquals("'Judging Error' does not map to Undefined: ", "UNDEFINED", jt3.getBig5EquivalentAcronym()) ;
        assertFalse("'Judging Error' should return isBig5() = false", jt3.isBig5());

        CLICSJudgementType jt4 = new CLICSJudgementType("NEVER", "Never heard of it", true, false);
        assertEquals("'Never heard of it' does not map to null: ", null, jt4.getBig5EquivalentAcronym()) ;
        assertFalse("'Never heard of it' should return isBig5() = false", jt4.isBig5());

    }

}
