@echo off
set CURRENT_DIR=%~dp0
set CATALINA_HOME=%CURRENT_DIR%\apache-tomcat-7.0.34
"%CATALINA_HOME%\bin\shutdown.bat"