@echo off
rem
rem Completely reset and erase contest data
rem USE WITH EXTREME CAUTION 
rem Revised: Thu Jul 17 20:31:39 PDT 2003
rem 
rem $HeadURL$
rem 

set RMCMD=rmdir /s /q
if %OS%. == . set RMCMD=deltree /y
set RM=rm -f 
if %OS%. == . set RM=del /Q

if not exist bin\pc2zip.bat goto wrongdir

echo Backing up settings to archive 
call bin\pc2zip.bat

%RMCMD% profiles
%RMCMD% db
%RMCMD% db.1
%RMCMD% db.2
%RMCMD% db.3
%RMCMD% db.4
%RMCMD% executes*

%RMCMD% logs
%RMCMD% packets
%RMCMD% reports
%RMCMD% html

del profiles.properties
del *.log

goto end
:wrongdir

echo.
echo Can not find bin\pc2zip.bat
echo.

:end

rem eof pc2reset.bat $Id$