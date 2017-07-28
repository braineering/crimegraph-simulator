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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.UndirectedGraph;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility for the original dataset generation.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 * @see Link
 */
public class DatagenLccTest {

  private static final Logger LOGGER = LogManager.getLogger(DatagenLccTest.class);

  /**
   * Generates the LCC of empty dataset.
   */
  @Test
  public void empty() throws Exception {
    final Path dataset = Paths.get(DatagenLccTest.class.getResource("/core/datagen/graph_empty.data").getPath());

    List<Link> links = CoreController.readLinks(dataset);
    UndirectedGraph<Long, CustomWeightedEdge> graph = GraphController.asGraph(links);
    List<Link> data = GraphController.datagenLargestConnectedComponent(graph);

    Assert.assertTrue(data.isEmpty());
  }

  /**
   * Generates the LCC of single CC dataset.
   */
  @Test
  public void single() throws Exception {
    final Path dataset = Paths.get(DatagenLccTest.class.getResource("/core/datagen/graph_singlecc.data").getPath());

    List<Link> links = CoreController.readLinks(dataset);
    UndirectedGraph<Long, CustomWeightedEdge> graph = GraphController.asGraph(links);

    LOGGER.trace("Edgeset: {}", graph.edgeSet());

    List<Link> data = GraphController.datagenLargestConnectedComponent(graph);

    Assume.assumeTrue(GraphController.isSingleConnectedComponent(graph));

    List<Link> expected = new ArrayList<>();
    try (BufferedReader reader = Files.newBufferedReader(dataset)) {
      while (reader.ready()) {
        Link link = Link.valueOf(reader.readLine());
        expected.add(link);
      }
    }

    LOGGER.info("data: {}", data);
    LOGGER.info("expected: {}", expected);

    Assert.assertTrue(data.containsAll(expected));
    Assert.assertTrue(expected.containsAll(data));
  }

  /**
   * Generates the LCC of multi CC dataset.
   */
  @Test
  public void multi() throws Exception {
    final Path dataset = Paths.get(DatagenLccTest.class.getResource("/core/datagen/graph_multicc.data").getPath());
    final Path result = Paths.get(DatagenLccTest.class.getResource("/core/datagen/graph_multicc_result.data").getPath());

    List<Link> links = CoreController.readLinks(dataset);
    UndirectedGraph<Long, CustomWeightedEdge> graph = GraphController.asGraph(links);
    List<Link> data = GraphController.datagenLargestConnectedComponent(graph);

    List<Link> expected = new LinkedList<>();
    try (BufferedReader reader = Files.newBufferedReader(result)) {
      while (reader.ready()) {
        Link link = Link.valueOf(reader.readLine());
        expected.add(link);
      }
    }

    Assert.assertTrue(data.containsAll(expected));
    Assert.assertTrue(expected.containsAll(data));
  }
}
