// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.shadow.ShadowController;

/**
 * A JFrame which displays a Shadow Scoreboard comparison results pane.
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class ShadowCompareScoreboardFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    
    public ShadowCompareScoreboardFrame(ShadowController shadowController) {
        Dimension size = new Dimension(1200,900);
        this.setPreferredSize(size);
        this.setMinimumSize(size);
        this.getContentPane().add(new ShadowCompareScoreboardPane(shadowController));
    }

}
