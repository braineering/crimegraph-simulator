#!/bin/bash

##
# CHECK
##

COMMAND="bash exec.sh"

DATASETS_DIR="${HOME}/crimegraph-monitor/datasets"
RESULTS_DIR="${HOME}/crimegraph-monitor/results"

KAFKA_BROKER_ADDR="localhost"
KAFKA_BROKER_PORT="9092"
KAFKA_TOPIC="main-topic"

if [ ! -d "$DATASETS_DIR" ]; then
    echo "cannot find directory: ${DATASETS_DIR}"
    exit 1
fi

if [ ! -d "$RESULTS_DIR" ]; then
    echo "cannot find directory: ${RESULTS_DIR}"
    exit 1
fi

$COMMAND check --kafkaBroker $KAFKA_BROKER_ADDR:$KAFKA_BROKER_PORT --kafkaTopic $KAFKA_TOPIC
