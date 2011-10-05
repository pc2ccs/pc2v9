package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Testing Frame for JPanePlugin.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// TODO cleanup insure this is the only defined TestingFrame

// $HeadURL$
public class TestingFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 9057858952147778214L;

    private JPanel centerPane = null;

    private JPanePlugin plugin = null;

    public TestingFrame() {
        throw new UnsupportedOperationException("default constructor not allowed, must use constructor with JPanePlugin.");
    }

    /**
     * Specify pane to be displayed.
     * 
     * @param pane
     */
    public TestingFrame(JPanePlugin pane) {
        super();
        this.plugin = pane;
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(456, 240));
        this.setContentPane(getCenterPane());
        this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        // this.setContentPane(getPluginPane());
        this.setTitle("Testing Frame for "+plugin.getPluginTitle());

        FrameUtilities.centerFrame(this);
        setVisible(true);
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            centerPane = new JPanel();
            centerPane.setLayout(new BorderLayout());
            centerPane.add(plugin, BorderLayout.CENTER);
        }
        return centerPane;
    }
}
