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

import com.acmutv.crimegraph_monitor.core.link.Link;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.SimpleGraph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class realizes the core business logic about graph manipulation.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 */
public class GraphController {

  private static final Logger LOGGER = LogManager.getLogger(GraphController.class);

  /**
   * Analyzes the dataset.
   * @param graph the graph.
   * @return the collection of analysis properties.
   */
  public static Properties analyzeGraph(UndirectedGraph<Long, CustomWeightedEdge> graph) {
    Properties analysis = new Properties();

    final long ccVertexsets = new ConnectivityInspector<>(graph).connectedSets().size();
    List<Link> lccLinks = GraphController.datagenLargestConnectedComponent(graph);

    Set<Long> lccNodes = new HashSet<>();
    for (Link link : lccLinks) {
      long src = link.getSrc();
      long dst = link.getDst();
      lccNodes.add(src);
      lccNodes.add(dst);
    }

    analysis.put("numnodes", graph.vertexSet().size());
    analysis.put("numlinks", graph.edgeSet().size());
    analysis.put("cc_vertexsets", ccVertexsets);
    analysis.put("lcc_numnodes",lccNodes.size());
    analysis.put("lcc_numlinks", lccLinks.size());

    return analysis;
  }

  /**
   * Creates the graph from the {@code dataset}.
   * @param dataset the dataset.
   * @return the graph.
   */
  public static UndirectedGraph<Long, CustomWeightedEdge> asGraph(List<Link> dataset) {
    LOGGER.trace("Reading the graph dataset");
    UndirectedGraph<Long, CustomWeightedEdge> graph = new Pseudograph<>(CustomWeightedEdge.class);

    long examined = 0;
    long total = dataset.size();
    double progress;
    double pace = 5.0;
    for (Link link : dataset) {
      long src = link.getSrc();
      long dst = link.getDst();
      double weight = link.getWeight();
      graph.addVertex(src);
      graph.addVertex(dst);
      CustomWeightedEdge edge = new CustomWeightedEdge(weight, examined);
      try {
        boolean addedEdge = graph.addEdge(src, dst, edge);
        LOGGER.trace("Link created ({},{}) {}: {}", src, dst, addedEdge, edge);
      } catch (IllegalArgumentException exc) {
        LOGGER.warn("Cannot add link ({},{}): {}", src, dst, exc.getMessage());
      }
      /*
      boolean containsEdge = graph.containsEdge(src, dst);
      if (containsEdge) {
        CustomWeightedEdge edge = graph.getEdge(src, dst);
        final double currWeight = edge.getWeight();
        final long currNum = edge.getNum();
        final long num = currNum + 1;
        weight = (currWeight * currNum + weight)/(num);
        edge.setNum(num);
        edge.setWeight(weight);
        LOGGER.trace("Link already existent, updating ({},{}): {}", src, dst, edge);
      } else {
        CustomWeightedEdge edge = new CustomWeightedEdge(weight, 1, examined);
        try {
          boolean addedEdge = graph.addEdge(src, dst, edge);
          LOGGER.trace("Link not existent, creating ({},{}) {}: {}", src, dst, addedEdge, edge);
        } catch (IllegalArgumentException exc) {LOGGER.trace(exc.getMessage());}
      }
      */
      examined++;
      progress = 100.0 * ((double)examined / (double)total);
      if (progress % pace < 0.001) {
        LOGGER.info("progress: {}% :: examined : {}/{} ", Math.round(progress), examined, total);
      }
    }
    return graph;
  }

  /**
   * Return the ordered list of edges.
   * @param graph the graph.
   * @return the list of edges.
   */
  public static List<CustomWeightedEdge> toEdgeList(UndirectedGraph<Long, CustomWeightedEdge> graph) {
    return graph.edgeSet().stream()
        .sorted(Comparator.comparingLong(CustomWeightedEdge::getTs)).collect(Collectors.toList());
  }

  /**
   * Computes the LCC vertex set.
   * @param graph the graph.
   * @return the LCC vertex set.
   */
  public static Set<Long> getLCCVertexset(UndirectedGraph<Long, CustomWeightedEdge> graph) {
    ConnectivityInspector<Long, CustomWeightedEdge> ci = new ConnectivityInspector<>(graph);
    List<Set<Long>> connectedSets = ci.connectedSets();
    LOGGER.trace("Found {} CC vertex sets: {}", connectedSets.size(), connectedSets);
    Set<Long> largestConnectedSet = new HashSet<>();
    for (Set<Long> connectedSet : connectedSets) {
      if (connectedSet.size() > largestConnectedSet.size()) {
        largestConnectedSet = connectedSet;
      }
    }
    LOGGER.trace("Found LCC vertex sets with {} nodes", largestConnectedSet.size());
    return largestConnectedSet;
  }

  /**
   * Generates the list of links belonging to the largest connected component of the graph in
   * {@code graph}.
   * @param graph the graph.
   * @return the list of links.
   */
  public static List<Link> datagenLargestConnectedComponent(UndirectedGraph<Long, CustomWeightedEdge> graph) {
    LOGGER.traceEntry();
    Set<Long> largestConnectedSet = GraphController.getLCCVertexset(graph);

    Set<CustomWeightedEdge> edges = new HashSet<>();
    for (Long node : largestConnectedSet) {
      long src = node;
      Set<CustomWeightedEdge> edgesOf = graph.edgesOf(node);
      LOGGER.trace("EdgesOfNode {} : {}", node, edgesOf);
      for (CustomWeightedEdge edge : edgesOf) {
        long dst = graph.getEdgeTarget(edge);
        if (src == dst) continue;
        if (largestConnectedSet.contains(dst)) {
          edges.add(edge);
        }
      }
    }

    List<Link> data = new LinkedList<>();
    edges.stream().sorted(Comparator.comparingLong(CustomWeightedEdge::getTs)).forEach(e -> {
      final long src = graph.getEdgeSource(e);
      final long dst = graph.getEdgeTarget(e);
      final double weight = e.getWeight();
      Link link = new Link(src, dst, weight);
      data.add(link);
    });

    return data;
  }

  /**
   * Checks if the {@code graph} is a single CC.
   * @param graph the graph.
   * @return true, if {@code graph} is a single CC; false, otherwise.
   */
  public static boolean isSingleConnectedComponent(UndirectedGraph<Long, CustomWeightedEdge> graph) {
    final Set<Long> lccNodes = GraphController.getLCCVertexset(graph);
    final long numNodes = graph.vertexSet().size();
    final long numLccNodes = lccNodes.size();
    final boolean result = (numLccNodes == numNodes);
    LOGGER.trace("isLCC {} : {}/{}", result, numLccNodes, numNodes);
    return result;
  }
}
