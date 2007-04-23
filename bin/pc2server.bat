@echo off

rem Purpose: Start the server 
rem Author : pc2@ecs.csus.edu
rem $HeadURL$

set JAVA=c:\jdk1.5.0_05

rem might need to set systemroot on win95/98/ME
rem set SYSTEMROOT=c:\windows

set PATH=%JAVA%\bin;%SYSTEMROOT%;%SYSTEMROOT%\system32;%PATH%

if exist ..\VERSION cd ..

java -cp lib\pc2.jar;lib\mclb.jar edu.csus.ecs.pc2.Starter --server %1 %2 %3 %4 %5 %6 %7 %8 %9

cd bin

rem eof pc2server.bat $Id$
