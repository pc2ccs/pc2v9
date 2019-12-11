// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.FlowLayout;
import java.util.Map;

import javax.swing.JLabel;

import edu.csus.ecs.pc2.shadow.ShadowJudgementPair;

/**
 * A plug-in pane which displays Shadow comparison results.
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class ShadowComparePane extends JPanePlugin {

    private static final long serialVersionUID = 1L;

    @Override
    public String getPluginTitle() {
        return "Shadow_Compare_Pane";
    }
    
    public ShadowComparePane(Map<String,ShadowJudgementPair> map) {
        
        //temporary hack to display some data
        this.setLayout(new FlowLayout());
        JLabel header = new JLabel("Comparison of PC2 vs. Remote Judgements");
        this.add(header);
        if (map != null && !map.keySet().isEmpty()) {
            for (String submission : map.keySet()) {
                ShadowJudgementPair pair = map.get(submission);
                String pc2Judgement = pair.getPc2Judgement();
                String remoteJudgement = pair.getRemoteCCSJudgement();
                JLabel label = new JLabel("Submission: " + submission + "  PC2: " + pc2Judgement + "  Remote: " + remoteJudgement);
                this.add(label);
            } 
        } else {
            JLabel label = new JLabel("Comparison map is null or empty");
            this.add(label);
        }
    }

}
