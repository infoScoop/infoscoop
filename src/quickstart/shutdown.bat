@echo off
set CURRENT_DIR=%~dp0
set CATALINA_HOME=%CURRENT_DIR%\apache-tomcat-6.0.28
"%CATALINA_HOME%\bin\shutdown.bat"