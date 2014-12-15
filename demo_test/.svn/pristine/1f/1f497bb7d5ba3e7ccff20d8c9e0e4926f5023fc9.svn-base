#!/bin/bash


POM_FILE=`pwd`/pom.xml
MVN_LOCATION=`which mvn | sed "s|/bin/mvn||"`
MVN_LIB=${MVN_LOCATION}/lib

if [ ! -d `pwd`/maven-parser/target ]; then
    echo "ERROR: The project has not been built yet (hint: mvn -U clean install)"
    exit -1
fi

PLEXUS_UTILS=`ls ${MVN_LIB} | grep plexus-utils`
MAVEN_MODEL=`ls ${MVN_LIB} | grep maven-model | head -n 1`
CLASSPATH="maven-parser/target/maven-parser-1.0-SNAPSHOT.jar:${MVN_LIB}/${PLEXUS_UTILS}:${MVN_LIB}/${MAVEN_MODEL}"

echo ${CLASSPATH}
java -cp ${CLASSPATH} edu.illinois.cs.utils.ParserUtils `pwd`/pom.xml

