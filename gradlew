#!/bin/sh

# Gradle wrapper script

APP_NAME="Gradle"
GRADLE_VERSION="8.6"

# Determine the script's directory
PRG="$0"
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`/"$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

APP_NAME="Gradle"
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Determine the Java command to use
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/bin/java" ] ; then
        JAVACMD="$JAVA_HOME/bin/java"
    else
        JAVACMD=java
    fi
else
    JAVACMD=java
fi

exec "$JAVACMD" \
    -classpath "$CLASSPATH" \
    -Dgradle.home="$APP_HOME" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
