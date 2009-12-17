package edu.csus.ecs.pc2.profile;

import java.io.Serializable;

/**
 * A collection of settings to clone a Profile.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfileCloneSettings implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3414081250056286867L;

    private String name = "";

    private String title = "";
    
    private String description = "";

    private char[] contestPassword = null;

    private char[] newContestPassword = null;

    private boolean copyAccounts = false;

    private boolean copyContestSettings = false;

    private boolean copyGroups = false;

    private boolean copyJudgements = false;

    private boolean copyLanguages = false;

    private boolean copyNotifications = false;

    private boolean copyProblems = false;

    private boolean copyRuns = false;

    private boolean copyClarifications = false;

    private boolean resetContestTimes = true;

    public ProfileCloneSettings(String name, String title, char[] contestPassword) {
        super();
        this.name = name;
        this.title = title;
        this.contestPassword = contestPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public char[] getContestPassword() {
        return contestPassword;
    }

    public void setContestPassword(char[] contestPassword) {
        this.contestPassword = contestPassword;
    }

    public boolean isResetContestTimes() {
        return resetContestTimes;
    }

    public void setResetContestTimes(boolean resetContestTimes) {
        this.resetContestTimes = resetContestTimes;
    }

    public char[] getNewContestPassword() {
        return newContestPassword;
    }

    public void setNewContestPassword(char[] newContestPassword) {
        this.newContestPassword = newContestPassword;
    }

    public boolean isCopyAccounts() {
        return copyAccounts;
    }

    public void setCopyAccounts(boolean copyAccounts) {
        this.copyAccounts = copyAccounts;
    }

    public boolean isCopyContestSettings() {
        return copyContestSettings;
    }

    public void setCopyContestSettings(boolean copyContestSettings) {
        this.copyContestSettings = copyContestSettings;
    }

    public boolean isCopyGroups() {
        return copyGroups;
    }

    public void setCopyGroups(boolean copyGroups) {
        this.copyGroups = copyGroups;
    }

    public boolean isCopyJudgements() {
        return copyJudgements;
    }

    public void setCopyJudgements(boolean copyJudgements) {
        this.copyJudgements = copyJudgements;
    }

    public boolean isCopyLanguages() {
        return copyLanguages;
    }

    public void setCopyLanguages(boolean copyLanguages) {
        this.copyLanguages = copyLanguages;
    }

    public boolean isCopyNotifications() {
        return copyNotifications;
    }

    public void setCopyNotifications(boolean copyNotifications) {
        this.copyNotifications = copyNotifications;
    }

    public boolean isCopyProblems() {
        return copyProblems;
    }

    public void setCopyProblems(boolean copyProblems) {
        this.copyProblems = copyProblems;
    }

    public boolean isCopyRuns() {
        return copyRuns;
    }

    public void setCopyRuns(boolean copyRuns) {
        this.copyRuns = copyRuns;
    }

    public boolean isCopyClarifications() {
        return copyClarifications;
    }

    public void setCopyClarifications(boolean copyClarifications) {
        this.copyClarifications = copyClarifications;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
