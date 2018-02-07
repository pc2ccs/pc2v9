@echo off

rem Purpose: start pc2 command line submitter/lister
rem Author : pc2@ecs.csus.edu
rem $HeadURL$

set params=

rem Windows 2000 and beyond syntax
set PC2BIN=%~dp0
if exist %PC2BIN%\pc2env.bat goto :loop

rem fallback to path (or current directory)
set PC2BIN=%0\..
if exist %PC2BIN%\pc2env.bat goto :loop

rem else rely on PC2INSTALL variable
set PC2BIN=%PC2INSTALL%\bin
if exist %PC2BIN%\pc2env.bat goto :loop

echo.
echo ERROR: Could not locate scripts.
echo.
echo Please set the variable PC2INSTALL to the location of
echo   the VERSION file (ex: c:\pc2-9.0.0)
echo.
pause
goto :end

:loop
if %1. == . goto :continue
set params=%params% %1
shift
goto :loop

:continue
call %PC2BIN%\pc2env.bat

set CLASSNAME=edu.csus.ecs.pc2.ui.team.Submitter
java -Djdk.crypto.KeyAgreement.legacyKDF=true -Xms64M -Xmx768M -cp %pc2_classpath% %CLASSNAME% %params%
rem without the exit /b the errorlevel does not get returned properly
exit /b %errorlevel%

:end
rem eof pc2submit.bat $Id$
