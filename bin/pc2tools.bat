@echo off
REM Copyright (C) 1989-2020 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.

rem Purpose: start the pc2tools program
rem Author : Douglas A. Lane <pc2@ecs.csus.edu>
rem https://pc2.ecs.csus.edu/bugzilla/show_bug.cgi?id=1521

rem Tue Jan 01 06:06:51 2019 written 

rem Windows 2000 and beyond syntax
set PC2BIN=%~dp0
if exist %PC2BIN%\pc2env.bat goto :continue

rem fallback to path (or current directory)
set PC2BIN=%0\..
if exist %PC2BIN%\pc2env.bat goto :continue

rem else rely on PC2INSTALL variable
set PC2BIN=%PC2INSTALL%\bin
if exist %PC2BIN%\pc2env.bat goto :continue

echo.
echo ERROR: Could not locate scripts.
echo.
echo Please set the variable PC2INSTALL to the location of
echo   the VERSION file (ex: c:\pc2-9.0.0)
echo.
pause
goto :end

:continue

set CLASSNAME=edu.csus.ecs.pc2.tools.PC2Tools

call %PC2BIN%\pc2env.bat
java -Dfile.encoding=UTF-8 -Djdk.crypto.KeyAgreement.legacyKDF=true -Xms64M -Xmx768M -cp "%libdir%\*" %CLASSNAME% %1 %2 %3 %4 %5 %6 %7 %8 %9

:end
rem eof pc2tools.bat
