// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.shadow.ShadowJudgementInfo;

/**
 * This class defines a {@link JPanel} containing a summary of current shadow operations based on the
 * supplied {@link ShadowJudgementInfo}.
 * 
 * @author John Clevenger -- PC^2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class ShadowCompareSummaryPane extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private JLabel submissionCountLabel = null;
    private final String SUBMISSION_COUNT_TEXT = "Total Submissions = " ;

    private JLabel matchCountLabel = null;
    private final String MATCH_COUNT_TEXT = "Matches: " ;
    
    private JLabel nonMatchCountLabel = null;
    private final String NON_MATCH_COUNT_TEXT = "Non-matches: " ;
    
    private JLabel pendingCountLabel = null;
    private final String PENDING_COUNT_TEXT = "Pending: " ;

    private boolean currentlyShowingNoSummaryAvailable = false;
    
    /**
     * Construct a ShadowCompareSummaryPane displaying a summary of the information contained in the
     * specified ShadowJudgementInfo map.  If the specified map is null then the summary pane will contain
     * a message that no summary information is available.
     * 
     * @param currentJudgementMap a map containing the shadow judgement information to be summarized and displayed in this pane.
     */
    public ShadowCompareSummaryPane(Map<String, ShadowJudgementInfo> currentJudgementMap) {
        
        this.setMaximumSize(new Dimension(500,40));
       
        updateSummary(currentJudgementMap);

    }
    
    /**
     * Updates this {@link ShadowCompareSummaryPane} with summary data taken from the specified map.
     * If the specified map is null a message to that effect is instead displayed in the pane.
     * 
     * Note that this method is called from the class constructor, but may also be called from external code
     * (typically, from a {@link ShadowCompareRunsPane} to invoke an update of the summary information being displayed.
     * 
     * @param currentJudgementMap a Map of ShadowJudgementInfo from which summary information to be displayed is extracted
     */
    public void updateSummary(Map<String, ShadowJudgementInfo> currentJudgementMap) {
        
        if (currentJudgementMap!=null) {
            
            //a previous call with a null map may have added a "No Summary Available" label
            if (currentlyShowingNoSummaryAvailable) {
                this.removeAll();
                currentlyShowingNoSummaryAvailable = false;
            }
            
            //calculate the current submission count
            int submissionCount = currentJudgementMap.keySet().size();

            //show the current submission count (labels may have been removed by a previous call with a null judgement map)
            if (submissionCountLabel==null) {
                submissionCountLabel = new JLabel();
                this.add(submissionCountLabel);
            }
            submissionCountLabel.setText(SUBMISSION_COUNT_TEXT + new Integer(submissionCount).toString());
            
            //calculate the number of matching/non-matching/pending judgements
            int match = 0;
            int noMatch = 0;
            int pending = 0;
            for (String submissionID : currentJudgementMap.keySet()) {

                String pc2Judgement = currentJudgementMap.get(submissionID).getShadowJudgementPair().getPc2Judgement();
                String remoteJudgement = currentJudgementMap.get(submissionID).getShadowJudgementPair().getRemoteCCSJudgement();
                if (pc2Judgement != null && remoteJudgement != null && 
                        !(pc2Judgement.contains("pending")) && !(remoteJudgement.contains("pending")) &&
                         (pc2Judgement.equalsIgnoreCase(remoteJudgement))) {
                    match++;
                } else {
                    //it's not a match; separate out "pendings"
                    if (   (pc2Judgement!=null && pc2Judgement.contains("pending")) ||
                           (remoteJudgement!=null && remoteJudgement.contains("pending")) ) {
                        pending++;
                    } else {
                        //it can't be counted "pending" either by virtue of the PC2 status or the Remote status; count as "noMatch"
                        // TODO: filter out late/deleted submissions into a separate category?
                        noMatch++;
                    }
                }
            }
            
            Component horizontalStrut_2 = Box.createHorizontalStrut(15);
            this.add(horizontalStrut_2);
            
            //show the matching count (note that labels may have been removed by a previous call with a null judgement map)
            if (matchCountLabel==null) {
                matchCountLabel = new JLabel();
                this.add(matchCountLabel);
            }
            matchCountLabel.setText(MATCH_COUNT_TEXT + match );

            Component horizontalStrut_3 = Box.createHorizontalStrut(15);
            this.add(horizontalStrut_3);
            
            //show the non-matching count (note that labels may have been removed by a previous call with a null judgement map)
            if (nonMatchCountLabel==null) {
                nonMatchCountLabel = new JLabel();
                this.add(nonMatchCountLabel);
            }
            nonMatchCountLabel.setText(NON_MATCH_COUNT_TEXT + noMatch);

            Component horizontalStrut_4 = Box.createHorizontalStrut(15);
            this.add(horizontalStrut_4);
            
            //show the pending count (note that labels may have been removed by a previous call with a null judgement map)
            if (pendingCountLabel==null) {
                pendingCountLabel = new JLabel();
                this.add(pendingCountLabel);
            }
            pendingCountLabel.setText(PENDING_COUNT_TEXT + pending);

        } else {
            //we have a null judgement map
            this.removeAll();
            this.add(new JLabel("No comparison summary available"));
            currentlyShowingNoSummaryAvailable  = true ;
        }
        
        this.repaint();

    }

}
