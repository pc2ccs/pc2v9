// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
    
    private boolean isOpen = true;
    
    public ShadowCompareScoreboardFrame(ShadowController shadowController) {
        Dimension size = new Dimension(1200,900);
        this.setPreferredSize(size);
        this.setMinimumSize(size);
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                // This will be called after the window has closed
                isOpen = false;
            }
        });
        this.getContentPane().add(new ShadowCompareScoreboardPane(shadowController));
    }
    
    public boolean isOpen() {
        return isOpen;
    }

}
