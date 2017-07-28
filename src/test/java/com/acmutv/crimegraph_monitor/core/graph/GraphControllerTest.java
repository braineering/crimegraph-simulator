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

package com.acmutv.crimegraph_monitor.core.graph;

import com.acmutv.crimegraph_monitor.core.CoreController;
import com.acmutv.crimegraph_monitor.core.link.Link;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.SimpleGraph;
import org.junit.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * JUnit test suite for {@link GraphController}.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 */
public class GraphControllerTest {

  private static final Logger LOGGER = LogManager.getLogger(GraphControllerTest.class);

  final Path DATASET = Paths.get(GraphControllerTest.class.getResource("/core/graph/dataset.data").getPath());

  /**
   * Tests simple graph transformations.
   */
  @Test
  public void simple() {
    UndirectedGraph<Long, CustomWeightedEdge> graph = new Pseudograph<>(CustomWeightedEdge.class);
    graph.addVertex(1L);
    graph.addVertex(2L);
    CustomWeightedEdge edge1 = new CustomWeightedEdge(1.0, 1);
    graph.addEdge(1L, 2L, edge1);

    Assert.assertEquals(1.0, graph.getEdge(1L, 2L).getWeight(), 0);

    edge1 = graph.getEdge(1L, 2L);
    edge1.setWeight(2.0);

    Assert.assertEquals(2.0, graph.getEdge(1L, 2L).getWeight(), 0);

    CustomWeightedEdge edge2 = new CustomWeightedEdge(5.0, 2);
    graph.addEdge(1L, 2L, edge2);

    Set<CustomWeightedEdge> edges = graph.getAllEdges(1L, 2L);

    Assert.assertTrue(edges.contains(edge1) && edges.contains(edge2));

    CustomWeightedEdge _edge2 = graph.getAllEdges(1L, 2L).stream()
        .sorted(Comparator.comparingLong(CustomWeightedEdge::getTs))
        .collect(Collectors.toList()).get(1);

    Assert.assertEquals(5.0, _edge2.getWeight(), 0);

  }

  /**
   * Tests the method asGraph.
   * @throws IOException when dataset cannot be read.
   */
  @Test
  public void test_asGraph() throws IOException {
    final List<Link> links = CoreController.readLinks(DATASET);
    final UndirectedGraph<Long, CustomWeightedEdge> graph = GraphController.asGraph(links);

    List<CustomWeightedEdge> edges = graph.getAllEdges(1L, 3L).stream()
        .sorted(Comparator.comparingLong(CustomWeightedEdge::getTs)).collect(Collectors.toList());

    final long expectedTs1 = 0;
    final double expectedWeight1 = 1.0;

    final long expectedTs2 = 1;
    final double expectedWeight2 = 3.0;

    final CustomWeightedEdge edge1 = edges.get(0);
    final CustomWeightedEdge edge2 = edges.get(1);

    Assert.assertEquals(expectedTs1, edge1.getTs());
    Assert.assertEquals(expectedWeight1, edge1.getWeight(), 0);

    Assert.assertEquals(expectedTs2, edge2.getTs());
    Assert.assertEquals(expectedWeight2, edge2.getWeight(), 0);
  }

  /**
   * Tests the method toEdgeList.
   * @throws IOException when the dataset cannot be read.
   */
  @Test
  public void test_toEdgeList() throws IOException {
    final List<Link> links = CoreController.readLinks(DATASET);
    final UndirectedGraph<Long, CustomWeightedEdge> graph = GraphController.asGraph(links);

    final List<CustomWeightedEdge> edges = GraphController.toEdgeList(graph);

    final List<Link> actual = edges.stream().map(e -> {
      final long src = graph.getEdgeSource(e);
      final long dst = graph.getEdgeTarget(e);
      final double weight = e.getWeight();
      return new Link(src, dst, weight);
    }).collect(Collectors.toList());
    Assert.assertEquals(links, actual);
  }
}
