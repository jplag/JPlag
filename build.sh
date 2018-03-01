#!/bin/sh
set -e
case $0 in
  /*) curdir=${0%/*}/;;
  */*) curdir=./${0%/*};;
  *) curdir=.;;
esac &&
cd "$curdir" &&
mvn clean generate-sources package install &&
cd jplag &&
mvn assembly:assembly
