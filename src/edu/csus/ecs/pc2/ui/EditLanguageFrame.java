package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;

/**
 * Edit Language Frame.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$

public class EditLanguageFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -6248957592340866836L;

    private IInternalContest contest;

    private IInternalController controller;

    private EditLanguagePane languagePane = null;

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
        this.setSize(new Dimension(549, 445));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getLanguagePane());
        this.setTitle("New Language");

        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        getLanguagePane().setContestAndController(contest, controller);
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
    private EditLanguagePane getLanguagePane() {
        if (languagePane == null) {
            languagePane = new EditLanguagePane();
        }
        return languagePane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
