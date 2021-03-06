#!/bin/sh
# This script will install all files in IntelliJ IDEA's lib/ folder to the local maven .m2 repository. This way we can use them during the build
#
# Usage:
#   ./install-intellij-libs.sh 13.1.4 /Users/ahe/Applications/IntelliJ-IDEA-13.app/

IDEA_VERSION=$1
INTELLIJ_HOME=$2

if [ -z "$INTELLIJ_HOME" ]
then
  echo "Please provide the version and path to the IntelliJ home directory. For example: ./install-intellij-libs.sh 13.1.4 /Users/ahe/Applications/IntelliJ-IDEA-13.app/"
  exit 1
fi

if [ ! -d "$INTELLIJ_HOME" ]
then
  echo "Directory does not exist: $INTELLIJ_HOME"
  exit 1
fi

echo 'Installing IntelliJ artifacts to Maven local repository'
echo "IntelliJ home: $INTELLIJ_HOME"
for i in `ls -1 ${INTELLIJ_HOME}/lib/*.jar`
do
    FOLDERS=(${i//\// })
    FILE_POS=${#FOLDERS[@]}
    JAR_FILE=${FOLDERS[${FILE_POS}-1]%.jar}
    mvn install:install-file -Dfile="$INTELLIJ_HOME/lib/${JAR_FILE}.jar" -DgroupId=com.intellij -DartifactId=${JAR_FILE} -Dversion=${IDEA_VERSION} -Dpackaging=jar
done
