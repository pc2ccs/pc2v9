// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api;

import edu.csus.ecs.pc2.VersionInfo;

/**
 * Provide version info.
 * 
 * @see VersionInfo
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public interface IVersionInfo {

    String getContactEMail();

    String getSystemName();

    String getSystemVersionInfo();

    String[] getSystemVersionInfoMultiLine();

    String getOperatingSystemInformation();

    String getPC2Version();

    String getJavaVersion();

    String getVersionDate();

    String getVersionNumber();

    String getBuildNumber();

    String getSystemURL();

}
