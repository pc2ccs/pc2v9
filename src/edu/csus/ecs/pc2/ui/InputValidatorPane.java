package edu.csus.ecs.pc2.ui;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;

/**
 * This class defines a plugin pane (a JPanel) containing components for 
 * (1) defining an Input Validator program name and Invocation Command, and
 * (2) Viewing the results of an input validator execution.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class InputValidatorPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;

    private DefineInputValidatorPane defineInputValidatorPane;

    private InputValidationResultPane inputValidationResultPane;

    private Component verticalStrut_1;
    private Component verticalStrut_3;
    private Component verticalStrut_4;

    private JPanePlugin parentPane;

    public InputValidatorPane() {
        
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(getVerticalStrut_4());

        this.add(getDefineInputValidatorPanel());
        this.add(getVerticalStrut_1());
        this.add(getInputValidationResultPanel());
        this.add(getVerticalStrut_3());
    }
    
    /**
     * Returns the Input Validator Program Name currently entered in this InputValidatorPane.
     * 
     * @return a String containing the Input Validator Program Name
     */
    public String getInputValidatorProgramName() {
        String progName = getDefineInputValidatorPanel().getInputValidatorProgramName();
        return progName;
    }
    
    /**
     * Sets the Input Validator Program name displayed in this InputValidatorPane to the specified text.
     * 
     * @param inputValidatorProg a String containing the Input Validator Program name
     */
    public void setInputValidatorProgramName(String progName) {
       getDefineInputValidatorPanel().setInputValidatorProgramName(progName);

    }
    
    /**
     * Returns the Input Validator Command currently entered into this InputValidatorPane.
     * 
     * @return a String containing the command to be used to invoke the Input Validator
     */
    public String getInputValidatorCommand() {
        String command = getDefineInputValidatorPanel().getInputValidatorCommand();
        return command;
    }

    private DefineInputValidatorPane getDefineInputValidatorPanel() {
        if (defineInputValidatorPane == null) {
            defineInputValidatorPane = new DefineInputValidatorPane();
            defineInputValidatorPane.setContestAndController(this.getContest(), this.getController());
            defineInputValidatorPane.setParentPane(this);
        }
        return defineInputValidatorPane;
    }
    

    private InputValidationResultPane getInputValidationResultPanel() {
        if (inputValidationResultPane == null) {
            inputValidationResultPane = new InputValidationResultPane();
            inputValidationResultPane.setContestAndController(this.getContest(), this.getController());
            inputValidationResultPane.setParentPane(this);
        }
        return inputValidationResultPane;
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

    public void setParentPane(JPanePlugin parentPane) {
        this.parentPane = parentPane; 
    }
    
    public JPanePlugin getParentPane() {
        return this.parentPane;
    }

    /**
     * Sets the ToolTip text for the InputValidatorProgramName displayed in this InputValidatorPane.
     * 
     * @param text the ToolTip text to set
     */
    public void setInputValidatorProgramNameToolTipText(String text) {
        getDefineInputValidatorPanel().setInputValidatorProgramNameToolTipText(text);
        
    }

    /**
     * Sets the Input Validator Command displayed in this InputValidatorPane to the specified value.
     * 
     * @param command a String containing the command used to invoke the Input Validator
     */
    public void setInputValidatorCommand(String command) {
        getDefineInputValidatorPanel().setInputValidatorCommand(command);
        
    }

    /**
     * Sets the ToolTip text for the Input Validator Command displayed in this InputValidatorPane.
     * 
     * @param text the ToolTip text to set
     */
    public void setInputValidatorCommandToolTipText(String text) {
        getDefineInputValidatorPanel().setInputValidatorCommandToolTipText(text);
        
    }


}
