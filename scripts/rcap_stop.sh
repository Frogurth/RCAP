#!/usr/bin/env bash
kill $(cat ${RCAP_HOME}/rcap.pid ${RCAP_HOME}/listener.pid);
rm ${RCAP_HOME}/*.pid