package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Properties Edit Frame (Window).
 * 
 * The method {@link #setProperties(Properties, IPropertyUpdater)} must
 * be used to initialize both the Properties and the Property Updater call back.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PropertiesEditFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -8615136784638991125L;

    private JPanel mainPanel = null;

    private PropertiesEditPane propertiesEditPane = null;

    /**
     * This method initializes
     * 
     */
    public PropertiesEditFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(248, 296));
        this.setContentPane(getMainPanel());
        this.setTitle("Edit Properties");

        FrameUtilities.centerFrame(this);
    }

    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(getPropertiesEditPane(), BorderLayout.CENTER);
        }
        return mainPanel;
    }

    /**
     * This method initializes propertiesEditPane
     * 
     * @return edu.csus.ecs.pc2.ui.PropertiesEditPane
     */
    private PropertiesEditPane getPropertiesEditPane() {
        if (propertiesEditPane == null) {
            propertiesEditPane = new PropertiesEditPane();
        }
        return propertiesEditPane;
    }

    /**
     * Set properties and when properties updated call back.
     * @param properties
     * @param propertyUpdater invoked when properties have been updated
     */
    public void setProperties(Properties properties, IPropertyUpdater propertyUpdater) {
        getPropertiesEditPane().setParentFrame(this);
        getPropertiesEditPane().setPropertyUpdater(propertyUpdater);
        getPropertiesEditPane().setProperties(properties);
    }

} // @jve:decl-index=0:visual-constraint="75,10"
