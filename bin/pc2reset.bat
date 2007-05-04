@echo off
rem
rem pc2reset.bat - Completely reset and erase contest data
rem USE WITH EXTREME CAUTION 
rem 
rem $HeadURL$
rem

set RMCMD=rmdir /s /q
if %OS%. == . set RMCMD=deltree /y

if not exist bin\pc2zip.bat goto wrongdir

echo Backing up settings to archive 
call bin\pc2zip.bat

%RMCMD% db
%RMCMD% db.1
%RMCMD% db.2
%RMCMD% db.3

%RMCMD% logs
%RMCMD% packets

del report*.txt
del *.log

goto end
:wrongdir

echo.
echo Can not find bin\pc2zip.bat
echo.
rem // TODO better message or handle this better
echo Run pc2reset from other dir...
echo
echo.


:end

rem eof pc2reset.bat $Id$
