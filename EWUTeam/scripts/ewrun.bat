@echo off

rem Purpose: Start the EWU Team server/java bridge 
rem Author : pc2@ecs.csus.edu
rem $HeadURL$

rem switch to lib directory

if exist PC2JavaServer.jar goto run
if exist lib\PC2JavaServer.jar cd lib
if exist ..\lib\PC2JavaServer.jar cd ..\lib

if exist PC2JavaServer.jar goto run

echo.
echo Unable to find PC2JavaServer.jar in lib directory
echo Change dirtectory to the EWU Team installation directory
echo and try again.
echo
echo.

goto end

:run 

rem run in lib directory
java -Xms64M -Xmx768M -jar PC2JavaServer.jar %1 %2 %3 %4 %5 %6 %7 %8 %9

:end

rem eof ewrun.bat $Id$
