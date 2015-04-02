
rem Purpose: to be called by the other scripts to setup the environment
rem Author : pc2@ecs.csus.edu
rem $HeadURL$

rem Change these (& uncomment) for non-standard installations
rem set libdir=..\lib

rem try development locations first
if exist %0\..\..\dist\pc2.jar set libdir=%0\..\..\dist
if exist lib\pc2.jar set libdir=lib
if exist ..\lib\pc2.jar set libdir=..\lib

rem then try the distribution locations
if exist %0\..\..\lib\pc2.jar set libdir=%0\..\..\lib

if x%libdir% == x goto nolibdir

goto end

:nolibdir
echo Could not find pc2.jar, please check your installation
rem XXX we really want to do a break here
rem pause

end:

set pc2_classpath=%libdir%\pc2.jar

rem eof ewuenv.bat $Id$
