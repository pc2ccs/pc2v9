package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: EditGroupFrame.java 2343 2011-09-16 16:37:02Z laned $
 * 
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/ui/EditGroupFrame.java $
// $Id: EditGroupFrame.java 2343 2011-09-16 16:37:02Z laned $

public class EditCategoryFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -6248957592340866836L;

    private IInternalContest contest;

    private IInternalController controller;

    private EditCategoryPane categoryPane = null;

    /**
     * This method initializes
     * 
     */
    public EditCategoryFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(549, 242));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getCategoryPane());
        this.setTitle("New Category");

        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        getCategoryPane().setContestAndController(contest, controller);
        getCategoryPane().setParentFrame(this);

    }
    
    public void setCategory(Category category) {
        if (category == null) {
            setTitle("Add New Category");
        } else {
            setTitle("Edit Category " +category);
        }
        getCategoryPane().setCategory(category);
    }

    public void setDeleteCheckBoxEnabled(boolean enabled) {
        getCategoryPane().setDeleteCheckBoxEnabled(enabled);
    }

    public String getPluginTitle() {
        return "Edit Category Frame";
    }

    /**
     * This method initializes categoryPane
     * 
     * @return edu.csus.ecs.pc2.ui.GroupPane
     */
    private EditCategoryPane getCategoryPane() {
        if (categoryPane == null) {
            categoryPane = new EditCategoryPane();
        }
        return categoryPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
