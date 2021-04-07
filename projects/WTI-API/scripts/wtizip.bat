@echo off
REM Copyright (C) 1989-2020 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.

rem Purpose: start the ZipWTI to archive WTI files
rem Author : Douglas A. Lane <pc2@ecs.csus.edu>

java -Djdk.crypto.KeyAgreement.legacyKDF=true -Xms64M -Xmx2048M -cp WebTeamInterface-1.1.jar edu.csus.ecs.pc2.wti.core.archive.ZipWTI %1 %2 %3 %4 %5 %6 %7 %8 %9 

rem eof pc2wti.bat 
