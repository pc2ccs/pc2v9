package edu.csus.ecs.pc2.team;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import edu.csus.ecs.pc2.core.IModel;
import edu.csus.ecs.pc2.core.Language;
import edu.csus.ecs.pc2.core.Problem;
import edu.csus.ecs.pc2.core.RunEvent;
import edu.csus.ecs.pc2.core.RunListener;

/**
 * Represents an arbitrary contest GUI.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class TeamView extends JFrame  {

    public static final String SVN_ID = "$Id$";

    // TODO remove @SuppressWarnings for theModel
    @SuppressWarnings("unused")
    private IModel theModel = null;

    private ITeamController teamController = null;

    /**
     * 
     */
    private static final long serialVersionUID = 8225187691479543638L;

    private JPanel submitRunPane = null;

    private JPanel mainViewPane = null;

    private JTabbedPane viewTabbedPane = null;

    private JButton submitRunButton = null;

    private JLabel problemLabel = null;

    private JLabel jLabel = null;

    private JLabel jLabel1 = null;

    private JLabel fileNameLabel = null;

    private JComboBox problemComboBox = null;

    private JComboBox languageComboBox = null;

    private JButton pickFileButton = null;

    private JList runSubmissionList = null;

    private JScrollPane runListScrollPane = null;

    private JPanel runListPane = null;

    private DefaultListModel runListModel = new DefaultListModel();

    public TeamView(IModel theModel, ITeamController teamController) {
        super();
        this.theModel = theModel;
        this.teamController = teamController;
        initialize();
        theModel.addRunListener(new RunListenerImplementation());
        
    }

    /**
     * Nevermind this method, needed for VE and other reasons.
     * 
     */
    public TeamView() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(490, 368));
        this.setContentPane(getMainViewPane());
        this.setTitle("The TeamView");
        setVisible(true);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        
        populateGUI();
    }
    
    private void populateGUI() {
        
        getProblemComboBox().removeAllItems();
        for (Problem problem : theModel.getProblems()){
            getProblemComboBox().addItem(problem);
        }
        
        getLanguageComboBox().removeAllItems();
        for (Language language : theModel.getLanguages()){
            getLanguageComboBox().addItem(language);
        }
        
    }

    private void updateListBox(String string) {
        runListModel.addElement(string);
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
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
     * This method initializes submitRunPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSubmitRunPane() {
        if (submitRunPane == null) {
            fileNameLabel = new JLabel();
            fileNameLabel.setBounds(new java.awt.Rectangle(126, 99, 219, 21));
            fileNameLabel.setText("samps/sumit.java");
            jLabel1 = new JLabel();
            jLabel1.setBounds(new java.awt.Rectangle(28, 99, 80, 21));
            jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel1.setText("Filename");
            jLabel = new JLabel();
            jLabel.setBounds(new java.awt.Rectangle(28, 62, 80, 21));
            jLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel.setText("Language");
            problemLabel = new JLabel();
            problemLabel.setBounds(new java.awt.Rectangle(28, 19, 80, 21));
            problemLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            problemLabel.setText("Problem");
            submitRunPane = new JPanel();
            submitRunPane.setLayout(null);
            submitRunPane.add(getSubmitRunButton(), null);
            submitRunPane.add(problemLabel, null);
            submitRunPane.add(jLabel, null);
            submitRunPane.add(jLabel1, null);
            submitRunPane.add(fileNameLabel, null);
            submitRunPane.add(getProblemComboBox(), null);
            submitRunPane.add(getLanguageComboBox(), null);
            submitRunPane.add(getPickFileButton(), null);
            submitRunPane.add(getRunListPane(), null);
        }
        return submitRunPane;
    }

    /**
     * This method initializes mainViewPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainViewPane() {
        if (mainViewPane == null) {
            mainViewPane = new JPanel();
            mainViewPane.setLayout(new BorderLayout());
            mainViewPane.add(getViewTabbedPane(), java.awt.BorderLayout.CENTER);
        }
        return mainViewPane;
    }

    /**
     * This method initializes viewTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getViewTabbedPane() {
        if (viewTabbedPane == null) {
            viewTabbedPane = new JTabbedPane();
            viewTabbedPane.addTab("Submit Run", null, getSubmitRunPane(), null);
        }
        return viewTabbedPane;
    }

    /**
     * This method initializes submitRunButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSubmitRunButton() {
        if (submitRunButton == null) {
            submitRunButton = new JButton();
            submitRunButton.setBounds(new java.awt.Rectangle(366, 131, 74, 26));
            submitRunButton.setText("Submit");
            submitRunButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    submitRun();
                }

            });
        }
        return submitRunButton;
    }

    private boolean fileExists(String fileName) {
        File file = new File(fileName);
        return file.isFile();
    }

    protected void submitRun() {

        String problem = ((Problem) getProblemComboBox().getSelectedItem()).toString();
        String language = ((Language) getLanguageComboBox().getSelectedItem()).toString();
        String filename = fileNameLabel.getText();

        if (!fileExists(filename)) {
            File curdir = new File(".");

            String message = filename + " not found";
            try {
                message = message + " in " + curdir.getCanonicalPath();
            } catch (Exception e) {
                // ignore exception
                message = message + ""; // What a waste of time and code.
            }
            JOptionPane.showMessageDialog(this, message);
            return;
        }

        try {

            teamController.submitRun(1, problem, language, filename);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Exception " + e.getMessage());
        }

    }

    /**
     * This method initializes problemComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getProblemComboBox() {
        if (problemComboBox == null) {
            problemComboBox = new JComboBox();
            problemComboBox.setBounds(new java.awt.Rectangle(126, 15, 221, 28));
            
            problemComboBox.addItem(new Problem("Select Problem"));
        }
        return problemComboBox;
    }

    /**
     * This method initializes jComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getLanguageComboBox() {
        if (languageComboBox == null) {
            languageComboBox = new JComboBox();
            languageComboBox.setBounds(new java.awt.Rectangle(127, 58, 221, 28));
            
            languageComboBox.addItem(new Language("Select Language"));
        }
        return languageComboBox;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getPickFileButton() {
        if (pickFileButton == null) {
            pickFileButton = new JButton();
            pickFileButton.setBounds(new java.awt.Rectangle(367, 94, 74, 26));
            pickFileButton.setText("Pick");
            pickFileButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
                }
            });
        }
        return pickFileButton;
    }

    /**
     * This method initializes runSubmissionList
     * 
     * @return javax.swing.JList
     */
    private JList getRunSubmissionList() {
        if (runSubmissionList == null) {
            runSubmissionList = new JList(runListModel);
        }
        return runSubmissionList;
    }

    /**
     * This method initializes runListScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getRunListScrollPane() {
        if (runListScrollPane == null) {
            runListScrollPane = new JScrollPane();
            runListScrollPane.setViewportView(getRunSubmissionList());
        }
        return runListScrollPane;
    }

    /**
     * This method initializes runListPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getRunListPane() {
        if (runListPane == null) {
            runListPane = new JPanel();
            runListPane.setLayout(new BorderLayout());
            runListPane.setBounds(new java.awt.Rectangle(22, 170, 418, 130));
            runListPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Runs",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
                    null));
            runListPane.add(getRunListScrollPane(), java.awt.BorderLayout.CENTER);
        }
        return runListPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
