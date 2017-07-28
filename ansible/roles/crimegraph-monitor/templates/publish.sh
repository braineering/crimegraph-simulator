#!/bin/bash

##
# PUBLISH
#
# $1: DATASET: the absolute path of the dataset.
##

DATASET="$1"

COMMAND="{{ crimegraph_monitor_command }}"

KAFKA_BROKER_ADDR="{{ crimegraph_monitor_kafka_broker_address }}"
KAFKA_BROKER_PORT="{{ crimegraph_monitor_kafka_broker_port }}"
KAFKA_TOPIC="{{ crimegraph_monitor_kafka_broker_topic }}"

NEO4J_MASTER_ADDR="bolt://{{ crimegraph_monitor_neo4j_master_address }}"
NEO4J_MASTER_PORT="{{ crimegraph_monitor_neo4j_master_port }}"
NEO4J_USERNAME="{{ crimegraph_monitor_neo4j_master_username }}"
NEO4J_PASSWORD="{{ crimegraph_monitor_neo4j_master_password }}"

WAIT="{{ crimegraph_monitor_wait_between_commands }}"

TIMEOUT="{{ crimegraph_monitor_wait_between_commands }}"

if [ ! -f "$DATASET" ]; then
    echo "dataset not found: ${DATASET}"
    exit 1
fi

$COMMAND publish --kafkaBroker $KAFKA_BROKER_ADDR:$KAFKA_BROKER_PORT --kafkaTopic $KAFKA_TOPIC --dataset $DATASET

echo "Waiting ${WAIT} seconds..."
sleep $WAIT

$COMMAND check_dataset_db --neo4jHostname $NEO4J_MASTER_ADDR:$NEO4J_MASTER_PORT --neo4jUsername $NEO4J_USERNAME --neo4jPassword $NEO4J_PASSWORD --dataset $DATASET --timeout $TIMEOUT

echo "Waiting ${WAIT} seconds..."
sleep $WAIT

$COMMAND wait_stability_db --neo4jHostname $NEO4J_MASTER_ADDR:$NEO4J_MASTER_PORT --neo4jUsername $NEO4J_USERNAME --neo4jPassword $NEO4J_PASSWORD --timeout $TIMEOUT
