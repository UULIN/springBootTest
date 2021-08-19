#!/usr/bin/env bash

PRG="$0"
PRGDIR=`dirname "$PRG"`
[ -z "$BACKEND_HOME" ] && BACKEND_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`

JAVA_OPTS=" -Xms512M -Xmx1024M"

_RUNJAVA=${JAVA_HOME}/bin/java
[ -z "$JAVA_HOME" ] && _RUNJAVA=java

eval exec "\"$_RUNJAVA\" ${JAVA_OPTS} -Dapplication.standard.env=prod -Dstage=prod -jar ${BACKEMD_HOME}/counter-slow.jar --spring.profiles.active=prod 1>/dev/null &"

if [ $? -eq 0 ]; then
    sleep 1
    echo "counter-slow started successfully!"
else
    echo "counter-slow started failure!"
    exit 1
fi
