@echo off

if "%JAVA_HOME%" == "" goto error

set LOCALCLASSPATH=
for %%i in ("lib\*.jar") do call lcp.bat %%i
REM echo set classpath .;%LOCALCLASSPATH%;bin;
"%JAVA_HOME%\bin\java" -classpath .;%LOCALCLASSPATH%;bin; ImportTool %1 %2 %3 %4 %5 %6 %7 %8 %9
goto end


:error
echo Please set the JAVA_HOME environment variable.

:end