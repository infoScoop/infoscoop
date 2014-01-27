@echo off
set CURRENT_DIR=%~dp0
set CATALINA_HOME=%CURRENT_DIR%\apache-tomcat-7.0.34
set JAVA_OPTS=-Xms128m -Xmx256m -XX:PermSize=64m -XX:MaxPermSize=128m
"%CATALINA_HOME%\bin\startup"