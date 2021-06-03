// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.shadow.ShadowScoreboardRowComparison;
import java.awt.Font;

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
        
        setMaximumSize(new Dimension(500, 100));

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
            scoreboardComparisonTextLabel.setFont(new Font("Arial", Font.BOLD, 12));
        }
        return scoreboardComparisonTextLabel;
    }

    private JLabel getScoreboardComparisonStatusValueLabel() {
        if (scoreboardComparisonResultLabel==null) {
            scoreboardComparisonResultLabel = new JLabel(DEFAULT_NO_COMPARISON_AVAILABLE_TEXT);
            scoreboardComparisonResultLabel.setFont(new Font("Arial", Font.BOLD, 12));
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
            getScoreboardComparisonStatusValueLabel().setForeground(new Color(51,204,51));
            
            int totalCount = 0;
            int nonMatchCount = 0;
            for (ShadowScoreboardRowComparison row : currentStatus) {
                totalCount++ ;
                if (!row.isMatch()) {
                    nonMatchCount++;
                }
            }
            
            if (nonMatchCount>0) {
                newValue = "Scoreboards DO NOT match  ";
                newValue += "(" + nonMatchCount + " of " + totalCount + " rows mismatched)";
                getScoreboardComparisonStatusValueLabel().setForeground(Color.red);     
            }
            
            getScoreboardComparisonStatusValueLabel().setText(newValue);
        }
            
        this.repaint();

    }

}
