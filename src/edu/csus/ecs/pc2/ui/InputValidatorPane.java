package edu.csus.ecs.pc2.ui;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;

/**
 * This class defines a plugin pane (a JPanel) containing components for 
 * (1) defining an Input Validator program name and Invocation Command,
 * (2) Executing an input validator, and
 * (3) Viewing the results of an input validator execution.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class InputValidatorPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;

    private Component defineInputValidatorPane;

    private Component inputValidationResultPanel;

    private Component verticalStrut_1;
    private Component verticalStrut_3;
    private Component verticalStrut_4;

    public InputValidatorPane() {
        
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(getVerticalStrut_4());

        this.add(getDefineInputValidatorPanel());
        this.add(getVerticalStrut_1());
        this.add(getInputValidationResultPanel());
        this.add(getVerticalStrut_3());
    }

    private Component getDefineInputValidatorPanel() {
        if (defineInputValidatorPane == null) {
            defineInputValidatorPane = new DefineInputValidatorPane();
            ((JPanePlugin) defineInputValidatorPane).setContestAndController(this.getContest(), this.getController());
        }
        return defineInputValidatorPane;
    }
    

    private Component getInputValidationResultPanel() {
        if (inputValidationResultPanel == null) {
            inputValidationResultPanel = new InputValidationResultPane();
            ((JPanePlugin) inputValidationResultPanel).setContestAndController(this.getContest(), this.getController());
        }
        return inputValidationResultPanel;
    }

    private Component getVerticalStrut_1() {
        if (verticalStrut_1 == null) {
            verticalStrut_1 = Box.createVerticalStrut(20);
        }
        return verticalStrut_1;
    }


    private Component getVerticalStrut_3() {
        if (verticalStrut_3 == null) {
            verticalStrut_3 = Box.createVerticalStrut(20);
        }
        return verticalStrut_3;
    }

    private Component getVerticalStrut_4() {
        if (verticalStrut_4 == null) {
        	verticalStrut_4 = Box.createVerticalStrut(20);
        }
        return verticalStrut_4;
    }
    
    @Override
    public String getPluginTitle() {

        return "Input Validator Pane";
    }
}
