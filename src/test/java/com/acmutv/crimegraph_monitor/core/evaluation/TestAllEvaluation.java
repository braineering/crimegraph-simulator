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
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.locks.ReentrantLock;

/**
 * JUnit test suite for datagen evaluation services.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 * @see EvaluationTest
 * @see EvaluationTypeTest
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    EvaluationTest.class,
    EvaluationTypeTest.class
})
public class TestAllEvaluation {

  /**
   * Evaluation lock.
   */
  public static final ReentrantLock EVALUATION_LOCK = new ReentrantLock();

  /**
   * Database configuration.
   */
  public static final DbConfiguration DBCONF = new DbConfiguration("bolt://localhost:7687", "neo4j", "password");

  /**
   * Prediction: test set ratio.
   */
  public static final double PREDICTION_TEST_RATIO = 0.1;

  /**
   * Prediction: the original dataset.
   */
  public static final Path PREDICTION_ORIGIN = FileSystems.getDefault().getPath("data/test/original.datagen");

  /**
   * Prediction: the training set.
   */
  public static final Path PREDICTION_TRAINING = FileSystems.getDefault().getPath(String.format("data/test/%.3f.prediction.training.datagen", 1 - PREDICTION_TEST_RATIO));

  /**
   * Prediction: the test set.
   */
  public static final Path PREDICTION_TEST = FileSystems.getDefault().getPath(String.format("data/test/%.3f.prediction.test.datagen", PREDICTION_TEST_RATIO));

  /**
   * Detection: the test set ratio.
   */
  public static final double DETECTION_TEST_RATIO = 0.1;

  /**
   * Detection: the original dataset.
   */
  public static final Path DETECTION_ORIGIN = FileSystems.getDefault().getPath("data/test/original.datagen");

  /**
   * Detection: the training set.
   */
  public static final Path DETECTION_TRAINING = FileSystems.getDefault().getPath(String.format("data/test/%.3f.detection.training.datagen", 1 - DETECTION_TEST_RATIO));

  /**
   * Detection: the test set.
   */
  public static final Path DETECTION_TEST = FileSystems.getDefault().getPath(String.format("data/test/%.3f.detection.test.datagen", DETECTION_TEST_RATIO));
}
