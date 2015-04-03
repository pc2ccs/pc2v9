@echo off

rem Purpose: print pc2 version number
rem Author : pc2@ecs.csus.edu
rem $HeadURL$

rem Windows 2000 and beyond syntax
set EWUBIN=%~dp0
if exist %EWUBIN%\ewuenv.bat goto :continue

rem fallback to path (or current directory)
set EWUBIN=%0\..
if exist %EWUBIN%\ewuenv.bat goto :continue

rem else rely on EWUINSTALL variable
set EWUBIN=%EWUINSTALL%\bin
if exist %EWUBIN%\ewuenv.bat goto :continue

echo.
echo ERROR: Could not locate scripts.
echo.
echo Please set the variable EWUINSTALL to the location of
echo   the VERSION file (ex: c:\eauteam)
echo.
pause
goto end

:continue

call %EWUBIN%\ewuenv.bat

echo java -cp %libdir%\PC2JavaServer.jar edu.csus.pc2.ewuteam.VersionInfo
java -cp %libdir%\PC2JavaServer.jar edu.csus.pc2.ewuteam.VersionInfo

:end
rem eof ewuver.bat $Id$
