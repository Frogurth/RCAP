#!/usr/bin/env bash
kill $(cat rcap.pid rcap_listener.pid);
rm *.pid