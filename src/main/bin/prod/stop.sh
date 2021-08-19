#!/usr/bin/env bash

PID=`ps -ef | grep counter-slow | grep -v grep| awk '{print $2}'`
[[ ! -z "$PID" ]] && echo "counter-slow is running, stop it..." && kill $PID
