@ECHO OFF
REM
REM
REM File: validiff.bat
REM Purpose: to show gvim diff and also execute internal PC^2 Validator
REM
REM
REM $HeadURL$
REM $Id$
REM Directions
REM Add/Edit Problem | Validator Tab 
REM Select Use External Validator
REM Validator Command Line must be: {:infile} {:outfile} {:ansfile} {:resfile} 
REM 


IF %4. == . GOTO :NO_PARAMS

IF EXIST EXITCODE.TXT GOTO :RUNTIME
GOTO :VALIDATE


:RUNTIME

ECHO ^<?xml version="1.0"?^> > %4
ECHO ^<result outcome =  "No - Run-time Error" security = "%4"^>Team execution exited non-zero, this is a possible run-time error >> %4
ECHO ^</result^> >> %4

ECHO. >> %2
ECHO PC2: Team program exit code below >> %2
TYPE EXITCODE.TXT >> %2

GOTO :END

:VALIDATE

java -cp C:\PC2\LIB\pc2.jar edu.csus.ecs.pc2.validator.Validator %1 %2 %3 %4 -pc2 1 true

IF NOT EXIST C:\WORK\COMPUTER gvim -d %2 %3

GOTO :END

:NO_PARAMS
ECHO MISSING NECESSARY PARAMTERS
GOTO :END

:END
