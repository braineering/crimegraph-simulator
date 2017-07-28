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


import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Evaluation types.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 */
@Getter
public enum EvaluationType {

  AUC       ("AUC"),
  PRECISION ("PRECISION");

  private final String name;

  EvaluationType(final String name) {
    this.name = name;
  }

  /**
   * Returns the evaluation type from {@code string}.
   * @param string the string to parse.
   * @return the evaluation type.
   */
  public static EvaluationType fromString(String string) {
    for (EvaluationType evaluation : EvaluationType.values()) {
      if (evaluation.name.equalsIgnoreCase(string)) {
        return evaluation;
      }
    }
    return null;
  }

  /**
   * Returns the list of evaluation types from {@code string}.
   * @param string the string to parse.
   * @return the list of evaluation types.
   */
  public static List<EvaluationType> fromList(String string) {
    if ("ALL".equalsIgnoreCase(string.trim())) {
      return Arrays.asList(EvaluationType.values());
    }
    List<EvaluationType> evaluations = new ArrayList<>();
    for (String substr : string.trim().split(",")) {
      EvaluationType evaluation = EvaluationType.fromString(substr);
      if (evaluation != null) evaluations.add(evaluation);
    }
    return evaluations;
  }
}
