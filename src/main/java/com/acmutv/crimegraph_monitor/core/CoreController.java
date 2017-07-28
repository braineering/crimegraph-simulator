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

package com.acmutv.crimegraph_monitor.core;

import com.acmutv.crimegraph_monitor.core.datagen.DatagenDetection;
import com.acmutv.crimegraph_monitor.core.datagen.DatagenPrediction;
import com.acmutv.crimegraph_monitor.core.db.DbConfiguration;
import com.acmutv.crimegraph_monitor.core.db.Neo4JManager;
import com.acmutv.crimegraph_monitor.core.evaluation.EvaluationController;
import com.acmutv.crimegraph_monitor.core.evaluation.EvaluationType;
import com.acmutv.crimegraph_monitor.core.kafka.StringKafkaConsumer;
import com.acmutv.crimegraph_monitor.core.kafka.StringKafkaProducer;
import com.acmutv.crimegraph_monitor.core.link.Links;
import com.acmutv.crimegraph_monitor.core.metric.MetricType;
import com.acmutv.crimegraph_monitor.core.link.Link;
import com.acmutv.crimegraph_monitor.core.mining.MiningType;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.AccessMode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

import static com.acmutv.crimegraph_monitor.core.evaluation.EvaluationType.AUC;
import static com.acmutv.crimegraph_monitor.core.evaluation.EvaluationType.PRECISION;

/**
 * This class realizes the core business logic.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 */
public class CoreController {

  private static final Logger LOGGER = LogManager.getLogger(CoreController.class);

  /**
   * Executes the evaluation.
   * @param dbconf the database configuration.
   * @param metric the metric to evaluate.
   * @param dataset the dataset.
   * @param trainset the trainset.
   * @param testset the testset.
   * @param params evaluation parameters
   * @return the PRECISION.
   * @throws IOException when datasets cannot be read.
   */
  public static Properties evaluate(EvaluationType evaluation, MetricType metric, DbConfiguration dbconf, Path dataset, Path trainset, Path testset, Properties params) throws IOException {
    Properties result = new Properties();
    switch (evaluation) {
      case AUC:
        result = EvaluationController.auc(dbconf, metric, dataset, trainset, testset);
        break;
      case PRECISION:
        int rank = Integer.valueOf(params.getProperty("rank", "3"));
        result = EvaluationController.precision(dbconf, metric, dataset, trainset, testset, rank);
        break;
      default: break;
    }
    return result;
  }

  /**
   * Publishes the {@code dataset} to {@code kafkaBroker} with {@code topic}.
   * @param kafkaBroker the Kafka broker address and port.
   * @param kafkaTopic the topic.
   * @param dataset the path of the dataset.
   * @throws IOException when datasets cannot be read or broker cannot be contacted.
   */
  public static void publish(String kafkaBroker, String kafkaTopic, Path dataset) throws IOException {
    StringKafkaProducer producer = new StringKafkaProducer(kafkaBroker);

    long examined = 0;
    long total = Files.lines(dataset).count();
    double progress;
    double pace = 5.0;
    try (BufferedReader reader = Files.newBufferedReader(dataset)) {
      while (reader.ready()) {
        examined++;
        String line = reader.readLine();
        Link link;
        try {
          link = Link.valueOf(line);
        } catch (IllegalArgumentException exc) {
          LOGGER.warn("Malformed link: {}", line);
          continue;
        }
        producer.send(kafkaTopic, link);
        LOGGER.debug("Link published: {}", link);
        progress = 100.0 * ((double)examined / (double)total);
        if (progress % pace < 0.01) {
          LOGGER.info("progress (publish): {}% :: examined : {}/{} ", Math.round(progress), examined, total);
        }
      }
    }

    producer.close();
  }

  /**
   * Save the {@code dataset} to {@code dbconfig}.
   * @param dbconf the database configuration.
   * @param dataset the path of the dataset.
   * @throws IOException when datasets cannot be read or broker cannot be contacted.
   */
  public static void save(DbConfiguration dbconf, Path dataset) throws IOException {
    Driver driver = Neo4JManager.open(dbconf);
    Session session = driver.session(AccessMode.WRITE);

    long examined = 0;
    long total = Files.lines(dataset).count();
    double progress;
    double pace = 5.0;
    try (BufferedReader reader = Files.newBufferedReader(dataset)) {
      while (reader.ready()) {
        examined++;
        String line = reader.readLine();
        Link link;
        try {
          link = Link.valueOf(line);
        } catch (IllegalArgumentException exc) {
          LOGGER.warn("Malformed link: {}", line);
          continue;
        }
        Neo4JManager.save(session, link);
        progress = 100.0 * ((double)examined / (double)total);
        if (progress % pace < 0.01) {
          LOGGER.info("progress (save): {}% :: examined : {}/{} ", Math.round(progress), examined, total);
        }
      }
    } finally {
      Neo4JManager.close(session, driver);
    }
  }

  /**
   * Generates a simple random dataset.
   * @param numnodes the nunber of nodes.
   * @param numlinks the number of links.
   * @param minweight the minimum link weight.
   * @param maxweight the maximum link weight.
   * @return the list of links.
   * @throws IOException when {@code output} cannot be written.
   */
  public static List<Link> randomSimple(int numnodes, int numlinks, double minweight, double maxweight) throws IOException {
    LOGGER.trace("numnodes: {}; numlinks: {}; minweight: {}; maxweight: {}",
        numnodes, numlinks, minweight, maxweight);
    Random rnd = new Random();

    List<Link> data = new LinkedList<>();
    Map<Integer,Set<Integer>> pairs = new HashMap<>();

    for (int i = 0; i < numlinks; i++) {
      int x = 0;
      int y;
      do {
        y = rnd.nextInt(numnodes);
        if (y == 0) continue;
        x = rnd.nextInt(y);
      } while (y <= x || (pairs.containsKey(x) && pairs.get(x).contains(y)));

      double weight = rnd.nextDouble() * (maxweight - minweight) + minweight;
      Link link = new Link(x, y, weight);
      data.add(link);
      pairs.putIfAbsent(x, new HashSet<>());
      pairs.get(x).add(y);
    }

    return data;
  }

  /**
   * Generates a circular random dataset.
   * @param numnodes the nunber of nodes.
   * @param numlinks the number of links.
   * @param minweight the minimum link weight.
   * @param maxweight the maximum link weight.
   * @return the list of links.
   * @throws IOException when {@code output} cannot be written.
   */
  public static List<Link> randomCircular(int numnodes, int numlinks, double minweight, double maxweight) throws IOException {
    Random rnd = new Random();

    List<Link> data = new LinkedList<>();

    for (int i = 1; i < numnodes; i++) {
      double weight = rnd.nextDouble() * (maxweight - minweight) + minweight;
      Link link = new Link(i, i+1, weight);
      data.add(link);
    }

    return data;
  }

  /**
   * Generates training-set and test-set for detection with {@code dataset} and test {@code ratio}.
   * @param mining the mining type.
   * @param dataset the path of the original dataset.
   * @param training the path of the training set.
   * @param test the path of the test set.
   * @param ratio the test ratio.
   * @throws IOException when dataset cannot be read.
   */
  public static void traintest(MiningType mining, Path dataset, Path training, Path test, double ratio) throws IOException {
    Pair<List<Link>,List<Link>> sets = null;
    switch (mining) {
      case DETECTION: sets = DatagenDetection.datasets(CoreController.readLinks(dataset), ratio);break;
      case PREDICTION: sets = DatagenPrediction.datasets(CoreController.readLinks(dataset), ratio);break;
      default: break;
    }

    final List<Link> trainset = sets.getLeft();
    final List<Link> testset = sets.getRight();

    CoreController.writeDataset(training, trainset);
    CoreController.writeDataset(test, testset);
  }

  /**
   * Writes the dataset {@code datagen} into {@code path}.
   * @param path the file to write onto.
   * @param data the dataset to write.
   * @throws IOException when {@code path} cannot be written.
   */
  public static void writeDataset(Path path, List<Link> data) throws IOException {
    LOGGER.trace("Path: {}; Data size: {}", path, data.size());

    if (!Files.isDirectory(path.getParent())) {
      Files.createDirectories(path.getParent());
    }

    Charset charset = Charset.defaultCharset();
    try (BufferedWriter writer = Files.newBufferedWriter(path, charset, StandardOpenOption.CREATE)) {
      for (Link link : data) {
        try {
          writer.append(link.toString()).append("\n");
        } catch (IOException exc) {
          LOGGER.error(exc.getMessage());
        }
      }
    } catch (IOException exc) {
      LOGGER.error(exc.getMessage());
    }
  }

  /**
   * Checks the {@code dataset}  against the database specified in {@code dbconf}.
   * The method executes a checkDatasetOnDb once every {@code period} seconds.
   * The method blocks until tha dataset has been matched.
   * @param dbconf the database configuration.
   * @param dataset the path of the dataset.
   * @param period the checking period; if null, no wait.
   * @return true, if the dataset has been completely saved on DB; false, otherwise.
   */
  public static boolean checkDatasetOnDb(DbConfiguration dbconf, Path dataset, Long period) throws IOException {
    long total = Files.lines(dataset).count();
    Set<Link> missing = new HashSet<>();
    Driver driver = Neo4JManager.open(dbconf);
    Session session;

    LOGGER.info("Links to check: {} | dataset: {}", total, dataset);

    long examined = 0;
    long saved = 0;
    double progress;
    session = driver.session(AccessMode.READ);
    try (BufferedReader reader = Files.newBufferedReader(dataset)) {
      while (reader.ready()) {
        String line = reader.readLine();
        Link link;
        try {
          link = Link.valueOf(line);
        } catch (IllegalArgumentException exc) {
          LOGGER.warn("Malformed link: {}", line);
          continue;
        }
        LOGGER.debug("Check link: {}", link);
        boolean exists = Neo4JManager.exists(session, link);
        if (!exists)
          missing.add(link);
        else
          saved++;
        examined++;
        progress = 100.0 * ((double)examined / (double)total);
        if (progress % 5.0 == 0.0)
          LOGGER.trace("Check progress: {}% (examined: {}/{} | saved: {} | missing: {})", progress, examined, total, saved, missing.size());
      }
    }
    session.close();

    if (period != null) {
      while (!missing.isEmpty()) {
        session = driver.session();
        Iterator<Link> iter = missing.iterator();
        while (iter.hasNext()) {
          Link link = iter.next();
          boolean exists = Neo4JManager.exists(session, link);
          if (exists) iter.remove();
        }
        LOGGER.info("Check missing: {}/{}", missing.size(), total);
        try {
          Thread.sleep(period * 1000);
        } catch (InterruptedException ignored) { /* ignored */}
        session.close();
      }
    }

    driver.close();

    return missing.size() == 0;
  }

  /**
   * Checks Kafka.
   * @param kafkaBroker the Kafka broker.
   * @param kafkaTopic the Kafka topic.
   * @return true, if Kafka is up and running; false, otherwise.
   */
  public static boolean checkKafka(String kafkaBroker, String kafkaTopic) {
    Link testLink = new Link(1, 2, 1.0);

    StringKafkaProducer producer = new StringKafkaProducer(kafkaBroker);
    StringKafkaConsumer consumer = new StringKafkaConsumer(kafkaBroker, "testers");

    producer.send(kafkaTopic, testLink);

    Link receivedLink = consumer.receive(kafkaTopic);

    boolean check = testLink.equals(receivedLink);

    producer.close();

    return check;
  }

  /**
   * Waits the stability of DB, counting its links
   * @param dbconf the database configuration.
   * @param timeout the delayt between checks.
   */
  public static void waitStabilityDb(DbConfiguration dbconf, long timeout) {
    Driver driver = Neo4JManager.open(dbconf);
    Session session;

    final int STABILITY_COUNT_MAX = 10;

    int stabilityCount = 0;
    long lastNumLinks = 0;
    while (stabilityCount < STABILITY_COUNT_MAX) {
      session = driver.session(AccessMode.READ);
      long numlinks = Neo4JManager.countLinks(session);
      if (numlinks != lastNumLinks) {
        stabilityCount = 0;
      } else {
        stabilityCount++;
      }
      LOGGER.info("DB Stability check: numlinks: {} | stabilityCount: {}", numlinks, stabilityCount);
      lastNumLinks = numlinks;
      try {
        Thread.sleep(timeout * 1000);
      } catch (InterruptedException ignored) {/**/}
      session.close();
    }

    driver.close();
  }

  /**
   * Reads links from {@code dataset}.
   * @param dataset the dataset to read.
   * @return the list of read links.
   * @throws IOException when {@code dataset} cannot be read.
   */
  public static List<Link> readLinks(Path dataset) throws IOException {
    LOGGER.trace("Reading links from {}", dataset);
    return Links.readLinks(dataset);
  }

}
