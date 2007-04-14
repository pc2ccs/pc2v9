@echo off

rem Purpose: start the team module
rem Author : pc2@ecs.csus.edu
rem $HeadURL$

if exist ..\VERSION cd ..

java -cp lib\pc2.jar;lib\mclb.jar edu.csus.ecs.pc2.Starter

cd bin

rem eof pc2team.bat $Id$
