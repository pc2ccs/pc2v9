package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.io.StringReader;
import java.util.Date;

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

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunEvent.Action;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;

/**
 * Standings Pane. 
 * @author pc2@ecs.csus.edu
 *
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

    public void setModelAndController(IModel inModel, IController inController) {
        super.setModelAndController(inModel, inController);
        
        log = getController().getLog();
        
        getModel().addRunListener(new RunListenerImplementation());

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

            xmlString = defaultScoringAlgorithm.getStandings(getModel());
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(new InputSource(new StringReader(xmlString)));
            String rootNode = document.getDocumentElement().getNodeName();
            System.out.println("debug22 Root is " + rootNode);
            
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
                System.out.println("Name: "+name);
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
     * 
     * @author pc2@ecs.csus.edu
     *
     */
    public class RunListenerImplementation implements IRunListener{

        public void runAdded(RunEvent event) {
            // TODO Auto-generated method stub
            // ignore
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
    
} // @jve:decl-index=0:visual-constraint="10,10"
