@echo off

rem Purpose: start pc2
rem Author : pc2@ecs.csus.edu
rem $HeadURL$

call %0\..\pc2env.bat

java -cp %libdir%\pc2.jar;%mclbdir%\mclb.jar edu.csus.ecs.pc2.Starter %1 %2 %3 %4 %5 %6 %7 %8 %9

rem eof pc2team.bat $Id$
