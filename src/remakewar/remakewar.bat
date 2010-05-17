@echo off

echo **************************************************************************
echo *
echo * Remake infoscoop.war
echo *
echo **************************************************************************

rem ----------------------------------------------------------------------------
rem check JAVA_HOME
rem ----------------------------------------------------------------------------
if not "%JAVA_HOME%" == "" goto okJavaHome
echo The JAVA_HOME environment variable is not defined correctly
echo this environment variable is needed to run this program
goto end
:okJavaHome

rem ----------------------------------------------------------------------------
rem check ANT_HOME
rem ----------------------------------------------------------------------------
if not "%ANT_HOME%" == "" goto okAntHome
echo The ANT_HOME environment variable is not defined correctly
echo this environment variable is needed to run this program
goto end
:okAntHome

rem ----------------------------------------------------------------------------
rem Remake msd-portal.war
rem ----------------------------------------------------------------------------
call "%ANT_HOME%\bin\ant" -f build.xml

:end
