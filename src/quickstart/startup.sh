SELF_DIR=`dirname $0`
CATALINA_HOME=${SELF_DIR}/apache-tomcat-7.0.34
JAVA_OPTS="-Xms128m -Xmx256m -XX:PermSize=64m -XX:MaxPermSize=128m"
${CATALINA_HOME}/bin/startup.sh
