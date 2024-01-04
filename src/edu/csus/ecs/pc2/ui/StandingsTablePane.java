// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.BalloonSettingsEvent;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IBalloonSettingsListener;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunEvent.Action;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;

/**
 * Standings Table Pane.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class StandingsTablePane extends JPanePlugin {

    /**
     *
     */
    private static final long serialVersionUID = -7721246142538681421L;

    private static final int VERT_PAD = 2;
    private static final int HORZ_PAD = 20;
    private static final int HORZ_INSET = 5;

    private JTableCustomized standingsTable = null;
    private DefaultTableModel standingsTableModel = null;

    private JPanel messagePane = null;

    private JLabel messageLabel = null;

    private JScrollPane scrollPane = null;

    private Log log;

    private String currentXMLString = "";

    private JScrollPane groupPane = null;

    private JCheckBoxJList groupsJList = null;

    private ListModel<Object> groupsListModel = new DefaultListModel<Object>();

    /**
     * This method initializes
     *
     */
    public StandingsTablePane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(470, 543));
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getStandingsPane(), java.awt.BorderLayout.CENTER);
        this.add(getGroupsPane(), java.awt.BorderLayout.EAST);

    }

    @Override
    public String getPluginTitle() {
        return "Standings Table Plugin";
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

        populateGroupsList();
        refreshStandings();
    }


    /**
     * Fetch string from nodes.
     *
     * @param node
     * @return
     */
    private Object [] fetchStanding(Node node) {

//        Object[] cols = { "Rank", "Name", "Solved", "Points" };

        Object[] outArray = new Object[4];

        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node standingNode = attributes.item(i);
            String value = standingNode.getNodeValue();
            String name = standingNode.getNodeName();
            if (name.equals("rank")) {
                outArray[0] = value;
            } else if (name.equals("teamName")) {
                outArray[1] = value;
            } else if (name.equals("solved")) {
                outArray[2] = value;
            } else if (name.equals("points")) {
                outArray[3] = value;
            }
        }

        return outArray;
    }

    /**
     * This method initializes scrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getStandingsPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane(getStandingsTable());
        }
        return scrollPane;
    }

    /**
     * Parse output of ScoringAlgorithm and display.
     *
     */
    protected void parseAndDisplay () {

        // Return value ignored; this just makes sure the table/table model are set up
        JTableCustomized atDummy = getStandingsTable();
        standingsTableModel.setRowCount(0);

        Document document = null;
        String xmlString = null;

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
        try {
            DefaultScoringAlgorithm defaultScoringAlgorithm = new DefaultScoringAlgorithm();
            Properties properties = getScoringProperties();

            xmlString = defaultScoringAlgorithm.getStandings(getContest(), null, null, garray, properties, getController().getLog());
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            log.log(Log.WARNING, "Trouble creating or parsing SA XML ", e);
            showMessage("Problem updating scoreboard (parse error), check log");

            return;  // ----------------------------- RETURN -------------------------------

        }



        try {
            // skip past nodes to find teamStanding node
            NodeList list = document.getDocumentElement().getChildNodes();

            for(int i=0; i<list.getLength(); i++) {
                Node node = list.item(i);
                String name = node.getNodeName();
                if (name.equals("teamStanding")){
                    try {
                        Object [] standingsRow = fetchStanding (node);
                        updateStandingsRow(standingsRow);
                    } catch (Exception e) {
                        log.log(Log.WARNING, "Exception while adding row ", e);
                    }
                }
            }
            resizeColumnWidth(standingsTable);
            showMessage("Last update "+new Date());
            firePropertyChange("standings", currentXMLString, xmlString);
            currentXMLString = xmlString;
        } catch (Exception e) {
            log.log(Log.WARNING, "Trouble parsing XML ", e);
            showMessage("Problem updating scoreboard, check log");
        }


    }

    protected void refreshStandings() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                parseAndDisplay();
            }
        });
    }

    /**
     * Add or update a site row
     *
     * @param site
     */
    private void updateStandingsRow(Object [] values) {
        standingsTableModel.addRow(values);
    }


    /**
     * This method initializes standingsListbox
     *
     * @return JTableCustomized
     */
    private JTableCustomized getStandingsTable() {
        Object[] cols = { "Rank", "Name", "Solved", "Points" };
        if (standingsTable == null) {
            standingsTableModel = new DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
            standingsTable = new JTableCustomized(standingsTableModel);
            standingsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            /*
             * Column headers are aligned differently
             */
            int[] headerAlignment = new int[] { JLabel.LEFT, JLabel.LEFT, JLabel.RIGHT, JLabel.RIGHT };

            TableColumnModel tcm = standingsTable.getTableHeader().getColumnModel();
            /*
             * It's a lot of work to re-align and provide an inset for column headers.
             */
            for(int i = 0; i < headerAlignment.length; i++) {
                HeaderRenderer hdrRenderer = new HeaderRenderer(standingsTable, headerAlignment[i]);
                tcm.getColumn(i).setHeaderRenderer(hdrRenderer);

            }

            Dimension cellDim = standingsTable.getIntercellSpacing();
            cellDim.width += HORZ_INSET;
            standingsTable.setIntercellSpacing(cellDim);

            standingsTable.setRowHeight(standingsTable.getRowHeight() + VERT_PAD);

            /*
             * Columns for solved and points, right justified
             */
            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

            standingsTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
            standingsTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

            resizeColumnWidth(standingsTable);
        }
        return standingsTable;
    }

    private void resizeColumnWidth(JTableCustomized table) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TableColumnAdjuster tca = new TableColumnAdjuster(table, HORZ_PAD);
                tca.adjustColumns();
            }
        });
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
//            messagePane.add(getGroupsPane(), java.awt.BorderLayout.SOUTH);
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
     *
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
     * Problem Listener for Standings Pane.
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
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
     *
     */
    public class RunListenerImplementation implements IRunListener{

        @Override
        public void runAdded(RunEvent event) {
            // TODO Auto-generated method stub
            // ignore
        }

        @Override
        public void refreshRuns(RunEvent event) {
            refreshStandings();
        }

        @Override
        public void runChanged(RunEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction().equals(Action.CHANGED)){
                refreshStandings();
            }
        }

        @Override
        public void runRemoved(RunEvent event) {
            // TODO Auto-generated method stub
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
     * Contest Information Listener for StandingsTablePane.
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
            // TODO Auto-generated method stub

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

    protected Properties getScoringProperties() {

        Properties properties = getContest().getContestInformation().getScoringProperties();

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

    /**
     * This class provides a custom renderer for column headers on the standings table
     *
     * The idea is to modify the component for the column header (JLabel) by changing its
     * alignment as desired (horzAlignment).  In addition, we inset the headers a little bit
     * so they're not right up against the edge of the JLabel.  We do this by creating a compound
     * Border the consists of the original border (whatever that may be), and adding an EmptyBorder
     * with the desired insets.  Seems like there SHOULD be an easier way.
     */
    private static class HeaderRenderer implements TableCellRenderer {
        DefaultTableCellRenderer renderer;
        int horAlignment;

        public HeaderRenderer(JTable table, int horizontalAlignment) {
          horAlignment = horizontalAlignment;
          renderer = (DefaultTableCellRenderer)table.getTableHeader()
              .getDefaultRenderer();
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int col) {
          Component c = renderer.getTableCellRendererComponent(table, value,
            isSelected, hasFocus, row, col);
          JLabel label = (JLabel)c;
          label.setHorizontalAlignment(horAlignment);
          Border b = label.getBorder();
          if(b.getClass() != CompoundBorder.class) {
              Border margin = new EmptyBorder(0, HORZ_INSET, 0, HORZ_INSET);
              label.setBorder(new CompoundBorder(b, margin));;
          }
          return label;
        }
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
            addGroupCheckBox(group);
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

} // @jve:decl-index=0:visual-constraint="10,10"
