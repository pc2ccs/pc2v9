// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.shadow.ShadowController;

/**
 * A JFrame which displays a Shadow comparison results pane.
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class ShadowCompareFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    
    public ShadowCompareFrame(ShadowController shadowController) {
        Dimension size = new Dimension(600,600);
        this.setPreferredSize(size);
//        this.setMinimumSize(size);
        this.getContentPane().add(new ShadowComparePane(shadowController));
    }

}
