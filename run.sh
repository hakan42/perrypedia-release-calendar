#!/bin/sh

SCRIPT=$(readlink -f $0)
HERE=$(dirname ${SCRIPT})

DEST=${HOME}/ownCloud/perrypedia-release-calendar

cd ${HERE}

git pull \
    > ${HERE}/git.log \
    2> ${HERE}/git.err

mvn -U clean install \
    > ${HERE}/mvn.log \
    2> ${HERE}/mvn.err

mkdir -p ${DEST}

if [ -r ${DEST}/../on-ownCloud.txt ]
then
    cd ${DEST}
    java -jar ${HERE}/target/perrypedia-release-calendar-*-executable.jar \
	 > ${HERE}/java-run.log \
	 2> ${HERE}/java-run.err
fi
