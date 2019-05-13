#!/bin/sh

SCRIPT=$(readlink -f $0)
HERE=$(dirname ${SCRIPT})

VERBOSE=${1:-no}

DEST=${HOME}/nextCloud-int/perrypedia-release-calendar

LOGBASE=${HERE}/logs

mkdir -p ${LOGBASE}

LOGDIR=${LOGBASE}/$(date +%Y-%m)/$(date +%Y-%d)

mkdir -p ${LOGDIR}

cd ${HERE}

git pull \
    > ${LOGDIR}/git.log \
    2> ${LOGDIR}/git.err

mvn -U clean install \
    > ${LOGDIR}/mvn.log \
    2> ${LOGDIR}/mvn.err

mkdir -p ${DEST}

if [ -r ${DEST}/../on-nextCloud.txt ]
then
    cd ${DEST}
    java -DLOGBASE=${LOGBASE} \
	 -Dsleep=30 \
	 -jar ${HERE}/target/perrypedia-release-calendar-*-executable.jar \
	 > ${LOGDIR}/java-run.log \
	 2> ${LOGDIR}/java-run.err
fi

if [ ${VERBOSE} = "-v" ]
then
    echo

    cat ${LOGDIR}/java-run.log

    echo

    cat ${LOGDIR}/java-run.err

    echo
fi
