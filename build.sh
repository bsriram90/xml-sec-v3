#!/bin/sh

CLASSPATH=.:$CLASSPATH
export CLASSPATH

ant ${1}
