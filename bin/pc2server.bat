@echo off
REM Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.

rem Purpose: Start the server 
rem Author : pc2@ecs.csus.edu
rem $HeadURL$

rem
rem Set the default Heap Size for 32-bit and 64-bit Java
rem
set Java64bit_HeapSize=4G
set Java32bit_HeapSize=768M

rem
rem Windows 2000 and beyond syntax
set PC2BIN=%~dp0
if exist %PC2BIN%\pc2env.bat goto :continue

rem fallback to path (or current directory)
set PC2BIN=%0\..
if exist %PC2BIN%\pc2env.bat goto :continue

rem else rely on PC2INSTALL variable
set PC2BIN=%PC2INSTALL%\bin
if exist %PC2BIN%\pc2env.bat goto :continue

echo.
echo ERROR: Could not locate scripts.
echo.
echo Please set the variable PC2INSTALL to the location of
echo   the VERSION file (ex: c:\pc2-9.0.0)
echo.
pause
goto :end

:continue
call %PC2BIN%\pc2env.bat

:CheckJavaVersion
REM Check Java architecture and set max Heap size 
java -Djdk.crypto.KeyAgreement.legacyKDF=true -d64 -version 2>&1 | findstr "Error" > NUL
IF ERRORLEVEL 1 goto :Java64
IF ERRORLEVEL 0 goto :Java32

:Java64
set HeapParam=-Xmx%Java64bit_HeapSize%
goto :startJava

:Java32
set HeapParam=-Xmx%Java32bit_HeapSize%

IF "%PROCESSOR_ARCHITECTURE%"=="x86" goto :OS32
goto :OS64

:OS64
cls
Echo.
Echo WARNING: We detected a 32 bit java running on a 64 bit OS.
Echo.
Echo We recommend that you run PC^2 Server using a 64 bit version of Java to allow
Echo for the best results.
:OS32
Echo.
Echo Note: We have set the Java Heap size to %Java32bit_HeapSize% for your 32-bit Java system,
echo but this may be too small to run the PC^2 Server. 
Echo.
echo You can increase the Heap size to whatever the maximum allowed on your machine 
echo is by editing the Java32bit_HeapSize constant at the start of the 
echo "pc2server.bat" file.
goto :startJava

:startJava
java -Djdk.crypto.KeyAgreement.legacyKDF=true %HeapParam% -cp "%libdir%\*" edu.csus.ecs.pc2.Starter --server %1 %2 %3 %4 %5 %6 %7 %8 %9

:end
rem eof pc2server.bat $Id$
