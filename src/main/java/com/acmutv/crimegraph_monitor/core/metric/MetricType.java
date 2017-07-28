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

package com.acmutv.crimegraph_monitor.core.metric;


import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Metric types.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 */
@Getter
public enum MetricType {
  NRA       ("NRA"),
  TA        ("TA"),
  NTA       ("NTA"),

  CN        ("CN"),
  JACCARD   ("JACCARD"),
  SALTON    ("SALTON"),
  SORENSEN  ("SORENSEN"),
  HPI       ("HPI"),
  HDI       ("HDI"),
  LHN1      ("LHN1"),
  PA        ("PA"),
  AA        ("AA"),
  RA        ("RA");

  private final String name;

  MetricType(final String name) {
    this.name = name;
  }

  /**
   * Returns the metric from {@code string}.
   * @param string the string to parse.
   * @return the metric.
   */
  public static MetricType fromString(String string) {
    for (MetricType metric : MetricType.values()) {
      if (metric.name.equalsIgnoreCase(string)) {
        return metric;
      }
    }
    return null;
  }

  /**
   * Returns the list of metric types from {@code string}.
   * @param string the string to parse.
   * @return the list of metric types.
   */
  public static List<MetricType> fromList(String string) {
    if ("ALL".equalsIgnoreCase(string.trim())) {
      return Arrays.asList(MetricType.values());
    }
    List<MetricType> metrics = new ArrayList<>();
    for (String substr : string.trim().split(",")) {
      MetricType metric = MetricType.fromString(substr);
      if (metric != null) metrics.add(metric);
    }
    return metrics;
  }
}
