package edu.csus.ecs.pc2.server;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.IModel;
import edu.csus.ecs.pc2.core.RunEvent;
import edu.csus.ecs.pc2.core.RunListener;

/**
 * GUI for Server.
 * 
 * @author pc2@ecs.csus.edu
 */
public class ServerView extends JFrame {

    private IModel serverModel = null;
    private IController serverController = null;

    /**
     * 
     */
    private static final long serialVersionUID = 4547574494017009634L;

    private JPanel mainViewPane = null;

    private JPanel runPane = null;

    private JScrollPane runScrollPane = null;

    private JList runJList = null;

    private DefaultListModel runListModel = new DefaultListModel();

    public ServerView(IModel serverModel, IController serverController) {
        super();
        this.serverModel = serverModel;
        this.serverController = serverController;
        serverModel.addRunListener(new RunListenerImplementation());
        initialize();
    }

    /**
     * This method initializes
     * 
     */
    public ServerView() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(472, 270));
        this.setTitle("Server View");
        this.setContentPane(getMainViewPane());
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(22);
            }
        });
        setVisible(true);
    }

    private void updateListBox(String string) {
        runListModel.addElement(string);
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     *      
     */
    private class RunListenerImplementation implements RunListener {

        public void runAdded(RunEvent event) {
            updateListBox(event.getSubmittedRun() + " ADDED ");
        }

        public void runChanged(RunEvent event) {
            updateListBox(event.getSubmittedRun() + " CHANGED ");
        }

        public void runRemoved(RunEvent event) {
            updateListBox(event.getSubmittedRun() + " REMOVED ");
        }
    }

    /**
     * This method initializes mainViewPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainViewPane() {
        if (mainViewPane == null) {
            mainViewPane = new JPanel();
            mainViewPane.setLayout(null);
            mainViewPane.add(getRunPane(), null);
        }
        return mainViewPane;
    }

    /**
     * This method initializes runPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getRunPane() {
        if (runPane == null) {
            runPane = new JPanel();
            runPane.setLayout(new BorderLayout());
            runPane.setBounds(new java.awt.Rectangle(12, 79, 429, 145));
            runPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Runs",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
                    null));
            runPane.add(getRunScrollPane(), java.awt.BorderLayout.CENTER);
        }
        return runPane;
    }

    /**
     * This method initializes runScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getRunScrollPane() {
        if (runScrollPane == null) {
            runScrollPane = new JScrollPane();
            runScrollPane.setViewportView(getRunJList());
        }
        return runScrollPane;
    }

    /**
     * This method initializes runJList
     * 
     * @return javax.swing.JList
     */
    private JList getRunJList() {
        if (runJList == null) {
            runJList = new JList(runListModel);
        }
        return runJList;
    }

    /**
     * Puts this frame to right of input frame.
     * 
     * @param sourceFrame
     */
    public void windowToRight(JFrame sourceFrame) {
        int rightX = sourceFrame.getX() + sourceFrame.getWidth();
        setLocation(rightX, getY());
    }

} // @jve:decl-index=0:visual-constraint="10,10"
