#!/usr/bin/env bash

export JDK11_HOME=/usr/lib/jvm/jdk-11.0.1/
export JAVA_HOME=$JDK11_HOME
export PATH=$JAVA_HOME/bin:$PATH
export MAVEN_HOME=~/Downloads/apache-maven-3.6.0

mvn clean compile dependency:sources dependency:resolve -Dclassifier=javadoc

## proxy
# mvn -Dhttp.proxyHost=www-proxy.us.oracle.com -Dhttp.proxyPort=80 -Dhttps.proxyHost=www-proxy.us.oracle.com -Dhttps.proxyPort=80 \
#    clean compile dependency:sources dependency:resolve -Dclassifier=javadoc
