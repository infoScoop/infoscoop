@echo off
set CURRENT_DIR=%~dp0
set CATALINA_HOME=%CURRENT_DIR%\apache-tomcat-6.0.28
set JAVA_OPTS=-Xms128m -Xmx256m -XX:PermSize=64m -XX:MaxPermSize=128m
"%CATALINA_HOME%\bin\startup"