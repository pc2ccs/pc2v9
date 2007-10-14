package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunEvent.Action;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.scoring.IScoringAlgorithm;
import edu.csus.ecs.pc2.core.util.XSLTransformer;
import javax.swing.JScrollPane;

/**
 * Standings HTML view pane.
 * 
 * Use the input style sheet to show a view of the contest standings.
 * <P>
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class StandingsHTMLPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -1570509469246188861L;

    private JPanel messagePane = null;

    private JLabel messageLabel = null;

    private Log log;

    private IScoringAlgorithm scoringAlgorithm = new DefaultScoringAlgorithm();

    private JTextPane textArea = null;

    /**
     * Name of style sheet filename
     */
    private String styleSheetFileName;

    /**
     * Name of style sheet directory
     */
    private String styleSheetDirectoryName = null;

    private JButton refreshButton = null;

    private JScrollPane scrollPane = null;

    private StandingsHTMLPane() {
        super();
        initialize();
    }

    public StandingsHTMLPane(String styleSheetFileName) {
        this();
        setStyleSheetFileName(styleSheetFileName);
    }

    public StandingsHTMLPane(String styleSheetFileName, String styleSheetDirectoryName) {
        this();
        setStyleSheetFileName(styleSheetFileName);
        setStyleSheetDirectoryName(styleSheetDirectoryName);
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(470, 243));
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getScrollPane(), java.awt.BorderLayout.CENTER);
    }

    @Override
    public String getPluginTitle() {
        return "Standings HTML Plugin";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);

        log = getController().getLog();

        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addProblemListener(new ProblemListenerImplementation());
        getContest().addRunListener(new RunListenerImplementation());
        getContest().addContestInformationListener(new ContestInformationListenerImplementation());

        refreshStandings();
    }

    private String getDefaultSyleSheetDirectoryName() {

        String xslDir = "data" + File.separator + "xsl";
        File xslDirFile = new File(xslDir);
        if (!(xslDirFile.canRead() && xslDirFile.isDirectory())) {
            VersionInfo versionInfo = new VersionInfo();
            xslDir = versionInfo.locateHome() + File.separator + xslDir;
        }
        return xslDir;
    }

    /**
     * Refresh Standings.
     * 
     * Regenerate standings and display new HTML on pane.
     * 
     */
    public void refreshStandings() {

        if (styleSheetFileName == null) {
            showMessage("Internal error - Style Sheet not defined in StandingsHTMLPane");
            log.log(Log.WARNING, "Style sheet not defined", new Exception("Programmer error - did not define stylesheet filename."));
            return;
        }

        showMessage("");

        String xmlString;
        try {
            xmlString = scoringAlgorithm.getStandings(getContest(), new Properties(), log);
            System.out.println("debug -- " + xmlString);

            transformAndDisplay(xmlString, styleSheetFileName);
            showMessage("Last update " + new Date());
        } catch (IllegalContestState e) {
            log.log(Log.WARNING, "Exception refreshing a standings display", e);
            showMessage("Unable to update, check logs");
        }
    }

    private void transformAndDisplay(String xmlString, String xsltFileName) {

        String xslDir = getStyleSheetDirectoryName();

        File inputDir = new File(xslDir);
        if (!inputDir.isDirectory()) {
            showMessage("Can not find xslt dir: " + xslDir);
            return;
        }

        try {
            XSLTransformer xslTransformer = new XSLTransformer();

            String fullPathFileName = xslDir + File.separator + xsltFileName;

            System.out.println("Creating HTML string from " + fullPathFileName);
            File xslFile = new File(fullPathFileName);
            final String htmlString = xslTransformer.transformToString(xslFile, xmlString);
            System.out.println("debug there are " + htmlString.length() + " characters ");

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {

                    // TODO display this string as interpreted HTML
                    getTextArea().setText(htmlString);
                }
            });

        } catch (Exception e) {
            log.log(Log.WARNING, "Error generating web/html display ", e);
            showMessage("Error generating web/html display, check logs");
        }
    }

    /**
     * This method initializes messagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePane() {
        if (messagePane == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new java.awt.Dimension(25, 25));
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
            messagePane.add(getRefreshButton(), java.awt.BorderLayout.EAST);
        }
        return messagePane;
    }

    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
            }
        });
    }

    /**
     * @author pc2@ecs.csus.edu
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            refreshStandings();
        }

        public void accountModified(AccountEvent event) {
            refreshStandings();
        }

    }

    /**
     * @author pc2@ecs.csus.edu
     */
    public class ProblemListenerImplementation implements IProblemListener {

        public void problemAdded(ProblemEvent event) {
            refreshStandings();
        }

        public void problemChanged(ProblemEvent event) {
            refreshStandings();
        }

        public void problemRemoved(ProblemEvent event) {
            refreshStandings();
        }

    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    public class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            // ignore
        }

        public void runChanged(RunEvent event) {
            if (event.getAction().equals(Action.CHANGED)) {
                refreshStandings();
            }
        }

        public void runRemoved(RunEvent event) {
            refreshStandings();
        }

    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    class ContestInformationListenerImplementation implements IContestInformationListener {

        public void contestInformationAdded(ContestInformationEvent event) {
            refreshStandings();
        }

        public void contestInformationChanged(ContestInformationEvent event) {
            refreshStandings();
        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            // ignored
        }

    }

    /**
     * This method initializes textArea
     * 
     * @return javax.swing.JTextPane
     */
    private JTextPane getTextArea() {
        if (textArea == null) {
            textArea = new JTextPane();
            // textArea.setContentType("text/html");
            textArea.setDoubleBuffered(true);
            textArea.setEditable(false);
        }
        return textArea;
    }

    public String getStyleSheetFileName() {
        return styleSheetFileName;
    }

    /**
     * Set the name of the Stylesheet filename.
     * 
     * @param styleSheetFileName
     */
    public void setStyleSheetFileName(String styleSheetFileName) {
        this.styleSheetFileName = styleSheetFileName;
    }

    /**
     * This method initializes refreshButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRefreshButton() {
        if (refreshButton == null) {
            refreshButton = new JButton();
            refreshButton.setText("Refresh");
            refreshButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    refreshStandings();
                }
            });
        }
        return refreshButton;
    }

    public String getStyleSheetDirectoryName() {
        if (styleSheetDirectoryName == null) {
            styleSheetDirectoryName = getDefaultSyleSheetDirectoryName();
        }
        return styleSheetDirectoryName;
    }

    public void setStyleSheetDirectoryName(String styleSheetDirectoryName) {
        this.styleSheetDirectoryName = styleSheetDirectoryName;
    }

    /**
     * This method initializes scrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getTextArea());
        }
        return scrollPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
