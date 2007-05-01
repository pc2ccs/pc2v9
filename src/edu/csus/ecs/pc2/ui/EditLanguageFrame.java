package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Language;

/**
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class EditLanguageFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -3349295529036840178L;

    private IContest model;

    private IController controller;

    private LanguagePane languagePane = null;

    /**
     * This method initializes
     * 
     */
    public EditLanguageFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(549, 278));
        this.setContentPane(getLanguagePane());
        this.setTitle("New Language");

        FrameUtilities.centerFrame(this);

    }

    public void setModelAndController(IContest inModel, IController inController) {
        this.model = inModel;
        this.controller = inController;

        getLanguagePane().setModelAndController(model, controller);
        getLanguagePane().setParentFrame(this);

    }

    public void setLanguage(Language language) {
        if (language == null) {
            setTitle("Add New Language");
        } else {
            setTitle("Edit Language " + language.getDisplayName());
        }
        getLanguagePane().setLanguage(language);
    }

    public String getPluginTitle() {
        return "Edit Language Frame";
    }

    /**
     * This method initializes languagePane
     * 
     * @return edu.csus.ecs.pc2.ui.LanguagePane
     */
    private LanguagePane getLanguagePane() {
        if (languagePane == null) {
            languagePane = new LanguagePane();
        }
        return languagePane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
