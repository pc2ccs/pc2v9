@echo off
REM Copyright (C) 1989-2020 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.

rem Purpose: start the WebTeamInterface (WTI) project team webserver module
rem Author : pc2@ecs.csus.edu

java -Djdk.crypto.KeyAgreement.legacyKDF=true -Xms64M -Xmx2048M -jar WebTeamInterface-1.1.jar

rem eof pc2wti.bat 
