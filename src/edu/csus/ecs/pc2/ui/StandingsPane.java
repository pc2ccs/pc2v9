package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.io.StringReader;
import java.util.Date;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ibm.webrunner.j2mclb.MultiColumnListbox;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.BalloonSettingsEvent;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IBalloonSettingsListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunEvent.Action;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;

/**
 * Standings Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class StandingsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 5947564887534500596L;

    private MCLB standingsListbox = null;

    private JPanel messagePane = null;

    private JLabel messageLabel = null;

    private Log log;

    private String currentXMLString = "";
    
    /**
     * This method initializes
     * 
     */
    public StandingsPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(470, 243));
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getStandingsListbox(), java.awt.BorderLayout.CENTER);

    }

    @Override
    public String getPluginTitle() {
        return "Standings Plugin";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        
        log = getController().getLog();
        
        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addProblemListener(new ProblemListenerImplementation());
        getContest().addRunListener(new RunListenerImplementation());
        getContest().addContestInformationListener(new ContestInformationListenerImplementation());
        getContest().addBalloonSettingsListener(new BalloonSettingsListenerImplementation());
        
        refreshStandings();
    }
    

    /**
     * Fetch string from nodes.
     * 
     * @param node
     * @return
     */
    private String[] fetchStanding(Node node) {
        
//        Object[] cols = { "Rank", "Name", "Solved", "Points" };

        String[] outArray = new String[4];

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
     * Parse output of ScoringAlgorithm and display.
     *
     */
    protected void parseAndDisplay () {

        standingsListbox.removeAllRows();
        
        Document document = null;
        String xmlString = null;

        try {
            DefaultScoringAlgorithm defaultScoringAlgorithm = new DefaultScoringAlgorithm();
            Properties properties = getScoringProperties();

            xmlString = defaultScoringAlgorithm.getStandings(getContest(), properties, getController().getLog());
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
                Node node = (Node)list.item(i);
                String name = node.getNodeName();
                if (name.equals("teamStanding")){
                    try {
                        String [] standingsRow = fetchStanding (node);
                        updateStandingsRow(standingsRow);
                    } catch (Exception e) {
                        log.log(Log.WARNING, "Exception while adding row ", e);
                    }
                }
            }
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
    private void updateStandingsRow(String [] values) {
        Object[] objects = buildSiteRow(values);
        standingsListbox.addRow(objects);
        standingsListbox.autoSizeAllColumns();
    }


    /**
     * This method initializes standingsListbox
     * 
     * @return com.ibm.webrunner.j2mclb.MultiColumnListbox
     */
    private MultiColumnListbox getStandingsListbox() {
        if (standingsListbox == null) {
            standingsListbox = new MCLB();
            Object[] cols = { "Rank", "Name", "Solved", "Points" };
            standingsListbox.addColumns(cols);
        }
        return standingsListbox;
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
    
    private Object[] buildSiteRow(String [] fields) {

//        Object[] cols = { "Rank", "Name", "Solved", "Points" };

        Object[] obj = new Object[standingsListbox.getColumnCount()];

        obj[0] = fields[0];
        obj[1] = fields[1];
        obj[2] = fields[2];
        obj[3] = fields[3];

        return obj;
    }
    
    /**
     * @author pc2@ecs.csus.edu
     *
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            refreshStandings();
        }

        public void accountModified(AccountEvent event) {
            refreshStandings();
        }

        public void accountsAdded(AccountEvent accountEvent) {
            refreshStandings();
        }

        public void accountsModified(AccountEvent accountEvent) {
            refreshStandings();
        }

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

        public void problemAdded(ProblemEvent event) {
            refreshStandings();
        }

        public void problemChanged(ProblemEvent event) {
            refreshStandings();
        }

        public void problemRemoved(ProblemEvent event) {
            refreshStandings();
        }

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

        public void runAdded(RunEvent event) {
            // TODO Auto-generated method stub
            // ignore
        }
        
        public void refreshRuns(RunEvent event) {
            refreshStandings();
        }

        public void runChanged(RunEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction().equals(Action.CHANGED)){
                refreshStandings();
            }
        }

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

        public void balloonSettingsAdded(BalloonSettingsEvent event) {
            refreshStandings();
        }

        public void balloonSettingsChanged(BalloonSettingsEvent event) {
            refreshStandings();
        }

        public void balloonSettingsRemoved(BalloonSettingsEvent event) {
            refreshStandings();
        }

        public void balloonSettingsRefreshAll(BalloonSettingsEvent balloonSettingsEvent) {
            refreshStandings();
        }
    }

    /**
     * Contest Information Listener for StandingsPane.
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    class ContestInformationListenerImplementation implements IContestInformationListener {

        public void contestInformationAdded(ContestInformationEvent event) {
            refreshStandings();

        }

        public void contestInformationChanged(ContestInformationEvent event) {
            refreshStandings();

        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            // TODO Auto-generated method stub

        }

        public void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent) {
            refreshStandings();
        }

    }

    protected Properties getScoringProperties() {

        Properties properties = getContest().getContestInformation().getScoringProperties();
        
        Properties defProperties = DefaultScoringAlgorithm.getDefaultProperties();

        /**
         * Fill in with default properties if not using them.
         */
        String [] keys = (String[]) defProperties.keySet().toArray(new String[defProperties.keySet().size()]);
        for (String key : keys) {
            if (! properties.containsKey(key)){
                properties.put(key, defProperties.get(key));
            }
        }
        
        return properties;
    }

    
} // @jve:decl-index=0:visual-constraint="10,10"
