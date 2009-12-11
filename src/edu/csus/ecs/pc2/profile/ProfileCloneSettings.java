package edu.csus.ecs.pc2.profile;

/**
 * A collection of settings to clone a Profile.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfileCloneSettings {

    private String name = "";

    private String title = "";

    private char[] contestPassword = null;

    private boolean removeAllLanguages = false;

    private boolean removeAllProblms = true;

    private boolean removeAllsubmissions = true;

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

    public boolean isRemoveAllLanguages() {
        return removeAllLanguages;
    }

    public void setRemoveAllLanguages(boolean removeAllLanguages) {
        this.removeAllLanguages = removeAllLanguages;
    }

    public boolean isRemoveAllProblms() {
        return removeAllProblms;
    }

    public void setRemoveAllProblms(boolean removeAllProblms) {
        this.removeAllProblms = removeAllProblms;
    }

    public boolean isRemoveAllsubmissions() {
        return removeAllsubmissions;
    }

    public void setRemoveAllsubmissions(boolean removeAllsubmissions) {
        this.removeAllsubmissions = removeAllsubmissions;
    }

    public boolean isResetContestTimes() {
        return resetContestTimes;
    }

    public void setResetContestTimes(boolean resetContestTimes) {
        this.resetContestTimes = resetContestTimes;
    }
}
