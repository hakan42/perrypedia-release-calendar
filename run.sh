#!/bin/sh

SCRIPT=$(readlink -f $0)
HERE=$(dirname ${SCRIPT})

DEST=${HOME}/ownCloud/perrypedia-release-calendar

cd ${HERE}
git pull              > ${HERE}/git.log
mvn -U clean install  > ${HERE}/mvn.log

mkdir -p ${DEST}

if [ -r ${DEST}/../on-ownCloud.txt ]
then
    cd ${DEST}
    java -jar ${HERE}/target/perrypedia-release-calendar-*-executable.jar > ${HERE}/java-run.log
fi
