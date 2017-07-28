#!/bin/bash

##
# CHECK
##

COMMAND="{{ crimegraph_monitor_command }}"

DATASETS_DIR="{{ crimegraph_monitor_datasets }}"
RESULTS_DIR="{{ crimegraph_monitor_results }}"

KAFKA_BROKER_ADDR="{{ crimegraph_monitor_kafka_broker_address }}"
KAFKA_BROKER_PORT="{{ crimegraph_monitor_kafka_broker_port }}"
KAFKA_TOPIC="{{ crimegraph_monitor_kafka_broker_topic }}"

if [ ! -d "$DATASETS_DIR" ]; then
    echo "cannot find directory: ${DATASETS_DIR}"
    exit 1
fi

if [ ! -d "$RESULTS_DIR" ]; then
    echo "cannot find directory: ${RESULTS_DIR}"
    exit 1
fi

$COMMAND check --kafkaBroker $KAFKA_BROKER_ADDR:$KAFKA_BROKER_PORT --kafkaTopic $KAFKA_TOPIC
