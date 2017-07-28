#!/bin/bash

##
# PUBLISH
#
# $1: DATASET: the absolute path of the dataset.
##

DATASET="$1"

COMMAND="bash exec.sh"

KAFKA_BROKER_ADDR="localhost"
KAFKA_BROKER_PORT="9092"
KAFKA_TOPIC="main-topic"

NEO4J_MASTER_ADDR="bolt://localhost"
NEO4J_MASTER_PORT="7687"
NEO4J_USERNAME="neo4j"
NEO4J_PASSWORD="password"

WAIT="10"

TIMEOUT="10"

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
