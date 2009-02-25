package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;

/**
 * Test Frame for PlaybackPane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PlaybackTestFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -6998999058762887038L;

    private JTabbedPane tabbedPane = null;

    private PlaybackPane playbackPane = null;

    /**
     * This method initializes
     * 
     */
    public PlaybackTestFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(600, 350);
        this.setContentPane(getTabbedPane());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("PC^2 Playback Testing Frame");

        // PlaybackPane pane = new PlaybackPane();
        // pane.addSampleEventRows();
        // mainPane.add(pane, java.awt.BorderLayout.CENTER);

        // frame.setContentPane(mainPane);
        FrameUtilities.centerFrame(this);

    }

    /**
     * This method initializes tabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Playback", null, getPlaybackPane(), null);
        }
        return tabbedPane;
    }

    /**
     * This method initializes playbackPane
     * 
     * @return edu.csus.ecs.pc2.ui.PlaybackPane
     */
    private PlaybackPane getPlaybackPane() {
        if (playbackPane == null) {
            playbackPane = new PlaybackPane();
//            playbackPane.addSampleEventRows();
        }
        return playbackPane;
    }

    public static void main(String[] args) {
        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createContest(1, 1, 12, 12, true);
        PlaybackTestFrame frame = new PlaybackTestFrame();
        frame.getPlaybackPane().setContest(contest);
        frame.setVisible(true);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
