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
package com.acmutv.crimegraph_monitor.core.evaluation;

/**
 * Collection of Neo4J queries for evaluation.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @since 1.0
 */
public class EvaluationQueries {

  public static final String GET_PARTIAL_N1N2_GENERAL =
      "OPTIONAL MATCH (x1 {id:{src1}})-[r1:%s]->(y1 {id:{dst1}}) " +
          "OPTIONAL MATCH (x2 {id:{src2}})-[r2:%s]->(y2 {id:{dst2}}) " +
          "RETURN r1.weight AS w1, r2.weight AS w2";

  public static final String GET_TOP_GENERAL =
      "MATCH (x)-[r:%s]->(y) " +
          "RETURN x.id AS src, y.id AS dst, r.weight AS weight " +
          "ORDER BY (r.weight) DESC " +
          "LIMIT {rank}";
}
