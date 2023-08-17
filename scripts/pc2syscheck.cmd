@echo off
REM Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.

rem Purpose: Check host system to see if it's setup properly for sandbox usage
rem Author : pc2@ecs.csus.edu
rem $HeadURL$

rem FAIL_RETCODE_BASE is 128 + 64 + xx
rem 128 = system error, like signal
rem 64 = biggest used signal
set FAIL_RETCODE_BASE=192
set FAIL_EXIT_CODE=237
set FAIL_NO_ARGS_EXIT_CODE=238
set FAIL_INSUFFICIENT_ARGS_EXIT_CODE=239
set FAIL_INVALID_CGROUP_INSTALLATION=240
set FAIL_MISSING_CGROUP_CONTROLLERS_FILE=241
set FAIL_MISSING_CGROUP_SUBTREE_CONTROL_FILE=242
set FAIL_CPU_CONTROLLER_NOT_ENABLED=243
set FAIL_MEMORY_CONTROLLER_NOT_ENABLED=244
set FAIL_MEMORY_LIMIT_EXCEEDED=245
set FAIL_TIME_LIMIT_EXCEEDED=246
set FAIL_WALL_TIME_LIMIT_EXCEEDED=247

rem Windows does not currently support sandbox's.  Simply return failure indicating that.
exit /b %FAIL_EXIT_CODE%
 