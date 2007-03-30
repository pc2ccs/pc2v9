@echo off

rem Purpose: start the team module
rem Author : pc2@ecs.csus.edu
rem $HeadURL$

if exist ..\VERSION cd ..

start java -cp lib\pc2.jar edu.csus.ecs.pc2.Starter

cd bin

rem eof pc2team.bat $Id$
