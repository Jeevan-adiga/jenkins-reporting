#!/bin/sh
BIN=$(cd -- "$(dirname "$0")" && pwd)
cd "$BIN"
java -classpath ../target/jenkins-reporting.jar:../conf Launch "$1" "$2" "$3"
