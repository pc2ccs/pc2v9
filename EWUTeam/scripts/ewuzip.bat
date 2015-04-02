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

rem else rely on PC2INSTALL variable
set EWUBIN=%PC2INSTALL%\bin
if exist %EWUBIN%\ewuenv.bat goto :continue

echo.
echo ERROR: Could not locate scripts.
echo.
echo Please set the variable EWUINSTALL to the location of
echo   the VERSION file (ex: c:\pc2-9.0.0)
echo.
pause
goto :end

:continue
call %EWUBIN%\ewuenv.bat

java -Xms64M -Xmx768M -cp %libdir%\PC2JavaServer.jar edu.csus.ecs.pc2.core.archive.ZipPC2

:end
rem eof ewuzip.bat $Id$
