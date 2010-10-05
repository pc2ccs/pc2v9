package edu.csus.ecs.pc2.core.archive;

import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.swing.tree.DefaultMutableTreeNode;

import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;

/**
 * Methods to format packet contents.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class PacketFormatter {

    private PacketFormatter() {
        //
    }

    public static String summaryFormat(Packet packet) {
        StringBuffer buffer = new StringBuffer();

        Object obj = packet.getContent();
        if (obj instanceof Properties) {
            Properties prop = (Properties) obj;
            Enumeration<?> enumeration = prop.keys();

            while (enumeration.hasMoreElements()) {
                String element = (String) enumeration.nextElement();
                String className = prop.get(element).getClass().getName();
                className = className.replaceFirst("edu.csus.ecs.pc2.core.model.", "");
                className = className.replaceFirst("edu.csus.ecs.pc2.core.", "");
                buffer.append(className);
                buffer.append(' ');
                if (prop.size() == 1) {
                    buffer.append(prop.get(element));
                }
            }
        } else {

            buffer.append("  Contains: ");
            buffer.append(obj.toString());
            buffer.append(' ');
            buffer.append(obj);
        }

        return buffer.toString();
    }
    
//    private static String getClassName (Class inClass){
//        StringBuffer buffer = new StringBuffer();
//
//        String className = inClass.getClass().getName();
//        className = className.replaceFirst("edu.csus.ecs.pc2.core.model.", "");
//        className = className.replaceFirst("edu.csus.ecs.pc2.core.", "");
//        buffer.append(className);
//        
//        return buffer.toString();
//    }
    
    /**
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    protected static class ObjectWrapper {

        private String string;

        private String key;

        protected ObjectWrapper(String key) {
            this.key = key;
        }

        public ObjectWrapper(String key, Object object) {
            this(key);
            if (object instanceof String) {
                string = "(String) " + (String) object;
            } else if (object instanceof ClientId) {
                string = "(ClientId) " + (ClientId) object;
            } else if (object instanceof Integer) {
                string = "(Integer) " + (Integer) object;
            } else if (object instanceof Run) {
                string = "(Run) " + (Run) object;
            } else if (object instanceof Clarification) {
                string = "(Clarification) " + (Clarification) object;
            } else {
                string = "Object:" + object.toString();
            }
        }

        @Override
        public String toString() {
            return key + "=" + string;
        }
    }
    
    public static DefaultMutableTreeNode buildContentTree(DefaultMutableTreeNode node, Packet packet){
        
        DefaultMutableTreeNode subNode = null;
        
        Object obj = packet.getContent();
        if (obj instanceof Properties) {
            Properties prop = (Properties) obj;
            Enumeration<?> enumeration = prop.keys();

            while (enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();
//                String className = getClassName(prop.get(element).getClass());
                subNode = createTree (prop.get(name));
                if (subNode == null){
                    subNode = new DefaultMutableTreeNode(new ObjectWrapper(name, prop.get(name)));
                }
                node.add(subNode);
            }
        } else {
            subNode = new DefaultMutableTreeNode(new ObjectWrapper(obj.getClass().getName(),obj));
            node.add(subNode);
        }
        
        return node;
        
    }

    private static DefaultMutableTreeNode createTree(Object object) {
        
        if (object instanceof Profile){

            Profile profile = (Profile) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("Profile: "+profile.getName());
            DefaultMutableTreeNode child;
            
            child = new DefaultMutableTreeNode("description = "+profile.getDescription());
            node.add(child);
       
            child = new DefaultMutableTreeNode("path = "+profile.getProfilePath());
            node.add(child);
            
            child = new DefaultMutableTreeNode("contest Id = "+profile.getContestId());
            node.add(child);
            
            child = new DefaultMutableTreeNode("ElementId: "+profile.getElementId().toString());
            node.add(child);
            return node;
        }
        
        if (object instanceof Site){

            Site site = (Site) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("Site: " + site.getSiteNumber() + " title: " + site.getDisplayName());
            DefaultMutableTreeNode child;

            Properties connectionInfo = site.getConnectionInfo();
            Enumeration<?> enumeration = connectionInfo.keys();

            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                String value = (String) connectionInfo.get(key);
                child = new DefaultMutableTreeNode(key + "=" + value);
                node.add(child);
            }

            child = new DefaultMutableTreeNode("ElementId: "+site.getElementId().toString());
            node.add(child);

            return node;
        }
        
        if (object instanceof Profile []){

            Profile [] profiles = (Profile []) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("Profile list: " + profiles.length+" profiles");
            DefaultMutableTreeNode child;
            
            for (Profile profile : profiles){
                
                child = createTree(profile);
                node.add(child);
            }

            return node;
        }

        if (object instanceof GregorianCalendar) {

            GregorianCalendar calendar = (GregorianCalendar) object;
            long secondsOffset = calendar.getTime().getTime() / 1000;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("Date offset: " + secondsOffset);
            return node;

        }
        if (object instanceof Language) {

            DefaultMutableTreeNode child;

            Language language = (Language) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("Language: " + language.getDisplayName());

            child = new DefaultMutableTreeNode("ElementId: " + language.getElementId().toString());
            node.add(child);

            return node;
        }

        if (object instanceof Problem) {

            DefaultMutableTreeNode child;

            Problem problem = (Problem) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("Problem: " + problem.getDisplayName());

            child = new DefaultMutableTreeNode("ElementId: " + problem.getElementId().toString());
            node.add(child);

            return node;
        }

        if (object instanceof Language[]) {

            Language[] languages = (Language[]) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("Language list: " + languages.length + " languages");
            DefaultMutableTreeNode child;

            for (Language language : languages) {

                child = createTree(language);
                node.add(child);
            }

            return node;
        }

        if (object instanceof Problem[]) {

            Problem[] problems = (Problem[]) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("Problem list: " + problems.length + " problems");
            DefaultMutableTreeNode child;

            for (Problem problem : problems) {

                child = createTree(problem);
                node.add(child);
            }

            return node;
        }
        
        if (object instanceof ProfileCloneSettings) {

            DefaultMutableTreeNode child;

            ProfileCloneSettings settings = (ProfileCloneSettings) object;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("Clone Settings: " + settings.getName());

            // public char[] getContestPassword());
            // public char[] getNewContestPassword());

            child = new DefaultMutableTreeNode("Name: " + settings.getName());
            node.add(child);
            child = new DefaultMutableTreeNode("Title: " + settings.getTitle());
            node.add(child);

            child = new DefaultMutableTreeNode("CopyRuns = =" + settings.isCopyRuns());
            node.add(child);
            child = new DefaultMutableTreeNode("CopyClarifications = =" + settings.isCopyClarifications());
            node.add(child);
            child = new DefaultMutableTreeNode("ResetContestTimes = " + settings.isResetContestTimes());
            node.add(child);
            child = new DefaultMutableTreeNode("CopyAccounts = " + settings.isCopyAccounts());
            node.add(child);
            child = new DefaultMutableTreeNode("CopyContestSettings = =" + settings.isCopyContestSettings());
            node.add(child);
            child = new DefaultMutableTreeNode("CopyGroups = =" + settings.isCopyGroups());
            node.add(child);
            child = new DefaultMutableTreeNode("CopyJudgements = =" + settings.isCopyJudgements());
            node.add(child);
            child = new DefaultMutableTreeNode("CopyLanguages = =" + settings.isCopyLanguages());
            node.add(child);
            child = new DefaultMutableTreeNode("CopyNotifications = =" + settings.isCopyNotifications());
            node.add(child);
            child = new DefaultMutableTreeNode("CopyProblems = =" + settings.isCopyProblems());
            node.add(child);

            return node;
        }
        
        return null;
    }
}
