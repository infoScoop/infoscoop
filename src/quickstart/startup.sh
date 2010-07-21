SELF_DIR=`dirname $0`
CATALINA_HOME=${SELF_DIR}/apache-tomcat-6.0.28
JAVA_OPTS="-Xms128m -Xmx256m -XX:PermSize=64m -XX:MaxPermSize=128m"
${CATALINA_HOME}/bin/startup.sh
