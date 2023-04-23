// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.implementation;

import javax.json.bind.annotation.JsonbProperty;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.api.IVersionInfo;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * Implementation for IVersionInfo
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class VersionInfoImplementation implements IVersionInfo {

    //    public RunImplementation(Run inRun, IInternalContest internalContest, IInternalController controller) {

    @JsonbProperty
    private VersionInfo versionInfo = new VersionInfo();

    public VersionInfoImplementation(VersionInfo versionInfo) {
        this.versionInfo = versionInfo;
    }

    public VersionInfoImplementation() {
        // no code neeeded
    }

    @Override
    public String getContactEMail() {
        return versionInfo.getContactEMail();
    }

    @Override
    public String getSystemName() {
        return versionInfo.getSystemName();
    }

    @Override
    public String getSystemVersionInfo() {
        return versionInfo.getSystemVersionInfo();
    }

    @Override
    public String[] getSystemVersionInfoMultiLine() {
        return versionInfo.getSystemVersionInfoMultiLine();
    }

    @Override
    public String getOperatingSystemInformation() {
        return versionInfo.getOperatingSystemInformation();
    }

    @Override
    public String getPC2Version() {
        return versionInfo.getPC2Version();
    }

    @Override
    public String getJavaVersion() {
        return versionInfo.getJavaVersion();
    }

    @Override
    public String getVersionDate() {
        return versionInfo.getVersionDate();
    }

    @Override
    public String getVersionNumber() {
        return versionInfo.getVersionNumber();
    }

    @Override
    public String getBuildNumber() {
        return versionInfo.getBuildNumber();
    }

    @Override
    public String getSystemURL() {
        return versionInfo.getSystemURL();
    }
    
    public String toJSON()  {
        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for version info "+e.getMessage();
        }

    }
    
    @Override
    public String toString() {
        return toJSON();
    }

}
