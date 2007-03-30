@echo off

rem Purpose: print pc2 version number
rem Author : pc2@ecs.csus.edu
rem $HeadURL$

if exist ..\VERSION cd ..

java -cp lib\pc2.jar edu.csus.ecs.pc2.VersionInfo

rem eof pc2ver.bat $Id$
