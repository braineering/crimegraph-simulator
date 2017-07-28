/*
  The MIT License (MIT)

  Copyright (c) 2016 Giacomo Marciani and Michele Porretta

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:


  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.


  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
 */

package com.acmutv.crimegraph_monitor.ui;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * This class realizes the command line interface options of the whole application.
 * The class is implemented as a singleton.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 * @see Option
 */
public class BaseOptions extends Options {

  private static final long serialVersionUID = 1L;

  /**
   * The CLI description for the option `version`.
   */
  private static final String DESCRIPTION_VERSION = "Show app version.";

  /**
   * The CLI description for the option `help`.
   */
  private static final String DESCRIPTION_HELP = "Show helper.";

  /**
   * The CLI description for the option `output`.
   */
  private static final String DESCRIPTION_OUTPUT = "Absolute path of the output file.";

  /**
   * The CLI description for the option `numNodes`.
   */
  private static final String DESCRIPTION_NUM_NODES = "Number of nodes";

  /**
   * The CLI description for the option `numLinks`.
   */
  private static final String DESCRIPTION_NUM_LINKS = "Number of links";

  /**
   * The CLI description for the option `minWeight`.
   */
  private static final String DESCRIPTION_MIN_WEIGHT = "Minimum amount of weight.";

  /**
   * The CLI description for the option `maxWeight`.
   */
  private static final String DESCRIPTION_MAX_WEIGHT = "Maximum amount of weight.";

  /**
   * The CLI description for the option `dataset`.
   */
  private static final String DESCRIPTION_DATASET = "Absolute path of the dataset.";

  /**
   * The CLI description for the option `trainset`.
   */
  private static final String DESCRIPTION_TRAINSET = "Absolute path of the trainset.";

  /**
   * The CLI description for the option `testset`.
   */
  private static final String DESCRIPTION_TESTSET = "Absolute path of the testset.";

  /**
   * The CLI description for the option `testRatio`.
   */
  private static final String DESCRIPTION_TEST_RATIO = "Test ratio in (0.0,1.0).";

  /**
   * The CLI description for the option `kafkaTopic`.
   */
  private static final String DESCRIPTION_KAFKA_TOPIC = "Kafka topic.";

  /**
   * The CLI description for the option `kafkaBootstrap`.
   */
  private static final String DESCRIPTION_KAFKA_BROKER = "Kafka broker server address.";

  /**
   * The CLI description for the option `kafkaZookeper`.
   */
  private static final String DESCRIPTION_KAFKA_ZOOKEPER = "Kafka Zookeper server address.";

  /**
   * The CLI description for the option `kafkaGroup`.
   */
  private static final String DESCRIPTION_KAFKA_GROUP = "Kafka group id.";

  /**
   * The CLI description for the option `neo4jHostname`.
   */
  private static final String DESCRIPTION_NEO4J_HOSTNAME = "Neo4J address.";

  /**
   * The CLI description for the option `neo4jUsername`.
   */
  private static final String DESCRIPTION_NEO4J_USERNAME = "Neo4J username.";

  /**
   * The CLI description for the option `neo4jPassword`.
   */
  private static final String DESCRIPTION_NEO4J_PASSWORD = "Neo4J password.";

  /**
   * The CLI description for the option `timeout`.
   */
  private static final String DESCRIPTION_TIMEOUT = "Timeout in seconds.";

  /**
   * The CLI description for the option `rank`.
   */
  private static final String DESCRIPTION_RANK = "Rank size.";

  /**
   * The CLI description for the option `metric`.
   */
  private static final String DESCRIPTION_METRIC = "Metric name.";

  /**
   * The CLI description for the option `evaluation`.
   */
  private static final String DESCRIPTION_EVALUATION = "Evaluation name.";

  /**
   * The CLI description for the option `mining`.
   */
  private static final String DESCRIPTION_MINING = "Mining name.";

  /**
   * The CLI description for the option `params`.
   */
  private static final String DESCRIPTION_PARAMS = "List of parameters (eg. param1=1,param2=2...).";

  /**
   * The singleton instance of {@link BaseOptions}.
   */
  private static BaseOptions instance;

  /**
   * Returns the singleton instance of {@link BaseOptions}.
   * @return the singleton.
   */
  public static BaseOptions getInstance() {
    if (instance == null) {
      instance = new BaseOptions();
    }
    return instance;
  }

  /**
   * Constructs the singleton of {@link BaseOptions}.
   */
  private BaseOptions() {
    Option version = this.optVersion();
    Option help = this.optHelp();
    Option output = this.optOutput();
    Option numNodes = this.optNumNodes();
    Option numLinks= this.optNumLinks();
    Option minWeight= this.optMinWeight();
    Option maxWeight= this.optMaxWeight();
    Option dataset = this.optDataset();
    Option trainset = this.optTrainset();
    Option testset = this.optTestset();
    Option testRatio = this.optTestRatio();
    Option kafkaTopic = this.optKafkaTopic();
    Option kafkaBootstrap = this.optKafkaBootstrap();
    Option kafkaZookeper = this.optKafkaZookeper();
    Option kafkaGroup = this.optKafkaGroup();
    Option neo4jHostname = this.optNeo4jHostname();
    Option neo4jUsername = this.optNeo4JUsername();
    Option neo4jPassword = this.optNeo4JPassword();
    Option timeout = this.optTimeout();
    Option rank = this.optRank();
    Option metric = this.optMetric();
    Option evaluation = this.optEvaluation();
    Option mining = this.optMining();
    Option params = this.optParams();

    super.addOption(version);
    super.addOption(help);
    super.addOption(output);
    super.addOption(numNodes);
    super.addOption(numLinks);
    super.addOption(minWeight);
    super.addOption(maxWeight);
    super.addOption(dataset);
    super.addOption(trainset);
    super.addOption(testset);
    super.addOption(testRatio);
    super.addOption(kafkaTopic);
    super.addOption(kafkaBootstrap);
    super.addOption(kafkaZookeper);
    super.addOption(kafkaGroup);
    super.addOption(neo4jHostname);
    super.addOption(neo4jUsername);
    super.addOption(neo4jPassword);
    super.addOption(timeout);
    super.addOption(rank);
    super.addOption(metric);
    super.addOption(evaluation);
    super.addOption(mining);
    super.addOption(params);
  }

  /**
   * Builds the option `version`.
   * @return the option.
   */
  private Option optVersion() {
    return Option.builder("v")
        .longOpt("version")
        .desc(DESCRIPTION_VERSION)
        .required(false)
        .hasArg(false)
        .build();
  }

  /**
   * Builds the option `help`.
   * @return the option.
   */
  private Option optHelp() {
    return Option.builder("h")
        .longOpt("help")
        .desc(DESCRIPTION_HELP)
        .required(false)
        .hasArg(false)
        .build();
  }

  /**
   * Builds the option `output`.
   * @return the option.
   */
  private Option optOutput() {
    return Option.builder()
        .longOpt("output")
        .desc(DESCRIPTION_OUTPUT)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("PATH")
        .build();
  }

  /**
   * Builds the option `numNodes`.
   * @return the option.
   */
  private Option optNumNodes() {
    return Option.builder()
        .longOpt("numNodes")
        .desc(DESCRIPTION_NUM_NODES)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("INTEGER")
        .build();
  }

  /**
   * Builds the option `numLinks`.
   * @return the option.
   */
  private Option optNumLinks() {
    return Option.builder()
        .longOpt("numLinks")
        .desc(DESCRIPTION_NUM_LINKS)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("INTEGER")
        .build();
  }

  /**
   * Builds the option `minWeight`.
   * @return the option.
   */
  private Option optMinWeight() {
    return Option.builder()
        .longOpt("minWeight")
        .desc(DESCRIPTION_MIN_WEIGHT)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("DOUBLE")
        .build();
  }

  /**
   * Builds the option `maxWeight`.
   * @return the option.
   */
  private Option optMaxWeight() {
    return Option.builder()
        .longOpt("maxWeight")
        .desc(DESCRIPTION_MAX_WEIGHT)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("DOUBLE")
        .build();
  }

  /**
   * Builds the option `dataset`.
   * @return the option.
   */
  private Option optDataset() {
    return Option.builder()
        .longOpt("dataset")
        .desc(DESCRIPTION_DATASET)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("PATH")
        .build();
  }

  /**
   * Builds the option `trainset`.
   * @return the option.
   */
  private Option optTrainset() {
    return Option.builder()
        .longOpt("trainset")
        .desc(DESCRIPTION_TRAINSET)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("PATH")
        .build();
  }

  /**
   * Builds the option `testset`.
   * @return the option.
   */
  private Option optTestset() {
    return Option.builder()
        .longOpt("testset")
        .desc(DESCRIPTION_TESTSET)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("PATH")
        .build();
  }

  /**
   * Builds the option `testRatio`.
   * @return the option.
   */
  private Option optTestRatio() {
    return Option.builder()
        .longOpt("testRatio")
        .desc(DESCRIPTION_TEST_RATIO)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("DOUBLE")
        .build();
  }

  /**
   * Builds the option `kafkaTopic`.
   * @return the option.
   */
  private Option optKafkaTopic() {
    return Option.builder()
        .longOpt("kafkaTopic")
        .desc(DESCRIPTION_KAFKA_TOPIC)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("PLAINTEXT")
        .build();
  }

  /**
   * Builds the option `kafkaBootstrap`.
   * @return the option.
   */
  private Option optKafkaBootstrap() {
    return Option.builder()
        .longOpt("kafkaBroker")
        .desc(DESCRIPTION_KAFKA_BROKER)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("ADDRESS:PORT")
        .build();
  }

  /**
   * Builds the option `kafkaZookeper`.
   * @return the option.
   */
  private Option optKafkaZookeper() {
    return Option.builder()
        .longOpt("kafkaZookeper")
        .desc(DESCRIPTION_KAFKA_ZOOKEPER)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("ADDRESS:PORT")
        .build();
  }

  /**
   * Builds the option `kafkaGroup`.
   * @return the option.
   */
  private Option optKafkaGroup() {
    return Option.builder()
        .longOpt("kafkaGroup")
        .desc(DESCRIPTION_KAFKA_GROUP)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("PLAINTEXT")
        .build();
  }

  /**
   * Builds the option `neo4jHostname`.
   * @return the option.
   */
  private Option optNeo4jHostname() {
    return Option.builder()
        .longOpt("neo4jHostname")
        .desc(DESCRIPTION_NEO4J_HOSTNAME)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("ADDRESS:PORT")
        .build();
  }

  /**
   * Builds the option `neo4jUsername`.
   * @return the option.
   */
  private Option optNeo4JUsername() {
    return Option.builder()
        .longOpt("neo4jUsername")
        .desc(DESCRIPTION_NEO4J_USERNAME)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("PLAINTEXT")
        .build();
  }

  /**
   * Builds the option `neo4jPassword`.
   * @return the option.
   */
  private Option optNeo4JPassword() {
    return Option.builder()
        .longOpt("neo4jPassword")
        .desc(DESCRIPTION_NEO4J_PASSWORD)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("PLAINTEXT")
        .build();
  }

  /**
   * Builds the option `timeout`.
   * @return the option.
   */
  private Option optTimeout() {
    return Option.builder()
        .longOpt("timeout")
        .desc(DESCRIPTION_TIMEOUT)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("SECONDS")
        .build();
  }

  /**
   * Builds the option `rank`.
   * @return the option.
   */
  private Option optRank() {
    return Option.builder()
        .longOpt("rank")
        .desc(DESCRIPTION_RANK)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("INTEGER")
        .build();
  }

  /**
   * Builds the option `metric`.
   * @return the option.
   */
  private Option optMetric() {
    return Option.builder()
        .longOpt("metric")
        .desc(DESCRIPTION_METRIC)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("METRIC_NAME")
        .build();
  }

  /**
   * Builds the option `evaluation`.
   * @return the option.
   */
  private Option optEvaluation() {
    return Option.builder()
        .longOpt("evaluation")
        .desc(DESCRIPTION_EVALUATION)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("EVALUATION_NAME")
        .build();
  }

  /**
   * Builds the option `mining`.
   * @return the option.
   */
  private Option optMining() {
    return Option.builder()
        .longOpt("mining")
        .desc(DESCRIPTION_MINING)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("MINING_NAME")
        .build();
  }

  /**
   * Builds the option `params`.
   * @return the option.
   */
  private Option optParams() {
    return Option.builder()
        .longOpt("params")
        .desc(DESCRIPTION_PARAMS)
        .required(false)
        .hasArg(true)
        .numberOfArgs(1)
        .argName("PARAMS")
        .build();
  }

}
