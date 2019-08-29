@echo off
REM Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.

rem Purpose: Start the ew Team server/java bridge 
rem Author : pc2@ecs.csus.edu

rem Windows 2000 and beyond syntax
set EWBIN=%~dp0
if exist %EWBIN%\ewenv.bat goto :continue

rem fallback to path (or current directory)
set EWBIN=%0\..
if exist %EWBIN%\ewenv.bat goto :continue

rem else rely on EWINSTALL variable
set EWBIN=%EWINSTALL%\bin
if exist %EWBIN%\ewenv.bat goto :continue

echo.
echo ERROR: Could not locate scripts.
echo.
echo Please set the variable EWINSTALL to the location of
echo   the VERSION file (ex: c:\eauteam)
echo.
pause
goto end

:continue

call %EWBIN%\ewenv.bat

java -Djdk.crypto.KeyAgreement.legacyKDF=true -Xms64M -Xmx768M -cp "%pc2_classpath%" -jar "%libdir%/PC2JavaServer.jar" %1 %2 %3 %4 %5 %6 %7 %8 %9

:end

rem eof ewrun.bat 
