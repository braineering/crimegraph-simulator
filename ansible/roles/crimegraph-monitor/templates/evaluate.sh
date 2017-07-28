#!/bin/bash

##
# EVALUATE
#
# $1: EVALUATION: the ebaluation method (ALL|AUC|PRECISION).
# $2: METRIC: the metric to evaluate (ALL|NRA|TA|NTA|CN|JACCARD|SALTON|SORENSEN|HPI|HDI|LHN1|PA|AA|RA).
# $3: DATASET: dataset in /opt/crimegraph-monitor/datasets/.
# $4: TRAINSET: trainset in /opt/crimegraph-monitor/datasets/.
# $5: TESTSET: testset in /opt/crimegraph-monitor/datasets/.
# ($6: PRMS): params for evaluation (e.g param1=val1,...,paramN=valN).
##

EVALUATION="$1"
METRIC="$2"
DATASET="$3"
TRAINSET="$4"
TESTSET="$5"
PRMS="$6"

COMMAND="{{ crimegraph_monitor_command }}"

RESULTS_DIR="{{ crimegraph_monitor_results }}"

NEO4J_MASTER_ADDR="bolt://{{ crimegraph_monitor_neo4j_master_address }}"
NEO4J_MASTER_PORT="{{ crimegraph_monitor_neo4j_master_port }}"
NEO4J_USERNAME="{{ crimegraph_monitor_neo4j_master_username }}"
NEO4J_PASSWORD="{{ crimegraph_monitor_neo4j_master_password }}"

if [ ! -z ${PRMS} ]; then
  PRMS_OPTION="--params ${PRMS}"
fi

${COMMAND} evaluate --evaluation ${EVALUATION} --metric ${METRIC} --neo4jHostname ${NEO4J_MASTER_ADDR}:${NEO4J_MASTER_PORT} --neo4jUsername ${NEO4J_USERNAME} --neo4jPassword ${NEO4J_PASSWORD} --dataset ${DATASET} --trainset ${TRAINSET} --testset ${TESTSET} --output ${RESULTS_DIR} ${PRMS_OPTION}
