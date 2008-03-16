package edu.csus.ecs.pc2.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * Contains methods to find and instantiate UI classes.
 * 
 * @see #loadUIClass(String)
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class LoadUIClass {
   
    /**
     * The base project path for all classes.
     */
    private static final String PROJECT_PACKAGE_PATH = "edu.csus.ecs.pc2";
    
    /**
     * Filename for default ui properties filename. 
     */
    public static final String UI_PROPERTIES_FILENAME = "uiname.properties";
    
    /**
     * Name of property which can contain a file name or path for uiname.
     * 
     * @see #loadUIClass(String)
     */
    public static final String UI_PROPERTY_NAME = "uiname";
    
    private LoadUIClass (){
        // present per CodeStyle
    }

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
        
        System.err.println("loadUIClass loading "+className);

        Class newClass = Class.forName(className);
        Object object = newClass.newInstance();
        if (object instanceof UIPlugin) {
            return (UIPlugin) object;
        }
        object = null;
        throw new SecurityException(className + " loaded, but not an instanceof UIPlugin");
    }


    /**
     * A Properties, key is client type, value is default UI class name.
     * @return default class names for each client type.
     */
    public static Properties getDefaultUIProperties() {

        Properties properties = new Properties();
        properties.put(Type.ADMINISTRATOR.toString().toLowerCase(), PROJECT_PACKAGE_PATH + ".ui.admin.AdministratorView");
        properties.put(Type.SCOREBOARD.toString().toLowerCase(), PROJECT_PACKAGE_PATH + ".ui.board.ScoreboardView");
        properties.put(Type.JUDGE.toString().toLowerCase(), PROJECT_PACKAGE_PATH + ".ui.judge.JudgeView");
        properties.put(Type.SPECTATOR.toString().toLowerCase(), PROJECT_PACKAGE_PATH + ".ui.judge.JudgeView");
        properties.put(Type.EXECUTOR.toString().toLowerCase(), PROJECT_PACKAGE_PATH + ".ui.judge.JudgeView");
        properties.put(Type.SERVER.toString().toLowerCase(), PROJECT_PACKAGE_PATH + ".ui.server.ServerView");
        properties.put(Type.TEAM.toString().toLowerCase(), PROJECT_PACKAGE_PATH + ".ui.team.TeamView");
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
     * Search for uiname.properties file, using uiname property.
     * <P>
     * If uiname property value is an existing directory will return directory + file.seperator + uiname.properties. <br>
     * Else If uiname property value is an existing file will return the name of that file. <br>
     * Else returns the default filename {@link #UI_PROPERTIES_FILENAME}
     * <P>
     * 
     * Here is an example of the command lines that can be used to set the uiname property.
     * 
     * <pre>
     *  java -Duiname=dirname ...
     *  java -Duiname=filename ...
     * </pre>
     * 
     * @return filename for UI properties.
     */
    protected static String getUiFileName() {

        String pathName = System.getProperty("uiname");

        if (pathName != null) {
            File file = new File(pathName);
            if (file.isDirectory()) {
                return pathName + File.separator + UI_PROPERTIES_FILENAME;
            } else if (file.isFile()) {
                return pathName;
            }
        }

        return UI_PROPERTIES_FILENAME;
    }
    
    
    /**
     * Return the UI class name for the input client.
     * 
     * Will read the properties file for loading custom UIs. If not found in properties file, will return a default UI name using {@link #defaultUIClassName(ClientId)}.
     * 
     * Code snippet.
     * 
     * <pre>
     * String uiClassName = getUIClassName(clientId);
     * UIPlugin plugin = loadUIClass(uiClassName);
     * plugin.setModelAndController(model, controller);
     * </pre>
     * 
     * @param clientId
     * @return name for the UI Class.
     * @throws IOException
     */
    public static String getUIClassName(ClientId clientId) throws IOException {

        String className = null;
        
        Properties properties = new Properties();
        
        String uiPropertiesFilename = getUiFileName();
        
        if (new File(uiPropertiesFilename).exists()){
            FileInputStream fileInputStream = new FileInputStream (uiPropertiesFilename);
            properties.load(fileInputStream);
            fileInputStream.close();
            fileInputStream = null;
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
