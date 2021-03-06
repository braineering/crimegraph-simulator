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

import com.acmutv.crimegraph_monitor.config.AppManifest;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * This class realizes the Command Line Interface services.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 */
public class CliService {

  private static final Logger LOGGER = LogManager.getLogger(CliService.class);

  /**
   * Handles the command line arguments passed to the main method, according to {@link BaseOptions}.
   * Loads the configuration and returns the list of arguments.
   * @param argv the command line arguments passed to the main method.
   * @return the arguments list.
   * @see CommandLine
   * @throws IllegalArgumentException when there are errors in arguments.
   */
  public static Properties handleArguments(String[] argv) throws IllegalArgumentException {
    LOGGER.trace("argv={}", Arrays.asList(argv));
    CommandLine cmd = getCommandLine(argv);
    Properties props = new Properties();

    /* OPTION: version */
    if (cmd.hasOption("version")) {
      LOGGER.trace("Detected option VERSION");
      printVersion();
      System.exit(0);
    }

    /* OPTION: help */
    if (cmd.hasOption("help")) {
      LOGGER.trace("Detected option HELP");
      printHelp();
      System.exit(0);
    }

    /* option: output */
    if (cmd.hasOption("output")) {
      final String output = cmd.getOptionValue("output");
      props.setProperty("output", output);
    }

    /* option: numNodes */
    if (cmd.hasOption("numNodes")) {
      final String numNodes = cmd.getOptionValue("numNodes");
      props.setProperty("numNodes", numNodes);
    }

    /* option: numLinks */
    if (cmd.hasOption("numLinks")) {
      final String numLinks = cmd.getOptionValue("numLinks");
      props.setProperty("numLinks", numLinks);
    }

    /* option: minWeight */
    if (cmd.hasOption("minWeight")) {
      final String minWeight = cmd.getOptionValue("minWeight");
      props.setProperty("minWeight", minWeight);
    }

    /* option: maxWeight */
    if (cmd.hasOption("maxWeight")) {
      final String maxWeight = cmd.getOptionValue("maxWeight");
      props.setProperty("maxWeight", maxWeight);
    }

    /* option: dataset */
    if (cmd.hasOption("dataset")) {
      final String dataset = cmd.getOptionValue("dataset");
      props.setProperty("dataset", dataset);
    }

    /* option: trainset */
    if (cmd.hasOption("trainset")) {
      final String trainset = cmd.getOptionValue("trainset");
      props.setProperty("trainset", trainset);
    }

    /* option: testset */
    if (cmd.hasOption("testset")) {
      final String testset = cmd.getOptionValue("testset");
      props.setProperty("testset", testset);
    }

    /* option: testRatio */
    if (cmd.hasOption("testRatio")) {
      final String testRatio = cmd.getOptionValue("testRatio");
      props.setProperty("testRatio", testRatio);
    }

    /* option: kafkaTopic */
    if (cmd.hasOption("kafkaTopic")) {
      final String kafkaTopic = cmd.getOptionValue("kafkaTopic");
      props.setProperty("kafkaTopic", kafkaTopic);
    }

    /* option: kafkaBroker */
    if (cmd.hasOption("kafkaBroker")) {
      final String kafkaBootstrap = cmd.getOptionValue("kafkaBroker");
      props.setProperty("kafkaBroker", kafkaBootstrap);
    }

    /* option: kafkaZookeper */
    if (cmd.hasOption("kafkaZookeper")) {
      final String kafkaZookeper = cmd.getOptionValue("kafkaZookeper");
      props.setProperty("kafkaZookeper", kafkaZookeper);
    }

    /* option: kafkaGroup */
    if (cmd.hasOption("kafkaGroup")) {
      final String kafkaGroup = cmd.getOptionValue("kafkaGroup");
      props.setProperty("kafkaGroup", kafkaGroup);
    }

    /* option: neo4jHostname */
    if (cmd.hasOption("neo4jHostname")) {
      final String neo4jHostname = cmd.getOptionValue("neo4jHostname");
      props.setProperty("neo4jHostname", neo4jHostname);
    }

    /* option: neo4jUsername */
    if (cmd.hasOption("neo4jUsername")) {
      final String neo4jUsername = cmd.getOptionValue("neo4jUsername");
      props.setProperty("neo4jUsername", neo4jUsername);
    }

    /* option: neo4jPassword */
    if (cmd.hasOption("neo4jPassword")) {
      final String neo4jPassword = cmd.getOptionValue("neo4jPassword");
      props.setProperty("neo4jPassword", neo4jPassword);
    }

    /* option: timeout */
    if (cmd.hasOption("timeout")) {
      final String timeout = cmd.getOptionValue("timeout");
      props.setProperty("timeout", timeout);
    }

    /* option: rank */
    if (cmd.hasOption("rank")) {
      final String rank = cmd.getOptionValue("rank");
      props.setProperty("rank", rank);
    }

    /* option: metric */
    if (cmd.hasOption("metric")) {
      final String metric = cmd.getOptionValue("metric");
      props.setProperty("metric", metric);
    }

    /* option: evaluation */
    if (cmd.hasOption("evaluation")) {
      final String evaluation = cmd.getOptionValue("evaluation");
      props.setProperty("evaluation", evaluation);
    }

    /* option: mining */
    if (cmd.hasOption("mining")) {
      final String mining = cmd.getOptionValue("mining");
      props.setProperty("mining", mining);
    }

    /* option: params */
    if (cmd.hasOption("params")) {
      final String params = cmd.getOptionValue("params");
      props.setProperty("params", params);
    }

    List<String> arguments = cmd.getArgList();
    if (!arguments.isEmpty()) {
      String command = arguments.remove(0);
      props.setProperty("_command", command);
      props.setProperty("_other", arguments.toString());
    }

    LOGGER.debug("Configuration loaded: {}", props);

    return props;
  }

  /**
   * Returns command line options/arguments parsing utility.
   * @param argv The command line arguments passed to the main method.
   * @return The command line options/arguments parsing utility.
   * @see CommandLineParser
   * @see CommandLine
   */
  private static CommandLine getCommandLine(String argv[]) {
    CommandLineParser cmdParser = new DefaultParser();
    CommandLine cmd = null;

    try {
      cmd = cmdParser.parse(BaseOptions.getInstance(), argv);
    } catch (ParseException e) {
      LOGGER.error(e.getMessage());
      printHelp();
      System.exit(1);
    }

    return cmd;
  }

  /**
   * Prints the application version.
   */
  private static void printVersion() {
    System.out.format("%s version %s\n",
        AppManifest.APP_NAME,
        AppManifest.APP_VERSION);
  }

  /**
   * Prints the application command line helper.
   * @see Option
   * @see Options
   */
  public static void printHelp() {
    System.out.format("%s version %s\nTeam: %s (%s)\n\n%s\n\n",
        AppManifest.APP_NAME,
        AppManifest.APP_VERSION,
        AppManifest.APP_TEAM_NAME,
        AppManifest.APP_TEAM_URL,
        AppManifest.APP_DESCRIPTION.replaceAll("(.{80})", "$1\n"));
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(AppManifest.APP_NAME, BaseOptions.getInstance(), true);
  }

  /**
   * Print the splash message to {@code stdout}.
   */
  public static void printSplash() {
    System.out.println();
    System.out.println("#=========================================================================");
    System.out.println("# CRIMEGRAPH MONITOR");
    System.out.println("#=========================================================================");
  }

}
