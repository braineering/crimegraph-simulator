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

import com.acmutv.crimegraph_monitor.core.CoreController;
import com.acmutv.crimegraph_monitor.core.db.Neo4JManager;
import com.acmutv.crimegraph_monitor.core.metric.MetricType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.acmutv.crimegraph_monitor.core.evaluation.TestAllEvaluation.DBCONF;
import static com.acmutv.crimegraph_monitor.core.evaluation.TestAllEvaluation.EVALUATION_LOCK;

/**
 * JUnit test suite for {@link EvaluationController}.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 */
public class EvaluationTest {

  private static final Logger LOGGER = LogManager.getLogger(EvaluationTest.class);

  final Path DATASET = Paths.get(EvaluationTest.class.getResource("/core/evaluation/dataset.data").getPath());
  final Path TRAINSET = Paths.get(EvaluationTest.class.getResource("/core/evaluation/trainset.data").getPath());
  final Path COMPUTEDSET = Paths.get(EvaluationTest.class.getResource("/core/evaluation/computedset.data").getPath());
  final Path TESTSET = Paths.get(EvaluationTest.class.getResource("/core/evaluation/testset.data").getPath());

  final int PRECISION_RANK = 2;

  @Before
  public void init() throws IOException {
    EVALUATION_LOCK.lock();
    boolean neo4jActive = true;
    try {
      Neo4JManager.empyting(DBCONF);
    } catch (Exception exc) {
      neo4jActive = false;
      EVALUATION_LOCK.unlock();
    }
    Assume.assumeTrue(neo4jActive);
    Assume.assumeTrue(Files.exists(DATASET));
    Assume.assumeTrue(Files.exists(TRAINSET));
    Assume.assumeTrue(Files.exists(TESTSET));
    Assume.assumeTrue(Files.exists(COMPUTEDSET));
    CoreController.save(DBCONF, TRAINSET);
    CoreController.save(DBCONF, COMPUTEDSET);

    try {
      Thread.sleep(1500);
    } catch (InterruptedException ignored) { /* ignored */ }
  }

  @After
  public void deinit() {
    try {
      Neo4JManager.empyting(DBCONF);
    } catch (Exception exc) { /* ignored */ }
    if (EVALUATION_LOCK.isHeldByCurrentThread())
      EVALUATION_LOCK.unlock();
  }

  /**
   * Tests AUC evaluation for TA.
   */
  @Test
  public void test_auc() throws IOException {
    boolean checkTrainset = CoreController.checkDatasetOnDb(DBCONF, TRAINSET, null);
    boolean checkComputedset = CoreController.checkDatasetOnDb(DBCONF, COMPUTEDSET, null);

    Assume.assumeTrue(checkTrainset);
    Assume.assumeTrue(checkComputedset);

    double actual = Double.valueOf(EvaluationController.auc(DBCONF, MetricType.NTA, DATASET, TRAINSET, TESTSET).getProperty("result"));
    double expected = 0.6666666666666666;

    LOGGER.info("AUC TA: {}", actual);

    Assert.assertEquals(expected, actual, 0.001);
  }

  /**
   * Tests PRECISION evaluation for TA.
   */
  @Test
  public void test_precision() throws IOException {
    boolean checkTrainset = CoreController.checkDatasetOnDb(DBCONF, TRAINSET, null);
    boolean checkComputedset = CoreController.checkDatasetOnDb(DBCONF, COMPUTEDSET, null);

    Assume.assumeTrue(checkTrainset);
    Assume.assumeTrue(checkComputedset);

    double actual = Double.valueOf(EvaluationController.precision(DBCONF, MetricType.NTA, DATASET, TRAINSET, TESTSET, PRECISION_RANK).getProperty("result"));
    double expected = 0.5;

    LOGGER.info("PRECISION TA: {}", actual);

    Assert.assertEquals(expected, actual, 0.001);
  }
}
