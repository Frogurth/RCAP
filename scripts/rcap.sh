#!/usr/bin/env bash
java -jar $RCAP_HOME/rcap.jar $@ > /dev/null &
echo $! > $RCAP_HOME/rcap.pid
sleep 20
java -jar $RCAP_HOME/rcaplistener.jar $@ > /dev/null &
echo $! > $RCAP_HOME/rcap_listener.pid

