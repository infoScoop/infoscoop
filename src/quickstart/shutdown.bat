@echo off
set CURRENT_DIR=%~dp0
set CATALINA_HOME=%CURRENT_DIR%\apache-tomcat-8.5.43
"%CATALINA_HOME%\bin\shutdown.bat"