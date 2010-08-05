#!/bin/sh

if [ ! -e "$JAVA_HOME" ]
then
	echo Please set the JAVA_HOME emvironment valiable.
	exit
fi 	
 	
LOCALCLASSPATH=".:./bin"

for jar in `ls lib | grep ".jar"`
do
	LOCALCLASSPATH="$LOCALCLASSPATH:./lib/$jar"
done

#echo "set classpath $LOCALCLASSPATH"

"$JAVA_HOME/bin/java" -classpath $LOCALCLASSPATH ImportTool $1 $2 $3 $4 $5 $6 $7 $8 $9