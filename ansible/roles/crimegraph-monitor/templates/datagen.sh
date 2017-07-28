#!/bin/bash

##
# DATAGEN
#
# $1 (DATASET): absolute path to the original dataset.
# $2 (DEST): absolute path of the destination folder.
# $2 (TEST_RATIOS): comma separated list of test ratios.
##

COMMAND="{{ crimegraph_monitor_command }}"
MININGS=( "DETECTION" "PREDICTION" )

ORIGINAL_DATASET="$1"
DEST_DIR="${2%%/}"
IFS=',' read -r -a TEST_RATIOS <<< "$3"

if [ ! -f "$ORIGINAL_DATASET" ]; then
    echo "Cannot read dataset: ${ORIGINAL_DATASET}"
    exit 1
fi

if [ ! -d "$DEST_DIR" ]; then
    echo "Cannot find destination directory: ${DEST_DIR}"
    exit 1
fi

DECIMAL_REGEXP='^0\.([0-9])+$'
for TEST_RATIO in "${TEST_RATIOS[@]}"; do
  if ! [[ $TEST_RATIO =~ $DECIMAL_REGEXP ]] ; then
    echo "Not valid test ratio: ${TEST_RATIO}"
    exit 1
  fi
done

_DATASET_FILENAME_TMP="${ORIGINAL_DATASET##*/}"
DATASET_NAME=${_DATASET_FILENAME_TMP%.*}
unset _DATASET_FILENAME_TMP
DATASET="${DEST_DIR}/${DATASET_NAME}.data"

cp -rf $ORIGINAL_DATASET ${DATASET}

DATASET_LCC="${DEST_DIR}/${DATASET_NAME}_lcc.data"
$COMMAND datagen_lcc --dataset $DATASET --output $DATASET_LCC

for MINING in "${MININGS[@]}"; do
    for TEST_RATIO in "${TEST_RATIOS[@]}"; do
        TRAINSET="${DEST_DIR}/${DATASET_NAME}_lcc_train_${MINING}_${TEST_RATIO}.data"
        TESTSET="${DEST_DIR}/${DATASET_NAME}_lcc_test_${MINING}_${TEST_RATIO}.data"
        $COMMAND traintest --mining $MINING --dataset $DATASET_LCC --trainset $TRAINSET --testset $TESTSET --testRatio $TEST_RATIO
    done
done
