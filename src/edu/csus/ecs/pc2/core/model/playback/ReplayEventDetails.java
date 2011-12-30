package edu.csus.ecs.pc2.core.model.playback;

import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IElementObject;
import edu.csus.ecs.pc2.core.model.ISubmission;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * Information used with {@link ReplayEvent}.
 * 
 * Contains files, run reference, clarification reference.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ReplayEventDetails implements IElementObject {

    /**
     * 
     */
    private static final long serialVersionUID = 5888807626510351852L;
    
    private ElementId elementId = null;

    private Run run = null;
    
    private Clarification clarification = null;

    private SerializedFile[] files = new SerializedFile[0];

    private JudgementRecord judgementRecord;
    
    private ReplayEvent replayEvent;
    
    @SuppressWarnings("unused")
    private ReplayEventDetails() {
        
    }
    
    /**
     * Use for Run Judgement event.
     * 
     * @param replayEvent
     * @param run
     * @param judgementRecord
     */
    public ReplayEventDetails(ReplayEvent replayEvent, Run run, JudgementRecord judgementRecord) {
        super();
        this.replayEvent = replayEvent;
        this.run = run;
        this.judgementRecord = judgementRecord;
    }
    

    /**
     * Use for Run submission event.
     * @param replayEvent
     * @param run
     * @param files
     */
    public ReplayEventDetails(ReplayEvent replayEvent, Run run, SerializedFile[] files) {
        super();
        this.replayEvent = replayEvent;
        this.run = run;
        this.files = files;
    }
    
    
    /**
     * Use for Clarification submission or Clarification answer event.
     * @param replayEvent
     * @param clarification
     */
    public ReplayEventDetails(ReplayEvent replayEvent, Clarification clarification) {
        super();
        this.replayEvent = replayEvent;
        this.clarification = clarification;
    }


    public void setSiteNumber(int siteNumber) {
        // TODO can not set site number, no visibility in ElementId
        // elementId.setSiteNumber(siteNumber);
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }
    
    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    public ElementId getElementId() {
        return elementId;
    }

    public Run getRun() {
        return run;
    }

    public Clarification getClarification() {
        return clarification;
    }

    public SerializedFile[] getFiles() {
        return files;
    }

    public JudgementRecord getJudgementRecord() {
        return judgementRecord;
    }

    public ReplayEvent getReplayEvent() {
        return replayEvent;
    }
    
    private String toStringDetails() {
        return replayEvent.getEventType() + " ";
    }
    
    @Override
    public String toString() {
        if (run != null) {
            if (judgementRecord != null) {
                return toStringDetails() + judgementRecord + " " + run;
            } else {
                return toStringDetails() + run;
            }
        } else if (clarification != null) {
            return toStringDetails() + run;
        } else {
            return toStringDetails();
        }
    }

    public ISubmission getSubmission() {
        if (run != null) {
            return run;
        } else if (clarification != null) {
            return clarification;
        } else {
            return null;
        }
    }
}
