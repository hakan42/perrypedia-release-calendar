#!/bin/sh

SCRIPT=$(readlink -f $0)
HERE=$(dirname ${SCRIPT})

VERBOSE=${1:-no}

DEST=${HOME}/nextCloud-int/perrypedia-release-calendar

cd ${HERE}

git pull \
    > ${HERE}/git.log \
    2> ${HERE}/git.err

mvn -U clean install \
    > ${HERE}/mvn.log \
    2> ${HERE}/mvn.err

mkdir -p ${DEST}

if [ -r ${DEST}/../on-nextCloud.txt ]
then
    cd ${DEST}
    java -jar ${HERE}/target/perrypedia-release-calendar-*-executable.jar \
	 -Dsleep=30 \
	 > ${HERE}/java-run.log \
	 2> ${HERE}/java-run.err
fi

if [ ${VERBOSE} = "-v" ]
then
    echo

    cat ${HERE}/java-run.log

    echo

    cat ${HERE}/java-run.err

    echo
fi
