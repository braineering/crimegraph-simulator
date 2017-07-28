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

package com.acmutv.crimegraph_monitor.core.datagen;
import com.acmutv.crimegraph_monitor.core.CoreController;
import com.acmutv.crimegraph_monitor.core.graph.CustomWeightedEdge;
import com.acmutv.crimegraph_monitor.core.graph.GraphController;
import com.acmutv.crimegraph_monitor.core.link.Link;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.UndirectedGraph;
import org.junit.*;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.acmutv.crimegraph_monitor.core.datagen.TestAllDatagen.*;

/**
 * JUnit test suite for {@link DatagenDetection}.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 */
public class DatagenDetectionTest {

  private static final Logger LOGGER = LogManager.getLogger(DatagenDetectionTest.class);

  private static final Path DATASET_RANDOM = FileSystems.getDefault().getPath("data/test/datagen/detection.data");

  private static final Path DATASET_CRIMINAL_WR = FileSystems.getDefault().getPath("data/datasets/criminal_wr.data");

  /**
   * Tests datasets generation for detection.
   */
  @Test
  public void test_datasets_random() throws IOException {
    List<Link> data = CoreController.randomSimple(ORIGINAL_NUM_NODES, ORIGINAL_NUM_LINKS, ORIGINAL_MIN_WEIGHT, ORIGINAL_MAX_WEIGHT);
    CoreController.writeDataset(DATASET_RANDOM, data);
    Assume.assumeTrue(Files.exists(DATASET_RANDOM));

    final Path trainsetPath = Paths.get(DATASET_RANDOM.getParent().toString(),
        String.format("detection_training_%.5f_%s", 1.0 - RATIO, DATASET_RANDOM.getFileName()));
    final Path testsetPath = Paths.get(DATASET_RANDOM.getParent().toString(),
        String.format("detection_test_%.5f_%s", RATIO, DATASET_RANDOM.getFileName()));

    final List<Link> dataset = CoreController.readLinks(DATASET_RANDOM);
    UndirectedGraph<Long, CustomWeightedEdge> graphDataset = GraphController.asGraph(dataset);
    final boolean isDatasetCC = GraphController.isSingleConnectedComponent(graphDataset);
    Assume.assumeTrue(isDatasetCC);

    final Pair<List<Link>,List<Link>> sets = DatagenDetection.datasets(dataset, RATIO);
    final List<Link> trainset = sets.getLeft();
    final List<Link> testset = sets.getRight();

    LOGGER.info("Datasets generated: training {}, test {}", trainsetPath, testsetPath);

    UndirectedGraph<Long, CustomWeightedEdge> graphTrainset = GraphController.asGraph(trainset);
    final boolean isTrainsetCC = GraphController.isSingleConnectedComponent(graphTrainset);

    Assert.assertTrue(isTrainsetCC);
  }

  /**
   * Tests datasets generation for detection.
   */
  @Test
  public void test_datasets_criminal_wr() throws IOException {
    final List<Link> dataset = GraphController.datagenLargestConnectedComponent(
        GraphController.asGraph(CoreController.readLinks(DATASET_CRIMINAL_WR))
    );
    LOGGER.info("Generated LCC dataset");
    UndirectedGraph<Long, CustomWeightedEdge> graphDataset = GraphController.asGraph(dataset);
    final boolean isDatasetCC = GraphController.isSingleConnectedComponent(graphDataset);
    Assume.assumeTrue(isDatasetCC);

    final Pair<List<Link>,List<Link>> sets = DatagenDetection.datasets(dataset, RATIO);
    final List<Link> trainset = sets.getLeft();
    final List<Link> testset = sets.getRight();

    LOGGER.info("Datasets generated");

    UndirectedGraph<Long, CustomWeightedEdge> graphTrainset = GraphController.asGraph(trainset);

    final boolean isTrainsetCC = GraphController.isSingleConnectedComponent(graphTrainset);

    Assert.assertTrue(isTrainsetCC);
  }
}
