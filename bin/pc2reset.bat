@echo off
rem
rem pc2reset.bat - Completely reset and erase contest data
rem USE WITH EXTREME CAUTION 
rem 
rem $HeadURL$
rem

set RMCMD=rmdir /s /q
if %OS%. == . set RMCMD=deltree /y

if not exist %0\..\..\logs goto wrongdir
if not exist %0\..\..\packets goto wrongdir

echo Backing up settings to archive 
call %0\..\pc2zip.bat

%RMCMD% logs
%RMCMD% packets
%RMCMD% reports
%RMCMD% db
%RMCMD% db.1
%RMCMD% db.2
%RMCMD% db.3

rem TODO execute*

goto end
:wrongdir

echo.
echo Not in directory to reset, should be run from dir with logs and packets
echo.


:end

rem eof pc2reset.bat $Id$
