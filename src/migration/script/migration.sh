#!/bin/bash

export CLASSPATH=classes:classes/migration.jar

$ANT_HOME/bin/ant -f migration.xml -lib lib -listener org.apache.tools.ant.listener.Log4jListener $1 $2 $3 $4 $5 $6 $7 $8 $9
