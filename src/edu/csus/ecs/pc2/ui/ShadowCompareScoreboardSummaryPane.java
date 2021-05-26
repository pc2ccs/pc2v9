// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.shadow.ShadowScoreboardRowComparison;

/**
 * This class defines a {@link JPanel} containing a summary of current shadow scoreboard comparisons
 * based on the supplied {@link ShadowScoreboardRowComparison} array.
 * 
 * @author John Clevenger -- PC^2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class ShadowCompareScoreboardSummaryPane extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private JLabel scoreboardComparisonTextLabel = null;
    private final String COMPARISON_LABEL_TEXT = "Current status: " ;

    private JLabel scoreboardComparisonResultLabel;
    private final String DEFAULT_NO_COMPARISON_AVAILABLE_TEXT = "No comparison available";
    
    /**
     * Construct a ShadowCompareScoreboardSummaryPane used to display a summary of scoreboard comparisons.
     */
    public ShadowCompareScoreboardSummaryPane() {
        
        setMaximumSize(new Dimension(300,100));

//        JLabel header = new JLabel("Comparison of PC2 vs. Remote Scoreboard");
//        header.setAlignmentX(Component.CENTER_ALIGNMENT);
//        add(header);

        add(getScoreboardComparisonLabel());
        
        add(getScoreboardComparisonStatusValueLabel());
       
        updateSummary(null);

    }
    
    private JLabel getScoreboardComparisonLabel() {
        if (scoreboardComparisonTextLabel==null) {
            scoreboardComparisonTextLabel = new JLabel(COMPARISON_LABEL_TEXT);
        }
        return scoreboardComparisonTextLabel;
    }

    private JLabel getScoreboardComparisonStatusValueLabel() {
        if (scoreboardComparisonResultLabel==null) {
            scoreboardComparisonResultLabel = new JLabel(DEFAULT_NO_COMPARISON_AVAILABLE_TEXT);
        }
        return scoreboardComparisonResultLabel;
    }

    /**
     * Updates this {@link ShadowCompareScoreboardSummaryPane} with summary data taken from the specified array.
     * If the specified array is null or empty a message to that effect is instead displayed in the pane.
     * 
     * Note that this method is called from the class constructor, but may also be called from external code
     * (typically, from a {@link ShadowCompareScoreboardPane}) to invoke an update of the summary information being displayed.
     * 
     * @param currentStatus an array of {@link ShadowScoreboardRowComparison}s from which summary information to be displayed is extracted.
     */
    public void updateSummary(ShadowScoreboardRowComparison [] currentStatus) {
        
        if (currentStatus==null || currentStatus.length<=0) {
            getScoreboardComparisonStatusValueLabel().setText(DEFAULT_NO_COMPARISON_AVAILABLE_TEXT);
        } else {
            
            String newValue = "Scoreboards Match";
            for (ShadowScoreboardRowComparison row : currentStatus) {
                if (!row.isMatch()) {
                    newValue = "Scoreboards DO NOT match";
                    break;
                }
            }
            getScoreboardComparisonStatusValueLabel().setText(newValue);
        }
            
        this.repaint();

    }

}
