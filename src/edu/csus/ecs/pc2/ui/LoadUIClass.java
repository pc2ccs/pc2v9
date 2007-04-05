package edu.csus.ecs.pc2.ui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Contains methods to find/instantiate UI classes.
 * 
 * @see #loadUIClass(String)
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class LoadUIClass {
    
    /**
     * name of the UI load properties class.
     */
    private static final String uiPropertiesFilename = "uiname.properties";
    
    public static final String SVN_ID = "$Id$";

    /**
     * Find and create an instance of UIPlugin from className.
     * <P>
     * Code snippet.
     * <pre>
     * String uiClassName = "edu.csus.ecs.pc2.core.ui.editor.RunEditor";
     * UIPlugin plugin = loadUIClass(uiClassName);
     * plugin.setModelAndController(model, controller);
     * </pre>
     * 
     * @param className
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */

    public static UIPlugin loadUIClass(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        Class newClass = Class.forName(className);
        Object object = newClass.newInstance();
        if (object instanceof UIPlugin) {
            return (UIPlugin) object;
        }
        object = null;
        throw new SecurityException(className + " loaded, but not an instanceof UIPlugin");
    }


    /**
     * return default classnames per client type.
     */
    public static Properties getDefaultUIProperties() {

        Properties properties = new Properties();
        properties.put("admin", "edu.csus.ecs.pc2.ui.admin.AdminstratorView");
        properties.put("board", "edu.csus.ecs.pc2.ui.board.ScoreboardView");
        properties.put("judge", "edu.csus.ecs.pc2.ui.judge.JudgeView");
        properties.put("server", "edu.csus.ecs.pc2.ui.server.ServerView");
        properties.put("team", "edu.csus.ecs.pc2.ui.team.TeamView");
        return properties;

    }

    /**
     * Return default UI class name for input clientId.
     * 
     * @param clientId
     */
    private static String defaultUIClassName(ClientId clientId) {
        String clientName = clientId.getClientType().toString().toLowerCase();
        return getDefaultUIProperties().getProperty(clientName);
    }
    
    /**
     * Return the UI class name for the input client.
     * 
     * Will read the properties file for loading custom UIs.
     * If not found in properties file, will return a default
     * UI name using {@link #defaultUIClassName(ClientId)}.
     * 
     * Code snippet.
     * <pre>
     * String uiClassName = getUIClassName(clientId);
     * UIPlugin plugin = loadUIClass(uiClassName);
     * plugin.setModelAndController(model, controller);
     * </pre>
     * @param clientId
     * @return name for the UI Class.
     */
    public static String getUIClassName(ClientId clientId) {

        String className = null;
        
        Properties properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream (uiPropertiesFilename);
            properties.load(fileInputStream);
            fileInputStream.close();
            fileInputStream = null;
        } catch (Exception e) {
            // TODO: handle exception
            System.err.println("Note: unable to read UI properties from "+uiPropertiesFilename);
        }
        
        className = properties.getProperty(clientId.getName());  // lookup by short/login name

        if (className == null){
            return defaultUIClassName(clientId);
        } else {
            return className;
        }
    }
    
    /**
     * Write sample properties file using default values.
     * @param filename
     */
    public static void writeSample (String filename){
        try {
          Properties properties = getDefaultUIProperties();
          FileOutputStream fileOutputStream = new FileOutputStream(filename,false);
          properties.store(fileOutputStream, "PC^2 GUI Plugins ");
          fileOutputStream.close();
          fileOutputStream = null;
      } catch (Exception e) {
          e.printStackTrace(System.err);
      }
    }
}
