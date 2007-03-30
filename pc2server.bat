@echo off

rem Purpose: Start the server 
rem Author : pc2@ecs.csus.edu
rem $HeadURL$

if exist ..\VERSION cd ..

start java -cp lib\pc2.jar edu.csus.ecs.pc2.Starter

cd bin

rem eof pc2server.bat $Id$
