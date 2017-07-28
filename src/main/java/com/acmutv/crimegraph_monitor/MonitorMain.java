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

package com.acmutv.crimegraph_monitor;

import com.acmutv.crimegraph_monitor.core.CoreController;
import com.acmutv.crimegraph_monitor.core.db.DbConfiguration;
import com.acmutv.crimegraph_monitor.core.evaluation.EvaluationType;
import com.acmutv.crimegraph_monitor.core.graph.CustomWeightedEdge;
import com.acmutv.crimegraph_monitor.core.graph.GraphController;
import com.acmutv.crimegraph_monitor.core.metric.MetricType;
import com.acmutv.crimegraph_monitor.core.link.Link;
import com.acmutv.crimegraph_monitor.core.mining.MiningType;
import com.acmutv.crimegraph_monitor.ui.CliService;
import com.acmutv.crimegraph_monitor.ui.Command;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.UndirectedGraph;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static com.acmutv.crimegraph_monitor.ui.Command.*;

/**
 * The app word-point for application.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 */
public class MonitorMain {

  private static final Logger LOGGER = LogManager.getLogger(MonitorMain.class);

  /**
   * The app main method, executed when the program is launched.
   * @param args the command line arguments.
   */
  public static void main(String[] args) {

    CliService.printSplash();

    Properties props = CliService.handleArguments(args);

    Command command;
    try {
      command = Command.fromString(props.getProperty("_command"));
    } catch (IllegalArgumentException exc) {
      LOGGER.warn(exc.getMessage());
      command = null;
    }

    if (ANALYZE_DATASET.equals(command)) {
      final Path dataset = Paths.get(props.getProperty("dataset")).toAbsolutePath();
      LOGGER.info("Analyzing dataset {}", dataset);
      Properties analysis = null;
      try {
        List<Link> links = CoreController.readLinks(dataset);
        UndirectedGraph<Long, CustomWeightedEdge> graph = GraphController.asGraph(links);
        analysis = GraphController.analyzeGraph(graph);
      } catch (IOException exc) {
        LOGGER.error(exc.getMessage());
        System.exit(1);
      }
      LOGGER.info("Dataset analysis completed");
      LOGGER.info("------------------------------------");
      analysis.forEach((k,v) -> LOGGER.info("{} :: {}", k, v));
      LOGGER.info("------------------------------------");
    } else if (DATAGEN_RND.equals(command)) {
      final Path output = Paths.get(props.getProperty("output")).toAbsolutePath();
      final int numNodes = Integer.valueOf(props.getProperty("numNodes"));
      final int numLinks = Integer.valueOf(props.getProperty("numLinks"));
      final double minWeight = Double.valueOf(props.getProperty("minWeight"));
      final double maxWeight = Double.valueOf(props.getProperty("maxWeight"));
      LOGGER.info("Generating random dataset {} with numNodes {}, numLinks {}, minWeight {}, maxWeight {}",
          output, numNodes, numLinks, minWeight, maxWeight);
      try {
        List<Link> data = CoreController.randomSimple(numNodes, numLinks, minWeight, maxWeight);
        CoreController.writeDataset(output, data);
      } catch (IOException exc) {
        LOGGER.error(exc.getMessage());
        System.exit(1);
      }
      LOGGER.info("Generated random dataset {}", output);
    } else if (DATAGEN_LCC.equals(command)) {
      final Path dataset = Paths.get(props.getProperty("dataset")).toAbsolutePath();
      final Path output = Paths.get(props.getProperty("output")).toAbsolutePath();
      LOGGER.info("Generating dataset of largest connected component from dataset {} to output {}",
          dataset, output);
      try {
        List<Link> links = CoreController.readLinks(dataset);
        UndirectedGraph<Long, CustomWeightedEdge> graph = GraphController.asGraph(links);
        List<Link> data = GraphController.datagenLargestConnectedComponent(graph);
        CoreController.writeDataset(output, data);
      } catch (IOException exc) {
        LOGGER.error(exc.getMessage());
        System.exit(1);
      }
      LOGGER.info("Generated dataset of largest connected component {}", output);
    }else if (TRAINTEST.equals(command)) {
      final MiningType mining = MiningType.fromString(props.getProperty("mining"));
      final Path dataset = Paths.get(props.getProperty("dataset")).toAbsolutePath();
      final Path trainset = Paths.get(props.getProperty("trainset")).toAbsolutePath();
      final Path testset = Paths.get(props.getProperty("testset")).toAbsolutePath();
      final Double testRatio = Double.valueOf(props.getProperty("testRatio"));
      LOGGER.info("Generating training/test sets for {} from dataset {} with testRatio {}",
          mining, dataset, testRatio);
      try {
        CoreController.traintest(mining, dataset, trainset, testset, testRatio);
      } catch (IOException exc) {
        LOGGER.error(exc.getMessage());
        System.exit(1);
      }
      LOGGER.info("Generated trainset {} for {} from {} with testRatio {}",
          trainset, mining, dataset, testRatio);
      LOGGER.info("Generated testset {} for {} from {} with testRatio {}",
          testset, mining, dataset, testRatio);
    } else if (CHECK.equals(command)) {
      final String kafkaBroker = props.getProperty("kafkaBroker");
      final String kafkaTopic = props.getProperty("kafkaTopic");
      LOGGER.info("Checking Kafka with broker {} and topic {}", kafkaBroker, kafkaTopic);
      boolean check = CoreController.checkKafka(kafkaBroker, kafkaTopic);
      LOGGER.info("Kafka check: {}", check);
    } else if (PUBLISH.equals(command)) {
      final String kafkaBroker = props.getProperty("kafkaBroker");
      final String kafkaTopic = props.getProperty("kafkaTopic");
      final Path dataset = Paths.get(props.getProperty("dataset")).toAbsolutePath();
      LOGGER.info("Publishing to Kafka broker {} with topic {}, with dataset {}", kafkaBroker, kafkaTopic, dataset);
      try {
        CoreController.publish(kafkaBroker, kafkaTopic, dataset);
      } catch (IOException exc) {
        LOGGER.error(exc.getMessage());
        System.exit(1);
      }
    } else if (SAVE.equals(command)) {
      final DbConfiguration dbconfig = new DbConfiguration(
          props.getProperty("neo4jHostname"),
          props.getProperty("neo4jUsername"),
          props.getProperty("neo4jPassword")
      );
      final Path dataset = Paths.get(props.getProperty("dataset")).toAbsolutePath();
      LOGGER.info("Saving to Neo4J {} with dataset {}", dbconfig, dataset);
      try {
        CoreController.save(dbconfig, dataset);
      } catch (IOException exc) {
        LOGGER.error(exc.getMessage());
        System.exit(1);
      }
    } else if (CHECK_DATASET_DB.equals(command)) {
      final DbConfiguration dbconfig = new DbConfiguration(
          props.getProperty("neo4jHostname"),
          props.getProperty("neo4jUsername"),
          props.getProperty("neo4jPassword")
      );
      final Path dataset = Paths.get(props.getProperty("dataset")).toAbsolutePath();
      final long timeout = Long.valueOf(props.getProperty("timeout"));
      LOGGER.info("Checking dataset {} with timeout {} seconds against db {}", dataset, timeout, dbconfig);
      try {
        CoreController.checkDatasetOnDb(dbconfig, dataset, timeout);
      } catch (IOException exc) {
        LOGGER.error(exc.getMessage());
      }
      LOGGER.info("Check finished");
    } else if (WAIT_STABILITY_DB.equals(command)) {
      final DbConfiguration dbconfig = new DbConfiguration(
          props.getProperty("neo4jHostname"),
          props.getProperty("neo4jUsername"),
          props.getProperty("neo4jPassword")
      );
      final long timeout = Long.valueOf(props.getProperty("timeout"));
      LOGGER.info("Wait stability db {} with timeout {}", dbconfig, timeout);
      CoreController.waitStabilityDb(dbconfig, timeout);
      LOGGER.info("Check finished");
    } else if (EVALUATE.equals(command)) {
      final DbConfiguration dbconfig = new DbConfiguration(
          props.getProperty("neo4jHostname"),
          props.getProperty("neo4jUsername"),
          props.getProperty("neo4jPassword")
      );
      final List<EvaluationType> evaluations = EvaluationType.fromList(props.getProperty("evaluation"));
      final List<MetricType> metrics = MetricType.fromList(props.getProperty("metric"));
      final Path dataset = Paths.get(props.getProperty("dataset")).toAbsolutePath();
      final Path trainset = Paths.get(props.getProperty("trainset")).toAbsolutePath();
      final Path testset = Paths.get(props.getProperty("testset")).toAbsolutePath();
      final Properties params = new Properties();
      try {
        String optParams = props.getProperty("params", "").replace(',','\n');
        params.load(new StringReader(optParams));
      } catch (IOException exc) {
        LOGGER.warn("Cannot parse params: {}", exc.getMessage());
      }
      final Path outputDir = Paths.get(props.getProperty("output")).toAbsolutePath();

      if (outputDir != null && !Files.isDirectory(outputDir)) {
        try {
          Files.createDirectories(outputDir);
        } catch (IOException exc) {
          LOGGER.error("cannot create directory: {}", outputDir);
          System.exit(1);
        }
      }

      LOGGER.info("Evaluating {} for {} on Neo4J instance {}, with dataset {}, trainset {} testset {} output {} and params {}",
          evaluations, metrics, dbconfig, dataset, trainset, testset, outputDir, params);

      for (EvaluationType evaluation : evaluations) {
        for (MetricType metric : metrics) {
          final Path output = (outputDir != null) ? Paths.get(outputDir.toString(), String.format("%s_%s_%s.out",
              FilenameUtils.getBaseName(trainset.toString()), evaluation, metric)) : null;
          LOGGER.info("Evaluating {} for {} on Neo4J instance {}, with dataset {}, trainset {} testset {} output {} and params {}",
              evaluation, metric, dbconfig, dataset, trainset, testset, output, params);
          Properties result = null;
          try {
            result = CoreController.evaluate(evaluation, metric, dbconfig, dataset, trainset, testset, params);
          } catch (IOException exc) {
            LOGGER.error(exc.getMessage());
            System.exit(1);
          }

          LOGGER.info("Evaluation Result: {} on {} with {} :: {}", evaluation, metric, trainset, result);

          if (output != null) {
            writeEvaluationResult(result, output);
          }
        }
      }
    } else {
      LOGGER.warn("Invalid command {}", command);
      System.exit(1);
    }
  }

  private static void writeEvaluationResult(Properties analysis, Path output) {
    String evaluation = analysis.getProperty("evaluation");
    String metric = analysis.getProperty("metric");
    String result = analysis.getProperty("result");

    StringJoiner sj = new StringJoiner(" | ");
    for (String p : analysis.stringPropertyNames().stream().sorted().collect(Collectors.toList())) {
      if (p.equalsIgnoreCase("evaluation") ||
          p.equalsIgnoreCase("metric") ||
          p.equalsIgnoreCase("result")) continue;
      String v = analysis.getProperty(p);
      sj.add(p + ":" + v);
    }
    String details = sj.toString();

    String str = String.format("%s(%s) (%s) : %s", evaluation, metric, details, result);

    if (output != null) {
      try {
        if (!Files.isDirectory(output.getParent())) {
          Files.createDirectories(output.getParent());
        }
        try (BufferedWriter writer = Files.newBufferedWriter(output, Charset.defaultCharset(), StandardOpenOption.CREATE)) {
          writer.append(str).append("\n");
        }
      } catch (IOException exc) {
        LOGGER.warn(exc.getMessage());
      }
    }
  }

}
