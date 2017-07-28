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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;

import java.util.*;

import static com.acmutv.crimegraph_monitor.core.evaluation.EvaluationType.AUC;
import static com.acmutv.crimegraph_monitor.core.evaluation.EvaluationType.PRECISION;

/**
 * JUnit test suite for {@link EvaluationType}.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 */
public class EvaluationTypeTest {

  private static final Logger LOGGER = LogManager.getLogger(EvaluationTypeTest.class);

  /**
   * Tests from list.
   */
  @Test
  public void test_fromList() {
    Map<String,List<EvaluationType>> data = new HashMap<>();
    data.put("", new ArrayList<>());
    data.put("ALL", Arrays.asList(EvaluationType.values()));
    for (EvaluationType evaluation : EvaluationType.values()) data.put(evaluation.name(), new ArrayList<EvaluationType>(){{add(evaluation);}});
    data.put(AUC.name()+","+ PRECISION.name(), new ArrayList<EvaluationType>(){{add(AUC);add(PRECISION);}});

    for (Map.Entry<String,List<EvaluationType>> entry : data.entrySet()) {
      String string = entry.getKey();
      List<EvaluationType> expected = entry.getValue();
      List<EvaluationType> actual = EvaluationType.fromList(string);
      Assert.assertEquals(expected, actual);
    }
  }
}
