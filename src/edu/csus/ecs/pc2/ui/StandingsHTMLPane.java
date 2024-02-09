// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLEditorKit;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.BalloonSettingsEvent;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.GroupEvent;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IBalloonSettingsListener;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IGroupListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunEvent.Action;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.scoring.IScoringAlgorithm;
import edu.csus.ecs.pc2.core.util.XSLTransformer;

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

    private JScrollPane groupPane = null;

    private JCheckBoxJList groupsJList = null;

    private ListModel<Object> groupsListModel = new DefaultListModel<Object>();

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
        this.setSize(new java.awt.Dimension(470, 543));
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getScrollPane(), java.awt.BorderLayout.CENTER);
        this.add(getGroupsPane(), java.awt.BorderLayout.EAST);
    }

    @Override
    public String getPluginTitle() {
        return "Standings HTML Plugin";
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        log = getController().getLog();

        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addProblemListener(new ProblemListenerImplementation());
        getContest().addRunListener(new RunListenerImplementation());
        getContest().addContestInformationListener(new ContestInformationListenerImplementation());
        getContest().addBalloonSettingsListener(new BalloonSettingsListenerImplementation());
        getContest().addGroupListener(new GroupListenerImplementation());
        populateGroupsList();

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

        Object [] gobjs = getGroupsJList().getSelectedValues();
        ArrayList<Group> garray = null;

        if(gobjs.length > 0) {
            garray = new ArrayList<Group>();
            for(Object o : gobjs ) {
                JCheckBox groupCheck = (JCheckBox)o;
                Group group = (Group)groupCheck.getClientProperty("group");
                if(group != null) {
                    garray.add(group);
                }
            }
        }

        String xmlString;
        try {
            Properties scoringProperties = getScoringProperties();
            xmlString = scoringAlgorithm.getStandings(getContest(), null, null, garray, scoringProperties, log);
            transformAndDisplay(xmlString, styleSheetFileName);
            showMessage("Last update " + new Date());
        } catch (IllegalContestState e) {
            log.log(Log.WARNING, "Exception refreshing a standings display", e);
            showMessage("Unable to update, check logs");
        }
    }

    protected Properties getScoringProperties() {

        Properties properties = getContest().getContestInformation().getScoringProperties();
        if (properties == null){
            properties = new Properties();
        }

        Properties defProperties = DefaultScoringAlgorithm.getDefaultProperties();

        /**
         * Fill in with default properties if not using them.
         */
        String [] keys = defProperties.keySet().toArray(new String[defProperties.keySet().size()]);
        for (String key : keys) {
            if (! properties.containsKey(key)){
                properties.put(key, defProperties.get(key));
            }
        }

        return properties;
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

            File xslFile = new File(fullPathFileName);
            final String htmlString = xslTransformer.transformToString(xslFile, xmlString);
            viewHTML (htmlString);

        } catch (Exception e) {
            log.log(Log.WARNING, "Error generating web/html display ", e);
            showMessage("Error generating web/html display, check logs");
        }
    }

    private void viewHTML(String htmlString) {

        try {
            File tmpFile = File.createTempFile("__t",".htm");
            tmpFile.deleteOnExit();

            BufferedWriter out = new BufferedWriter(new FileWriter(tmpFile));
            out.write(htmlString);
            out.close();
            out = null;

            final URL tmpURL = tmpFile.toURI().toURL();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        getTextArea().setPage(tmpURL);
                    } catch (Exception e) {
                        log.log(Log.WARNING,"Could not display HTML", e);
                    }

                }
            });

        } catch (Exception e) {
            log.log(Log.WARNING, "In View HTML ", e);
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
            @Override
            public void run() {
                messageLabel.setText(string);
            }
        });
    }

    /**
     * @author pc2@ecs.csus.edu
     */
    public class AccountListenerImplementation implements IAccountListener {

        @Override
        public void accountAdded(AccountEvent accountEvent) {
            refreshStandings();
        }

        @Override
        public void accountModified(AccountEvent event) {
            refreshStandings();
        }

        @Override
        public void accountsAdded(AccountEvent accountEvent) {
            refreshStandings();
        }

        @Override
        public void accountsModified(AccountEvent accountEvent) {
            refreshStandings();
        }

        @Override
        public void accountsRefreshAll(AccountEvent accountEvent) {
            refreshStandings();
        }

    }

    /**
     * @author pc2@ecs.csus.edu
     */
    public class ProblemListenerImplementation implements IProblemListener {

        @Override
        public void problemAdded(ProblemEvent event) {
            refreshStandings();
        }

        @Override
        public void problemChanged(ProblemEvent event) {
            refreshStandings();
        }

        @Override
        public void problemRemoved(ProblemEvent event) {
            refreshStandings();
        }

        @Override
        public void problemRefreshAll(ProblemEvent event) {
            refreshStandings();
        }

    }

    /**
     *
     * @author pc2@ecs.csus.edu
     */
    public class RunListenerImplementation implements IRunListener {

        @Override
        public void runAdded(RunEvent event) {
            // ignore
        }

        @Override
        public void refreshRuns(RunEvent event) {
            refreshStandings();
        }

        @Override
        public void runChanged(RunEvent event) {
            if (event.getAction().equals(Action.CHANGED)) {
                refreshStandings();
            }
        }

        @Override
        public void runRemoved(RunEvent event) {
            refreshStandings();
        }

    }

    /**
     * Contest Information Listener for StandingsHTMLPane.
     *
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    class ContestInformationListenerImplementation implements IContestInformationListener {

        @Override
        public void contestInformationAdded(ContestInformationEvent event) {
            refreshStandings();
        }

        @Override
        public void contestInformationChanged(ContestInformationEvent event) {
            refreshStandings();
        }

        @Override
        public void contestInformationRemoved(ContestInformationEvent event) {
            // ignored
        }

        @Override
        public void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent) {
            refreshStandings();
        }

        @Override
        public void finalizeDataChanged(ContestInformationEvent contestInformationEvent) {
            refreshStandings();
        }


    }

    /**
     * @author pc2@ecs.csus.edu
     *
     */
    public class BalloonSettingsListenerImplementation implements IBalloonSettingsListener {

        @Override
        public void balloonSettingsAdded(BalloonSettingsEvent event) {
            refreshStandings();
        }

        @Override
        public void balloonSettingsChanged(BalloonSettingsEvent event) {
            refreshStandings();
        }

        @Override
        public void balloonSettingsRemoved(BalloonSettingsEvent event) {
            refreshStandings();
        }

        @Override
        public void balloonSettingsRefreshAll(BalloonSettingsEvent balloonSettingsEvent) {
            refreshStandings();
        }

    }

    /**
     * If any groups change, we need to possibly update our checkbox lists
     *
     * @author John Buck
     *
     */
    public class GroupListenerImplementation implements IGroupListener {

        @Override
        public void groupAdded(GroupEvent event) {
            commonGroupUpdate();
        }

        @Override
        public void groupChanged(GroupEvent event) {
            commonGroupUpdate();
        }

        @Override
        public void groupRemoved(GroupEvent event) {
            commonGroupUpdate();
        }

        @Override
        public void groupRefreshAll(GroupEvent groupEvent) {
            commonGroupUpdate();
        }

        @Override
        public void groupsAdded(GroupEvent event) {
            commonGroupUpdate();
        }

        @Override
        public void groupsChanged(GroupEvent event) {
            commonGroupUpdate();
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
            textArea.setEditorKit(new HTMLEditorKit());
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
            refreshButton.setToolTipText("Refresh display");
            refreshButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
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
    /**
     * This method initializes groups ScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getGroupsPane() {
        if (groupPane == null) {
            groupPane = new JScrollPane();
            groupPane.setPreferredSize(new Dimension(180, 200));
//            groupPane.setBounds(new java.awt.Rectangle(14, 14, 200, 200));
            groupPane.setViewportView(getGroupsJList());
        }
        return groupPane;
    }

    private void populateGroupsList() {
        ((DefaultListModel<Object>) groupsListModel).removeAllElements();

        Group [] allgroups = getContest().getGroups();
        Arrays.sort(allgroups, new GroupComparator());
        for(Group group : allgroups ) {
            if(group.isDisplayOnScoreboard()) {
                addGroupCheckBox(group);
            }
        }

    }

    private void addGroupCheckBox(Group group) {
        JCheckBox checkBox = new JCheckBox(group.getDisplayName());
        checkBox.putClientProperty("group", group);
        ((DefaultListModel<Object>) groupsListModel).addElement(checkBox);
    }

    /**
     * This method initializes groupsJList
     *
     * @return javax.swing.JList
     */
    private JCheckBoxJList getGroupsJList() {
        if (groupsJList == null) {
            groupsJList = new JCheckBoxJList();
            groupsJList.setModel(groupsListModel);

            // ListSelectionListeners are called before JCheckBoxes get updated
            groupsJList.addPropertyChangeListener("change", new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    refreshStandings();
                }
            });
        }
        return groupsJList;
    }

    /**
     * Called when groups change to force regeneration of group checklists and possibly
     * the standings if a removed group was checked.
     */
    private void commonGroupUpdate() {
        populateGroupsList();
        refreshStandings();
    }

} // @jve:decl-index=0:visual-constraint="10,10"
