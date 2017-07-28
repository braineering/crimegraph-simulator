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

package com.acmutv.crimegraph_monitor.core.evaluation;

import com.acmutv.crimegraph_monitor.core.db.DbConfiguration;
import com.acmutv.crimegraph_monitor.core.db.Neo4JManager;
import com.acmutv.crimegraph_monitor.core.metric.MetricType;
import com.acmutv.crimegraph_monitor.core.link.Link;
import com.acmutv.crimegraph_monitor.core.link.LinkType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.v1.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static com.acmutv.crimegraph_monitor.core.evaluation.EvaluationQueries.GET_PARTIAL_N1N2_GENERAL;
import static com.acmutv.crimegraph_monitor.core.evaluation.EvaluationQueries.GET_TOP_GENERAL;
import static com.acmutv.crimegraph_monitor.core.evaluation.EvaluationType.AUC;
import static com.acmutv.crimegraph_monitor.core.evaluation.EvaluationType.PRECISION;
import static com.acmutv.crimegraph_monitor.core.link.LinkType.REAL;
import static org.neo4j.driver.v1.Values.parameters;

/**
 * Evaluation services.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 */
public class EvaluationController {

  private static final Logger LOGGER = LogManager.getLogger(EvaluationController.class);

  /**
   * Writes in {@code output} the AUC analysis of detection on the graph pointed by {@code dbconf}
   * with the specified {@code dataset}, {@code trainingset} and {@code testset}.
   * @param dbconf the database configuration.
   * @param metric the metric to evaluate.
   * @param dataset the original dataset.
   * @param trainset the training set.
   * @param testset the test set.
   * @return the result.
   * @throws IOException when datasets cannot be read.
   */
  public static Properties auc(DbConfiguration dbconf, MetricType metric, Path dataset, Path trainset, Path testset) throws IOException {
    LOGGER.info("Evaluating AUC for {}", metric.name());
    Driver driver = Neo4JManager.open(dbconf);
    Session session = driver.session(AccessMode.READ);

    /* DATASET LINKS */
    long examinedDataset = 0;
    long totalDataset = Files.lines(dataset).count();
    double progressDataset;
    double paceDataset = 5.0;
    LOGGER.info("Generating dataset links: {} links", totalDataset);
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") Set<Long> nodes_dataset = new HashSet<>(); // existent nodes
    Set<Pair<Long,Long>> links_dataset = new HashSet<>(); // existent links
    try (BufferedReader datasetReader = Files.newBufferedReader(dataset)) {
      while (datasetReader.ready()) {
        String line = datasetReader.readLine();
        Link link;
        try {
          link = Link.valueOf(line);
        } catch (IllegalArgumentException exc) {
          LOGGER.warn("Malformed link: {}", line);
          continue;
        }

        long src = link.getSrc();
        long dst = link.getDst();

        nodes_dataset.add(src);
        nodes_dataset.add(dst);

        if (src < dst) {
          links_dataset.add(new ImmutablePair<>(src, dst));
        } else if (dst < src) {
          links_dataset.add(new ImmutablePair<>(dst, src));
        }

        examinedDataset++;
        progressDataset = 100.0 * ((double)examinedDataset / (double)totalDataset);
        if (progressDataset % paceDataset < 0.01) {
          LOGGER.info("progress (dataset): {}% :: examined : {}/{} ", Math.round(progressDataset), examinedDataset, totalDataset);
        }
      }
    }

    long numnodes_dataset = nodes_dataset.size();
    long numlinks_dataset = links_dataset.size();
    LOGGER.info("Generated dataset links (nodes: {} | links: {})", numnodes_dataset, numlinks_dataset);

    /* TRAINSET LINKS */
    long examinedTrainset = 0;
    long totalTrainset = Files.lines(trainset).count();
    double progressTrainset;
    double paceTrainset = 5.0;
    LOGGER.info("Generating trainset links: {} links", totalTrainset);
    Set<Long> nodes_trainset = new HashSet<>(); // existent nodes in training set
    Set<Pair<Long,Long>> links_trainset = new HashSet<>(); // existent links in training set
    try (BufferedReader trainingReader = Files.newBufferedReader(trainset)) {
      while (trainingReader.ready()) {
        String line = trainingReader.readLine();
        Link link;
        try {
          link = Link.valueOf(line);
        } catch (IllegalArgumentException exc) {
          LOGGER.warn("Malformed link: {}", line);
          continue;
        }
        long src = link.getSrc();
        long dst = link.getDst();

        nodes_trainset.add(src);
        nodes_trainset.add(dst);

        if (src < dst) {
          links_trainset.add(new ImmutablePair<>(src, dst));
        } else if (dst < src) {
          links_trainset.add(new ImmutablePair<>(dst, src));
        }

        examinedTrainset++;
        progressTrainset = 100.0 * ((double)examinedTrainset / (double)totalTrainset);
        if (progressTrainset % paceTrainset < 0.01) {
          LOGGER.info("progress (trainset): {}% :: examined : {}/{} ", Math.round(progressTrainset), examinedTrainset, totalTrainset);
        }
      }
    }
    int numnodes_trainset = nodes_trainset.size();
    int numlinks_trainset = links_trainset.size();
    LOGGER.info("Generated trainset links (nodes: {} | links: {})", numnodes_trainset, numlinks_trainset);

    /* MISSING LINKS */
    long examinedMissing = 0;
    long totalMissing = Files.lines(testset).count();
    double progressMissing;
    double paceMissing = 5.0;
    LOGGER.info("Generating missing links: {} links", totalMissing);
    Set<Pair<Long,Long>> links_missing = new HashSet<>(); // links in test set between nodes connected in training set
    try (BufferedReader testReader = Files.newBufferedReader(testset)) {
      while (testReader.ready()) {
        String line = testReader.readLine();
        Link link;
        try {
          link = Link.valueOf(line);
        } catch (IllegalArgumentException exc) {
          LOGGER.warn("Malformed link: {}", line);
          continue;
        }

        long src = link.getSrc();
        long dst = link.getDst();

        if (nodes_trainset.contains(src) && nodes_trainset.contains(dst)) {
          if (src < dst) {
            links_missing.add(new ImmutablePair<>(src, dst));
          } else if (dst < src) {
            links_missing.add(new ImmutablePair<>(dst, src));
          }
          LOGGER.info("Missing link added: {}", link);
        }
        examinedMissing++;
        progressMissing = 100.0 * ((double)examinedMissing / (double)totalMissing);
        if (progressMissing % paceMissing < 0.01) {
          LOGGER.info("progress (missing): {}% :: examined : {}/{} ", Math.round(progressMissing), examinedMissing, totalMissing);
        }
      }
    }
    long numlinks_missing = links_missing.size();
    LOGGER.info("Generated missing links (links: {})", numlinks_missing);

    /* NOT EXISTING LINKS */
    long examinedNotexisting = 0;
    //long totalNotexisting = Files.lines(testset).count() * Files.lines(testset).count();
    long totalNotexisting = ((nodes_dataset.size() * (nodes_dataset.size() - 1)) / 2) - links_dataset.size();
    double progressNotexisting;
    double paceNotexisting = 5.0;
    LOGGER.info("Generating not existing links: at most {} links", totalNotexisting);
    Set<Pair<Long,Long>> links_notexistent = new HashSet<>(); // link not existent in origin, between nodes connected in training set
    for (long src : nodes_trainset) {
      for (long dst : nodes_trainset) {
        if (src < dst) {
          Pair<Long,Long> link = new ImmutablePair<>(src, dst);
          if (!links_dataset.contains(link)) {
            links_notexistent.add(link);
            LOGGER.debug("Not existent link: ({},{})", src, dst);
            examinedNotexisting++;
            progressNotexisting = 100.0 * ((double)examinedNotexisting / (double)totalNotexisting);
            if (progressNotexisting % paceNotexisting < 0.01) {
              LOGGER.info("progress (not existing): {}% :: examined : {}/{} ", Math.round(progressNotexisting), examinedNotexisting, totalNotexisting);
            }
          }
        }
      }
    }
    long numlinks_notExistent = links_notexistent.size();
    LOGGER.info("Generated not existing links (links: {})", numlinks_notExistent);

    /* EVALUATION */
    final String GET_PARTIAL_N1N2_METRIC = String.format(GET_PARTIAL_N1N2_GENERAL, metric.name(), metric.name());

    long examinedEvaluation = 0;
    long totalEvaluation = numlinks_missing * numlinks_notExistent;
    double progressEvaluation;
    double paceEvaluation = 5.0;
    LOGGER.info("Evaluation started: {} comparations", totalEvaluation);
    long n1 = 0; // numero di volte in cui lo score di un missing link è maggiore di quello di un link non esistente.
    long n2 = 0; // numero di volte in cui lo score di un missing link è uguale a quello di un link non esistente.
    long n = 0;
    for (Pair<Long,Long> missingLink : links_missing) {
      for (Pair<Long,Long> notExistentLink : links_notexistent) {
        long src1 = missingLink.getLeft();
        long dst1 = missingLink.getRight();
        long src2 = notExistentLink.getLeft();
        long dst2 = notExistentLink.getRight();
        Value params = parameters("src1", src1, "dst1", dst1, "src2", src2, "dst2", dst2);
        StatementResult result = session.run(GET_PARTIAL_N1N2_METRIC, params);
        if (result.hasNext()) {
          Record rec = result.next();
          double w1 = rec.get("w1", 0.0);
          double w2 = rec.get("w2", 0.0);
          boolean n1_bool = (w1 > w2);
          boolean n2_bool = (w1 == w2);
          LOGGER.debug("({},{}) ({},{}) :: n1={}, n2={}", src1, dst1, src2, dst2, n1_bool, n2_bool);
          if (n1_bool) n1++;
          if (n2_bool) n2++;
          n++;
        } else {
          LOGGER.error("Ignored comparison ({},{}) ({},{})", src1, dst1, src2, dst2);
        }
        examinedEvaluation++;
        progressEvaluation = 100.0 * ((double)examinedEvaluation / (double)totalEvaluation);
        if (progressEvaluation % paceEvaluation < 0.01) {
          LOGGER.info("progress (evaluation): {}% :: examined : {}/{} ", Math.round(progressEvaluation), examinedEvaluation, totalEvaluation);
        }
      }
    }
    LOGGER.info("Evaluation completed");

    double auc = ((double)n1 + 0.5*n2) / (double)n;

    Properties result = new Properties();
    result.setProperty("evaluation", AUC.name());
    result.setProperty("metric", metric.name());
    result.setProperty("missing_links", String.valueOf(numlinks_missing));
    result.setProperty("notexistent_links", String.valueOf(numlinks_notExistent));
    result.setProperty("n1", String.valueOf(n1));
    result.setProperty("n2", String.valueOf(n2));
    result.setProperty("n", String.valueOf(n));
    result.setProperty("result", String.valueOf(auc));

    session.close();
    driver.close();

    return result;
  }

  /**
   * Writes in {@code output} the PRECISION analysis of detection on the graph pointed by {@code dbconf}
   * with the specified {@code dataset}, {@code trainingset} and {@code testset}.
   * @param dbconf the database configuration.
   * @param metric the metric to evaluate.
   * @param dataset the original dataset.
   * @param trainset the training set.
   * @param testset the test set.
   * @param rank the precision rank.
   * @return the result.
   * @throws IOException when datasets cannot be read.
   */
  public static Properties precision(DbConfiguration dbconf, MetricType metric, Path dataset, Path trainset, Path testset, int rank) throws IOException {
    LOGGER.info("Evaluating PRECISION for {} with rank {}", metric.name(), rank);
    Driver driver = Neo4JManager.open(dbconf);
    Session session = driver.session();

    /* TOP */
    final String GET_TOP_METRIC = String.format(GET_TOP_GENERAL, metric.name());

    Set<Pair<Long,Long>> links_top_trainset = new HashSet<>(); // top-rank detected
    Value params = parameters("rank", rank);
    StatementResult topTrainingResult = session.run(GET_TOP_METRIC, params);
    while (topTrainingResult.hasNext()) {
      Record rec = topTrainingResult.next();
      long src = rec.get("src").asLong();
      long dst = rec.get("dst").asLong();

      if (src < dst) {
        links_top_trainset.add(new ImmutablePair<>(src, dst));
        LOGGER.info("TOP-TRAINING (rank:{}): ({},{})", rank, src, dst);
      } else if (dst < src) {
        links_top_trainset.add(new ImmutablePair<>(dst, src));
        LOGGER.info("TOP-TRAINING (rank:{}): ({},{})", rank, src, dst);
      }
    }

    Set<Long> nodes_trainset = new HashSet<>(); // existent nodes in training set
    try (BufferedReader trainingReader = Files.newBufferedReader(trainset)) {
      while (trainingReader.ready()) {
        String line = trainingReader.readLine();
        Link link;
        try {
          link = Link.valueOf(line);
        } catch (IllegalArgumentException exc) {
          LOGGER.warn("Malformed link: {}", line);
          continue;
        }
        long src = link.getSrc();
        long dst = link.getDst();
        nodes_trainset.add(src);
        nodes_trainset.add(dst);
      }
    }

    /* EVALUATION */
    Set<Pair<Long,Long>> links_testset = new HashSet<>();
    try (BufferedReader testReader = Files.newBufferedReader(testset)) {
      while (testReader.ready()) {
        String line = testReader.readLine();
        Link link;
        try {
          link = Link.valueOf(line);
        } catch (IllegalArgumentException exc) {
          LOGGER.warn("Malformed link: {}", line);
          continue;
        }
        long src = link.getSrc();
        long dst = link.getDst();
        if (nodes_trainset.contains(src) && nodes_trainset.contains(dst)) {
          if (src < dst) {
            links_testset.add(new ImmutablePair<>(src, dst));
            LOGGER.debug("Can be detected: ({},{})", src, dst);
          } else if (dst < src) {
            links_testset.add(new ImmutablePair<>(dst, src));
            LOGGER.debug("Can be detected: ({},{})", src, dst);
          }
        }
      }
    }
    long numlinks_test = links_testset.size();

    /* COUNT TOP HIT */
    long hits = 0;
    for (Pair<Long,Long> trainingLink : links_top_trainset) {
      if (links_testset.contains(trainingLink)) {
        hits ++;
      }
    }

    double precision = (double)hits / (double)numlinks_test;

    Properties result = new Properties();
    result.setProperty("evaluation", PRECISION.name());
    result.setProperty("metric", metric.name());
    result.setProperty("rank", String.valueOf(rank));
    result.setProperty("hits", String.valueOf(hits));
    result.setProperty("links_test", String.valueOf(numlinks_test));
    result.setProperty("result", String.valueOf(precision));

    session.close();
    driver.close();

    return result;
  }

}
