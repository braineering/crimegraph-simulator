/*
  The MIT License (MIT)

  Copyright (c) 2017 Giacomo Marciani

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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.UndirectedGraph;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Utility to generate training and test sets from a original dataset for prediction.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 */
public class DatagenPrediction {

  private static final Logger LOGGER = LogManager.getLogger(CoreController.class);

  /**
   * Generates the training and test sets from the original dataset with {@code ratio}, for prediction.
   * @param dataset the original dataset.
   * @param ratio the test ratio.
   * @return the pair of list, where the first is the trainset of links, and the second is the testset of links.
   * @throws IOException when datasets cannot be read or written.
   */
  public static Pair<List<Link>,List<Link>> datasets(List<Link> dataset, double ratio) throws IOException {
    final long originalSize = dataset.size();
    final long toRemove = Math.round(ratio * originalSize);

    List<Link> trainset = new LinkedList<>(dataset);
    List<Link> testset = new LinkedList<>();

    UndirectedGraph<Long, CustomWeightedEdge> graph = GraphController.asGraph(trainset);
    final boolean isDatasetLCC = GraphController.isSingleConnectedComponent(graph);
    LOGGER.trace("Dataset is {}single LCC", ((isDatasetLCC)?"": "not "));

    long removed = 0;
    ListIterator iter = trainset.listIterator(trainset.size());
    while (iter.hasPrevious() && removed < toRemove) {
     Link link = (Link) iter.previous();
     long src = link.getSrc();
     long dst = link.getDst();
     double weight = link.getWeight();

     graph.removeEdge(src, dst);
     boolean isLCC = GraphController.isSingleConnectedComponent(graph);

     if (!isLCC) {
       CustomWeightedEdge edge = graph.addEdge(src, dst);
       edge.setWeight(weight);
       LOGGER.trace("Skipping removal of link: {}", link);
     } else {
       iter.remove();
       testset.add(link);
       removed++;
       LOGGER.trace("Removed link ({}/{}): {}", removed, toRemove, link);
     }
    }

    return new ImmutablePair<>(trainset, testset);
  }
}
