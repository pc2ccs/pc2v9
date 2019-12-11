// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.util.Map;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.shadow.ShadowJudgementPair;

/**
 * A JFrame which displays a Shadow comparison results pane.
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class ShadowCompareFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    
    public ShadowCompareFrame(Map<String, ShadowJudgementPair> map) {
        this.getContentPane().add(new ShadowComparePane(map));
    }

}
