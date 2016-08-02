package edu.csus.ecs.pc2.core;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

/**
 * Utilities for setting and fetching data from Clipboard.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 * @author laned@ecs.csus.edu
 */
public final class ClipboardUtilities {
    
    private ClipboardUtilities() {
        super();
    }
    
    /**
     * Put text into clipboard.
     * 
     * @param string
     */
    public static void put(String string) {
        StringSelection stringSelection = new StringSelection (string);
        Clipboard clipboard = Toolkit.getDefaultToolkit ().getSystemClipboard ();
        clipboard.setContents (stringSelection, null);
    }
    
    public static void main(String[] args) {
        
        
//        String test = "ClipboardUtilities Test";
//        ClipboardUtilities.put(test);
//        System.out.println("Put '"+test+"' into clipboard");
        
        String string = ClipboardUtilities.get();
        System.out.println("Get from clipboard '"+string+"'");
    }

    /**
     * Fetch text from clipboard.
     * 
     * @return null if not text in clipboard, else the text
     */
    private static String get() {
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        try {
            String result = (String) clipboard.getData(DataFlavor.stringFlavor);
            return result;
            
        } catch (Exception e) {
            return null;
        }
    }

}
