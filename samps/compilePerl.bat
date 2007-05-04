@echo off
rem File:    compilePerl.bat
rem Purpose: Used to compile/use Perl with PC^2
rem Author:  pc2@ecs.csus.edu
rem
rem Dependencies:  perl 
rem
rem pc2 language settings:
rem 
rem Display Name                       : Perl
rem Compile Command Line               : compilePerl {:mainfile} 
rem Executable Filename specification  : OK 
rem Program Execution Command Line     : perl {:mainfile}
rem
rem $HeadURL$
rem

if exist %1 goto compile:

echo No perl source specified or file '%1' does not exist

goto end:
:compile
perl -c %1 %2 %3

if ERRORLEVEL 0 echo > OK

:end 

rem eof compilePerl $Id$
