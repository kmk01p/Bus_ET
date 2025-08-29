#!/bin/sh
# Maven Wrapper for Spring Boot Application

MAVEN_VERSION=3.8.6
MAVEN_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.tar.gz"
MAVEN_HOME="${HOME}/.m2/wrapper/maven-${MAVEN_VERSION}"

if [ ! -d "$MAVEN_HOME" ]; then
    echo "Downloading Maven ${MAVEN_VERSION}..."
    mkdir -p "${HOME}/.m2/wrapper"
    curl -fsSL "$MAVEN_URL" | tar -xzC "${HOME}/.m2/wrapper"
    mv "${HOME}/.m2/wrapper/apache-maven-${MAVEN_VERSION}" "$MAVEN_HOME"
fi

exec "$MAVEN_HOME/bin/mvn" "$@"