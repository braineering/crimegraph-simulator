# CRIMEGRAPH MONITOR

*Monitoring utilities for Crimegraph*

Big data analytics is a disruptive technology that can reshape core tasks of security intelligence.
The real-time discovery of hidden criminal patterns is an outstanding challenge for security and law enforcement agencies.
In particular, predicting the evolution of criminal networks and uncovering concealed relationships can efficiently guide investigations for better decision-making.

In this context, it is necessary to develop social network metrics that are both domain-aware and ready to be executed in a data stream environment.
That is why we propose two structural local metrics for link detection and prediction, together with their data stream processing implementation.
The experimental results show that the proposed metrics can reach up to ??\% accuracy with an average latency of ?? ms.

## Requirements
To execute the app locally you need the following to be installed to your system:

* Java
* Maven

To deploy the app to a Digital Ocean droplet, you need the following to be installed on your system:
* Vagrant
* Vagrant plugin for Digital Ocean
* Ansible


## Build
The app building is provided by Apache Maven. To build the app you need to run

    $crimegraph-monitor> mvn clean package

## Deploy

    $crimegraph-monitor> source .credentials.sh

    $crimegraph-monitor> vagrant up


## Usage
Analyze dataset:

    $crimegraph-monitor> java -jar target/crimegraph-monitor-1.0.jar analyze_dataset --dataset datasets/datagen.data

Generate random dataset:

    $crimegraph-monitor> java -jar target/crimegraph-monitor-1.0.jar datagen_rnd --numNodes 20 --numLinks 100 --minWeight 1.0 --maxWeight 100.0 --output datasets/datagen.data

Generate a LCC dataset:

    $crimegraph-monitor> java -jar target/crimegraph-monitor-1.0.jar datagen_lcc --dataset datasets/datagen.data --output datasets/datagen_lcc.data

Generate trainset and testset:

    $crimegraph-monitor> java -jar target/crimegraph-monitor-1.0.jar traintest --mining [MINING] --dataset datasets/datagen.data --trainset datasets/datagen_train_detection.data --testset datasets/datagen_test_detection.data --testRatio 0.1

Publish dataset:

    $crimegraph-monitor> java -jar target/crimegraph-monitor-1.0.jar publish --kafkaBroker [KAFKA_BROKER] --kafkaTopic [KAFKA_TOPIC] --dataset [DATASET]

Check dataset on db:

    $crimegraph-monitor> java -jar target/crimegraph-monitor-1.0.jar check_dataset_db --neo4jHostname [NEO4J] --neo4jUsername [USERNAME] --neo4jPassword [PASSWORD] --dataset [DATASET] --timeout [TIMEOUT]

Evaluate:

    $crimegraph-monitor> java -jar target/crimegraph-monitor-1.0.jar evaluate --evaluation [EVAL,...,EVAL] --metric [METRIC,...,METRIC] --neo4jHostname [NEO4J] --neo4jUsername [USERNAME] --neo4jPassword [PASSWORD] --dataset [DATASET] --trainset [TRAINSET] --testset [TESTSET] --output [OUTPUT] --params [PARAMS]

where *[EVAL]=(ALL|AUC|PRECISION)*, *[METRIC]=(ALL|NRA|TA|NTA|CN|JACCARD|SALTON|SORENSEN|HPI|HDI|LHN1|PA|AA|RA)*.

Notice that you can run previous commands, also without logging into the EC2 instance. For example, if you want to run the command [MY_COMMAND arg_1 ... arg_N], you need to run:

    $crimegraph-monitor> vagrant ssh -c "[MY_COMMAND arg_1 ... arg_N]"


## Shortcuts

Check:

    $crimegraph-monitor> vagrant ssh -c "sudo /opt/crimegraph-monitor/check.sh"

Datagen:

    $crimegraph-monitor> vagrant ssh -c "sudo /opt/crimegraph-monitor/datagen.sh [DATASET] [DATASETS_DIR] [TEST_RATIO,...,TEST_RATIO]"

Publish:

    $crimegraph-monitor> vagrant ssh -c "sudo /opt/crimegraph-monitor/publish.sh [DATASET]"

Evaluate:

    $crimegraph-monitor> vagrant ssh -c "sudo /opt/crimegraph-monitor/evaluate.sh [EVAL,..,EVAL] [METRIC,..,METRIC] [DATASET] [TRAINSET] [TESTSET] [PARAMS]"

where *[EVAL]=(ALL|AUC|PRECISION)*, *[METRIC]=(ALL|NRA|TA|NTA|CN|JACCARD|SALTON|SORENSEN|HPI|HDI|LHN1|PA|AA|RA)*.


## Deploy
Run the provisioning with Vagrant

    $crimegraph-monitor> vagrant up

To destroy the EC2 instance:

    $crimegraph-monitor> vagrant destroy


## Datasets
* citations: Arxiv citations
  ref: http://snap.stanford.edu/data/cit-HepPh.html
  original: numlinks=421579, numnodes=34548
  original_lcc: numlinks=146838, numnodes=26697
  500_lcc: numlinks=153, numnodes=117
  1000_lcc: numlinks=446, numnodes=388
  5000_lcc: numlinks=1111, numnodes=891
  10000_lcc: numlinks=2197, numnodes=1550

* facebook:
  ref: http://snap.stanford.edu/data/higgs-twitter.html
  original: numlinks=88234, numnodes=4039
  original_lcc: numlinks=88234, numnodes=4039
  500_lcc: numlinks=500, numnodes=348
  1000_lcc: numlinks=1000, numnodes=351
  5000_lcc: numlinks=5000, numnodes=1724
  10000_lcc: numlinks=9568, numnodes=1831

* google:
  ref: http://snap.stanford.edu/data/higgs-twitter.html
  original: numlinks=5105035, numnodes=875713
  original_lcc:
  500_lcc: numlinks=103, numnodes=53
  1000_lcc: numlinks=103, numnodes=53
  5000_lcc: numlinks=111, numnodes=84
  10000_lcc: numlinks=1650, numnodes=798


* internet  (Autonomous Systems by Skitter) internet topology
  ref: http://snap.stanford.edu/data/as-skitter.html
  original:
  original_lcc:
  500_lcc: numlinks=500, numnodes=501
  1000_lcc: numlinks=1000, numnodes=967
  5000_lcc: numlinks=5000, numnodes=4967
  10000_lcc: numlinks=10000, numnodes=8721


* twitter
  ref: http://snap.stanford.edu/data/higgs-twitter.html
  original: numlinks=511473, numnodes=304691
  original_lcc: numlinks=43525, numnodes=47434
  500_lcc: numlinks=4, numnodes=7
  1000_lcc: numlinks=20, numnodes=28
  5000_lcc: numlinks=378, numnodes=413
  10000_lcc: numlinks=799, numnodes=909



## Authors
Giacomo Marciani, [gmarciani@acm.org](mailto:gmarciani@acm.org)

Michele Porretta, [mporretta@acm.org](mailto:mporretta@acm.org)


## References
Giacomo Marciani, Michele Porretta. 2017. *Crimegraph*. Series. Organization, Country [Read here](https://gmarciani.com)


## License
The project is released under the [MIT License](https://opensource.org/licenses/MIT).
