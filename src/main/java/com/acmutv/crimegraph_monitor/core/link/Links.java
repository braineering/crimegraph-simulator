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
package com.acmutv.crimegraph_monitor.core.link;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * This class realizes services related to {@code Link}.
 * @author Giacomo Marciani {@literal <gmarciani@acm.org>}
 * @author Michele Porretta {@literal <mporretta@acm.org>}
 * @since 1.0
 */
public class Links {

  private static final Logger LOGGER = LogManager.getLogger(Links.class);

  /**
   * Reads links from {@code dataset}.
   * @param dataset the dataset to read.
   * @return the list of read links.
   * @throws IOException when {@code dataset} cannot be read.
   */
  public static List<Link> readLinks(Path dataset) throws IOException {
    LOGGER.trace("Reading links from {}", dataset);
    List<Link> links = new LinkedList<>();

    try (BufferedReader reader = Files.newBufferedReader(dataset)) {
      long lineno = 0;
      while (reader.ready()) {
        String line = reader.readLine();
        lineno++;
        Link link;
        try {
          link = Link.valueOf(line);
        } catch (IllegalArgumentException exc) {
          LOGGER.warn("Malformed link (line: {}): {}", lineno, line);
          continue;
        }
        links.add(link);
      }
    }
    return links;
  }
}
