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

import com.acmutv.crimegraph_monitor.core.datagen.TestAllDatagen;
import com.acmutv.crimegraph_monitor.core.db.TestAllDb;
import com.acmutv.crimegraph_monitor.core.evaluation.TestAllEvaluation;
import com.acmutv.crimegraph_monitor.core.graph.TestAllGraph;
import com.acmutv.crimegraph_monitor.core.link.TestAllTuple;
import com.acmutv.crimegraph_monitor.core.metric.TestAllMetric;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * JUnit test suite for all tuples.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 * @see TestAllDatagen
 * @see TestAllDb
 * @see TestAllEvaluation
 * @see TestAllGraph
 * @see TestAllMetric
 * @see TestAllTuple
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestAllDatagen.class,
    TestAllDb.class,
    TestAllEvaluation.class,
    TestAllGraph.class,
    TestAllMetric.class,
    TestAllTuple.class
})
public class TestAllCore {
}
