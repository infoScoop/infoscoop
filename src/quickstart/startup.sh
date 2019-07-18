SELF_DIR=`dirname $0`
CATALINA_HOME=${SELF_DIR}/apache-tomcat-8.5.43
JAVA_OPTS="-Xms128m -Xmx256m -XX:PermSize=64m -XX:MaxPermSize=128m"
${CATALINA_HOME}/bin/startup.sh
