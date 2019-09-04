// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import javax.swing.JCheckBox;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.DisplayTeamName;

/**
 * Provides a Wrapper for a check box.
 * 
 * 
 * @author pc2@ecs.csus.edu
 */
public class WrapperJCheckBox extends JCheckBox {

    /**
     * 
     */
    private static final long serialVersionUID = 991427730095971274L;

    private Object contents;

    /**
     * Create a check box with a toString() title.
     */
    public WrapperJCheckBox(Object object) {
        this(object, object.toString());
    }

    /**
     * Create a check box with the titlt text.
     * 
     * @param object
     *            any object
     * @param text
     *            the text to display
     */
    public WrapperJCheckBox(Object object, String text) {
        super();
        contents = object;
        setText(text);
    }

    public WrapperJCheckBox(ClientId clientId, DisplayTeamName displayTeamName) {
        super();
        contents = clientId;
        setText(displayTeamName.getDisplayName(clientId));
    }

    public Object getContents() {
        return contents;
    }
}
