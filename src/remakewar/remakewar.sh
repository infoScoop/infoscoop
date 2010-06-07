#!/bin/bash
 
if [ x"$JAVA_HOME" == x ]; then
   echo The JAVA_HOME environment variable is not defined correctly
   echo this environment variable is needed to run this program
   exit 1
fi

if [ x"$ANT_HOME" == x ]; then
   echo The ANT_HOME environment variable is not defined correctly
   echo this environment variable is needed to run this program
   exit 1
fi

$ANT_HOME/bin/ant -f build.xml

exit 0
